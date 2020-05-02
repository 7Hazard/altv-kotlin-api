package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.hash
import hazard7.altv.kotlin.math.Float3
import hazard7.altv.kotlin.layout
import hazard7.altv.kotlin.ptr
import jnr.ffi.Pointer

class Vehicle internal constructor(pointer: Pointer)
    : Entity(CAPI.func.alt_IVehicle_to_alt_IEntity(pointer))
{
    private val vehicle: Pointer = pointer

    constructor(modelname: String, position: Float3, rotation: Float3)
            : this(hash(modelname), position, rotation)

    constructor(modelhash: UInt, position: Float3, rotation: Float3) : this(
            kotlin.run {
                val s = CAPI.alt_RefBase_RefStore_IVehicle()
                CAPI.func.alt_ICore_CreateVehicle(
                        CAPI.core, modelhash.toInt(), position.layout().ptr(), rotation.layout().ptr(), s.ptr()
                )
                s.ptr.get()
            }
    )


}
