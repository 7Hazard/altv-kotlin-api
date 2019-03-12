package alt.v.kotlin.entities

import alt.v.kotlin.hash
import alt.v.kotlin.math.Float3
import alt.v.kotlin.toAltPosition
import jnr.ffi.Pointer
import alt.v.jvm.CAPI

class Vehicle internal constructor(pointer: Pointer)
{
    val ptr: Pointer = pointer;

    constructor(modelname: String, position: Float3, heading: Float)
            : this(hash(modelname), position, heading)

    constructor(modelhash: Int, position: Float3, heading: Float)
            : this(CAPI.func.alt_server_create_vehicle(CAPI.server, modelhash, toAltPosition(position), heading))
}