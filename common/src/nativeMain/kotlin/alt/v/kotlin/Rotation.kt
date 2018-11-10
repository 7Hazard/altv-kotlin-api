package alt.v.kotlin

import alt.v.c.alt_rotation_t
import kotlinx.cinterop.CValue
import kotlinx.cinterop.*

inline class Rotation(internal val rotation: CValue<alt_rotation_t>) {
    constructor(pitch: Float, roll: Float, yaw: Float) : this(cValue<alt_rotation_t> {
        this.pitch = pitch
        this.roll = roll
        this.yaw = yaw
    })

    var pitch: Float
        get() = memScoped {
            return@memScoped rotation.getPointer(this).pointed.pitch
        }
        set(value) {
            memScoped {
                rotation.getPointer(this).pointed.pitch = value
            }
        }

    var roll: Float
        get() = memScoped {
            return@memScoped rotation.getPointer(this).pointed.roll
        }
        set(value) {
            memScoped {
                rotation.getPointer(this).pointed.roll = value
            }
        }

    var yaw: Float
        get() = memScoped {
            return@memScoped rotation.getPointer(this).pointed.yaw
        }
        set(value) {
            memScoped {
                rotation.getPointer(this).pointed.yaw = value
            }
        }
}