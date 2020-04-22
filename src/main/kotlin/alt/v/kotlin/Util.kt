package alt.v.kotlin

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import jnr.ffi.Struct
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

//internal fun fromCString(bufferCapacity: Int, f: (bufferptr: Pointer) -> Unit): String
//{
//    try {
//        val buffer = ByteBuffer.allocate(bufferCapacity+1) // +1 for null terminator
//        val ptr = Pointer.wrap(CAPI.runtime, buffer)
//        f(ptr)
//        for (i in 0..bufferCapacity)
//        {
//            if(buffer[i].compareTo(0) == 0) return String(buffer.array(), 0, i, StandardCharsets.UTF_8)
//        }
//    } catch (e: Exception)
//    {
//        Log.exception(e, "[Kotlin-JVM] Exception while retrieving C String: " +
//                "\n\tBuffer Capacity: $bufferCapacity")
//        return "[Kotlin-JVM] INVALID STRING"
//    }
//
//    return "[Kotlin-JVM] INVALID STRING"
//}
//

fun hash(string: String): UInt
{
    return CAPI.func.alt_ICore_Hash(CAPI.core, AltStringView(string).ptr()).toUInt()
}

internal fun Float3.layout(): CAPI.alt_Vector_float_3_PointLayout
{
    val s = CAPI.alt_Vector_float_3_PointLayout()
    s.x.set(this.x)
    s.y.set(this.y)
    s.z.set(this.z)
    return s
}

internal fun Struct.ptr() = Struct.getMemory(this)

internal fun CAPI.alt_StringView.str() =
        this.data.get().getString(0, this.size.get().toInt(), Charset.forName("UTF-8"))

internal fun StringView(f: (Pointer) -> Unit): String
{
    val s = CAPI.alt_StringView()
    f(s.ptr())
    return s.str()
}

internal fun Vector3f(f: (Pointer) -> Unit): Float3
{
    val s = CAPI.alt_Vector_float_3_PointLayout()
    f(s.ptr())
    return Float3(s.x.get(), s.y.get(), s.z.get())
}
