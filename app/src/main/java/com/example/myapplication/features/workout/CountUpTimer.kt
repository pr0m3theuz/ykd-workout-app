package com.example.myapplication.features.workout

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock

abstract class CountUpTimer(private val interval: Long) {
  //  TODO Figure out to old elapsed time to new elapsed time
  private var base: Long = 0
  private var oldElapsedTime: Long = 0
  private val MSG = 1

  fun start(elapsedTime: Long) {
    base = SystemClock.elapsedRealtime()
    oldElapsedTime = elapsedTime
    handler.sendMessage(handler.obtainMessage(MSG))
  }

  fun stop() {
      handler.removeMessages(MSG)
  }

  fun reset() {
    synchronized(this) { base = SystemClock.elapsedRealtime() }
  }

  abstract fun onTick(elapsedTime: Long)

  private val handler: Handler =
    object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        synchronized(this@CountUpTimer) {
          val elapsedTime = SystemClock.elapsedRealtime() - base + oldElapsedTime
          onTick(elapsedTime)
          sendMessageDelayed(obtainMessage(MSG), interval)
        }
      }
    }
}

interface WorkoutTimer {
  val base: Long
  val oldElapsedTime: Long
  val handler: Handler
  val MSG: Int
  fun start(elapsedTime: Long)
  fun stop()
  fun reset()
  fun onTick(elapsedTime: Long)
}

class WorkoutTimerImpl(private val interval: Long) : WorkoutTimer {
  //  TODO Figure out to old elapsed time to new elapsed time
  override var base: Long = 0
  override var oldElapsedTime: Long = 0
  override val MSG = 1

  override fun start(elapsedTime: Long) {
    base = SystemClock.elapsedRealtime()
    oldElapsedTime = elapsedTime
    handler.sendMessage(handler.obtainMessage(MSG))
  }

  override fun stop() {
    handler.removeMessages(MSG)
  }

  override fun reset() {
    synchronized(this) { base = SystemClock.elapsedRealtime() }
  }

  override fun onTick(elapsedTime: Long) {
  }

  override val handler: Handler =
    object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        synchronized(this@WorkoutTimerImpl) {
          val elapsedTime = SystemClock.elapsedRealtime() - base + oldElapsedTime
          onTick(elapsedTime)
          sendMessageDelayed(obtainMessage(MSG), interval)
        }
      }
    }
}
