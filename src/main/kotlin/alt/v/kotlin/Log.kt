package alt.v.kotlin

import java.lang.Exception
import alt.v.jvm.CAPI

object Log {
    fun info(msg: String)
    {
        alt.v.jvm.Log.info("[Kotlin-JVM] $msg")
    }
    
    fun error(msg: String)
    {
        alt.v.jvm.Log.error("[Kotlin-JVM] $msg")
    }

    fun exception(e: Exception, msg: String = "[Kotlin-JVM][ERROR] Exception thrown")
    {
        Log.error(msg +
                "\n\tMessage: ${e.localizedMessage}" +
                "\n\tCause: ${e.cause}" +
                "\n\tStack Trace: ${e.stackTrace.toString()}")
    }
}