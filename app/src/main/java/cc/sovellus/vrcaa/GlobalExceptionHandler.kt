package cc.sovellus.vrcaa

import android.content.Context
import android.content.Intent
import kotlin.system.exitProcess

class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler,
    private val activityToBeLaunched: Class<*>,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        try {
            launchActivity(applicationContext, activityToBeLaunched, exception)
            exitProcess(0)
        } catch (_: Exception) {
            defaultHandler.uncaughtException(thread, exception)
        }
    }

    private fun launchActivity(
        applicationContext: Context,
        activity: Class<*>,
        exception: Throwable,
    ) {
        val intent = Intent(applicationContext, activity).apply {
            putExtra(INTENT_EXTRA, exception.stackTraceToString())
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        applicationContext.startActivity(intent)
    }

    companion object {
        private const val INTENT_EXTRA = "Throwable"

        fun initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<*>,
        ) {
            val handler = GlobalExceptionHandler(
                applicationContext,
                Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler,
                activityToBeLaunched,
            )
            Thread.setDefaultUncaughtExceptionHandler(handler)
        }

        fun getThrowableFromIntent(intent: Intent): String {
            return intent.getStringExtra(INTENT_EXTRA)!!
        }
    }
}
