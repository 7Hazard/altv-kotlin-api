package alt.v.kotlin

import alt.v.c.alt_position_t

import kotlinx.cinterop.nativeHeap.alloc

object Position {
    fun create(x: Float, y: Float, z: Float): alt_position_t {
        val position = alloc(alt_position_t.size, alt_position_t.align) as alt_position_t
        position.x = x
        position.y = y
        position.z = z
        return position
    }
}