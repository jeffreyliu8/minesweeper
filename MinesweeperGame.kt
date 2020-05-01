const val CMD_NEW = "new"
const val CMD_REVEAL = "reveal"
const val CMD_MARK = "mark"


fun main() {
    val game = MinesweeperGame()

    while (true) {
        val enteredString = readLine()
        enteredString?.let {
            val splitted = it.split(' ')

            if (splitted.size != 3) {
                println("Invalid input, must be 3 arguments")
            } else if (!splitted[1].isInt() || !splitted[2].isInt()) {
                println("Invalid number input at location 1 or 2")
            }
            val input1 = splitted[1].toIntOrNull()!!
            val input2 = splitted[2].toIntOrNull()!!

            if (splitted[0].toLowerCase() == CMD_NEW) {
                val isSuccussful = game.newBoard(input1, input2)
                if (isSuccussful) {
                    game.printBoard()
                } else {
                    println("Failed to create board");
                }
            } else if (splitted[0].toLowerCase() == CMD_REVEAL) {
                game.reveal(input2, input1)
            } else if (splitted[0].toLowerCase() == CMD_MARK) {
                game.mark(input2, input1)
            } else {
                println("Invalid cmd")
            }
        }
    }
}

class MinesweeperGame {
    var board: Array<IntArray>? = null

    fun newBoard(size: Int, numberOfMines: Int): Boolean {
        if (numberOfMines < 0) {
            return false
        }
        if (numberOfMines >= size * size) {
            return false
        }
        if (size > 10) {
            return false
        }

        board = Array(size, { IntArray(size) })

        return true
    }

    fun reveal(col: Int, row: Int) {

    }

    fun mark(col: Int, row: Int) {

    }

    fun printBoard() {
        board?.let {
            for (array in it) {
                for (value in array) {
                    print("$value ")
                }
                println()
            }
        } ?: run {
            println("Board has not been initialized")
        }
    }
}

fun String.isInt(): Boolean {
    if (this == null) {
        return false
    }
    val result = this.toIntOrNull()
    if (result == null) {
        return false
    }
    return true
}