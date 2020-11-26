package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.*
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.altview
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer
import java.lang.Exception
import java.lang.reflect.MalformedParametersException
import java.lang.reflect.Method
import java.security.InvalidParameterException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.reflect

class ClientEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CClientScriptEvent(ceventptr)
    val name = StringView { CAPI.func.alt_CClientScriptEvent_GetName(pointer, it) }
    internal val player = run {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CClientScriptEvent_GetTarget(pointer, ref.pointer)
        ref.ptr.get()
    }
    fun getPlayer(resource: Resource) = resource.getOrCreatePlayer(player)

    val args = CAPI.alt_Array_RefBase_RefStore_constIMValue(
        CAPI.func.alt_CClientScriptEvent_GetArgs(pointer)
    )
    val argsCount = args.size.get().toInt()

    inline fun <reified T> getArg(index: Int): T {
        if(index < 0 && index > argsCount-1)
            throw IndexOutOfBoundsException("Event had $argsCount arguments, index must be between 0 and ${argsCount-1}")

        val mvalueref = CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Access_unsignedlonglong_1(args.pointer, index.toLong())
        try {
            return getMValue(mvalueref)
        } catch (e: MValueTypeMismatch) {
            throw ParamTypeMismatch(index, e.type)
        }
    }

    class ParamTypeMismatch(paramNum: Int, eventType: String) :
        Exception("Event arg $paramNum was a $eventType")
}
