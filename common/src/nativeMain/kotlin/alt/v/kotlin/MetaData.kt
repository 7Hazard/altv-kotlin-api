package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue

inline class MetaData(val pointer: CPointer<alt_entity_t>) : Data {

    internal companion object {
        internal val nil = alt_mvalue_create_nil()
    }

    override operator fun get(key: String): CValue<alt_mvalue_t>? {
        val value = alt_entity_get_meta_data(pointer, key)
        if (value == nil) {
            return null
        }
        return value
    }

    override operator fun set(key: String, value: CValue<alt_mvalue_t>) {
        alt_entity_set_meta_data(pointer, key, value)
    }

    override operator fun set(key: String, value: int64_t) {
        alt_entity_set_meta_data(pointer, key, alt_mvalue_create_int(value))
    }

    override operator fun set(key: String, value: Boolean) {
        alt_entity_set_meta_data(pointer, key, alt_mvalue_create_bool(value))
    }

    override operator fun set(key: String, value: Double) {
        alt_entity_set_meta_data(pointer, key, alt_mvalue_create_double(value))
    }

    override operator fun set(key: String, value: String) {
        alt_entity_set_meta_data(pointer, key, alt_mvalue_create_str(value))
    }

    override operator fun set(key: String, value: uint64_t) {
        alt_entity_set_meta_data(pointer, key, alt_mvalue_create_uint(value))
    }
}