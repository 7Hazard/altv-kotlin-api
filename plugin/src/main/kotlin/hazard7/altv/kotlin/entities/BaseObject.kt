package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.altStringView
import hazard7.altv.kotlin.createMValue
import hazard7.altv.kotlin.createMValueAndFree
import hazard7.altv.kotlin.getMValue
import jnr.ffi.Pointer
import java.lang.RuntimeException

open class BaseObject internal constructor(pointer: Pointer) {
    val baseObjectPtr: Pointer = run {
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
    fun <T> notDeleted(f: ()->T): T {
        if(deleted) throw DeletedException()
        else return f()
    }

    val type = notDeleted {
        CAPI.func.alt_IBaseObject_GetType(baseObjectPtr).toString()
    }

    inline fun <reified T> getMetadata(key: String): T {
        notDeleted {  }

        return getMValue {
            CAPI.func.alt_IBaseObject_GetMetaData(baseObjectPtr, key.altStringView.ptr(), it)
        }
    }

    fun setMetadata(key: String, value: Any) {
        notDeleted {}
        createMValueAndFree(value) {
            CAPI.func.alt_IBaseObject_SetMetaData(baseObjectPtr, key.altStringView.ptr(), it)
        }
    }
}
