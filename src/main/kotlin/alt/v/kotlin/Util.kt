package alt.v.kotlin

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import jnr.ffi.Struct
import java.lang.Exception
import java.nio.ByteBuffer
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
    var res = 0u
    AltStringView(string).use {
        res = CAPI.func.alt_ICore_Hash(CAPI.core, it.ptr()).toUInt()
    }
    return res
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
