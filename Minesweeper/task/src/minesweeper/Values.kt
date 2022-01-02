package minesweeper

enum class Values(val char: Char) {
    MINE('X'),
    FIELD('.'),
    MARK('*'),
    FREE('/'),
    ZERO('0')
}