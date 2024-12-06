package com.reathin.tetris

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gameBoard: GridLayout
    private lateinit var scoreTextView: TextView
    private lateinit var restartButton: Button
    private lateinit var startButton: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var rotateButton: Button
    private lateinit var dropButton: Button
    private lateinit var gameOverLayout: FrameLayout
    private lateinit var startGameLayout: FrameLayout
    private lateinit var nextPieceBoard: GridLayout
    private var nextPiece: Piece? = null

    private val boardWidth = 14
    private val boardHeight = 20
    private val board = Array(boardHeight) { IntArray(boardWidth) }
    private var currentPiece: Piece? = null
    private var score = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isGameRunning = false
    private var level = 1
    private var linesCleared = 0
    private var combo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
    }

    private fun initializeViews() {
        gameBoard = findViewById(R.id.gameBoard)
        scoreTextView = findViewById(R.id.scoreTextView)
        startButton = findViewById(R.id.startButton)
        restartButton = findViewById(R.id.restartButton)
        leftButton = findViewById(R.id.leftButton)
        rightButton = findViewById(R.id.rightButton)
        rotateButton = findViewById(R.id.rotateButton)
        dropButton = findViewById(R.id.dropButton)
        gameOverLayout = findViewById(R.id.gameOverLayout)
        startGameLayout = findViewById(R.id.startGameLayout)
        nextPieceBoard = findViewById(R.id.nextPieceBoard)

        startButton.setOnClickListener { startGame() }
        leftButton.setOnClickListener { movePiece(-1, 0) }
        rightButton.setOnClickListener { movePiece(1, 0) }
        rotateButton.setOnClickListener { rotatePiece() }
        dropButton.setOnClickListener { dropPiece() }
        restartButton.setOnClickListener { startGame() }

        for (i in 0 until boardHeight) {
            for (j in 0 until boardWidth) {
                val cell = View(this)
                cell.setBackgroundColor(Color.BLACK)
                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = 0
                params.columnSpec = GridLayout.spec(j, 1f)
                params.rowSpec = GridLayout.spec(i, 1f)
                params.setMargins(1, 1, 1, 1)
                cell.layoutParams = params
                gameBoard.addView(cell)
            }
        }

        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val cell = View(this)
                cell.setBackgroundColor(Color.BLACK)
                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = 0
                params.columnSpec = GridLayout.spec(j, 1f)
                params.rowSpec = GridLayout.spec(i, 1f)
                params.setMargins(1, 1, 1, 1)
                cell.layoutParams = params
                nextPieceBoard.addView(cell)
            }
        }
    }

    /**
     * 开始游戏
     */
    private fun startGame() {
        isGameRunning = true
        score = 0
        level = 1
        linesCleared = 0
        combo = 0
        clearBoard()
        updateScore()
        startGameLayout.visibility = View.GONE
        gameOverLayout.visibility = View.GONE
        restartButton.visibility = View.GONE
        nextPiece = Piece.random()
        spawnNewPiece()
        handler.post(gameLoop)
    }

    private val gameLoop = object : Runnable {
        override fun run() {
            if (isGameRunning) {
                if (!movePiece(0, 1)) {
                    placePiece()
                    clearLines()
                    if (!spawnNewPiece()) {
                        gameOver()
                        return
                    }
                }
                updateBoardUI()
                handler.postDelayed(this, 800)
            }
        }
    }

    private fun clearBoard() {
        for (i in 0 until boardHeight) {
            for (j in 0 until boardWidth) {
                board[i][j] = 0
            }
        }
        updateBoardUI()
    }

    private fun updateBoardUI() {
        for (i in 0 until boardHeight) {
            for (j in 0 until boardWidth) {
                val cell = gameBoard.getChildAt(i * boardWidth + j)
                cell.setBackgroundColor(if (board[i][j] == 0) Color.BLACK else Color.MAGENTA)
            }
        }
        currentPiece?.let { drawPiece(it, Color.MAGENTA) }
    }

    private fun spawnNewPiece(): Boolean {
        currentPiece = nextPiece ?: Piece.random()
        nextPiece = Piece.random()
        updateNextPieceUI()
        if (canPlacePiece(currentPiece!!, 0, 0)) {
            updateBoardUI()
            return true
        }
        return false
    }

    private fun updateNextPieceUI() {
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val cell = nextPieceBoard.getChildAt(i * 4 + j)
                cell.setBackgroundColor(Color.BLACK)
            }
        }

        nextPiece?.let { piece ->
            for (i in piece.shape.indices) {
                for (j in piece.shape[i].indices) {
                    if (piece.shape[i][j] == 1) {
                        val cell = nextPieceBoard.getChildAt(i * 4 + j)
                        cell.setBackgroundColor(Color.CYAN)
                    }
                }
            }
        }
    }

    private fun movePiece(dx: Int, dy: Int): Boolean {
        val piece = currentPiece ?: return false
        if (canPlacePiece(piece, dx, dy)) {
            drawPiece(piece, Color.BLACK)
            piece.x += dx
            piece.y += dy
            drawPiece(piece, Color.CYAN)
            return true
        }
        return false
    }

    private fun drawPiece(piece: Piece, color: Int) {
        for (i in piece.shape.indices) {
            for (j in piece.shape[i].indices) {
                if (piece.shape[i][j] == 1) {
                    val x = piece.x + j
                    val y = piece.y + i
                    if (y >= 0 && y < boardHeight && x >= 0 && x < boardWidth) {
                        val cell = gameBoard.getChildAt(y * boardWidth + x)
                        cell.setBackgroundColor(color)
                    }
                }
            }
        }
    }

    private fun rotatePiece() {
        val piece = currentPiece ?: return
        val rotatedPiece = piece.rotate()
        if (canPlacePiece(rotatedPiece, 0, 0)) {
            currentPiece = rotatedPiece
            updateBoardUI()
        }
    }

    private fun dropPiece() {
        var dropDistance = 0
        while (movePiece(0, 1)) {
            dropDistance++
        }
        score += dropDistance * 2
        updateScore()
    }

    private fun canPlacePiece(piece: Piece, dx: Int, dy: Int): Boolean {
        for (i in piece.shape.indices) {
            for (j in piece.shape[i].indices) {
                if (piece.shape[i][j] == 1) {
                    val newX = piece.x + j + dx
                    val newY = piece.y + i + dy
                    if (newX < 0 || newX >= boardWidth || newY >= boardHeight || (newY >= 0 && board[newY][newX] == 1)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun placePiece() {
        val piece = currentPiece ?: return
        for (i in piece.shape.indices) {
            for (j in piece.shape[i].indices) {
                if (piece.shape[i][j] == 1) {
                    val y = piece.y + i
                    val x = piece.x + j
                    if (y in 0..<boardHeight && x >= 0 && x < boardWidth) {
                        board[y][x] = 1
                    }
                }
            }
        }
        updateBoardUI()
    }

    private fun clearLines() {
        var linesCleared = 0
        for (i in boardHeight - 1 downTo 0) {
            if (board[i].all { it == 1 }) {
                for (j in i downTo 1) {
                    board[j] = board[j - 1].clone()
                }
                board[0] = IntArray(boardWidth)
                linesCleared++
            }
        }
        if (linesCleared > 0) {
            val baseScore = when (linesCleared) {
                1 -> 100
                2 -> 300
                3 -> 500
                4 -> 800
                else -> 0
            }
            combo++
            val comboBonus = combo * 50
            score += (baseScore + comboBonus) * level
            this.linesCleared += linesCleared
            updateLevel()
            updateScore()
        } else {
            combo = 0
        }
    }

    private fun updateScore() {
        scoreTextView.text = "分数: $score\n等级: $level\n已消除行数: $linesCleared"
    }

    private fun updateLevel() {
        level = (linesCleared / 10) + 1
        // 调整游戏速度
        handler.removeCallbacks(gameLoop)
        handler.postDelayed(gameLoop, (800 / sqrt(level.toDouble())).toLong())
    }

    private fun gameOver() {
        isGameRunning = false
        gameOverLayout.visibility = View.VISIBLE
        restartButton.visibility = View.VISIBLE
    }

    data class Piece(var x: Int, var y: Int, val shape: Array<IntArray>) {
        fun rotate(): Piece {
            val rotated = Array(shape[0].size) { IntArray(shape.size) }
            for (i in shape.indices) {
                for (j in shape[i].indices) {
                    rotated[j][shape.size - 1 - i] = shape[i][j]
                }
            }
            return Piece(x, y, rotated)
        }

        companion object {
            private val shapes = listOf(
                arrayOf(intArrayOf(1, 1, 1, 1)),  // I
                arrayOf(intArrayOf(1, 1), intArrayOf(1, 1)),  // O
                arrayOf(intArrayOf(1, 1, 1), intArrayOf(0, 1, 0)),  // T
                arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1)),  // Z
                arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0)),  // S
                arrayOf(intArrayOf(1, 1, 1), intArrayOf(1, 0, 0)),  // L
                arrayOf(intArrayOf(1, 1, 1), intArrayOf(0, 0, 1))   // J
            )

            fun random(): Piece {
                val shape = shapes[Random.nextInt(shapes.size)]
                return Piece(10 / 2 - shape[0].size / 2, 0, shape)
            }
        }
    }
}