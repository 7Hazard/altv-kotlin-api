package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.*
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.math.Float3
import jnr.ffi.Pointer
import kotlinx.coroutines.runBlocking

open class Player constructor(pointer: Pointer)
    : Entity(CAPI.func.alt_IPlayer_to_alt_IEntity(pointer))
{
    internal val playerPtr: Pointer = pointer

    val name = notDeleted {
        StringView { ptr -> CAPI.func.alt_IPlayer_GetName(playerPtr, ptr) }
    }

    fun setHealth(value: Short) = nextTick {
        nextTick { CAPI.func.alt_IPlayer_SetHealth(playerPtr, (value+100).toShort()) }
    }
    var health: Short
        get() = notDeleted {
            (CAPI.func.alt_IPlayer_GetHealth(playerPtr) - 100).toShort()
        }
        set(value) = runBlocking { setHealth(value).await() }

    fun setModel(value: Int) = notDeleted {
        nextTick {
            CAPI.func.alt_IPlayer_SetModel(playerPtr, value)
        }
    }
    fun setModel(name: String) = setModel(hash(name))
    var model
        get() = notDeleted { CAPI.func.alt_IPlayer_GetModel(playerPtr) }
        set(value) = runBlocking { setModel(value).await() }

    fun spawn(pos: Float3, delay: Int = 0) = notDeleted {
        nextTick {
            CAPI.func.alt_IPlayer_Spawn(
                playerPtr,
                pos.layout().pointer,
                delay
            )
        }
    }

    fun giveWeapon(weapon: String, ammo: Int, equip: Boolean) = notDeleted {
        nextTick {
            CAPI.func.alt_IPlayer_GiveWeapon(playerPtr, hash(weapon), ammo, equip)
        }
    }

    fun emit(name: String, vararg args: Any)
    {
        notDeleted {  }

        val arr = CAPI.alt_Array_RefBase_RefStore_constIMValue(
            CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Create_CAPI_Heap()
        )
        CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Reserve(arr.pointer, args.size.toLong())

        for ((i, arg) in args.withIndex()) {
            createMValueAndFree(arg) {
                CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Push(arr.pointer, it)
            }
        }

        val playerRef = CAPI.alt_RefBase_RefStore_IPlayer()
        playerRef.ptr.set(playerPtr)
        CAPI.func.alt_ICore_TriggerClientEvent(CAPI.core, playerRef.pointer, name.altStringView.ptr(), arr.pointer)
        // refcounts=2
        CAPI.func.alt_Array_RefBase_RefStore_constIMValue_CAPI_Free(arr.pointer)
        // refcounts=1
    }
}
