package com.toxicflame427.simpletimer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.toxicflame427.simpletimer.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var timerIsRunning : Boolean = false
    private var intervalTimer : Timer = Timer()
    private var alarmSound : MediaPlayer? = null

    var initialSecondsValue : Int = 0
    var initialMinutesValue : Int = 0
    var initialHoursValue : Int = 0

    var currentSecondsValue : Int = 0
    var currentMinutesValue : Int = 0
    var currentHoursValue : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        alarmSound = MediaPlayer.create(this, R.raw.alarm)

        applyEvents()
        changeTimerValues()
        setViewToTimerData()
    }

    private fun applyEvents(){
        binding.secondsCheck.setOnClickListener {
            if(binding.secondsCheck.isChecked){
                binding.secondTimer.visibility = View.VISIBLE
            } else {
                binding.secondTimer.visibility = View.GONE
            }
        }

        binding.minutesCheck.setOnClickListener {
            if(binding.minutesCheck.isChecked){
                binding.minuteTimer.visibility = View.VISIBLE
            } else {
                binding.minuteTimer.visibility = View.GONE
                binding.minutesInput.setText("0")
            }
        }

        binding.hoursCheck.setOnClickListener {
            if(binding.hoursCheck.isChecked){
                binding.hourTimer.visibility = View.VISIBLE
            } else {
                binding.hourTimer.visibility = View.GONE
                binding.hoursInput.setText("0")
            }

            //Minutes must be in the presence of hours
            if (binding.hoursCheck.isChecked) {
                binding.minutesCheck.isChecked = true
                binding.minuteTimer.visibility = View.VISIBLE
            }
        }

        binding.secondsInput.addTextChangedListener {
            changeTimerValues()
            setViewToTimerData()
        }

        binding.minutesInput.addTextChangedListener {
            changeTimerValues()
            setViewToTimerData()
        }

        binding.hoursInput.addTextChangedListener {
            changeTimerValues()
            setViewToTimerData()
        }

        binding.startButton.setOnClickListener {
            validateTimerInput()
        }

        binding.stopButton.setOnClickListener {
            pauseTimer()
            timerSnooze()
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun validateTimerInput(){
        when {
            binding.secondsInput.text.toString().toInt() > 59 -> {
                Toast.makeText(this, "Cannot have a second value above 59", Toast.LENGTH_LONG).show()
            }
            binding.minutesInput.text.toString().toInt() > 59 -> {
                Toast.makeText(this, "Cannot have a minute value above 59", Toast.LENGTH_LONG).show()
            }
            binding.secondsInput.text.toString() == "" || binding.minutesInput.text.toString() == "" || binding.hoursInput.text.toString() == "" -> {
                Toast.makeText(this, "Fill in all time values. If a value is not wanted, set it to 0", Toast.LENGTH_LONG).show()
            }
            else -> {
                startTimer()
            }
        }
    }

    private fun changeTimerValues(){
        if (binding.hoursInput.text.toString() == ""){
            initialHoursValue = 0
            currentHoursValue = 0
        } else {
            initialHoursValue = binding.hoursInput.text.toString().toInt()
            currentHoursValue = binding.hoursInput.text.toString().toInt()
        }

        if (binding.minutesInput.text.toString() == ""){
            initialMinutesValue = 0
            currentMinutesValue = 0
        } else {
            initialMinutesValue = binding.minutesInput.text.toString().toInt()
            currentMinutesValue = binding.minutesInput.text.toString().toInt()
        }

        if (binding.secondsInput.text.toString() == ""){
            initialSecondsValue = 0
            currentSecondsValue = 0
        } else {
            initialSecondsValue = binding.secondsInput.text.toString().toInt()
            currentSecondsValue = binding.secondsInput.text.toString().toInt()
        }

        Log.i("Hours, Minutes, Seconds", "$currentHoursValue $currentMinutesValue $currentSecondsValue")
    }

    private fun setViewToTimerData(){
        binding.hourTimer.text = currentHoursValue.toString()

        if (currentMinutesValue < 10) {
            binding.minuteTimer.text = "0${currentMinutesValue}"
        } else {
            binding.minuteTimer.text = currentMinutesValue.toString()
        }

        if (currentSecondsValue < 10) {
            binding.secondTimer.text = "0${currentSecondsValue}"
        } else {
            binding.secondTimer.text = currentSecondsValue.toString()
        }
    }

    private fun startTimer(){
        binding.allTimerSettings.visibility = View.GONE
        binding.resetButton.visibility = View.GONE
        binding.startButton.visibility = View.GONE

        if (timerIsRunning){
            Toast.makeText(this, "Timer is already running!", Toast.LENGTH_LONG).show()
        } else {
            timerIsRunning = true
            resumeTimer()
            intervalTimer.scheduleAtFixedRate(object : TimerTask(){
                override fun run() {
                    runOnUiThread {
                        currentSecondsValue--
                        if (currentSecondsValue == -1) {
                            currentSecondsValue = 59
                            currentMinutesValue--
                        }
                        if (currentMinutesValue == -1) {
                            currentMinutesValue = 59
                            currentHoursValue--
                        }
                        setViewToTimerData()

                        if(currentMinutesValue == 0 && currentSecondsValue == 0 && currentHoursValue == 0){
                            timerEnd()
                        }
                    }
                }
            },1000, 1000)
        }
    }

    private fun resumeTimer(){
        intervalTimer = Timer()
    }

    private fun pauseTimer(){
        intervalTimer.cancel()
        timerIsRunning = false
        binding.allTimerSettings.visibility = View.VISIBLE
        binding.timerLayout.visibility = View.VISIBLE
        binding.resetButton.visibility = View.VISIBLE
        binding.startButton.visibility = View.VISIBLE
    }

    private fun timerEnd(){
        intervalTimer.cancel()
        timerIsRunning = false

        intervalTimer = Timer()
        var intervalClock = 0

        intervalTimer.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                runOnUiThread {
                    if (intervalClock == 0) {
                        alarmSound!!.start()
                        intervalClock = 1
                        binding.timerLayout.visibility = View.INVISIBLE
                    } else {
                        //Play alarm sound until snoozed
                        intervalClock = 0
                        binding.timerLayout.visibility = View.VISIBLE
                    }
                    Log.i("Hi", intervalClock.toString())
                }
            }
        }, 1000, 750)
    }

    private fun timerSnooze(){
        binding.timerLayout.visibility = View.VISIBLE
        binding.allTimerSettings.visibility = View.VISIBLE
        binding.resetButton.visibility = View.VISIBLE
        binding.startButton.visibility = View.VISIBLE

        alarmSound!!.stop()
        alarmSound!!.reset()

        alarmSound = MediaPlayer.create(applicationContext, R.raw.alarm)

        intervalTimer.cancel()
    }

    private fun resetTimer(){
        currentHoursValue = initialHoursValue
        currentMinutesValue = initialMinutesValue
        currentSecondsValue = initialSecondsValue

        setViewToTimerData()
    }
}