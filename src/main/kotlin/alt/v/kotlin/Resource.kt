package alt.v.kotlin

import alt.v.jvm.CAPI
import jnr.ffi.Pointer
import jnr.ffi.Struct
import alt.v.kotlin.events.Event
import alt.v.kotlin.events.PlayerConnectEvent
import alt.v.kotlin.events.PlayerDisconnectEvent
import java.io.File
import java.net.URLClassLoader
import java.net.URL


internal val resources = mutableMapOf<String, Resource>()

class Resource {
    internal var ptr: Pointer? = null
    internal var loaded = false

    //internal constructor(resourceptr: Pointer) { ptr = resourceptr }
    internal constructor(info: CAPI.alt_resource_creation_info_t)
    {
        ptr = CAPI.func.alt_script_runtime_create_resource(
                Struct.getMemory(info),
                on_event,
                on_tick
        )

        // Load jar
        val name = info.name.get()
        val main = info.main.get()

        val jarfile = File("resources/$name/$name.jar")
        if (!jarfile.isFile) {
            Log.error("[Kotlin-JVM] Could not open '${jarfile.absolutePath}'")
            return
        }

        try {
            val child = URLClassLoader(
                    arrayOf<URL>(jarfile.toURI().toURL()),
                    this.javaClass.classLoader
            )
            val classToLoad = Class.forName(main, true, child)
            val method = classToLoad.getDeclaredMethod("main", Resource::class.java)
            method.invoke(null, this)
        } catch (e: Exception) {
            Log.error("[Kotlin-JVM] Exception while loading resource '$name'"
                    + "\n\t Message: " + e.localizedMessage
                    + "\n\t Cause: " + e.cause
                    + "\n\t Ext: " + e.toString()
                    //+"\n\tStack trace: "
            )
            e.printStackTrace()
        }

        resources[info.name.get()] = this
        loaded = true
    }

    val onPlayerConnectHandlers = mutableListOf<(PlayerConnectEvent) -> Boolean>()
    fun onPlayerConnect(f: (PlayerConnectEvent) -> Boolean) { onPlayerConnectHandlers.add(f) }

    val onPlayerDisconnectHandlers = mutableListOf<(PlayerDisconnectEvent) -> Boolean>()
    fun onPlayerDisconnect(f: (PlayerDisconnectEvent) -> Boolean) { onPlayerDisconnectHandlers.add(f) }

    private val on_event = CAPI.alt_resource_on_event_callback_t(fun(ptr: Pointer): Boolean {
        val event = Event(ptr)

        when (event.type) {
            CAPI.alt_event_type_t.PLAYER_CONNECT -> {
                for (f in onPlayerConnectHandlers) if(!f(PlayerConnectEvent(event))) return false
            }
            CAPI.alt_event_type_t.PLAYER_DISCONNECT -> {
                for (f in onPlayerDisconnectHandlers) if(!f(PlayerDisconnectEvent(event))) return false
            }
        }

        return true
    })

    private val onTickCallbacks = mutableListOf<() -> Unit>()
    fun onTick(f: () -> Unit) { onTickCallbacks.add(f) }
    private val on_tick = CAPI.alt_resource_on_tick_callback_t { for (f in onTickCallbacks) f() }
}