const val CMD_NEW = "new"
const val CMD_REVEAL = "reveal"
const val CMD_MARK = "mark"


fun main() {
    println("Please enter a command with 2 int arguments:")
    val game = MinesweeperGame()

    while (true) {
        val enteredString = readLine()
        if (enteredString == null) {
            println("Invalid input null, try again")
            continue
        }

        val splitted = enteredString.split(' ')

        if (splitted.size < 3) {
            println("Invalid input, must be at least 3 arguments")
            continue
        } else if (!splitted[1].isInt() || !splitted[2].isInt()) {
            println("Invalid number input at location 1 or 2")
            continue
        }
        val input1 = splitted[1].toIntOrNull()!!
        val input2 = splitted[2].toIntOrNull()!!

        if (splitted[0].toLowerCase() == CMD_NEW) {
            val isSuccussful = game.newBoard(input1, input2)
            if (isSuccussful) {
                game.printBoard()
                println("New board created, go ahead with \"reveal\" or \"mark\" cmd");
            } else {
                println("Failed to create board");
            }
        } else if (splitted[0].toLowerCase() == CMD_REVEAL) {
            val isSuccussful = game.reveal(input2, input1)
            if (isSuccussful) {
                game.printBoard()
            } else {
                println("reveal failed, please check input");
            }
        } else if (splitted[0].toLowerCase() == CMD_MARK) {
            val isSuccussful = game.mark(input2, input1)
            if (isSuccussful) {
                game.printBoard()
            } else {
                println("mark failed, please check input");
            }
        } else {
            println("Invalid cmd")
        }
    }
}

class MinesweeperGame {
    lateinit var board: Array<CharArray>
    val mines = HashSet<Pair<Int, Int>>() // actual location of mines
    var numberOfMines = 0 // how many the user set

    fun newBoard(size: Int, numberOfMines: Int): Boolean {
        if (numberOfMines < 0) {
            return false
        }
        if (numberOfMines >= size * size) {
            return false
        }
        if (size > 19) {
            return false
        }
        this.numberOfMines = numberOfMines

        board = Array(size, { CharArray(size) })
        return true
    }

    fun reveal(row: Int, col: Int): Boolean {
        if (!::board.isInitialized) {
            return false
        }
        if (row >= board.size || col >= board.size || row < 0 || col < 0) {
            return false
        }
        if (mines.size == 0) {
            initMinesWithLocation(row, col)
        }
        return true
    }

    fun mark(row: Int, col: Int): Boolean {
        if (!::board.isInitialized) {
            return false
        }
        if (row >= board.size || col >= board.size || row < 0 || col < 0) {
            return false
        }
        if (mines.size == 0) {
            initMinesWithLocation(row, col)
        }
        return true
    }

    private fun initMinesWithLocation(row: Int, col: Int) {
        while (board[row][col] != '_') {
            initMines()
        }
    }

    private fun initMines() {
        var count = 0

        for (i in 0 until board.size) {
            for (j in 0 until board.size) {
                if (count >= numberOfMines) {
                    board[i][j] = '_'
                } else {
                    board[i][j] = 'b'
                    count++
                }
            }
        }

        for (j in 0 until board.size) {
            val temp = mutableListOf<Char>()
            for (i in 0 until board.size) {
                temp.add(board[i][j])
            }
            temp.shuffle()
            for (k in 0 until board.size) {
                board[k][j] = temp[k]
            }
        }
        for (i in 0 until board.size) {
            board[i] = board[i].toMutableList().shuffled().toCharArray()
        }
        mines.clear()
        for (i in 0 until board.size) {
            for (j in 0 until board.size) {
                if (board[i][j] == 'b') {
                    mines.add(Pair(i, j))
                }
            }
        }
//        mines.forEach {
//            val r = "${it.first} ${it.second}"
//            println(r)
//        }
    }

    fun printBoard() {
        if (::board.isInitialized) {
            for (array in board) {
                for (value in array) {
                    print("$value ")
                }
                println()
            }
        } else {
            println("Board has not been initialized")
        }
    }
}

fun String.isInt(): Boolean {
    return this.toIntOrNull() != null
}