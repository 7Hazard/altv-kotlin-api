package alt.v.kotlin

import java.io.ByteArrayOutputStream
import java.io.PrintStream
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

    fun exception(e: Throwable, msg: String = "[Kotlin-JVM] Exception thrown")
    {
        val baos = ByteArrayOutputStream()
        PrintStream(baos, true, "UTF-8").use { ps -> e.printStackTrace(ps) }
        val stackTraceStr = String(baos.toByteArray(), StandardCharsets.UTF_8)
        Log.error(msg +
                "\n  Message: ${e.localizedMessage}" +
                "\n  Cause: ${e.cause}" +
                "\n  Stack Trace: $stackTraceStr")
    }
}
