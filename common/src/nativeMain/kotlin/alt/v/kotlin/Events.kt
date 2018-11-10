package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import kotlin.reflect.*

// Player, Boolean
typealias PlayerConnectEvent = KFunction1<Player?, Boolean>

// Target, Attacker, Weapon, Damage
typealias WeaponEvent = KFunction4<Player?, COpaquePointer?, uint32_t, uint8_t, Boolean>

// Target, Killer, Weapon
typealias PlayerDeathEvent = KFunction3<Player?, COpaquePointer?, uint32_t, Boolean>

// Player, Reason, Boolean
typealias PlayerDisconnectEvent = KFunction2<Player?, String?, Boolean>

// None, Boolean
typealias RemoveEntityEvent = KFunction0<Boolean>

object Events {

    private var playerConnectCallback: PlayerConnectEvent? = null

    private var playerDamageCallback: WeaponEvent? = null

    private var playerDeathCallback: PlayerDeathEvent? = null

    private var playerDisconnectCallback: PlayerDisconnectEvent? = null

    private var removeEntityCallback: RemoveEntityEvent? = null

    fun subscribe(event: alt_event_type_t, callback: alt_event_callback_t) {
        alt_server_subscribe_event(event, callback)
    }

    fun subscribePlayerConnnect(callback: PlayerConnectEvent) {
        playerConnectCallback = callback
        subscribe(EVENT_PLAYER_CONNECT, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val playerPointer = alt_player_connect_event_get_target(event)
            val player = playerPointer?.let { Player(it) }
            return@staticCFunction playerConnectCallback?.invoke(player) ?: false
        })
    }

    fun subscribePlayerDamage(callback: WeaponEvent) {
        playerDamageCallback = callback
        subscribe(EVENT_PLAYER_DAMAGE, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val playerPointer = alt_player_damage_event_get_target(event)
            val attacker = alt_player_damage_event_get_attacker(event)
            val weapon = alt_player_damage_event_get_weapon(event)
            val damage = alt_player_damage_event_get_damage(event)
            val player = playerPointer?.let { Player(it) }
            return@staticCFunction playerDamageCallback?.invoke(player, attacker, weapon, damage) ?: false
        })
    }

    fun subscribePlayerDeath(callback: PlayerDeathEvent) {
        playerDeathCallback = callback
        subscribe(EVENT_PLAYER_DEAD, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val playerPointer = alt_player_dead_event_get_target(event)
            val player = playerPointer?.let { Player(it) }
            val killer = alt_player_dead_event_get_killer(event)
            val weapon = alt_player_dead_event_get_weapon(event)
            return@staticCFunction playerDeathCallback?.invoke(player, killer, weapon) ?: false
        })
    }

    fun subscribePlayerDisconnnect(callback: PlayerDisconnectEvent) {
        playerDisconnectCallback = callback
        subscribe(EVENT_PLAYER_DISCONNECT, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val playerPointer = alt_player_disconnect_event_get_target(event)
            val reason = alt_player_disconnect_event_get_reason(event)?.toKString()
            val player = playerPointer?.let { Player(it) }
            return@staticCFunction playerDisconnectCallback?.invoke(player, reason) ?: false
        })
    }

    fun subscribeRemoveEntityEvent(callback: RemoveEntityEvent) {
        removeEntityCallback = callback
        subscribe(EVENT_REMOVE_ENTITY_EVENT, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            return@staticCFunction removeEntityCallback?.invoke() ?: false
        })
    }
}