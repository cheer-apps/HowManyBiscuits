package jp.cheerapps.howmanybiscuits.utils

import kotlin.math.max
import kotlin.math.min

class FpsManager(private val targetFps: Double) {
    private val maxSleepTime = (1000 / targetFps).toLong()
    private var frameCount = 0
    private var times = LongArray(MAX_FRAME_COUNT)
    private var updateFps = false
    private var _fps = targetFps

    val fps: Double
        get() {
            if (updateFps) {
                _fps = min(targetFps, 1000 / times.average())
                updateFps = false
            }
            return _fps
        }

    var sleepTime: Long = maxSleepTime
        private set

    fun setProcessingTime(time: Long) {
        times[frameCount++] = time
        if (frameCount == MAX_FRAME_COUNT) {
            updateFps = true
            frameCount = 0
        }
        sleepTime = max(0, maxSleepTime - time)
    }

    companion object {
        private const val MAX_FRAME_COUNT = 10
    }
}