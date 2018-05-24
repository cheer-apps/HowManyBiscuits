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
import jp.cheerapps.howmanybiscuits.utils.FpsManager
import jp.cheerapps.howmanybiscuits.views.custom.component.Biscuit
import java.util.*
import kotlin.math.min
import kotlin.system.measureTimeMillis

class GameView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback2 {

    private var thread: Thread? = null
    private val fpsManager = FpsManager(targetFps = 60.0)
    private val random = Random()
    private var biscuits = mutableListOf<Biscuit>()
    private val bitmap: Bitmap
    private lateinit var size: Vector

    init {
        holder.addCallback(this)
        bitmap = BitmapFactory.decodeResource(
                resources,
                R.drawable.coin100_min,
                BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_4444 })
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        size = Vector(width.toFloat(), height.toFloat())
        if (biscuits.size == 0) {
            biscuits.add(Biscuit(p = Vector(width / 2f, height / 2f),
                                 v = Vector(0f, 0f),
                                 r = min(400f, min(width / 2f, height / 2f)),
                                 random = random))
        }
        thread?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        thread = null
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = Thread {
            while (thread != null) {
                val drawTime = measureTimeMillis { draw(holder) }
                val updateTime = measureTimeMillis { update() }
                Log.d(TAG, "drawTime = $drawTime, updateTime = $updateTime")
                fpsManager.setProcessingTime(drawTime + updateTime)
                try {
                    Thread.sleep(fpsManager.sleepTime)
                }
                catch (e: InterruptedException) {
                    break
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                biscuits.reversed().forEach {
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
        synchronized(biscuits) {
            biscuits.forEach { canvas.drawBitmap(bitmap, Rect(0, 0, bitmap.width, bitmap.height), it.calcDrawRect(), paint) }
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
            }
            val biscuitTime = measureTimeMillis {
                biscuits.forEach {
                    it.restitutionBiscuit(biscuits)
                }
            }
            val wallTime = measureTimeMillis {
                biscuits.forEach {
                    it.restitutionWall(size)
                }
            }
//            Log.d(TAG, "biscuitTime = $biscuitTime, wallTime = $wallTime")
        }
    }

    companion object {
        private val TAG = GameView::class.java.simpleName
    }
}