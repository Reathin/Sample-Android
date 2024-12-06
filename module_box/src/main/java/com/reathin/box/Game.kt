package com.reathin.box

enum class Cell {
    EMPTY, WALL, BOX, TARGET, PLAYER
}

class Game(val width: Int, val height: Int) {
    private val board = Array(height) { Array(width) { Cell.EMPTY } }
    private var playerX = 0
    private var playerY = 0

    fun setCell(x: Int, y: Int, cell: Cell) {
        board[y][x] = cell
        if (cell == Cell.PLAYER) {
            playerX = x
            playerY = y
        }
    }

    fun reset() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                board[y][x] = Cell.EMPTY
            }
        }
        playerX = 0
        playerY = 0
    }

    fun getCell(x: Int, y: Int): Cell = board[y][x]

    fun move(dx: Int, dy: Int): Boolean {
        val newX = playerX + dx
        val newY = playerY + dy

        if (newX < 0 || newX >= width || newY < 0 || newY >= height) return false

        when (board[newY][newX]) {
            Cell.EMPTY, Cell.TARGET -> {
                board[playerY][playerX] =
                    if (board[playerY][playerX] == Cell.PLAYER) Cell.EMPTY else Cell.TARGET
                board[newY][newX] = Cell.PLAYER
                playerX = newX
                playerY = newY
                return true
            }

            Cell.BOX -> {
                val behindX = newX + dx
                val behindY = newY + dy
                if (behindX < 0 || behindX >= width || behindY < 0 || behindY >= height) return false
                if (board[behindY][behindX] == Cell.EMPTY || board[behindY][behindX] == Cell.TARGET) {
                    board[behindY][behindX] = Cell.BOX
                    board[newY][newX] = Cell.PLAYER
                    board[playerY][playerX] =
                        if (board[playerY][playerX] == Cell.PLAYER) Cell.EMPTY else Cell.TARGET
                    playerX = newX
                    playerY = newY
                    return true
                }
            }

            else -> return false
        }
        return false
    }

    fun isCompleted(): Boolean {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (board[y][x] == Cell.TARGET) return false
            }
        }
        return true
    }
}