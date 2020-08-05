package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.logException
import hazard7.altv.kotlin.logInfo
import hazard7.altv.kotlin.logWarning
import jnr.ffi.Pointer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class Event internal constructor(pointer: Pointer) {
    internal val thread = Thread.currentThread()
    internal val type = CAPI.func.alt_CEvent_GetType(pointer)
    val wasCancelled = CAPI.func.alt_CEvent_WasCancelled(pointer)

    companion object {
        internal val handler = CAPI.alt_ICore_SubscribeEvent_cb_Callback { cevent, data ->
            var event = Event(cevent)

            when (event.type)
            {
                // Player
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_CONNECT -> {event = PlayerConnectEvent(cevent)}
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DISCONNECT -> {event = PlayerDisconnectEvent(cevent)}
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DEATH -> {event = PlayerDiedEvent(cevent)}
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DAMAGE -> {event = PlayerRecievedDamageEvent(cevent)}
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_ENTER_VEHICLE -> {event = PlayerEnteredVehicleEvent(cevent)}
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_LEAVE_VEHICLE -> {event = PlayerLeftVehicleEvent(cevent)}

                // Misc
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_SERVER_SCRIPT_EVENT -> {event = ServerEvent(cevent)}

                // Skipped
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_RESOURCE_START -> {return@alt_ICore_SubscribeEvent_cb_Callback true}

                else -> {
                    logWarning("[Kotlin-JVM] Internally unhandled event ${event.type.name}")
                    return@alt_ICore_SubscribeEvent_cb_Callback true
                }
            }

            for ((ptr, resource) in Resource.ptrmap) {
                when (event.type)
                {
                    CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_CONNECT -> {
                        runBlocking {
                            for (handler in resource.onPlayerConnectHandlers) {
                                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                    logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerConnect handler")
                                }) { handler(event as PlayerConnectEvent) }
                            }
                        }
                    }

                    CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DISCONNECT -> {
                        runBlocking {
                            for (handler in resource.onPlayerDisconnectHandlers){
                                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                    logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDisconnect handler")
                                }) { handler(event as PlayerDisconnectEvent) }
                            }
                        }
                    }

                    CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DEATH -> {
                        runBlocking {
                            for (handler in resource.onPlayerDiedHandlers){
                                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                    logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDeath handler")
                                }) { handler(event as PlayerDiedEvent) }
                            }
                        }
                    }

                    CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DAMAGE -> {
                        runBlocking {
                            for (handler in resource.onPlayerRecievedDamageHandlers){
                                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                    logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDeath handler")
                                }) { handler(event as PlayerRecievedDamageEvent) }
                            }
                        }
                    }

                    CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_ENTER_VEHICLE -> {
                        runBlocking {
                            for (handler in resource.onPlayerEnteredVehicleHandlers){
                                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                    logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDeath handler")
                                }) { handler(event as PlayerEnteredVehicleEvent) }
                            }
                        }
                    }

                    CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_LEAVE_VEHICLE -> {
                        runBlocking {
                            for (handler in resource.onPlayerLeftVehicleHandlers){
                                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                    logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDeath handler")
                                }) { handler(event as PlayerLeftVehicleEvent) }
                            }
                        }
                    }

                    CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_SERVER_SCRIPT_EVENT -> {
                        runBlocking {
                            event as ServerEvent
                            val handlers = resource.onServerEventHandlers[event.name] ?: return@runBlocking
                            for (handler in handlers){
                                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                    logException(throwable, "[Kotlin-JVM] Exception thrown in onServerEventHandlers handler")
                                }) {
                                    handler.invoke(event.name, *event.getArgs(handler))
                                }
                            }
                        }
                    }
                }
            }

            !event.wasCancelled
        }
    }
}
