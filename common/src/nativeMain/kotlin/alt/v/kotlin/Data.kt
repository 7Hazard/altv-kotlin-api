package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.CValue

interface Data {
    operator fun get(key: String): CValue<alt_mvalue_t>?

    operator fun set(key: String, value: CValue<alt_mvalue_t>)

    operator fun set(key: String, value: int64_t)

    operator fun set(key: String, value: Boolean)

    operator fun set(key: String, value: Double)

    operator fun set(key: String, value: String)

    operator fun set(key: String, value: uint64_t)
}