package com.reathin.box

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class GameView(context: Context) : View(context) {
    private val cellSize = 50f
    private val paint = Paint()

    private lateinit var game: Game

    fun setGame(game: Game) {
        this.game = game
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        for (y in 0 until game.height) {
            for (x in 0 until game.width) {
                when (game.getCell(x, y)) {
                    Cell.WALL -> paint.color = Color.GRAY
                    Cell.BOX -> paint.color = Color.MAGENTA
                    Cell.TARGET -> paint.color = Color.RED
                    Cell.PLAYER -> paint.color = Color.BLUE
                    else -> paint.color = Color.WHITE
                }
                canvas.drawRect(x * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize, paint)
            }
        }
    }
}