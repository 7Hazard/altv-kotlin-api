package alt.v.kotlin

import alt.v.c.*

object Events {
    private fun subscribe(event: alt_event_type_t, callback: alt_event_callback_t) {
        alt_server_subscribe_event(event, callback)
    }

    /*fun subscribe(event: alt_event_type_t, callback: KFunction1<COpaquePointer, Unit>) {
        subscribe(event, staticCFunction(callback) as alt_event_callback_t)
    }

    fun subscribe(callback: KFunction1<Player, Unit>) {
        subscribe(EVENT_PLAYER_CONNECT, staticCFunction<COpaquePointer, Unit> { pointer ->
            val playerPointer = alt_player_connect_event_get_target(pointer) ?: return@staticCFunction
            val player = Player(playerPointer)
            callback(player)
        } as alt_event_callback_t)
    }*/
}