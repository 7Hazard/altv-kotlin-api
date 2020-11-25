package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.hash
import hazard7.altv.kotlin.layout
import hazard7.altv.kotlin.math.Float3
import hazard7.altv.kotlin.nextTick
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer
import kotlinx.coroutines.runBlocking

open class Vehicle internal constructor(pointer: Pointer) : Entity(CAPI.func.alt_IVehicle_to_alt_IEntity(pointer)) {
    internal val vehicle = pointer

    constructor(modelname: String, position: Float3, rotation: Float3)
            : this(hash(modelname), position, rotation)

    constructor(modelhash: Int, position: Float3, rotation: Float3) : this(
        kotlin.run {
            val s = CAPI.alt_RefBase_RefStore_IVehicle()
            CAPI.func.alt_ICore_CreateVehicle(
                CAPI.core, modelhash.toInt(), position.layout().pointer, rotation.layout().pointer, s.pointer
            )
            val p = s.ptr.get()
            if (p == null)
                throw RuntimeException("Could not create vehicle")
            p
        }
    )

    fun setBodyHealth(value: UInt) = nextTick {
        CAPI.func.alt_IVehicle_SetBodyHealth(vehicle, value.toInt())
    }
    var bodyHealth: UInt
        get() = CAPI.func.alt_IVehicle_GetBodyHealth(vehicle).toUInt()
        set(value) = runBlocking { setBodyHealth(value).await() }
}
