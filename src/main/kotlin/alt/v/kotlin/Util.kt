package alt.v.kotlin

import alt.v.jvm.CAPI
import jnr.ffi.Pointer
import jnr.ffi.Struct
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

internal fun GetCString(bufferCapacity: Int, f: (bufferptr: Pointer) -> Unit): String
{
    val buffer = ByteBuffer.allocate(bufferCapacity)
    val ptr = Pointer.wrap(CAPI.runtime, buffer)
    f(ptr)
    for (i in 0..50)
    {
        if(buffer[i].compareTo(0) == 0) return String(buffer.array(), 0, i, StandardCharsets.UTF_8)
    }
    return "[KOTLIN] INVALID STRING"
}


