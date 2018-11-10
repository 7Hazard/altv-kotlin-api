package alt.v.kotlin

import alt.v.c.*

open class Entity internal constructor(
    internal val pointer: kotlinx.cinterop.COpaquePointer
) {
    var dimension: uint16_t
        set(value) {
            alt_entity_set_dimension(pointer, value)
        }
        get() = alt_entity_get_dimension(pointer)
    var position: Position
        set(value) {
            alt_entity_set_position(pointer, value.position)
        }
        get() = Position(alt_entity_get_position(pointer))
    var rotation: Rotation
        set(value) {
            alt_entity_set_rotation(pointer, value.rotation)
        }
        get() = Rotation(alt_entity_get_rotation(pointer))
    val data: Data
        get() = MetaData(pointer)
    val syncedData: Data
        get() = SyncedMetaData(pointer)
}