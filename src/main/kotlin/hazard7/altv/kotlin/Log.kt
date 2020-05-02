package hazard7.altv.kotlin

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets


object Log {
    fun info(msg: String)
    {
        hazard7.altv.jvm.Log.info(msg)
    }
    
    fun error(msg: String)
    {
        hazard7.altv.jvm.Log.error(msg)
    }

    fun exception(e: Throwable, msg: String = "[Kotlin-JVM] Exception thrown")
    {
        val baos = ByteArrayOutputStream()
        PrintStream(baos, true, "UTF-8").use { ps -> e.printStackTrace(ps) }
        val stackTraceStr = String(baos.toByteArray(), StandardCharsets.UTF_8)
        error(msg +
                "\n  Message: ${e.localizedMessage}" +
                "\n  Cause: ${e.cause}" +
                "\n  Stack Trace: $stackTraceStr")
    }
}
