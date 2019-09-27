package alt.v.kotlin

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
//internal fun fromAltPosition(f: (bufferptr: Pointer) -> Unit): Float3
//{
//    val pos = CAPI.alt_position_t()
//    f(Struct.getMemory(pos))
//    return Float3(pos.x.get(), pos.y.get(), pos.z.get())
//}
//
//internal fun toAltPosition(pos: Float3): Pointer
//{
//    val altpos = CAPI.alt_position_t()
//    altpos.x.set(pos.x)
//    altpos.y.set(pos.y)
//    altpos.z.set(pos.z)
//    return Struct.getMemory(altpos)
//}
//
//fun hash(string: String) = CAPI.func.alt_server_hash(CAPI.server, string)
