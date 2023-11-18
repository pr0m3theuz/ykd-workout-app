package com.example.myapplication.components

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock

abstract class CountUpTimer(private val interval: Long) {
  //  TODO Figure out to old elapsed time to new elapsed time
  private var base: Long = 0
  private var oldElapsedTime: Long = 0

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

  companion object {
    private const val MSG = 1
  }
}

