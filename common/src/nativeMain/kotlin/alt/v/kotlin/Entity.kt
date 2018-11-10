package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.*

open class Entity internal constructor(
    internal val pointer: kotlinx.cinterop.COpaquePointer
) {
    open class Data(val pointer: COpaquePointer) {

        internal companion object {
            internal val nil = alt_mvalue_create_nil()
        }

        open operator fun get(key: String): CValue<alt_mvalue_t>? {
            val value = alt_entity_get_meta_data(pointer, key)
            if (value == nil) {
                return null
            }
            return value
        }

        open operator fun set(key: String, value: CValue<alt_mvalue_t>) {
            alt_entity_set_meta_data(pointer, key, value)
        }
    }

    class SyncedData(pointer: COpaquePointer) : Data(pointer) {
        override operator fun get(key: String): CValue<alt_mvalue_t>? {
            val value = alt_entity_get_synced_meta_data(pointer, key)
            if (value == nil) {
                return null
            }
            return value
        }

        override operator fun set(key: String, value: CValue<alt_mvalue_t>) {
            alt_entity_set_synced_meta_data(pointer, key, value)
        }
    }

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
    val data = Data(pointer)
    val syncedData = SyncedData(pointer)
}