package com.reathin.block

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gameBoard: GridLayout
    private lateinit var scoreTextView: TextView
    private var score = 0
    private val blocks = Array(8) { IntArray(8) }
    private val blockButtons = Array(8) { arrayOfNulls<Button>(8) }

    private val colors = arrayOf(
        R.color.empty,
        R.color.red,
        R.color.blue,
        R.color.green,
        R.color.yellow,
        R.color.cyan,
        R.color.magenta,
        R.color.orange,
        R.color.purple,
        R.color.pink,
        R.color.lime,
        R.color.teal,
        R.color.brown,
        R.color.navy
    )

    private var isGameOver = false
    private var currentMaxColor = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameBoard = findViewById(R.id.gameBoard)
        scoreTextView = findViewById(R.id.scoreTextView)

        findViewById<Button>(R.id.restartButton).setOnClickListener {
            restartGame()
        }

        initializeGameBoard()
        restartGame()
    }

    private fun initializeGameBoard() {
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                val button = Button(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(j, 1f)
                        rowSpec = GridLayout.spec(i, 1f)
                        setMargins(1, 1, 1, 1)
                    }
                    setOnClickListener {
                        onBlockClicked(i, j)
                    }
                    background = null
                    isAllCaps = false
                }
                blockButtons[i][j] = button
                gameBoard.addView(button)
            }
        }
    }

    private fun restartGame() {
        score = 0
        isGameOver = false
        currentMaxColor = 5
        updateScore()
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                blocks[i][j] = Random.nextInt(1, currentMaxColor)
                updateBlockColor(i, j)
            }
        }
        if (!hasValidMoves()) {
            restartGame()
        }
    }

    private fun updateScore() {
        scoreTextView.text = "分数: $score"
    }

    private fun onBlockClicked(row: Int, col: Int) {
        if (isGameOver) return
        val color = blocks[row][col]
        if (color != 0) {
            val connectedBlocks = findConnectedBlocks(row, col, color)
            if (connectedBlocks.size >= 2) {
                removeBlocks(connectedBlocks)
                dropBlocks()
                fillBlocks()
                updateScore()
                if (!hasValidMoves()) {
                    gameOver()
                }
            }
        }
    }

    private fun findConnectedBlocks(row: Int, col: Int, color: Int): List<Pair<Int, Int>> {
        val visited = Array(8) { BooleanArray(8) }
        val connectedBlocks = mutableListOf<Pair<Int, Int>>()

        fun dfs(r: Int, c: Int) {
            if (r < 0 || r >= 8 || c < 0 || c >= 8 || visited[r][c] || blocks[r][c] != color) return
            visited[r][c] = true
            connectedBlocks.add(Pair(r, c))
            dfs(r + 1, c)
            dfs(r - 1, c)
            dfs(r, c + 1)
            dfs(r, c - 1)
        }

        dfs(row, col)
        return connectedBlocks
    }

    private fun removeBlocks(connectedBlocks: List<Pair<Int, Int>>) {
        for ((row, col) in connectedBlocks) {
            blocks[row][col] = 0
            updateBlockColor(row, col)
            score++
        }
        if (score > 50 && currentMaxColor < 5) currentMaxColor = 5
        if (score > 100 && currentMaxColor < 6) currentMaxColor = 6
        if (score > 200 && currentMaxColor < 7) currentMaxColor = 7
        if (score > 300 && currentMaxColor < 8) currentMaxColor = 8
        if (score > 400 && currentMaxColor < 9) currentMaxColor = 9
        if (score > 500 && currentMaxColor < 10) currentMaxColor = 10
        if (score > 600 && currentMaxColor < 11) currentMaxColor = 11
        if (score > 700 && currentMaxColor < 12) currentMaxColor = 12
        if (score > 800 && currentMaxColor < 13) currentMaxColor = 13
        if (score > 900) currentMaxColor = 13
    }

    private fun dropBlocks() {
        for (col in 0 until 8) {
            var emptyRow = 7
            for (row in 7 downTo 0) {
                if (blocks[row][col] != 0) {
                    blocks[emptyRow][col] = blocks[row][col]
                    updateBlockColor(emptyRow, col)
                    if (emptyRow != row) {
                        blocks[row][col] = 0
                        updateBlockColor(row, col)
                    }
                    emptyRow--
                }
            }
        }
    }

    private fun hasValidMoves(): Boolean {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val color = blocks[row][col]
                if (color != 0) {
                    if (findConnectedBlocks(row, col, color).size >= 2) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun fillBlocks() {
        for (col in 0 until 8) {
            for (row in 0 until 8) {
                if (blocks[row][col] == 0) {
                    blocks[row][col] = Random.nextInt(1, currentMaxColor)
                    updateBlockColor(row, col)
                }
            }
        }
    }

    private fun gameOver() {
        isGameOver = true
        scoreTextView.text = "Game Over! 最终得分: $score"
    }

    private fun updateBlockColor(row: Int, col: Int) {
        blockButtons[row][col]?.setBackgroundColor(
            ContextCompat.getColor(
                this,
                colors[blocks[row][col]]
            )
        )
    }
}