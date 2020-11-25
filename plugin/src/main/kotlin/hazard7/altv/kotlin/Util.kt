package hazard7.altv.kotlin

import hazard7.altv.jvm.AltStringView
import hazard7.altv.jvm.CAPI
import hazard7.altv.jvm.StringUtil
import hazard7.altv.kotlin.math.Float3
import jnr.ffi.Memory
import jnr.ffi.Pointer
import jnr.ffi.Struct
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

internal val Struct.pointer get() = Struct.getMemory(this)

internal val CAPI.alt_StringView.string
    get() = this.data.get().getString(0, this.size.get().toInt(), Charset.forName("UTF-8"))

internal fun StringView(f: (Pointer) -> Unit): String
{
    val s = CAPI.alt_StringView()
    f(s.pointer)
    return s.string
}
internal fun StringView(ptr: Pointer) = CAPI.alt_StringView(ptr).string

internal val String.altview get() = AltStringView(this)
internal val String.altstring: CAPI.alt_String get() {
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
