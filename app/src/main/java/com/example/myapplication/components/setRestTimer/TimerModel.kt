package com.example.myapplication.components.setRestTimer

import androidx.annotation.StringRes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TimerModel {
    //    TODO
    fun startTimer(length: Long): Timer {
        // Create the timer instance.
        val timer = Timer(
            state = timerState.RUNNING,
            length = 60.toDuration(DurationUnit.SECONDS).toLong(DurationUnit.MILLISECONDS),
            totalLength = 60.toDuration(DurationUnit.SECONDS).toLong(DurationUnit.MILLISECONDS),
            startTime = System.currentTimeMillis(),
            wallClockTime = System.currentTimeMillis(),
            lastRemainingTime = 60.toDuration(DurationUnit.SECONDS)
                .toLong(DurationUnit.MILLISECONDS)
        )

        timer.start()

        // Update the timer notification.
//        updateNotification()
        // Heads-Up notification is unaffected by this change

        if (timer.remainingTime <= 0) {
            expireTimer(timer)
        }

        return timer
    }

    /**
     * @param service used to start foreground notifications for expired timers
     * @param timer the timer to be expired
     */
    fun expireTimer(timer: Timer) {
        timer.expire()
//        TODO implement updateHeadsUpNotification()
    }

    /**
     * If the given `timer` is expired and marked for deletion after use then this method
     * removes the timer. The timer is otherwise transitioned to the reset state and continues
     * to exist.
     *
     * @param timer the timer to be reset
     * @param allowDelete `true` if the timer is allowed to be deleted instead of reset
     * (e.g. one use timers)
     * @param eventLabelId the label of the timer event to send; 0 if no event should be sent
     * @return the reset `timer` or `null` if the timer was deleted
     */
    fun resetTimer(timer: Timer, allowDelete: Boolean, @StringRes eventLabelId: Int): Timer {
//        TODO implement below
        // Update the notification after updating the timer data.
//        when {
//            timer.isExpired -> updateHeadsUpNotification()
//            else -> updateNotification()
//        }

        return timer.reset()
    }
}
