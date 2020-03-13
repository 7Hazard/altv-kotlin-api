package alt.v.kotlin

import java.lang.Exception
import alt.v.jvm.CAPI
import java.io.PrintStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets


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
        val baos = ByteArrayOutputStream()
        PrintStream(baos, true, "UTF-8").use { ps -> e.printStackTrace(ps) }
        val stackTraceStr = String(baos.toByteArray(), StandardCharsets.UTF_8)
        Log.error(msg +
                "\n\tMessage: ${e.localizedMessage}" +
                "\n\tCause: ${e.cause}" +
                "\n\tStack Trace: $stackTraceStr")
    }
}
