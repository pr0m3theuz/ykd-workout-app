package com.example.myapplication.components.setRestTimer

import android.text.format.DateUtils
import java.lang.Long.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

// TODO Create a Notification, NotificationCompat.Builder

class Timer(
    /** The current state of the timer.  */
    val state: TimerState,
    /** The original length of the timer in milliseconds when it was created.  */
    val length: Long,
    /** The length of the timer in milliseconds including additional time added by the user.  */
    val totalLength: Long,
    /** The time the set was completed or was started in milliseconds by the user.  */
    val startTime: Long,
    /** The time since epoch at which the timer was last started.  */
    val wallClockTime: Long,
    /** The time at which the timer is scheduled to expire; negative if it is already expired.  */
    val lastRemainingTime: Long
) {
    val isReset: Boolean
        get() = state == TimerState.RESET

    val isRunning: Boolean
        get() = state == TimerState.RUNNING

    val isExpired: Boolean
        get() = state == TimerState.EXPIRED

    /**
     * @return the total amount of time remaining up to this moment; expired and missed timers will
     * return a negative amount
     */
    val remainingTime: Long
        get() {
            // In practice, "now" can be any value due to device reboots. When the real-time clock
            // is reset, there is no more guarantee that "now" falls after the last start time. To
            // ensure the timer is monotonically decreasing, normalize negative time segments to 0,
            val timeSinceStart = System.currentTimeMillis() - startTime
            return max(0, timeSinceStart)
        }

    // TODO Convert remainingTime to String
    val remainingTimeString: Duration
        get() {
            return remainingTime.toDuration(DurationUnit.MILLISECONDS)
        }

    /**
     * @return the elapsed realtime at which this timer will or did expire
     */
    val expirationTime: Long
        get() {
            check(!(state != TimerState.RUNNING && state != TimerState.EXPIRED)) {
                "cannot compute expiration time in timerState $state"
            }
            return startTime + remainingTime
        }

    /**
     * @return the wall clock time at which this timer will or did expire
     */
    val wallClockExpirationTime: Long
        get() {
            check(!(state != TimerState.RUNNING && state != TimerState.EXPIRED)) {
                "cannot compute expiration time in state $state"
            }
            return wallClockTime + remainingTime
        }

    /**
     *
     * @return the total amount of time elapsed up to this moment; expired timers will report more
     * than the [total length][.getTotalLength]
     */
    val elapsedTime: Long
        get() = totalLength - remainingTime

    /**
     * @return a copy of this timer that is running, expired or missed
     */
    fun start(): Timer {
        return if (state == TimerState.RUNNING || state == TimerState.EXPIRED) {
            this
        } else {
            Timer(
                TimerState.RUNNING,
                length,
                totalLength,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                remainingTime
            )
        }
    }

    /**
     * @return a copy of this timer that is expired, missed or reset
     */
    fun expire(): Timer {
        if (state == TimerState.EXPIRED || state == TimerState.RESET) {
            return this
        }

        val remainingTime = min(0L, remainingTime)
        return Timer(
            TimerState.EXPIRED,
            length,
            0L,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            remainingTime
        )
    }

    /**
     * @return a copy of this timer that is reset
     */
    fun reset(): Timer {
        return if (state == TimerState.RESET) {
            this
        } else {
            Timer(
                TimerState.RESET,
                0L,
                0L,
                0L,
                0L,
                0L
            )
        }
    }

    /**
     * @return a copy of this timer that has its times adjusted after time has been set
     */
    fun updateAfterTimeSet(): Timer {
        if (state == TimerState.RESET) {
            return this
        }
        val timeSinceBoot = System.currentTimeMillis()
        val wallClockTime = System.currentTimeMillis()
        val delta = timeSinceBoot - startTime
        val remainingTime = lastRemainingTime - delta
        return if (delta < 0) {
            // Avoid negative time deltas. They typically happen following reboots when TIME_SET is
            // broadcast before BOOT_COMPLETED. Simply ignore the time update and hope
            // updateAfterReboot() can successfully correct the data at a later time.
            this
        } else {
            Timer(
                state,
                length,
                totalLength,
                timeSinceBoot,
                wallClockTime,
                remainingTime
            )
        }
    }

    /**
     * @return a copy of this timer with the given `remainingTime` or this timer if the
     * remaining time could not be legally adjusted
     */
    fun setRemainingTime(remainingTime: Long): Timer {
        // Do not change the remaining time of a reset timer.
        if (lastRemainingTime == remainingTime || state == TimerState.RESET) {
            return this
        }

        val delta = remainingTime - lastRemainingTime
        val totalLength = totalLength + delta

        val lastStartTime: Long
        val lastWallClockTime: Long
        val state: TimerState?
        if (remainingTime > 0 && (this.state == TimerState.EXPIRED)) {
            state = TimerState.RUNNING
            lastStartTime = System.currentTimeMillis()
            lastWallClockTime = System.currentTimeMillis()
        } else {
            state = this.state
            lastStartTime = this.startTime
            lastWallClockTime = this.wallClockTime
        }

        return Timer(
            state,
            length,
            totalLength,
            lastStartTime,
            lastWallClockTime,
            remainingTime
        )
    }

    /**
     * @return a copy of this timer with an additional minute added to the remaining time and total
     * length, or this Timer if the minute could not be added
     */
    fun addMinute(): Timer {
        return if (state == TimerState.EXPIRED) {
            // Expired and missed timers restart with 60 seconds of remaining time.
            setRemainingTime(DateUtils.MINUTE_IN_MILLIS)
        } else {
            // Otherwise try to add a minute to the remaining time.
            setRemainingTime(lastRemainingTime + DateUtils.MINUTE_IN_MILLIS)
        }
    }
}

enum class TimerState() {
    RUNNING,
    EXPIRED,
    RESET
}
