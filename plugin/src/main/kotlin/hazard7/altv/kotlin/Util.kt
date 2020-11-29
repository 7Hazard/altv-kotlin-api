package hazard7.altv.kotlin

import hazard7.altv.jvm.AltStringView
import hazard7.altv.jvm.CAPI
import hazard7.altv.jvm.StringUtil
import hazard7.altv.kotlin.events.ClientEvent
import hazard7.altv.kotlin.math.Float3
import jnr.ffi.Memory
import jnr.ffi.Pointer
import jnr.ffi.Struct
import java.lang.Exception
import java.nio.charset.Charset

fun hash(string: String) = CAPI.func.alt_ICore_Hash(CAPI.core, AltStringView(string).ptr())

internal fun Float3.layout(): CAPI.alt_Vector_float_3_PointLayout
{
    val s = CAPI.alt_Vector_float_3_PointLayout()
    s.x.set(this.x)
    s.y.set(this.y)
    s.z.set(this.z)
    return s
}

val Struct.pointer get() = Struct.getMemory(this)

internal val CAPI.alt_StringView.string
    get() = this.data.get().getString(0, this.size.get().toInt(), Charset.forName("UTF-8"))

fun StringView(f: (Pointer) -> Unit): String
{
    val s = CAPI.alt_StringView()
    f(s.pointer)
    return s.string
}
internal fun StringView(ptr: Pointer) = CAPI.alt_StringView(ptr).string

val String.altStringView get() = AltStringView(this)
internal val String.altString: CAPI.alt_String get() {
    val s = CAPI.alt_String()
    val buf = Memory.allocateDirect(CAPI.runtime, this.length)
    buf.putString(0, this, this.length, StringUtil.UTF8)
    s.data.set(buf)
    s.size.set(this.length)
    return s
}

internal fun Vector3f(f: (Pointer) -> Unit): Float3
{
    val s = CAPI.alt_Vector_float_3_PointLayout()
    f(s.pointer)
    return Float3(s.x.get(), s.y.get(), s.z.get())
}

class MValueTypeMismatch(val type: String) : Exception()
inline fun <reified T> getMValue(mvalueref: Pointer): T
{
    val mval = CAPI.func.alt_RefBase_RefStore_constIMValue_Get(mvalueref)
    val type = CAPI.func.alt_IMValue_GetType(mval)

    when (type) {
        CAPI.alt_IMValue_Type.ALT_IMVALUE_TYPE_BOOL -> {
            when (T::class) {
                Boolean::class ->
                    return CAPI.func.alt_IMValueBool_Value(CAPI.func.alt_IMValue_to_alt_IMValueBool(mval)) as T
                Int::class ->
                    return (if (CAPI.func.alt_IMValueBool_Value(CAPI.func.alt_IMValue_to_alt_IMValueBool(mval))) 1 else 0) as T
                String::class ->
                    return CAPI.func.alt_IMValueBool_Value(CAPI.func.alt_IMValue_to_alt_IMValueBool(mval)).toString() as T
                else ->
                    throw MValueTypeMismatch("Boolean")
            }
        }
        CAPI.alt_IMValue_Type.ALT_IMVALUE_TYPE_INT -> {
            when (T::class) {
                Boolean::class ->
                    return (CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)) == 1L) as T
                Int::class ->
                    return CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toInt() as T
                UInt::class ->
                    return CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toUInt() as T
                Long::class ->
                    return CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toLong() as T
                ULong::class ->
                    return CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toULong() as T
                Float::class ->
                    return CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toFloat() as T
                Double::class ->
                    return CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toDouble() as T
                String::class ->
                    return CAPI.func.alt_IMValueInt_Value(CAPI.func.alt_IMValue_to_alt_IMValueInt(mval)).toString() as T
                else -> {
                    throw MValueTypeMismatch("Long")
                }
            }
        }
        CAPI.alt_IMValue_Type.ALT_IMVALUE_TYPE_STRING -> {
            if (T::class != String::class)
                throw MValueTypeMismatch("String")
            return StringView { CAPI.func.alt_IMValueString_Value(CAPI.func.alt_IMValue_to_alt_IMValueString(mval), it) } as T
        }
        else -> {
            throw NotImplementedError("Unhandled MValue type ${type.name}")
        }
    }
}

inline fun <reified T> getMValue(f: (mvalueref: Pointer) -> Unit): T {
    val mvalueref = CAPI.alt_RefBase_RefStore_constIMValue()
    f(mvalueref.pointer)
    return getMValue(mvalueref.pointer)
}

// creates MValue with creator, play with typed mvalue and return base MValue
fun createMValue(create: () -> Pointer, play: (Pointer) -> Unit, cast: (Pointer) -> Pointer): Pointer
{
    // TODO: PROFILE HEAP VS STACK METHODS

    // Ref<MValueBool>
    val refmvaluetype = create()
    // MValueBool
    val mvaluetype = CAPI.func.alt_RefBase_RefStore_constIMValue_Get(refmvaluetype)

    play(mvaluetype)

    // MValue
    val mvalue = cast(mvaluetype)
    // Ref<MValue>
    val refmvalue = CAPI.func.alt_RefBase_RefStore_constIMValue_Create_4_CAPI_Heap(mvalue)
    CAPI.func.alt_IMValue_RemoveRef(mvalue)
    return refmvalue
}

// creates MValue with creator and return base MValue
fun createMValue(create: () -> Pointer, cast: (Pointer) -> Pointer): Pointer
{
    return createMValue(create, {}, cast)
}

/**
 * returns Ref<MValue>
 * must be freed with alt_RefBase_RefStore_constIMValue_CAPI_Free
 */
fun createMValue(value: Any?): Pointer {
    return when (value) {
        null -> {
            createMValue(
                { CAPI.func.alt_ICore_CreateMValueNil_CAPI_Heap(CAPI.core) },
                { CAPI.func.alt_IMValueBool_to_alt_IMValue(it) }
            )
        }
        is Boolean -> {
            createMValue(
                { CAPI.func.alt_ICore_CreateMValueBool_CAPI_Heap(CAPI.core, value) },
                { CAPI.func.alt_IMValueBool_to_alt_IMValue(it) }
            )
        }
        is Int -> {
            createMValue(
                { CAPI.func.alt_ICore_CreateMValueInt_CAPI_Heap(CAPI.core, value.toLong()) },
                { CAPI.func.alt_IMValueInt_to_alt_IMValue(it) }
            )
        }
        is String -> {
            createMValue(
                { CAPI.func.alt_ICore_CreateMValueString_CAPI_Heap(CAPI.core, value.altString.pointer) },
                { CAPI.func.alt_IMValueString_to_alt_IMValue(it) }
            )
        }
        is Array<*> -> {
            createMValue(
                { CAPI.func.alt_ICore_CreateMValueList_CAPI_Heap(CAPI.core, value.size.toLong()) },
                { mvaluelist ->
                    for ((i, v) in value.withIndex()){
                        createMValueAndFree(v) {
                            CAPI.func.alt_IMValueList_Set(mvaluelist, i.toLong(), it)
                        }
                    }
                },
                { CAPI.func.alt_IMValueList_to_alt_IMValue(it) },
            )
        }
        is Iterable<*> -> {
            createMValue(
                { CAPI.func.alt_ICore_CreateMValueList_CAPI_Heap(CAPI.core, value.count().toLong()) },
                { mvaluelist ->
                    for ((i, v) in value.withIndex()){
                        createMValueAndFree(v) {
                            CAPI.func.alt_IMValueList_Set(mvaluelist, i.toLong(), it)
                        }
                    }
                },
                { CAPI.func.alt_IMValueList_to_alt_IMValue(it) },
            )
        }
        is Map<*, *> -> {
            for ((key, v) in value) {
                if(key is String) break;
                else {
                    throw TypeCastException("Maps must be of type Map<String, *>")
                }
            }

            createMValue(
                { CAPI.func.alt_ICore_CreateMValueDict_CAPI_Heap(CAPI.core) },
                { mvaluedict ->
                    for ((key, v) in value){
                        key as String
                        createMValueAndFree(v) {
                            CAPI.func.alt_IMValueDict_Set(mvaluedict, key.altStringView.ptr(), it)
                        }
                    }
                },
                { CAPI.func.alt_IMValueDict_to_alt_IMValue(it) },
            )
        }
        else -> {
            throw TypeCastException("Unsupported arg type '${value::class}', value: '$value'")
        }
    }
}

fun createMValueAndFree(value: Any?, f: (mvalue: Pointer) -> Unit) {
    val mvalue = createMValue(value)
    f(mvalue)
    CAPI.func.alt_RefBase_RefStore_constIMValue_CAPI_Free(mvalue)
}
