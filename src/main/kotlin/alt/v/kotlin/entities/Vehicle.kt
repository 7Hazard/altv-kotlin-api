package alt.v.kotlin.entities

import alt.v.jvm.CAPI
import alt.v.kotlin.hash
import alt.v.kotlin.math.Float3
import alt.v.kotlin.layout
import alt.v.kotlin.ptr
import jnr.ffi.Pointer

class Vehicle internal constructor(pointer: Pointer)
    : Entity(CAPI.func.alt_IVehicle_to_alt_IEntity(pointer))
{
    private val vehicle: Pointer = pointer

    constructor(modelname: String, position: Float3, rotation: Float3)
            : this(hash(modelname), position, rotation)

    constructor(modelhash: UInt, position: Float3, rotation: Float3)
            : this(
            CAPI.func.alt_RefBase_RefStore_IPlayer_Get(
                    CAPI.func.alt_ICore_CreateVehicle(
                            CAPI.core, modelhash.toInt(), position.layout().ptr(), rotation.layout().ptr()
                    )
            )
    )


}
