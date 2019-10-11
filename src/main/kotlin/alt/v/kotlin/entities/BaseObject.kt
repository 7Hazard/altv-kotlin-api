package alt.v.kotlin.entities

import alt.v.jvm.CAPI
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import jnr.ffi.Struct

open class BaseObject internal constructor(pointer: Pointer) {
    private val baseobject: Pointer = pointer

    enum class Type
    {
        Player,
        Vehicle,
        Checkpoint,
        Blip,
        WebView,
        VoiceChannel,
        ColShape
    }

    val type = CAPI.func.alt_IBaseObject_GetType(baseobject).toString()
}

