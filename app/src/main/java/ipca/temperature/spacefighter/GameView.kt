package ipca.temperature.spacefighter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi

class GameView: SurfaceView, Runnable{

    // Global Variables
    var playing = false
    var gameThread: Thread? = null
    lateinit var player: Player
    lateinit var boom: Boom
    lateinit var surfaceHolder: SurfaceHolder
    lateinit var canvas: Canvas
    lateinit var paint: Paint

    var stars = arrayListOf<Star>()
    var enemies = arrayListOf<Enemy>()

    private fun init(context: Context, width: Int, Height: Int) {
        player = Player(context, width, Height)
        boom = Boom(context, width, Height)

        for (i in 1..100) {
            stars.add(Star(context, width, Height))
        }

        for (i in 1..3) {
            enemies.add(Enemy(context, width, Height))
        }

        surfaceHolder = holder
        paint = Paint()
    }

    constructor(context: Context?, width: Int, height: Int) : super(context) {
        init(context!!, width, height)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context!!, 0, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context!!, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context!!, 0, 0)
    }

    override fun run() {
        while (playing) {
            update()
            draw()
            control()
        }
    }

    fun update() {

        boom.x = -300
        boom.y = -300

        player.update()

        for (s in stars) {
            s.update(player.speed)
        }

        for (e in enemies) {
            e.update()
            if (Rect.intersects(player.detectCollision, e.detectCollision)) {
                boom.x = e.x
                boom.y = e.y
                e.x = -300
            }
        }

    }

    fun draw() {
        if (surfaceHolder.surface.isValid) {
            canvas = surfaceHolder.lockCanvas()
            canvas.drawColor(Color.BLACK)

            paint.color = Color.WHITE

            for (s in stars) {
                paint.strokeWidth = s.getStarWidth()
                canvas.drawPoint(s.x.toFloat(), s.y.toFloat(), paint)
            }

            for (e in enemies) {
                canvas.drawBitmap(e.bitmap, e.x.toFloat(), e.y.toFloat(), paint)
            }

            canvas.drawBitmap(boom.bitmap, boom.x.toFloat(), boom.y.toFloat(), paint)

            canvas.drawBitmap(player.bitmap, player.x.toFloat(), player.y.toFloat(), paint)

            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    fun control() {
        Thread.sleep(17)
    }

    fun pause() {
        playing = false
        gameThread?.join()
    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {

            MotionEvent.ACTION_UP -> {
                player.boosting = false
            }

            MotionEvent.ACTION_DOWN -> {
                player.boosting = true
            }

        }

        return true
    }

}