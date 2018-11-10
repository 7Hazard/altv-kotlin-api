package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

object Events {

    private var playerConnectCallback: KFunction1<Player, Boolean>? = null

    private var playerDamageCallback: KFunction2<Player, COpaquePointer?, Boolean>? = null

    fun subscribe(event: alt_event_type_t, callback: alt_event_callback_t) {
        alt_server_subscribe_event(event, callback)
    }

    fun subscribePlayerConnnect(callback: KFunction1<Player, Boolean>) {
        playerConnectCallback = callback
        subscribe(EVENT_PLAYER_CONNECT, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val playerPointer = alt_player_connect_event_get_target(event) ?: return@staticCFunction false
            val player = Player(playerPointer)
            return@staticCFunction playerConnectCallback?.invoke(player) ?: false
        })
    }

    fun subscribePlayerDamage(callback: KFunction2<Player, COpaquePointer?, Boolean>?) {
        playerDamageCallback = callback
        subscribe(EVENT_PLAYER_DAMAGE, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val playerPointer = alt_player_damage_event_get_target(event) ?: return@staticCFunction false
            val attacker = alt_player_damage_event_get_attacker(event)
            val player = Player(playerPointer)
            return@staticCFunction playerDamageCallback?.invoke(player, attacker) ?: false
        })
    }
}