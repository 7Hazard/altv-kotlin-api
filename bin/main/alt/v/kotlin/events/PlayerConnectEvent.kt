package alt.v.kotlin.events

import alt.v.jvm.CAPI
//import alt.v.kotlin.entities.Player
//import alt.v.kotlin.fromCString

//class PlayerConnectEvent internal constructor(event: Event) : Event(event) {
//    val player = Player(CAPI.func.alt_player_connect_event_get_target(ptr))
//    val reason = fromCString(CAPI.func.alt_player_connect_event_get_reason_size(ptr).toInt())
//    { bufferptr -> CAPI.func.alt_player_connect_event_get_reason(ptr, bufferptr) }
//}