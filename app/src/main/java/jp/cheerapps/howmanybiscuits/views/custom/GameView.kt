package jp.cheerapps.howmanybiscuits.views.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import jp.cheerapps.howmanybiscuits.R
import jp.cheerapps.howmanybiscuits.data.Vector
import jp.cheerapps.howmanybiscuits.extensions.saveRestore
import jp.cheerapps.howmanybiscuits.utils.FpsManager
import jp.cheerapps.howmanybiscuits.views.custom.component.Biscuit
import java.util.*
import java.util.concurrent.CyclicBarrier
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

class GameView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback2 {

    private val barrier = CyclicBarrier(2, this::barrierAction)
    private var drawThread: Thread? = null
    private var updateThread: Thread? = null
    private var threadTime = 0L
    private val fpsManager = FpsManager(targetFps = 50.0)
    private val random = Random()
    private val bitmap: Bitmap
    private val biscuits = mutableListOf<Biscuit>()
    private var dataList = listOf<Biscuit.Data>()
    private lateinit var size: Vector

    init {
        holder.addCallback(this)
        bitmap = BitmapFactory.decodeResource(
                resources,
                R.drawable.coin100,
                BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_4444 })
        dataList
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        size = Vector(width.toFloat(), height.toFloat())
        if (biscuits.size == 0) {
            biscuits.add(Biscuit(p = Vector(width / 2f, height / 2f),
                                 v = Vector(0f, 0f),
                                 r = min(400f, min(width / 2f, height / 2f)),
                                 random = random))
            dataList = biscuits.map { it.generateData() }
        }
        drawThread?.start()
        updateThread?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        drawThread = null
        updateThread = null
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawThread = Thread {
            while (drawThread != null) {
                val drawTime = measureTimeMillis { draw(holder) }
                threadTime = max(threadTime, drawTime)
//                Log.d(TAG, "drawTime = $drawTime")
                barrier.await()
            }
        }
        updateThread = Thread {
            while (updateThread != null) {
                val updateTime = measureTimeMillis { update() }
                threadTime = max(threadTime, updateTime)
//                Log.d(TAG, "updateTime = $updateTime")
                barrier.await()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                biscuits.forEach {
                    if (it.inside(Vector(event.x, event.y))) {
                        synchronized(biscuits) {
                            biscuits.remove(it)
                            biscuits.addAll(it.divide())
                            return true
                        }
                    }
                }
            }
        }
        return true
    }

    private fun draw(holder: SurfaceHolder) {
        val canvas = holder.lockCanvas() ?: return
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val paint = Paint()
        val bitmapRect = Rect(0, 0, bitmap.width, bitmap.height)
        dataList.forEach {
            canvas.saveRestore {
                translate(it.center.x, it.center.y )
                rotate(it.angle)
                translate(-it.size / 2f, -it.size / 2f)
                drawBitmap(bitmap, bitmapRect, Rect(0, 0, it.size, it.size), paint)
            }
        }

        paint.textSize = 40f
        paint.color = Color.WHITE
        canvas.drawText("count = ${biscuits.size}", 0f, 40f, paint)
        canvas.drawText("fps = %.2f".format(fpsManager.fps), 0f, 80f, paint)

        holder.unlockCanvasAndPost(canvas)
    }

    private fun update() {
        synchronized(biscuits) {
            biscuits.forEach {
                it.move()
                it.restitutionBiscuit(biscuits)
                it.restitutionWall(size)
            }
        }
    }

    private fun barrierAction() {
        synchronized(biscuits) {
            dataList = biscuits.map { it.generateData() }
        }
        Log.d(TAG, "threadTime = $threadTime")

        fpsManager.setProcessingTime(threadTime)
        try { Thread.sleep(fpsManager.sleepTime) }
        catch (e: InterruptedException) {}
        threadTime = 0L
    }

    companion object {
        private val TAG = GameView::class.java.simpleName
    }
}