package hazard7.altv.kotlin

import alt.v.jvm.*
import hazard7.altv.kotlin.events.Event
import hazard7.altv.kotlin.events.PlayerConnectEvent
import hazard7.altv.kotlin.events.PlayerDeathEvent
import hazard7.altv.kotlin.events.PlayerDisconnectEvent
import jnr.ffi.Pointer
import jnr.ffi.Struct
import java.io.File
import java.net.URLClassLoader
import java.net.URL
import kotlinx.coroutines.*

class Resource {
    companion object {
        internal val ptrmap = HashMap<Pointer?, Resource>()
    }

    // js as client type by default
    var clientType = "js"

    internal val on_make_client = CAPIExtra.MakeClientFn { resource, info, files ->
        try {
            val sinfo = CAPI.alt_IResource_CreationInfo(info)
            CAPI.func.alt_String_Resize(Struct.getMemory(sinfo.type), clientType.length.toLong())
            sinfo.type.data.get().putString(0, clientType, clientType.length, StringUtil.UTF8)
            val curtype = CAPI.func.alt_String_CStr(Struct.getMemory(sinfo.type))
            Log.info("[Kotlin-JVM] Set client type to '$curtype'")

            true
        } catch (e: Exception)
        {
            Log.exception(e, "[Kotlin-JVM] Exception when making client resource")
            false
        }
    }

    internal val on_start = CAPIExtra.StartFn { resource ->
        ptrmap[resource] = this

        // Load jar
        val name = StringView { ptr -> CAPI.func.alt_IResource_GetName(resource, ptr) }
        val main = StringView { ptr -> CAPI.func.alt_IResource_GetMain(resource, ptr) }.split(':')
        val jarpath = main[0]
        val mainclass = main[1]

        val jarfile = File("resources/$name/$jarpath")
        if (!jarfile.isFile) {
            Log.error("[Kotlin-JVM] Could not open '${jarfile.absolutePath}'")
            false
        }
        else {
            try {
                val child = URLClassLoader(
                        arrayOf<URL>(jarfile.toURI().toURL()),
                        this.javaClass.classLoader
                )
                val classToLoad = Class.forName(mainclass, true, child)
                val method = classToLoad.getDeclaredMethod("main", Resource::class.java)
                method.invoke(null, this)

                true
            } catch (e: Exception) {
                Log.error("[Kotlin-JVM] Exception while loading resource '$name'"
                        + "\n\t Message: " + e.localizedMessage
                        + "\n\t Cause: " + e.cause
                        + "\n\t Ext: " + e.toString()
                        //+"\n\tStack trace: "
                )
                e.printStackTrace()

                false
            }
        }
    }

    internal val on_stop = CAPIExtra.StopFn { resource ->
        ptrmap.remove(resource)
        true
    }

    internal val on_event = CAPIExtra.OnEventFn { resourceptr, eventptr ->
        try {
            val event = Event(eventptr)

            when (event.capiType)
            {
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_CONNECT -> {
                    val ev = PlayerConnectEvent(eventptr)
                    runBlocking {
                        for (handler in onPlayerConnectHandlers)
                            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                Log.exception(throwable, "[Kotlin-JVM] Exception thrown in onPlayerConnect handler")
                            }) { handler(ev) }
                    }
                }

                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DISCONNECT -> {
                    val ev = PlayerDisconnectEvent(eventptr)
                    runBlocking {
                        for (handler in onPlayerDisconnectHandlers)
                            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                Log.exception(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDisconnect handler")
                            }) { handler(ev) }
                    }
                }

                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DEATH -> {
                    val ev = PlayerDeathEvent(eventptr)
                    runBlocking {
                        for (handler in onPlayerDeathHandlers)
                            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                Log.exception(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDeath handler")
                            }) { handler(ev) }
                    }
                }
            }

            !event.wasCancelled
        } catch (e: Throwable)
        {
            Log.exception(e, "[Kotlin-JVM] Exception when invoking event handler")
            false
        }
    }

    internal val on_tick = CAPIExtra.OnResourceTickFn { resource ->
        runBlocking {
            for (handler in onTickHandlers)
                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                    Log.exception(throwable, "[Kotlin-JVM] Exception thrown in onTick handler")
                }) { handler() }
        }
    }

    internal val on_create_base_object = CAPIExtra.OnCreateBaseObjectFn { resource, refobj ->

    }

    internal val on_remove_base_object = CAPIExtra.OnRemoveBaseObjectFn { resource, refobj ->

    }

    private val pointer: Pointer
    constructor(pointer: Pointer)
    {
        this.pointer = pointer
    }


    ////// Events //////
    // Player Connect
    private val onPlayerConnectHandlers = arrayListOf<(PlayerConnectEvent) -> Boolean>()
    fun onPlayerConnect(f: (PlayerConnectEvent) -> Boolean) {
        onPlayerConnectHandlers.add(f)
    }

    // Player Disconnect
    private val onPlayerDisconnectHandlers = arrayListOf<(PlayerDisconnectEvent) -> Boolean>()
    fun onPlayerDisconnect(f: (PlayerDisconnectEvent) -> Boolean) {
        onPlayerDisconnectHandlers.add(f)
    }

    // Player Death
    private val onPlayerDeathHandlers = arrayListOf<(PlayerDeathEvent) -> Boolean>()
    fun onPlayerDeath(f: (PlayerDeathEvent) -> Boolean) {
        onPlayerDeathHandlers.add(f)
    }

    // Tick
    private val onTickHandlers = arrayListOf<() -> Unit>()
    fun onTick(f: () -> Unit) {
        onTickHandlers.add(f)
    }
}
