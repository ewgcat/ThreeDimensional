package com.lishuaihua.threedimensional

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Scroller
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class SensorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SensorEventListener {
    private val mSensorManager: SensorManager?
    private var mAccelerateValues: FloatArray? = null
    private var mMagneticValues: FloatArray? = null
    private val mScroller: Scroller
    private var mDegreeYMin = -50.0
    private var mDegreeYMax = 50.0
    private var mDegreeXMin = -50.0
    private var mDegreeXMax = 50.0
    private var hasChangeX = false
    private var hasChangeY = false
    private var mDirection = 1
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mAccelerateValues = event.values
        }
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagneticValues = event.values
        }
        val values = FloatArray(3)
        val R = FloatArray(9)
        if (mMagneticValues != null && mAccelerateValues != null) SensorManager.getRotationMatrix(
            R,
            null,
            mAccelerateValues,
            mMagneticValues
        )
        SensorManager.getOrientation(R, values)
        // x轴的偏转角度
        values[1] = Math.toDegrees(values[1].toDouble()).toFloat()
        // y轴的偏转角度
        values[2] = Math.toDegrees(values[2].toDouble()).toFloat()
        val degreeX = values[1].toDouble()
        val degreeY = values[2].toDouble()
        if (degreeY <= 0 && degreeY > mDegreeYMin) {
            hasChangeX = true
            Companion.scrollX = (degreeY / Math.abs(mDegreeYMin) * mXMoveDistance * mDirection).toInt()
        } else if (degreeY > 0 && degreeY < mDegreeYMax) {
            hasChangeX = true
            Companion.scrollX = (degreeY / Math.abs(mDegreeYMax) * mXMoveDistance * mDirection).toInt()
        }
        if (degreeX <= 0 && degreeX > mDegreeXMin) {
            hasChangeY = true
            Companion.scrollY = (degreeX / Math.abs(mDegreeXMin) * mYMoveDistance * mDirection).toInt()
        } else if (degreeX > 0 && degreeX < mDegreeXMax) {
            hasChangeY = true
            Companion.scrollY = (degreeX / Math.abs(mDegreeXMax) * mYMoveDistance * mDirection).toInt()
        }
        smoothScroll(
            if (hasChangeX) Companion.scrollX else mScroller.finalX,
            if (hasChangeY) Companion.scrollY else mScroller.finalY
        )
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    fun smoothScroll(destX: Int, destY: Int) {
        val scrollY = getScrollY()
        val delta = destY - scrollY
        mScroller.startScroll(destX, scrollY, 0, delta, 200)
        invalidate()
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
    }

    fun unregister() {
        mSensorManager!!.unregisterListener(this)
    }

    fun setDegreeYMin(degreeYMin: Double) {
        mDegreeYMin = degreeYMin
    }

    fun setDegreeYMax(degreeYMax: Double) {
        mDegreeYMax = degreeYMax
    }

    fun setDegreeXMin(degreeXMin: Double) {
        mDegreeXMin = degreeXMin
    }

    fun setDegreeXMax(degreeXMax: Double) {
        mDegreeXMax = degreeXMax
    }

    fun setDirection(@ADirection direction: Int) {
        mDirection = direction
    }

    @IntDef(DIRECTION_LEFT, DIRECTION_RIGHT)
    @Retention(RetentionPolicy.SOURCE)
    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class ADirection
    companion object {
        private const val mXMoveDistance = 40.0
        private const val mYMoveDistance = 20.0
        const val DIRECTION_LEFT = 1
        const val DIRECTION_RIGHT = -1
        private var scrollY = 0
        private var scrollX = 0
    }

    init {
        mScroller = Scroller(context)
        mSensorManager = getContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 重力传感器
        if (mSensorManager != null) {
            val accelerateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            // 地磁场传感器
            val magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            mSensorManager.registerListener(this, accelerateSensor, SensorManager.SENSOR_DELAY_GAME)
            mSensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }
}