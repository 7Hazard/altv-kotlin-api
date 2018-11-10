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

        open operator fun set(key: String, value: int64_t) {
            alt_entity_set_meta_data(pointer, key, alt_mvalue_create_int(value))
        }

        open operator fun set(key: String, value: Boolean) {
            alt_entity_set_meta_data(pointer, key, alt_mvalue_create_bool(value))
        }

        open operator fun set(key: String, value: Double) {
            alt_entity_set_meta_data(pointer, key, alt_mvalue_create_double(value))
        }

        open operator fun set(key: String, value: String) {
            alt_entity_set_meta_data(pointer, key, alt_mvalue_create_str(value))
        }

        open operator fun set(key: String, value: uint64_t) {
            alt_entity_set_meta_data(pointer, key, alt_mvalue_create_uint(value))
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

        override operator fun set(key: String, value: int64_t) {
            alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_int(value))
        }

        override operator fun set(key: String, value: Boolean) {
            alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_bool(value))
        }

        override operator fun set(key: String, value: Double) {
            alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_double(value))
        }

        override operator fun set(key: String, value: String) {
            alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_str(value))
        }

        override operator fun set(key: String, value: uint64_t) {
            alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_uint(value))
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
    var rotation: Rotation
        set(value) {
            alt_entity_set_rotation(pointer, value.rotation)
        }
        get() = Rotation(alt_entity_get_rotation(pointer))
    val data = Data(pointer)
    val syncedData = SyncedData(pointer)
}