package alt.v.kotlin.events

import alt.v.jvm.CAPI
import alt.v.kotlin.Log
import alt.v.kotlin.Resource
import jnr.ffi.Pointer
import java.lang.Exception
import java.lang.NullPointerException
import kotlin.reflect.KClass

//open class Event internal constructor(instance: Pointer) {
//    internal val ptr = instance
//
//    constructor(event: Event) : this(event.ptr)
//
//    private val type: CAPI.alt_event_type_t
//        get() = CAPI.func.alt_event_get_type(ptr)
//
//
//    // Event subscriptions
//    companion object {
//        init {
//            // Tick
//            CAPI.func.alt_server_subscribe_tick(CAPI.server) { for (f in onTickCallbacks) f() }
//        }
//
//        private val onPlayerConnectHandlers = subscribe(CAPI.alt_event_type_t.PLAYER_CONNECT)
//        { event -> PlayerConnectEvent(event)}
//        fun onPlayerConnect(f: (PlayerConnectEvent) -> Boolean) { onPlayerConnectHandlers.add(f) }
//
//        private val onPlayerDisconnectHandlers = subscribe(CAPI.alt_event_type_t.PLAYER_DISCONNECT)
//        { event -> PlayerDisconnectEvent(event)}
//        fun onPlayerDisconnect(f: (PlayerDisconnectEvent) -> Boolean) { onPlayerDisconnectHandlers.add(f) }
//
//        private val onTickCallbacks = mutableListOf<() -> Unit>()
//        fun onTick(f: () -> Unit) { onTickCallbacks.add(f) }
//
//        // Utils
//        private fun <EventClass : Event> subscribe(type: CAPI.alt_event_type_t, factory: (Event) -> EventClass): MutableList<(EventClass) -> Boolean>
//        {
//            val handlers = mutableListOf<(EventClass) -> Boolean>()
//
//            CAPI.func.alt_server_subscribe_event(
//                    CAPI.server,
//                    type,
//                    fun(eventptr: Pointer): Boolean {
//                        val event = Event(eventptr)
//
//                        for (f in handlers)
//                        {
//                            try {
//                                if(!f(factory(event))) return false
//                            } catch (e: Exception)
//                            {
//                                Log.error("[Kotlin-JVM] Exception thrown when executing event handler:" +
//                                        "\n\t${e.localizedMessage}" +
//                                        "\n\t${e.cause}" +
//                                        "\n\t${e.stackTrace}")
//                            }
//                        }
//
//                        return true
//                    }
//            )
//
//            return handlers
//        }
//    }
//}

open class Event internal constructor(pointer: Pointer) {
    companion object {
        internal fun useMemory(pointer: Pointer): CAPI.alt_CEvent
        {
            val s = CAPI.alt_CEvent()
            s.useMemory(pointer)
            return s
        }
    }

    internal val pointer = pointer
    private val struct = useMemory(pointer)
//    private val struct = StructUtil.useMemory<CAPI.alt_CEvent>(pointer)

    internal val capiType get() = struct.type.get()
}
