package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.hash
import hazard7.altv.kotlin.layout
import hazard7.altv.kotlin.math.Float3
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer
import jnr.ffi.Struct

class Player internal constructor(pointer: Pointer)
    : Entity(CAPI.func.alt_IPlayer_to_alt_IEntity(pointer))
{
    private val player: Pointer = pointer

    val name by lazy { StringView { ptr -> CAPI.func.alt_IPlayer_GetName(player, ptr) } }

    var health: Short
        get() = (CAPI.func.alt_IPlayer_GetHealth(player) - 100).toShort()
        set(value) = CAPI.func.alt_IPlayer_SetHealth(player, (value+100).toShort())

    var model
        get() = CAPI.func.alt_IPlayer_GetModel(player)
        set(value) = CAPI.func.alt_IPlayer_SetModel(player, value)

    fun setModel(name: String) { CAPI.func.alt_IPlayer_SetModel(player, hash(name)) }

    fun spawn(pos: Float3, delay: Int = 0)
    {
        CAPI.func.alt_IPlayer_Spawn(
                player,
                pos.layout().pointer,
                delay
        )
    }

    fun giveWeapon(weapon: String, ammo: Int, equip: Boolean)
    {
        CAPI.func.alt_IPlayer_GiveWeapon(player, hash(weapon), ammo, equip)
    }

//    fun triggerEvent(name:String, vararg args: Any)
//    {
////        throw NotImplementedError()
//
//        // Could maybe be done with CAPI.alt_Array_RefBase_RefStore_constIMValue but whatever
//        val mvalueArgs = CAPI.func.alt_Array_RefBase_RefStore_constIMValue_Create_2(args.size.toLong(),
//                Pointer.wrap(CAPI.runtime, 0))
//
//        val badargs: MutableList<Pair<Int, Any>> = mutableListOf()
//
//        for((index, arg) in args.withIndex())
//        {
//            val mvalue: Pointer = when (arg)
//            {
//                is Boolean -> CAPI.func.alt_ICore_CreateMValueBool(CAPI.core, arg)
//
//                // integers
//                is Byte -> CAPI.func.alt_ICore_CreateMValueInt(CAPI.core, arg.toLong())
//                is UByte -> CAPI.func.alt_ICore_CreateMValueUInt(CAPI.core, arg.toLong())
//                is Short -> CAPI.func.alt_ICore_CreateMValueInt(CAPI.core, arg.toLong())
//                is UShort -> CAPI.func.alt_ICore_CreateMValueUInt(CAPI.core, arg.toLong())
//                is Int -> CAPI.func.alt_ICore_CreateMValueInt(CAPI.core, arg.toLong())
//                is UInt -> CAPI.func.alt_ICore_CreateMValueUInt(CAPI.core, arg.toLong())
//                is Long -> CAPI.func.alt_ICore_CreateMValueInt(CAPI.core, arg.toLong())
//                is ULong -> CAPI.func.alt_ICore_CreateMValueUInt(CAPI.core, arg.toLong())
//
//                // decimals
//                is Float -> CAPI.func.alt_ICore_CreateMValueDouble(CAPI.core, arg.toDouble())
//                is Double -> CAPI.func.alt_ICore_CreateMValueDouble(CAPI.core, arg.toDouble())
//
//                // vectors, not supportive of structures in MValue
////                is Float3 -> {
////                    val s = CAPI.alt_Vector_float_3_PointLayout()
////                    s.x.set(pos.x)
////                    s.y.set(pos.y)
////                    s.z.set(pos.z)
////                    CAPI.func.alt_ICore_CreateMValueVector3(CAPI.core, Struct.getMemory(s))
////                }
//
//                else -> {
//                    badargs.add(Pair(index, arg))
//                    Pointer.wrap(CAPI.runtime, 0)
//                }
//            }
//
//        }
//
//        if(badargs.size != 0)
//        {
//            var msg = ""
//            for (arg in args)
//            {
//                msg += "\n    "+arg.toString()
//            }
//
//            // clean up before throwing
//
//            throw TypeCastException("Bad event types passed:"+msg)
//        }
//
//        CAPI.func.alt_ICore_TriggerClientEvent(CAPI.core, player, AltStringView(name).ptr(), mvalueArgs)
//        CAPI.func.alt_Array_RefBase_RefStore_constIMValue_free(mvalueArgs)
//    }
}

