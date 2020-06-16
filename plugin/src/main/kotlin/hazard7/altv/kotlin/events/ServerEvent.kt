package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.*
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.altview
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.MalformedParametersException

class ServerEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CServerScriptEvent(ceventptr)
    val name = StringView { CAPI.func.alt_CServerScriptEvent_GetName(pointer, it) }

    internal class ParamTypeMismatch(paramNum: Int, eventType: String, handlerType: String) :
        Exception("Event arg $paramNum was a $eventType, expected $handlerType")
    {}

    internal fun getArgs(handler: Handler): Array<Any?> {
        val args = CAPI.alt_Array_RefBase_RefStore_constIMValue(
            CAPI.func.alt_CServerScriptEvent_GetArgs(pointer)
        )
        val count = args.size.get().toInt()
        val params = handler.invoker.parameterTypes
        val ret = Array<Any?>(count) { null }
        for (i in 0 until count)
        {
            val mvalueref = CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Access_unsignedlonglong_1(args.pointer, i.toLong())
            val mval = CAPI.func.alt_RefBase_RefStore_constIMValue_Get(mvalueref)
            val type = CAPI.func.alt_IMValue_GetType(mval)

            ret[i] = when (type) {
                CAPI.alt_IMValue_Type.ALT_IMVALUE_TYPE_BOOL -> {
                    when (params[i]) {
                        Boolean::class.java ->
                            CAPI.func.alt_IMValueBool_Value(CAPI.func.alt_IMValue_to_alt_IMValueBool(mval))
                        Int::class.java ->
                            if (CAPI.func.alt_IMValueBool_Value(CAPI.func.alt_IMValue_to_alt_IMValueBool(mval))) 1 else 0
                        String::class.java ->
                            CAPI.func.alt_IMValueBool_Value(CAPI.func.alt_IMValue_to_alt_IMValueBool(mval)).toString()
                        else ->
                            throw ParamTypeMismatch(i,"Boolean", params[i].name)
                    }
                }
                CAPI.alt_IMValue_Type.ALT_IMVALUE_TYPE_INT -> {
                    when (params[i]) {
                        Boolean::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)) == 1L
                        Int::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toInt()
                        UInt::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toUInt()
                        Long::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toLong()
                        ULong::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toULong()
                        Float::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toFloat()
                        Double::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toDouble()
                        String::class.java ->
                            CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toString()
                        else -> {
                            throw ParamTypeMismatch(i,"Long", params[i].name)
                        }
                    }
                }
                CAPI.alt_IMValue_Type.ALT_IMVALUE_TYPE_STRING -> {
                    if (params[i] != String::class.java)
                        throw ParamTypeMismatch(i, "String",params[i].name)
                    StringView { CAPI.func.alt_IMValueString_Value(CAPI.func.alt_IMValue_to_alt_IMValueString(mval), it) }
                }
                else -> {
                    throw NotImplementedError("Unhandled MValue type ${type.name}")
                }
            }
        }
        return ret
    }

    companion object {
        fun send(name: String, vararg args: Any)
        {
            val emptyMValue = CAPI.alt_RefBase_RefStore_constIMValue()
            emptyMValue.ptr.set(0)
            val arr = CAPI.alt_Array_RefBase_RefStore_constIMValue(
                CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Create_CAPI_Heap()
            )
            CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Reserve(arr.pointer, args.size.toLong())

            fun getBaseRefMValue(create: () -> Pointer, cast: (Pointer) -> Pointer): Pointer
            {
                // TODO: PROFILE HEAP VS STACK METHODS

                // Ref<MValueBool>
                val refmvaluetype = create()
                // MValueBool
                val mvaluetype = CAPI.func.alt_RefBase_RefStore_constIMValue_Get(refmvaluetype)
                // MValue
                val mvalue = cast(mvaluetype)
                // Ref<MValue>
                val refmvalue = CAPI.func.alt_RefBase_RefStore_constIMValue_Create_4_CAPI_Heap(mvalue)
                CAPI.func.alt_IMValue_RemoveRef(mvalue)
                return refmvalue
            }

            for ((i, arg) in args.withIndex()) {
                val refmvalue = when (arg) {
                    is Boolean -> {
                        getBaseRefMValue(
                            { CAPI.func.alt_ICore_CreateMValueBool_CAPI_Heap(CAPI.core, arg) },
                            { CAPI.func.alt_IMValueBool_to_alt_IMValue(it) }
                        )
                    }
                    is Int -> {
                        getBaseRefMValue(
                            { CAPI.func.alt_ICore_CreateMValueInt_CAPI_Heap(CAPI.core, arg.toLong()) },
                            { CAPI.func.alt_IMValueInt_to_alt_IMValue(it) }
                        )
                    }
                    is String -> {
                        getBaseRefMValue(
                            { CAPI.func.alt_ICore_CreateMValueString_CAPI_Heap(CAPI.core, arg.altstring.pointer) },
                            { CAPI.func.alt_IMValueString_to_alt_IMValue(it) }
                        )
                    }
                    else -> {
                        throw TypeCastException("Unsupported event arg type '${arg::class.java}', value: '$arg'")
                    }
                }

                // refcount=1, ++ after push
                CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Push(arr.pointer, refmvalue)
                // refcount=2, ok to capi free, will deref
                CAPI.func.alt_RefBase_RefStore_constIMValue_CAPI_Free(refmvalue)
                // refcount = 1
            }

            CAPI.func.alt_ICore_TriggerLocalEvent(CAPI.core, name.altview.ptr(), arr.pointer)
            // refcounts=2
            CAPI.func.alt_Array_RefBase_RefStore_constIMValue_CAPI_Free(arr.pointer)
            // refcounts=1
        }
    }

    internal class Handler internal constructor(val func: Function<*>) {
        val invoker by lazy {
            val method = func.javaClass.declaredMethods[1] // seems to always be the second one (with param types)
            if(!method.trySetAccessible())
            {
                throw RuntimeException("Could not make handler callable")
            }
            method
        }

        fun invoke(name: String, vararg args: Any?) {
            val hc = invoker.parameterCount
            val ec = args.size
            if (hc > ec)
                throw MalformedParametersException("Event '$name' with $ec args was sent to $hc params handler")
            else if (hc < ec)
            {
                logWarning("Recieved event '${name}' with ${ec} args did not match handlers ${hc} parameters")
                invoker.invoke(func, *(args.copyOfRange(0, hc))) // discard args overflow
            }
            else invoker.invoke(func, *args)
        }
    }
}

/**
 * Server script events, annotation for function handlers
 * @param name the name if the event to handle for
 */
//@Retention(AnnotationRetention.RUNTIME)
//@Target(AnnotationTarget.FUNCTION)
//annotation class OnServerEvent(val name: String)