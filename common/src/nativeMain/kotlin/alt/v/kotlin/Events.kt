package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.*
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

    private val serverEvents = mutableMapOf<String, ServerEventCallback>()

    private class ServerEventCallback(val function: KFunction<*>, vararg val arguments: KClass<*>) {
        fun call(args: alt_mvalue_array_t) {
            val argsSize = args.size.toInt()
            if (argsSize != arguments.size) return
            val data = args.data?.reinterpret<alt_mvalue_t>() ?: return
            val verifiedArguments = arrayOfNulls<Any>(argsSize)
            arguments.forEachIndexed { index, kClass ->
                when (kClass) {
                    Player::class -> {
                        val value = data[index]

                        /*val entityId = args.data[index] as? uint16_t?
                        if (entityId != null) {
                            val entity = alt_server_get_entity_by_id(entityId)
                            if (entity != null) {
                                val entityType = alt_entity_get_type(entity)
                                if (entityType == ENTITY_PLAYER) {
                                    verifiedArguments[index] = Player(entity)
                                }
                            }
                        }*/
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun invoke(args: Array<Any>) {
            when (function) {
                is Function0<*> -> function.invoke()
                is Function1<*, *> -> (function as? Function1<Any, *>?)?.invoke(args[0])
                is Function2<*, *, *> -> (function as? Function2<Any, Any, *>?)?.invoke(args[0], args[1])
                is Function3<*, *, *, *> -> (function as? Function3<Any, Any, Any, *>?)?.invoke(
                    args[0],
                    args[1],
                    args[2]
                )
                is Function4<*, *, *, *, *> -> (function as? Function4<Any, Any, Any, Any, *>?)?.invoke(
                    args[0],
                    args[1],
                    args[2],
                    args[3]
                )
                is Function5<*, *, *, *, *, *> -> (function as? Function5<Any, Any, Any, Any, Any, *>?)?.invoke(
                    args[0],
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                )
                is Function6<*, *, *, *, *, *, *> -> (function as? Function6<Any, Any, Any, Any, Any, Any, *>?)?.invoke(
                    args[0],
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5]
                )
                is Function7<*, *, *, *, *, *, *, *> -> (function as? Function7<Any, Any, Any, Any, Any, Any, Any, *>?)?.invoke(
                    args[0],
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    args[6]
                )
            }
        }
    }

    init {
    }

    fun subscribe(event: alt_event_type_t, callback: alt_event_callback_t) {
        alt_server_subscribe_event(event, callback)
    }

    fun subscribePlayerConnnect(callback: PlayerConnectEvent) {
        playerConnectCallback = callback
        subscribe(EVENT_PLAYER_CONNECT, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val connectEvent = alt_event_to_player_connect_event(event) ?: return@staticCFunction false
            val playerPointer = alt_player_connect_event_get_target(connectEvent)
            val player = playerPointer?.let { alt_player_to_entity(it)?.let { entity -> Player(entity) } }
            return@staticCFunction playerConnectCallback?.invoke(player) ?: false
        })
    }

    fun subscribePlayerDamage(callback: WeaponEvent) {
        playerDamageCallback = callback
        subscribe(EVENT_PLAYER_DAMAGE, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val damageEvent = alt_event_to_player_damage_event(event) ?: return@staticCFunction false
            val playerPointer = alt_player_damage_event_get_target(damageEvent)
            val attacker = alt_player_damage_event_get_attacker(damageEvent)
            val weapon = alt_player_damage_event_get_weapon(damageEvent)
            val damage = alt_player_damage_event_get_damage(damageEvent)
            val player = playerPointer?.let { alt_player_to_entity(it)?.let { entity -> Player(entity) } }
            return@staticCFunction playerDamageCallback?.invoke(player, attacker, weapon, damage) ?: false
        })
    }

    fun subscribePlayerDeath(callback: PlayerDeathEvent) {
        playerDeathCallback = callback
        subscribe(EVENT_PLAYER_DEAD, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val deathEvent = alt_event_to_player_dead_event(event) ?: return@staticCFunction false
            val playerPointer = alt_player_dead_event_get_target(deathEvent)
            val player = playerPointer?.let { alt_player_to_entity(it)?.let { entity -> Player(entity) } }
            val killer = alt_player_dead_event_get_killer(deathEvent)
            val weapon = alt_player_dead_event_get_weapon(deathEvent)
            return@staticCFunction playerDeathCallback?.invoke(player, killer, weapon) ?: false
        })
    }

    fun subscribePlayerDisconnnect(callback: PlayerDisconnectEvent) {
        playerDisconnectCallback = callback
        subscribe(EVENT_PLAYER_DISCONNECT, staticCFunction { event ->
            if (event == null) return@staticCFunction false
            val disconnectEvent = alt_event_to_player_disconnect_event(event) ?: return@staticCFunction false
            val playerPointer = alt_player_disconnect_event_get_target(disconnectEvent)
            val reason = alt_player_disconnect_event_get_reason(disconnectEvent)?.toKString()
            val player = playerPointer?.let { alt_player_to_entity(it)?.let { entity -> Player(entity) } }
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

    inline fun <reified A> registerServerEvent(function: KFunction1<A, Boolean>) {
        registerServerEvent(function, A::class)
    }

    inline fun <reified A, reified B> registerServerEvent(function: KFunction2<A, B, Boolean>) {
        registerServerEvent(function, A::class, B::class)
    }

    inline fun <reified A, reified B, reified C> registerServerEvent(function: KFunction3<A, B, C, Boolean>) {
        registerServerEvent(function, A::class, B::class, C::class)
    }

    inline fun <reified A, reified B, reified C, reified D> registerServerEvent(function: KFunction4<A, B, C, D, Boolean>) {
        registerServerEvent(function, A::class, B::class, C::class, D::class)
    }

    inline fun <reified A, reified B, reified C, reified D, reified E> registerServerEvent(function: KFunction5<A, B, C, D, E, Boolean>) {
        registerServerEvent(function, A::class, B::class, C::class, D::class, E::class)
    }

    inline fun <reified A, reified B, reified C, reified D, reified E, reified F> registerServerEvent(function: KFunction6<A, B, C, D, E, F, Boolean>) {
        registerServerEvent(function, A::class, B::class, C::class, D::class, E::class, F::class)
    }

    inline fun <reified A, reified B, reified C, reified D, reified E, reified F, reified G> registerServerEvent(
        function: KFunction7<A, B, C, D, E, F, G, Boolean>
    ) {
        registerServerEvent(function, A::class, B::class, C::class, D::class, E::class, F::class, G::class)
    }

    fun registerServerEvent(function: KFunction<*>, vararg argumentTypes: KClass<*>) {
        serverEvents[function.name] = ServerEventCallback(function, *argumentTypes)
    }
}