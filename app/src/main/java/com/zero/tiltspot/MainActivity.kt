package com.zero.tiltspot

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zero.tiltspot.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {


    private val mSensorManager: SensorManager by lazy {
        getSystemService(SENSOR_SERVICE) as SensorManager
    }

    // Accelerometer and magnetometer sensors, as retrieved from the
    // sensor manager.
    private val mSensorAccelerometer: Sensor by lazy {
        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    private val mSensorMagnetometer: Sensor by lazy {
        mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var mAccelerometerData = FloatArray(3)
    private var mMagnetometerData = FloatArray(3)

    private val VALUE_DRIFT = 0.05f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }

    override fun onStart() {
        super.onStart()
        mSensorManager.registerListener(
            this, mSensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        mSensorManager.registerListener(
            this, mSensorMagnetometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onStop() {
        super.onStop()
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        when (p0?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                mAccelerometerData = p0.values.clone()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                mMagnetometerData = p0.values.clone()
            }
        }

        val rotationMatryix = FloatArray(9)
        SensorManager.getRotationMatrix(
            rotationMatryix,
            null,
            mAccelerometerData,
            mMagnetometerData
        )

        val orientationValues = FloatArray(3)
        SensorManager.getOrientation(rotationMatryix, orientationValues)

        val azimuth = orientationValues[0]
        var pitch = orientationValues[1]
        var roll = orientationValues[2]

        with(binding) {
            valueAzimuth.text = resources.getString(R.string.value_format).format(azimuth)
            valuePitch.text = resources.getString(R.string.value_format).format(pitch)
            valueRoll.text = resources.getString(R.string.value_format).format(roll)

            spotRight.alpha = 0f
            spotLeft.alpha = 0f
            spotBottom.alpha = 0f
            spotTop.alpha = 0f

            if (abs(pitch) < VALUE_DRIFT) {
                pitch = 0f;
            }
            if (abs(roll) < VALUE_DRIFT) {
                roll = 0f;
            }
            if (pitch > VALUE_DRIFT) {
                spotBottom.alpha = pitch
            }else{
                spotTop.alpha = abs(pitch)
            }

            if (roll > VALUE_DRIFT) {
                spotLeft.alpha = roll
            }else{
                spotRight.alpha = abs(roll)
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}