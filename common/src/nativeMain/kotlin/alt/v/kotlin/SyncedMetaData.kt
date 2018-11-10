package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import alt.v.kotlin.MetaData.Companion.nil

inline class SyncedMetaData(val pointer: COpaquePointer) {
    operator fun get(key: String): CValue<alt_mvalue_t>? {
        val value = alt_entity_get_synced_meta_data(pointer, key)
        if (value == nil) {
            return null
        }
        return value
    }

    operator fun set(key: String, value: CValue<alt_mvalue_t>) {
        alt_entity_set_synced_meta_data(pointer, key, value)
    }

    operator fun set(key: String, value: int64_t) {
        alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_int(value))
    }

    operator fun set(key: String, value: Boolean) {
        alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_bool(value))
    }

    operator fun set(key: String, value: Double) {
        alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_double(value))
    }

    operator fun set(key: String, value: String) {
        alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_str(value))
    }

    operator fun set(key: String, value: uint64_t) {
        alt_entity_set_synced_meta_data(pointer, key, alt_mvalue_create_uint(value))
    }
}