package alt.v.kotlin.entities

import alt.v.jvm.CAPI
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import jnr.ffi.Struct

open class Entity internal constructor(pointer: Pointer)
    : BaseObject(CAPI.func.alt_IEntity_to_alt_IBaseObject(pointer))
{
    companion object {
        fun fromRef(pointer: Pointer): Entity
        {
            return Entity(CAPI.func.alt_RefBase_RefStore_IPlayer_Get(pointer))
        }
    }

    private val entity: Pointer = pointer

    var pos: Float3
        get() {
            val s = CAPI.alt_Vector_float_3_PointLayout()
            s.useMemory(CAPI.func.alt_IEntity_GetPosition(entity))
            return Float3(s.x.get(), s.y.get(), s.z.get())
        }
        set(value) {
            val s = CAPI.alt_Vector_float_3_PointLayout()
            s.x.set(value.x)
            s.y.set(value.y)
            s.z.set(value.z)
            CAPI.func.alt_IEntity_SetPosition(entity, Struct.getMemory(s))
        }

    var rot: Float3
        get() {
            val s = CAPI.alt_Vector_float_3_PointLayout()
            s.useMemory(CAPI.func.alt_IEntity_GetRotation(entity))
            return Float3(s.x.get(), s.y.get(), s.z.get())
        }
        set(value) {
            val s = CAPI.alt_Vector_float_3_PointLayout()
            s.x.set(value.x)
            s.y.set(value.y)
            s.z.set(value.z)
            CAPI.func.alt_IEntity_SetRotation(entity, Struct.getMemory(s))
        }
}

