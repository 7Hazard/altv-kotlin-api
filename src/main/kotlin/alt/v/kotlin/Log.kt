package alt.v.kotlin

import java.lang.Exception
import alt.v.jvm.CAPI

object Log {
    fun info(msg: String)
    {
        CAPI.func.alt_server_log_info(CAPI.server, msg)
    }
    
    fun error(msg: String)
    {
        CAPI.func.alt_server_log_error(CAPI.server, msg)
    }

    fun exception(e: Exception, msg: String = "[Kotlin-JVM] Exception thrown")
    {
        Log.error(msg +
                "\n\tMessage: ${e.localizedMessage}" +
                "\n\tCause: ${e.cause}" +
                "\n\tStack Trace: ${e.stackTrace.toString()}")
    }
}