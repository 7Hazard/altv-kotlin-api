package alt.v.kotlin.entities

import alt.v.jvm.CAPI
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import jnr.ffi.Struct

open class BaseObject internal constructor(pointer: Pointer) {
    private val baseobject: Pointer = run {
        CAPI.func.alt_IBaseObject_AddRef(pointer)
        return@run pointer
    }

    protected fun finalize() {
        CAPI.func.alt_IBaseObject_RemoveRef(baseobject)
    }

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

