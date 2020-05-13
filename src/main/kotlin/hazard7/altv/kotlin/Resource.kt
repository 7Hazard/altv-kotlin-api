package hazard7.altv.kotlin

import hazard7.altv.jvm.CAPI
import hazard7.altv.jvm.CAPIExtra
import hazard7.altv.jvm.StringUtil
import hazard7.altv.kotlin.events.Event
import hazard7.altv.kotlin.events.PlayerConnectEvent
import hazard7.altv.kotlin.events.PlayerDeathEvent
import hazard7.altv.kotlin.events.PlayerDisconnectEvent
import jnr.ffi.Pointer
import jnr.ffi.Struct
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile


class Resource internal constructor(resourceptr: Pointer) {
    companion object {
        internal val ptrmap = HashMap<Pointer?, Resource>()
    }

//    private val resourceptr = pointer // not needed
    val name = StringView { ptr -> CAPI.func.alt_IResource_GetName(resourceptr, ptr) }
    // js as client type by default
    var clientType = "js"

    internal val on_make_client = CAPIExtra.MakeClientFn { resource, info, files ->
        try {
            val sinfo = CAPI.alt_IResource_CreationInfo(info)
            CAPI.func.alt_String_Resize(Struct.getMemory(sinfo.type), clientType.length.toLong())
            sinfo.type.data.get().putString(0, clientType, clientType.length, StringUtil.UTF8)
            val curtype = CAPI.func.alt_String_CStr(Struct.getMemory(sinfo.type))
            Log.info("[Kotlin-JVM] Set client type to '$curtype' for '$name'")

            true
        } catch (e: Throwable)
        {
            Log.exception(e, "[Kotlin-JVM] Exception when making client resource")
            false
        }
    }

    internal val on_start = CAPIExtra.StartFn { resource ->
        ptrmap[resource] = this

        // Load jar
        val jarpath = StringView { ptr -> CAPI.func.alt_IResource_GetMain(resource, ptr) }
        val jarfile = File("resources/$name/$jarpath")
        if (!jarfile.isFile) {
            Log.error("[Kotlin-JVM] Could not open '${jarfile.absolutePath}'")
            return@StartFn false
        }

        try {
            val jar = JarFile(jarfile)
            val mainclass = jar.manifest.mainAttributes.getValue("Main-Class")
            if(mainclass == null) {
                Log.error("[Kotlin-JVM] Could not get Main-Class from manifest")
                return@StartFn false
            }

            val child = URLClassLoader(
                arrayOf<URL>(jarfile.toURI().toURL()),
                this.javaClass.classLoader
            )
            val classToLoad = Class.forName(mainclass, true, child)
            val method = classToLoad.getDeclaredMethod("main", Resource::class.java)
            method.invoke(null, this)

            true
        } catch (e: Throwable) {
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

    internal val on_stop = CAPIExtra.StopFn { resource ->
        ptrmap.remove(resource)
        true
    }

    internal val on_event = CAPIExtra.OnEventFn { resourceptr, eventptr ->
        try {
            val event = Event(eventptr)

            when (event.type)
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

                else -> {
                    Log.warning("[Kotlin-JVM] Unhandled event ${event.type.name}")
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
