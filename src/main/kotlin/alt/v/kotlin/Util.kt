package alt.v.kotlin

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import jnr.ffi.Struct
import java.nio.charset.Charset

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
