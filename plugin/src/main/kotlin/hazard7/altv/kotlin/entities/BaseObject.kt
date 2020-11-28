package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import jnr.ffi.Pointer
import java.lang.RuntimeException

open class BaseObject internal constructor(pointer: Pointer) {
    internal val baseObjectPtr: Pointer = run {
        CAPI.func.alt_IBaseObject_AddRef(pointer)
        return@run pointer
    }

    protected fun finalize() {
        CAPI.func.alt_IBaseObject_RemoveRef(baseObjectPtr)
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

    internal class DeletedException : RuntimeException("alt:V object is deleted")
    internal var deleted = false
    internal fun <T> notDeleted(f: ()->T): T {
        if(deleted) throw DeletedException()
        else return f()
    }

    val type = notDeleted {
        CAPI.func.alt_IBaseObject_GetType(baseObjectPtr).toString()
    }
}

