package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.*

open class Entity internal constructor(
    internal val pointer: kotlinx.cinterop.COpaquePointer
) {
    var dimension: uint16_t
        set(value) {
            alt_entity_set_dimension(pointer, value)
        }
        get() = alt_entity_get_dimension(pointer)
    var position: CValue<alt_position_t>
        set(value) {
            alt_entity_set_position(pointer, value)
        }
        get() = alt_entity_get_position(pointer)
}