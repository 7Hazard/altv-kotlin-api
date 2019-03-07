package alt.v.kotlin.entities

import alt.v.kotlin.hash
import alt.v.jvm.CAPI as capi
import alt.v.kotlin.toAltPosition
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer

class Vehicle internal constructor(pointer: Pointer)
{
    val ptr: Pointer = pointer;

    constructor(modelhash: Int, position: Float3, heading: Float)
            : this(capi.func.alt_server_create_vehicle(capi.server, modelhash, toAltPosition(position), heading))
    constructor(modelname: String, position: Float3, heading: Float)
            : this(capi.func.alt_server_create_vehicle(capi.server, hash(modelname), toAltPosition(position), heading))
}