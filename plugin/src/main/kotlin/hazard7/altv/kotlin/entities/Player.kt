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
    internal val player: Pointer = pointer

    val name = StringView { ptr -> CAPI.func.alt_IPlayer_GetName(player, ptr) }

    fun setHealth(value: Short) = nextTick { CAPI.func.alt_IPlayer_SetHealth(player, (value+100).toShort()) }
    var health: Short
        get() = (CAPI.func.alt_IPlayer_GetHealth(player) - 100).toShort()
        set(value) = runBlocking { setHealth(value).await() }

    fun setModel(value: Int) = nextTick {
        CAPI.func.alt_IPlayer_SetModel(player, value)
    }
    fun setModel(name: String) = setModel(hash(name))
    var model
        get() = CAPI.func.alt_IPlayer_GetModel(player)
        set(value) = runBlocking { setModel(value).await() }

    fun spawn(pos: Float3, delay: Int = 0) = nextTick {
        CAPI.func.alt_IPlayer_Spawn(
            player,
            pos.layout().pointer,
            delay
        )
    }

    fun giveWeapon(weapon: String, ammo: Int, equip: Boolean) = nextTick {
        CAPI.func.alt_IPlayer_GiveWeapon(player, hash(weapon), ammo, equip)
    }

    fun emit(name: String, vararg args: Any)
    {
        val emptyMValue = CAPI.alt_RefBase_RefStore_constIMValue()
        emptyMValue.ptr.set(0)
        val arr = CAPI.alt_Array_RefBase_RefStore_constIMValue(
            CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Create_CAPI_Heap()
        )
        CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Reserve(arr.pointer, args.size.toLong())

        fun getBaseRefMValue(create: () -> Pointer, cast: (Pointer) -> Pointer): Pointer
        {
            // TODO: PROFILE HEAP VS STACK METHODS

            // Ref<MValueBool>
            val refmvaluetype = create()
            // MValueBool
            val mvaluetype = CAPI.func.alt_RefBase_RefStore_constIMValue_Get(refmvaluetype)
            // MValue
            val mvalue = cast(mvaluetype)
            // Ref<MValue>
            val refmvalue = CAPI.func.alt_RefBase_RefStore_constIMValue_Create_4_CAPI_Heap(mvalue)
            CAPI.func.alt_IMValue_RemoveRef(mvalue)
            return refmvalue
        }

        for ((i, arg) in args.withIndex()) {
            val refmvalue = when (arg) {
                is Boolean -> {
                    getBaseRefMValue(
                        { CAPI.func.alt_ICore_CreateMValueBool_CAPI_Heap(CAPI.core, arg) },
                        { CAPI.func.alt_IMValueBool_to_alt_IMValue(it) }
                    )
                }
                is Int -> {
                    getBaseRefMValue(
                        { CAPI.func.alt_ICore_CreateMValueInt_CAPI_Heap(CAPI.core, arg.toLong()) },
                        { CAPI.func.alt_IMValueInt_to_alt_IMValue(it) }
                    )
                }
                is String -> {
                    getBaseRefMValue(
                        { CAPI.func.alt_ICore_CreateMValueString_CAPI_Heap(CAPI.core, arg.altstring.pointer) },
                        { CAPI.func.alt_IMValueString_to_alt_IMValue(it) }
                    )
                }
                else -> {
                    throw TypeCastException("Unsupported event arg type '${arg::class.java}', value: '$arg'")
                }
            }

            // refcount=1, ++ after push
            CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Push(arr.pointer, refmvalue)
            // refcount=2, ok to capi free, will deref
            CAPI.func.alt_RefBase_RefStore_constIMValue_CAPI_Free(refmvalue)
            // refcount = 1
        }

        val playerRef = CAPI.alt_RefBase_RefStore_IPlayer()
        playerRef.ptr.set(player)
        CAPI.func.alt_ICore_TriggerClientEvent(CAPI.core, playerRef.pointer, name.altview.ptr(), arr.pointer)
        // refcounts=2
        CAPI.func.alt_Array_RefBase_RefStore_constIMValue_CAPI_Free(arr.pointer)
        // refcounts=1
    }
}
