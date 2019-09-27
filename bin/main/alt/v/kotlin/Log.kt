package alt.v.kotlin

import java.lang.Exception
import alt.v.jvm.CAPI

object Log {
    fun info(msg: String)
    {
        alt.v.jvm.Log.info(msg)
    }
    
    fun error(msg: String)
    {
        alt.v.jvm.Log.error(msg)
    }

    fun exception(e: Exception, msg: String = "[Kotlin-JVM] Exception thrown")
    {
        Log.error(msg +
                "\n\tMessage: ${e.localizedMessage}" +
                "\n\tCause: ${e.cause}" +
                "\n\tStack Trace: ${e.stackTrace.toString()}")
    }
}