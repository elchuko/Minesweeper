package minesweeper

fun main() {
    println("How Many mines do you want on the field?")
    val minesNumber = readLine()!!.toInt()
    val field = Grid(mines = minesNumber)
    field.printField()
    while (true) {
        print("Set/delete mines marks (x and y coordinates): ")
        val input = readLine()!!.split(" ")
        if (input[0] == "show") {
            field.showMines()
            continue
        }
        if (!field.markCoordinates(input[1].toInt(), input[0].toInt())) continue
        field.printField()
        if (field.finished()) break
    }
    println("Congratulations! You found all the mines!")
}
