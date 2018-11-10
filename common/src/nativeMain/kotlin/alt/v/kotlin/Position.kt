package alt.v.kotlin

import alt.v.c.alt_position_t
import kotlinx.cinterop.CValue
import kotlinx.cinterop.*

inline class Position(internal val position: CValue<alt_position_t>) {
    constructor(x: Float, y: Float, z: Float) : this(cValue<alt_position_t> {
        this.x = x
        this.y = y
        this.z = z
    })

    var x: Float
        get() = memScoped {
            return@memScoped position.getPointer(this).pointed.x
        }
        set(value) {
            memScoped {
                position.getPointer(this).pointed.x = value
            }
        }

    var y: Float
        get() = memScoped {
            return@memScoped position.getPointer(this).pointed.y
        }
        set(value) {
            memScoped {
                position.getPointer(this).pointed.y = value
            }
        }

    var z: Float
        get() = memScoped {
            return@memScoped position.getPointer(this).pointed.z
        }
        set(value) {
            memScoped {
                position.getPointer(this).pointed.z = value
            }
        }
}