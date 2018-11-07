package alt.v.kotlin

import alt.v.c.alt_position_t
import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue
import kotlinx.cinterop.*

inline class Position(internal val position: alt_position_t) {
    constructor(x: Float, y: Float, z: Float) : this(nativeHeap.alloc<alt_position_t>().apply {
        this.x = x
        this.y = y
        this.z = z
    })

    var x: Float
        set(value) {
            position.x = value
        }
        get() = position.x
    var y: Float
        set(value) {
            position.y = value
        }
        get() = position.y
    var z: Float
        set(value) {
            position.z = value
        }
        get() = position.z
}