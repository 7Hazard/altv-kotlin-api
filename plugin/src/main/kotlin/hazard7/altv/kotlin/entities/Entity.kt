package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.*
import hazard7.altv.kotlin.Vector3f
import hazard7.altv.kotlin.layout
import hazard7.altv.kotlin.math.Float3
import jnr.ffi.Pointer
import kotlinx.coroutines.runBlocking

open class Entity internal constructor(pointer: Pointer)
    : BaseObject(CAPI.func.alt_IEntity_to_alt_IBaseObject(pointer))
{
    internal val entityPtr = pointer

    fun setPos(value: Float3) = notDeleted {
        nextTick {
            CAPI.func.alt_IEntity_SetRotation(entityPtr, value.layout().pointer)
        }
    }
    var pos: Float3
        get() = notDeleted {
            Vector3f { CAPI.func.alt_IEntity_GetPosition(entityPtr, it) }
        }
        set(value) = runBlocking { setPos(value).await() }

    fun setRot(value: Float3) = notDeleted {
        nextTick {
            CAPI.func.alt_IEntity_SetRotation(entityPtr, value.layout().pointer)
        }
    }
    var rot: Float3
        get() = notDeleted {
            Vector3f { ptr -> CAPI.func.alt_IEntity_GetRotation(entityPtr, ptr) }
        }
        set(value) = runBlocking { setRot(value).await() }

    val owner: Player? get() {
        return notDeleted {
            val ent = CAPI.alt_RefBase_RefStore_IPlayer();
            CAPI.func.alt_IEntity_GetNetworkOwner(entityPtr, ent.pointer)
            if(ent.ptr.get() == null) null
            else Player(ent.ptr.get())
        }
    }

    fun setOwner(owner: Player?, disableMigration: Boolean) = notDeleted {
        nextTick {
            val ent = CAPI.alt_RefBase_RefStore_IPlayer()
            if(owner == null) {
                CAPI.func.alt_IEntity_SetNetworkOwner(entityPtr, ent.pointer, disableMigration)
            } else {
                ent.ptr.set(owner.playerPtr)
                CAPI.func.alt_IEntity_SetNetworkOwner(entityPtr, ent.pointer, false)
            }
        }
    }

    fun getSyncedMetadata(key: String) {
        notDeleted {  }
        return getMValue {
            CAPI.func.alt_IEntity_GetSyncedMetaData(entityPtr, key.altStringView.ptr(), it)
        }
    }

    fun setSyncedMetadata(key: String, value: Any) {
        notDeleted {  }
        createMValueAndFree(value) {
            CAPI.func.alt_IEntity_SetSyncedMetaData(entityPtr, key.altStringView.ptr(), it)
        }
    }

    fun getStreamSyncedMetadata(key: String) {
        notDeleted {  }
        return getMValue {
            CAPI.func.alt_IEntity_GetStreamSyncedMetaData(entityPtr, key.altStringView.ptr(), it)
        }
    }

    fun setStreamSyncedMetadata(key: String, value: Any) {
        notDeleted {  }
        createMValueAndFree(value) {
            CAPI.func.alt_IEntity_SetStreamSyncedMetaData(entityPtr, key.altStringView.ptr(), it)
        }
    }
}
