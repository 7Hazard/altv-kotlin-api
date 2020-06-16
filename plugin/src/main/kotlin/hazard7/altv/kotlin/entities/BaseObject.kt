package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.logInfo
import jnr.ffi.Pointer
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

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

    val type by lazy { CAPI.func.alt_IBaseObject_GetType(baseobject).toString() }
}

