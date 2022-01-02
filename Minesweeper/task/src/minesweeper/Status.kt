package minesweeper

enum class Status(val status: String, val message: String) {
    FAIL("fail", "You stepped on a mine"),
    PLAYING("playing", "Set/unset mines marks or claim a cell as free: "),
    WIN("win","Congratulations! You found all the mines!")
}