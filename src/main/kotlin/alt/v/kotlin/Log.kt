package alt.v.kotlin

import alt.v.jvm.CAPI as capi

object Log {
    fun info(msg: String)
    {
        capi.func.alt_server_log_info(capi.server, msg)
    }
    
    fun error(msg: String)
    {
        capi.func.alt_server_log_error(capi.server, msg)
    }
}