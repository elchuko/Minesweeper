package minesweeper

import kotlin.random.Random

class Grid(var length: Int = 9, var width: Int = 9, var mines: Int = 5) {

    private val _MINE = 'X'
    private val _FIELD = '.'
    private val extLength = length + 2
    private val extWidth = width + 2
    private val field: MutableList<MutableList<Cell>> = MutableList(extLength) { MutableList(extWidth) { Cell(it) } }
    private val minesPosition = mutableListOf<Int>()
    private val listOfMines = mutableListOf<Cell>()
    private val listOfMarks = mutableListOf<Cell>()
    private val alwaysClear = mutableListOf<Int>()

    init {
        var index = 0
        var row = 0
        var column = 0
        var max = extWidth.coerceAtLeast(extLength)

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

    /*
    fun populateField(mines: Int) {

        var index = 0
        var row = 0
        var column = 0
        var max = extWidth.coerceAtLeast(extLength)

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
     */

    fun finished(): Boolean {
        return checkAllMinesMarked() && checkMarkAreMines()
    }

    fun clickOnCoordintes(row: Int, column: Int) {

    }

    fun markCoordinates(row: Int, column: Int): Boolean {
        return field[row][column].mark()
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

    private fun calculateAlwaysClearCells() {
        for (i in 0..(extLength * extWidth) - 1) {
            when {
                i in 0..extWidth -> alwaysClear.add(i) // First row
                i % extWidth == 0 -> alwaysClear.add(i) // first column // TODO NO FUNCIONA
                (i + 1) % extWidth == 0 -> alwaysClear.add(i) // last column ?? TOMA LAS ULTIMAS 2 COLUMNAS
                i in ((extLength - 1) * extWidth)..( (extLength * extWidth) - 1 ) -> alwaysClear.add(i) // last row // TODO no funciona, agarra las ultimas 2 lineas
            }
        }
    }

    private fun calculateAdjacency() {
        for (row in 1..extLength - 2) {
            for (column in  1..extWidth - 2) {
                if (field[row][column].isMine()) continue
                field[row][column].cellValue = countAdjacentMines(row, column)
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
            0 -> '.'
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
        var cellValue: Char = _FIELD
        var marked: Boolean = false

        fun setMine() {
            mine = true
        }

        fun isMine(): Boolean {
            return mine
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

        private fun isNumber(): Boolean {
            return cellValue.isDigit()
        }

        private fun toggleMark() {
            marked = !marked
            setMarkValue()
        }

        private fun setMarkValue() {
            cellValue = if (marked) {
                '*'
            } else {
                '.'
            }
        }

    }
}