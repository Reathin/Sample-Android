package com.reathin.game2048

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var gridLayout: GridLayout
    private lateinit var scoreTextView: TextView
    private lateinit var gestureDetector: GestureDetector
    private val board = Array(4) { IntArray(4) }
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        scoreTextView = findViewById(R.id.scoreTextView)
        gestureDetector = GestureDetector(this, this)

        initializeGame()
    }

    private fun initializeGame() {
        // Clear the board
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                board[i][j] = 0
            }
        }

        // Add two initial tiles
        addNewTile()
        addNewTile()

        updateUI()
    }

    private fun addNewTile() {
        val emptyTiles = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (board[i][j] == 0) {
                    emptyTiles.add(Pair(i, j))
                }
            }
        }

        if (emptyTiles.isNotEmpty()) {
            val (i, j) = emptyTiles[Random.nextInt(emptyTiles.size)]
            board[i][j] = if (Random.nextFloat() < 0.9f) 2 else 4
        }
    }

    private fun updateUI() {
        gridLayout.removeAllViews()
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                val tileView = TextView(this)
                tileView.text = if (board[i][j] != 0) board[i][j].toString() else ""
                tileView.setBackgroundResource(getTileBackground(board[i][j]))
                tileView.setTextColor(ContextCompat.getColor(this, getTileTextColor(board[i][j])))
                tileView.textSize = 24f
                tileView.gravity = android.view.Gravity.CENTER

                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = 0
                params.columnSpec = GridLayout.spec(j, 1f)
                params.rowSpec = GridLayout.spec(i, 1f)
                params.setMargins(4, 4, 4, 4)
                tileView.layoutParams = params

                gridLayout.addView(tileView)
            }
        }
        scoreTextView.text = "Score: $score"
    }

    private fun getTileTextColor(value: Int): Int {
        return when (value) {
            2, 4 -> R.color.tile_dark
            else -> R.color.tile_light
        }
    }

    private fun getTileBackground(value: Int): Int {
        return when (value) {
            2 -> R.drawable.tile_2
            4 -> R.drawable.tile_4
            8 -> R.drawable.tile_8
            16 -> R.drawable.tile_16
            32 -> R.drawable.tile_32
            64 -> R.drawable.tile_64
            128 -> R.drawable.tile_128
            256 -> R.drawable.tile_256
            512 -> R.drawable.tile_512
            1024 -> R.drawable.tile_1024
            2048 -> R.drawable.tile_2048
            else -> R.drawable.tile_empty
        }
    }

    private fun checkGameOver() {
        if (isGameOver()) {
            val intent = Intent(this, EndActivity::class.java)
            intent.putExtra("SCORE", score)
            startActivity(intent)
            finish()
        }
    }

    private fun isGameOver(): Boolean {
        // Check if there are any empty cells
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (board[i][j] == 0) {
                    return false
                }
            }
        }

        // Check if there are any adjacent cells with the same value
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (i < 3 && board[i][j] == board[i + 1][j]) {
                    return false
                }
                if (j < 3 && board[i][j] == board[i][j + 1]) {
                    return false
                }
            }
        }

        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean = true

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val diffX = e2.x - (e1?.x ?: 0f)
        val diffY = e2.y - (e1?.y ?: 0f)

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (diffX > 0) {
                moveRight()
            } else {
                moveLeft()
            }
        } else {
            if (diffY > 0) {
                moveDown()
            } else {
                moveUp()
            }
        }

        addNewTile()
        updateUI()
        return true
    }

    private fun moveLeft() {
        var changed = false
        for (i in 0 until 4) {
            val row = board[i].filter { it != 0 }.toMutableList()
            var j = 0
            while (j < row.size - 1) {
                if (row[j] == row[j + 1]) {
                    row[j] *= 2
                    score += row[j]
                    row.removeAt(j + 1)
                    changed = true
                }
                j++
            }
            while (row.size < 4) {
                row.add(0)
            }
            if (board[i] != row.toIntArray()) {
                changed = true
            }
            board[i] = row.toIntArray()
        }
        if (changed) {
            addNewTile()
        }
        checkGameOver()
    }

    private fun moveRight() {
        var changed = false
        for (i in 0 until 4) {
            val row = board[i].filter { it != 0 }.toMutableList()
            var j = row.size - 1
            while (j > 0) {
                if (row[j] == row[j - 1]) {
                    row[j] *= 2
                    score += row[j]
                    row.removeAt(j - 1)
                    changed = true
                }
                j--
            }
            while (row.size < 4) {
                row.add(0, 0)
            }
            if (board[i] != row.toIntArray()) {
                changed = true
            }
            board[i] = row.toIntArray()
        }
        if (changed) {
            addNewTile()
        }
        checkGameOver()
    }

    private fun moveUp() {
        var changed = false
        for (j in 0 until 4) {
            val column = (0 until 4).map { board[it][j] }.filter { it != 0 }.toMutableList()
            var i = 0
            while (i < column.size - 1) {
                if (column[i] == column[i + 1]) {
                    column[i] *= 2
                    score += column[i]
                    column.removeAt(i + 1)
                    changed = true
                }
                i++
            }
            while (column.size < 4) {
                column.add(0)
            }
            for (i in 0 until 4) {
                if (board[i][j] != column[i]) {
                    changed = true
                }
                board[i][j] = column[i]
            }
        }
        if (changed) {
            addNewTile()
        }
        checkGameOver()
    }

    private fun moveDown() {
        var changed = false
        for (j in 0 until 4) {
            val column = (0 until 4).map { board[it][j] }.filter { it != 0 }.toMutableList()
            var i = column.size - 1
            while (i > 0) {
                if (column[i] == column[i - 1]) {
                    column[i] *= 2
                    score += column[i]
                    column.removeAt(i - 1)
                    changed = true
                }
                i--
            }
            while (column.size < 4) {
                column.add(0, 0)
            }
            for (i in 0 until 4) {
                if (board[i][j] != column[i]) {
                    changed = true
                }
                board[i][j] = column[i]
            }
        }
        if (changed) {
            addNewTile()
        }
        checkGameOver()
    }

    // Implement other GestureDetector.OnGestureListener methods
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean = false

    override fun onLongPress(e: MotionEvent) {}
}