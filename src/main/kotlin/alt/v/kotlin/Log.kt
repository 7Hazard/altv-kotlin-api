package alt.v.kotlin

import alt.v.jvm.*

object Log {
    fun info(msg: String)
    {
        CAPI.func.alt_server_log_info(server, msg)
    }
    
    fun error(msg: String)
    {
        CAPI.func.alt_server_log_error(server, msg)
    }
}