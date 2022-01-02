package minesweeper

fun main() {
    println("How Many mines do you want on the field?")
    val minesNumber = readLine()!!.toInt()
    val field = Grid(mines = minesNumber)
    field.printField()
    while (true) {
        print("Set/delete mines marks (x and y coordinates): ")
        val input = readLine()!!.split(" ")
        val result = field.interact(input[1].toInt(), input[0].toInt(), input[2])
        when (result) {
            is Boolean -> if (!result) continue // if it fails, then continue without printing
            else -> ""
        }

        field.printField()
        if (field.finished()) break
    }
    println("Congratulations! You found all the mines!")
}
