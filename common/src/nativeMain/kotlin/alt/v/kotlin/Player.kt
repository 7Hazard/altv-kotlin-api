package alt.v.kotlin

import alt.v.c.alt_player_get_name
import alt.v.c.alt_player_set_name
import kotlinx.cinterop.toKString

class Player internal constructor(pointer: kotlinx.cinterop.COpaquePointer) :
    Entity(pointer) {
    var name: String?
        get() = alt_player_get_name(pointer)?.toKString()
        set(value) {
            alt_player_set_name(pointer, value)
        }
}