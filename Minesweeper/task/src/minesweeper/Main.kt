package minesweeper

fun main() {
    var status: Status
    println("How Many mines do you want on the field?")
    val minesNumber = readLine()!!.toInt()
    val field = Grid(mines = minesNumber)
    field.printField()

    while (true) {
        status = field.finished()
        when (status) {
            Status.PLAYING -> println(status.message)
            else -> {
                println(status.message)
                break
            }
        }
        val input = readLine()!!.split(" ")
        val result = field.interact(input[1].toInt(), input[0].toInt(), input[2])
        when (result) {
            is Boolean -> if (!result) continue // if it fails, then continue without printing
            else -> ""
        }

        field.printField()
    }
}
