package alt.v.kotlin

import alt.v.c.alt_position_t
import kotlinx.cinterop.CValue
import kotlinx.cinterop.cValue

object Position {
    fun create(x: Float, y: Float, z: Float): CValue<alt_position_t> {
        return cValue {
            this.x = x
            this.y = y
            this.z = z
        }
    }
}