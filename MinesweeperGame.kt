enum class GameCommands {
    new, reveal, mark
}

const val BOMB = 'B' // bomb
const val MARK = 'M' // mark
const val BOMB_DIE = 'X' // bomb where player dies
const val BLANK = '_'

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
                    println("You lost, game over, please new game.")
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
                    println("Congrat! You win! Please new game.")
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
    private lateinit var board: Array<CharArray> // internal use, sees can't see
    private lateinit var userboard: Array<CharArray> // what the player sees
    private val mines = HashSet<Pair<Int, Int>>() // actual location of mines
    private var correctMinesCount = 0 // how many player got right
    private var totalMarks = 0 // how many marks user put, right or wrong
    private var numberOfMines = 0 // how many the player set
    private var status = GameStatus.not_started

    fun newBoard(size: Int, numberOfMines: Int): Boolean {
        if (numberOfMines <= 0) {
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
        correctMinesCount = 0
        totalMarks = 0

        board = Array(size) {
            CharArray(size, {
                ' '
            })
        }
        userboard = Array(size) {
            CharArray(size, {
                BLANK
            })
        }
        status = GameStatus.ongoing
        return true
    }

    fun reveal(row: Int, col: Int): Boolean {
        if (status != GameStatus.ongoing) {
            return false
        }
        if (!::board.isInitialized) {
            return false
        }
        if (row >= board.size || col >= board.size || row < 0 || col < 0) {
            return false
        }
        if (userboard[row][col]==MARK) {
            println("Please unmark before revealing")
            return false
        }
        if (mines.size == 0) {
            initMinesWithLocation(row, col)
        }
        revealHelper(row, col)
        return true
    }

    private fun revealHelper(row: Int, col: Int) {
        if (row >= board.size || col >= board.size || row < 0 || col < 0) {
            return
        }
        if (userboard[row][col] >= '0' && userboard[row][col] <= '8') {
            return
        }
        if (board[row][col] == BOMB) {
            status = GameStatus.lose
            userboard[row][col] = BOMB_DIE // dead, exploded
            revealAnswer()
        } else {
            // not bomb
            val numOfMines = board[row][col]
            if (userboard[row][col] == MARK) {
                totalMarks--
            }
            val v = numOfMines.toString().first()
            userboard[row][col] = v
            if (numOfMines == '0') {
                // auto reveal all neighbor
                revealHelper(row + 1, col)
                revealHelper(row - 1, col)
                revealHelper(row, col + 1)
                revealHelper(row, col - 1)
                revealHelper(row + 1, col + 1)
                revealHelper(row - 1, col - 1)
                revealHelper(row + 1, col - 1)
                revealHelper(row - 1, col + 1)
            }
        }
    }

    fun mark(row: Int, col: Int): Boolean {
        if (status != GameStatus.ongoing) {
            return false
        }
        if (!::board.isInitialized) {
            return false
        }
        if (row >= board.size || col >= board.size || row < 0 || col < 0) {
            return false
        }
        if (mines.size == 0) {
            initMinesWithLocation(row, col)
        }

        if (userboard[row][col] == MARK) {
            userboard[row][col] = BLANK
            totalMarks--
            if (mines.contains(Pair(row, col))) {
                correctMinesCount--
            }
            checkWin()
        } else if (userboard[row][col] == BLANK) {
            userboard[row][col] = MARK
            totalMarks++
            if (mines.contains(Pair(row, col))) {
                correctMinesCount++
            }
            checkWin()
        } else {
            return false
        }
        return true
    }

    private fun checkWin() {
        println(correctMinesCount)
        println(mines.size)
        println(totalMarks)
        if (correctMinesCount == mines.size && totalMarks == correctMinesCount) {
            status = GameStatus.win
        }
    }

    private fun initMinesWithLocation(row: Int, col: Int) {
        while (board[row][col] != BLANK) {
            initMines()
        }
        fillAnswerInBoard()
    }

    private fun initMines() {
        var count = 0
        for (i in board.indices) {
            for (j in board.indices) {
                if (count >= numberOfMines) {
                    board[i][j] = BLANK
                } else {
                    board[i][j] = BOMB
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
                if (board[i][j] == BOMB) {
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

    private fun fillAnswerInBoard() {
        for (i in board.indices) {
            for (j in board.indices) {
                if (board[i][j] == BOMB) {
                    continue
                }
                board[i][j] = numberOfMinesAround(i, j).toString().first()
            }
        }
    }

    private fun revealAnswer() {
        for (i in board.indices) {
            for (j in board.indices) {
                if (userboard[i][j] == BOMB_DIE) {
                    continue
                } else if (board[i][j] == BOMB) {
                    userboard[i][j] = BOMB
                }
            }
        }
    }

    private fun numberOfMinesAround(row: Int, col: Int): Int {
        var count = 0
        if (row - 1 >= 0 && board[row - 1][col] == BOMB) {
            count++
        }
        if (row + 1 < board.size && board[row + 1][col] == BOMB) {
            count++
        }
        if (col - 1 >= 0 && board[row][col - 1] == BOMB) {
            count++
        }
        if (col + 1 < board.size && board[row][col + 1] == BOMB) {
            count++
        }
        if (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1] == BOMB) {
            count++
        }
        if (row + 1 < board.size && col + 1 < board.size && board[row + 1][col + 1] == BOMB) {
            count++
        }
        if (row - 1 >= 0 && col + 1 < board.size && board[row - 1][col + 1] == BOMB) {
            count++
        }
        if (row + 1 < board.size && col - 1 >= 0 && board[row + 1][col - 1] == BOMB) {
            count++
        }
        return count
    }
}

fun String.isInt(): Boolean {
    return this.toIntOrNull() != null
}