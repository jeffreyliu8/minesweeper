enum class GameCommands {
    new, reveal, mark
}

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

        if (splitted[0].toLowerCase() == GameCommands.new.name) {
            val isSuccussful = game.newBoard(input1, input2)
            if (isSuccussful) {
                game.printBoard()
                game.printUserBoard()
                println("New board created, go ahead with \"reveal\" or \"mark\" cmd")
            } else {
                println("Failed to create board")
            }
        } else if (splitted[0].toLowerCase() == GameCommands.reveal.name) {
            val isSuccussful = game.reveal(input2, input1)
            if (isSuccussful) {
                game.printBoard()
                game.printUserBoard()

                if (game.getStatus() == GameStatus.lose) {
                    println("You lost, game over")
                }
            } else {
                println("reveal failed, please check input")
            }
        } else if (splitted[0].toLowerCase() == GameCommands.mark.name) {
            val isSuccussful = game.mark(input2, input1)
            if (isSuccussful) {
                game.printBoard()
                game.printUserBoard()

                if (game.getStatus() == GameStatus.win) {
                    println("Congrat! You win!")
                }
            } else {
                println("mark failed, please check input")
            }
        } else {
            println("Invalid cmd")
        }
    }
}

enum class GameStatus {
    not_started, ongoing, win, lose
}

class MinesweeperGame {
    private lateinit var board: Array<CharArray>
    private lateinit var userboard: Array<CharArray>
    private val mines = HashSet<Pair<Int, Int>>() // actual location of mines
    private var numberOfMines = 0 // how many the user set
    private var status = GameStatus.not_started

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
        mines.clear()

        board = Array(size) {
            CharArray(size, {
                ' '
            })
        }
        userboard = Array(size) {
            CharArray(size, {
                '_'
            })
        }
        status = GameStatus.not_started
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
        status = GameStatus.ongoing
        if (board[row][col] == 'b') {
            status = GameStatus.lose
            userboard[row][col] = 'X' // dead, exploded
            revealAnswer()
        } else {
            // not bomb,
            val v = numberOfMinesAround(row, col).toString().first()
            userboard[row][col] = v
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
        status = GameStatus.ongoing
        if (userboard[row][col] == 'M') {
            userboard[row][col] = '_'
        } else if (userboard[row][col] == '_') {
            userboard[row][col] = 'M'
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
        for (i in board.indices) {
            for (j in board.indices) {
                if (count >= numberOfMines) {
                    board[i][j] = '_'
                } else {
                    board[i][j] = 'b'
                    count++
                }
            }
        }

        for (j in board.indices) {
            val temp = mutableListOf<Char>()
            for (i in board.indices) {
                temp.add(board[i][j])
            }
            temp.shuffle()
            for (k in board.indices) {
                board[k][j] = temp[k]
            }
        }
        for (i in board.indices) {
            board[i] = board[i].toMutableList().shuffled().toCharArray()
        }
        mines.clear()
        for (i in board.indices) {
            for (j in board.indices) {
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

    fun printUserBoard() {
        println("================= user board ======")
        if (::userboard.isInitialized) {
            for (array in userboard) {
                for (value in array) {
                    print("$value ")
                }
                println()
            }
        } else {
            println("User Board has not been initialized")
        }
    }

    fun getStatus(): GameStatus {
        return status
    }

    private fun revealAnswer() {
        for (i in board.indices) {
            for (j in board.indices) {
                if (userboard[i][j] == 'X') {
                    continue
                }
                if (board[i][j] == 'b') {
                    userboard[i][j] = 'b'
                    continue
                }
                userboard[i][j] = numberOfMinesAround(i, j).toString().first()
            }
        }
    }

    private fun numberOfMinesAround(row: Int, col: Int): Int {
        var count = 0
        if (row - 1 >= 0 && board[row - 1][col] == 'b') {
            count++
        }
        if (row + 1 < board.size && board[row + 1][col] == 'b') {
            count++
        }
        if (col - 1 >= 0 && board[row][col - 1] == 'b') {
            count++
        }
        if (col + 1 < board.size && board[row][col + 1] == 'b') {
            count++
        }
        if (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1] == 'b') {
            count++
        }
        if (row + 1 < board.size && col + 1 < board.size && board[row + 1][col + 1] == 'b') {
            count++
        }
        if (row - 1 >= 0 && col + 1 < board.size && board[row - 1][col + 1] == 'b') {
            count++
        }
        if (row + 1 < board.size && col - 1 >= 0 && board[row + 1][col - 1] == 'b') {
            count++
        }
        return count
    }
}

fun String.isInt(): Boolean {
    return this.toIntOrNull() != null
}