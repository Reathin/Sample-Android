package com.reathin.box

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var game: Game
    private lateinit var gestureDetector: GestureDetectorCompat
    private var currentLevel = 0
    private val levels = listOf(
        // Level 1
        { game: Game ->
            for (i in 0 until 10) {
                game.setCell(i, 0, Cell.WALL)
                game.setCell(i, 9, Cell.WALL)
                game.setCell(0, i, Cell.WALL)
                game.setCell(9, i, Cell.WALL)
            }
            game.setCell(1, 1, Cell.PLAYER)
            game.setCell(3, 3, Cell.BOX)
            game.setCell(5, 5, Cell.BOX)
            game.setCell(7, 7, Cell.TARGET)
            game.setCell(7, 3, Cell.TARGET)
        },
        // Level 2
        { game: Game ->
            for (i in 0 until 10) {
                game.setCell(i, 0, Cell.WALL)
                game.setCell(i, 9, Cell.WALL)
                game.setCell(0, i, Cell.WALL)
                game.setCell(9, i, Cell.WALL)
            }
            game.setCell(1, 1, Cell.PLAYER)
            game.setCell(2, 2, Cell.BOX)
            game.setCell(3, 3, Cell.BOX)
            game.setCell(4, 4, Cell.BOX)
            game.setCell(7, 7, Cell.TARGET)
            game.setCell(7, 6, Cell.TARGET)
            game.setCell(7, 5, Cell.TARGET)
            game.setCell(5, 5, Cell.WALL)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        game = Game(10, 10)
        initializeGame()

        gameView = GameView(this)
        gameView.setGame(game)

        setContentView(gameView)

        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null || e2 == null) return false
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) movePlayer(1, 0) else movePlayer(-1, 0)
                } else {
                    if (dy > 0) movePlayer(0, 1) else movePlayer(0, -1)
                }
                return true
            }
        })
    }

    private fun initializeGame() {
        game.reset()
        levels[currentLevel](game)
    }

    private fun movePlayer(dx: Int, dy: Int) {
        if (game.move(dx, dy)) {
            gameView.invalidate()
            if (game.isCompleted()) {
                currentLevel++
                if (currentLevel < levels.size) {
                    Toast.makeText(this, "Level Complete! Next level starting...", Toast.LENGTH_SHORT).show()
                    initializeGame()
                    gameView.invalidate()
                } else {
                    Toast.makeText(this, "Congratulations! You completed all levels!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }
}