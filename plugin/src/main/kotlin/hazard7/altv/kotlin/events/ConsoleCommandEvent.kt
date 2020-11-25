package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.logInfo
import jnr.ffi.Pointer

class ConsoleCommandEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CConsoleCommandEvent(ceventptr)
    val command = StringView { CAPI.func.alt_CConsoleCommandEvent_GetName(pointer, it) }
    val args = run {
        val args = arrayListOf<String>()
        val array = CAPI.func.alt_CConsoleCommandEvent_GetArgs(pointer)
        val size = CAPI.func.alt_Array_StringView_GetSize(array)
        logInfo("SIZE $size")
        for (i in 0..size-1) {
            val stringview = CAPI.func.alt_Array_StringView_Access_unsignedlonglong_1(array, i)
            val str = StringView(stringview)
            args.add(str)
        }
        args
    }
}
