package minesweeper

import kotlin.random.Random

class Grid(length: Int = 5, width: Int = 5, var mines: Int = 5) {


    var status = Status.PLAYING.status
    private val extLength = length + 2
    private val extWidth = width + 2
    private val field: MutableList<MutableList<Cell>> = MutableList(extLength) { MutableList(extWidth) { Cell(it) } }
    private val minesPosition = mutableListOf<Int>()
    private val listOfMines = mutableListOf<Cell>()
    private val listOfMarks = mutableListOf<Cell>()
    private val listOfFree = mutableListOf<Cell>()
    private val alwaysClear = mutableListOf<Int>()

    init {
        var index = 0
        var row = 0
        var column = 0

        calculateAlwaysClearCells()

        repeat(mines) {
            minesPosition.add(index)
            do {
                index = Random.nextInt(0, extLength * extWidth)
            } while(minesPosition.contains(index) || alwaysClear.contains(index))
            row = index / extWidth
            column = index % extWidth

            field[row][column].setMine()
            listOfMines.add(field[row][column])
        }
        calculateAdjacency()
    }

    fun finished(): Boolean {
        // Falta agregar que si se exploran todas las celdas vacias, tmb se acaba el juego
        return checkAllMinesMarked() && checkMarkAreMines()
    }

    fun interact(row: Int, column: Int, command: String): Any {
        return when (command) {
            "mine" -> markAsMine(row, column)
            "free" -> explore(row, column)
            "show" -> showMines()
            else -> displayCommandError()
        }
    }

    private fun displayCommandError(): Boolean {
        println("Unknown command")
        return false
    }

    private fun explore(row: Int, column: Int) {
        when {
            field[row][column].isMine() -> listOfMines.forEach { it.displayValue() }
            field[row][column].isAdjacentNotZero() -> field[row][column].displayValue()
            else -> floodFill(row, column)
        }
    }

    private fun markAsMine(row: Int, column: Int): Boolean {
        return field[row][column].mark()
    }

    private fun floodFill(row: Int, column: Int) {
        var index = (row * extWidth) + column

        if (field[row][column].isMine() || alwaysClear.contains(index) || field[row][column].displayed) return

        field[row][column].displayValue()

        floodFill(row + 1, column) //go down
        floodFill(row - 1, column) //go up
        floodFill(row, column + 1) //go right
        floodFill(row, column - 1) //go left
    }

    private fun checkMarkAreMines(): Boolean {
        for (cell in listOfMarks) {
            if (!cell.isMine()) return false
        }
        return true
    }


    private fun checkAllMinesMarked(): Boolean {
        for (cell in listOfMines) {
            if (!cell.marked) {
                return false
            }
        }
        return true
    }

    private fun checkAllFreeSpaces(): Boolean { // TODO
        var allCells = field.flatten()
        for (cell in allCells) {
            if (listOfMines.contains(cell)) continue
            if (cell.cellValue == Values.FIELD.char) return false
        }
        status = Status.WIN.status
        return true
    }

    private fun calculateAlwaysClearCells() {
        for (i in 0..(extLength * extWidth) - 1) {
            when {
                i in 0..extWidth -> alwaysClear.add(i) // First row
                i % extWidth == 0 -> alwaysClear.add(i) // first column
                (i + 1) % extWidth == 0 -> alwaysClear.add(i) // last column
                i in ((extLength - 1) * extWidth)..( (extLength * extWidth) - 1 ) -> alwaysClear.add(i) // last row
            }
        }
    }

    private fun calculateAdjacency() {
        for (row in 1..extLength - 2) {
            for (column in  1..extWidth - 2) {
                if (field[row][column].isMine()) continue
                field[row][column].adjacent = countAdjacentMines(row, column)
            }
        }
    }

    private fun countAdjacentMines(row: Int, column: Int): Char {
        var count = 0
        val cellMatrix = mutableListOf<Cell>()

        cellMatrix.add(field[row - 1][column - 1])
        cellMatrix.add(field[row - 1][column])
        cellMatrix.add(field[row - 1][column + 1])
        cellMatrix.add(field[row][column - 1])
        cellMatrix.add(field[row][column + 1])
        cellMatrix.add(field[row + 1][column - 1])
        cellMatrix.add(field[row + 1][column])
        cellMatrix.add(field[row + 1][column + 1])

        for(cell in cellMatrix) {
            if (cell.isMine()) count++
        }

        return when (count) {
            0 -> Values.ZERO.char
            else -> {
                count.toString().first()
            }
        }
    }

    private fun printHeader() {
        for (i in 0..(extWidth - 1)) {
            print( when {
                i == 0 -> " |"
                i == extWidth - 1 -> "|\n"
                else -> i
            })
        }
        printLine()
    }

    private fun printLine() {
        for (i in 0..(extWidth - 1)) {
            print( when {
                i == 0 -> "—|"
                i == extWidth - 1 -> "|\n"
                else -> "—"
            })
        }
    }

    fun showMines() {
        for (i in 0..(extLength - 1)) {
            when {
                i == 0 -> printHeader()
                i == extLength - 1 -> printLine()
                else -> println("${i}|" + field[i].slice(1..(extWidth - 2)).joinToString("") { it.isMine().toString() } + "|")
            }
        }
    }

    fun printField() {
        println("")
        for (i in 0..(extLength - 1)) {
            when {
                i == 0 -> printHeader()
                i == extLength - 1 -> printLine()
                else -> println("${i}|" + field[i].slice(1..(extWidth - 2)).joinToString("") { it.cellValue.toString() } + "|")
            }
        }
    }

    inner class Cell(var position: Int ) {
        private var mine: Boolean = false
        var displayed: Boolean = false
        var adjacent: Char = Values.ZERO.char
        var cellValue: Char = Values.FIELD.char
        var marked: Boolean = false

        fun setMine() {
            mine = true
        }

        fun isMine(): Boolean {
            return mine
        }

        fun displayValue() {
            cellValue = when {
                isMine() -> Values.MINE.char
                adjacent != Values.ZERO.char -> adjacent
                else -> Values.FREE.char
            }
            displayed = true
        }

        fun mark(): Boolean {
            return when {
                isNumber() -> {
                    println("There is a number here!")
                    return false
                }
                else -> {
                    toggleMark()
                    return true
                }
            }
        }

        fun isAdjacentNotZero(): Boolean {
            return adjacent != Values.ZERO.char
        }

        private fun isNumber(): Boolean {
            return cellValue.isDigit()
        }

        private fun toggleMark() {
            marked = !marked
            setMarkValue()
        }

        private fun setMarkValue() {
            cellValue = if (marked) {
                Values.MARK.char
            } else {
                Values.FIELD.char
            }
        }

    }
}