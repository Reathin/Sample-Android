package com.reathin.snake

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var gameBoard: GridLayout
    private lateinit var scoreTextView: TextView
    private val boardSize = 20
    private val snake = mutableListOf<Pair<Int, Int>>()
    private var direction = Pair(0, 1) // 初始方向向右
    private var food = Pair(0, 0)
    private var score = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateDelay = 200L // 游戏更新间隔，毫秒
    private var isGameRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameBoard = findViewById(R.id.gameBoard)
        scoreTextView = findViewById(R.id.scoreTextView)

        findViewById<Button>(R.id.upButton).setOnClickListener {
            if (isGameRunning) {
                direction = Pair(-1, 0)
            } else {
                restartGame()
            }
        }
        findViewById<Button>(R.id.downButton).setOnClickListener {
            if (isGameRunning) {
                direction = Pair(1, 0)
            } else {
                restartGame()
            }
        }
        findViewById<Button>(R.id.leftButton).setOnClickListener {
            if (isGameRunning) {
                direction = Pair(0, -1)
            } else {
                restartGame()
            }
        }
        findViewById<Button>(R.id.rightButton).setOnClickListener {
            if (isGameRunning) {
                direction = Pair(0, 1)
            } else {
                restartGame()
            }
        }

        initializeGame()
        restartGame()
    }

    private fun initializeGame() {
        // 初始化蛇
        snake.add(Pair(boardSize / 2, boardSize / 2))
        // 生成食物
        generateFood()
        // 初始化游戏板
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                val cell = TextView(this)
                cell.width = 50
                cell.height = 50
                cell.setBackgroundColor(Color.WHITE)
                gameBoard.addView(cell)
            }
        }
        updateBoard()
    }

    private fun startGameLoop() {
        handler.post(object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    moveSnake()
                    if (isGameRunning) {  // 再次检查，因为 moveSnake 可能会结束游戏
                        checkCollision()
                        updateBoard()
                        handler.postDelayed(this, updateDelay)
                    }
                }
            }
        })
    }

    private fun moveSnake() {
        val head = snake.first()
        val newHead = Pair(
            head.first + direction.first,
            head.second + direction.second
        )

        // 检查是否撞到边界
        if (newHead.first < 0 || newHead.first >= boardSize ||
            newHead.second < 0 || newHead.second >= boardSize
        ) {
            endGame()
            return
        }

        snake.add(0, newHead)

        if (newHead == food) {
            score++
            scoreTextView.text = "分数: $score"
            generateFood()
        } else {
            snake.removeAt(snake.size - 1)
        }
    }

    private fun checkCollision() {
        val head = snake.first()
        if (snake.subList(1, snake.size).contains(head)) {
            // 游戏结束
            endGame()
        }
    }

    private fun endGame() {
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)
        scoreTextView.text = "游戏结束！最终分数: $score\n点击任意方向键重新开始"
    }

    private fun restartGame() {
        snake.clear()
        snake.add(Pair(boardSize / 2, boardSize / 2))
        direction = Pair(0, 1)
        score = 0
        generateFood()
        isGameRunning = true
        startGameLoop()
        updateBoard()
        scoreTextView.text = "分数: 0"
    }

    private fun generateFood() {
        do {
            food = Pair(Random.nextInt(boardSize), Random.nextInt(boardSize))
        } while (snake.contains(food))
    }

    private fun updateBoard() {
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                val cell = gameBoard.getChildAt(i * boardSize + j) as TextView
                when {
                    Pair(i, j) == snake.first() -> cell.setBackgroundColor(Color.RED)
                    snake.contains(Pair(i, j)) -> cell.setBackgroundColor(Color.GREEN)
                    Pair(i, j) == food -> cell.setBackgroundColor(Color.BLUE)
                    else -> cell.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }
}