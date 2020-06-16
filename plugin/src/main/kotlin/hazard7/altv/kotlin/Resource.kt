package hazard7.altv.kotlin

import hazard7.altv.jvm.CAPI
import hazard7.altv.jvm.CAPIExtra
import hazard7.altv.jvm.StringUtil
import hazard7.altv.kotlin.events.*
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
        internal val ptrmap = HashMap<Pointer, Resource>()
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
            logInfo("[Kotlin-JVM] Set client type to '$curtype' for '$name'")

            true
        } catch (e: Throwable)
        {
            logException(e, "[Kotlin-JVM] Exception when making client resource")
            false
        }
    }

    internal val on_start = CAPIExtra.StartFn { resource ->
        ptrmap[resource] = this

        // Load jar
        val jarpath = StringView { ptr -> CAPI.func.alt_IResource_GetMain(resource, ptr) }
        val jarfile = File("resources/$name/$jarpath")
        if (!jarfile.isFile) {
            logError("[Kotlin-JVM] Could not open '${jarfile.absolutePath}'")
            return@StartFn false
        }

        try {
            val jar = JarFile(jarfile)
            val mainclass = jar.manifest.mainAttributes.getValue("Main-Class")
            if(mainclass == null) {
                logError("[Kotlin-JVM] Could not get Main-Class from manifest")
                return@StartFn false
            }

            val classLoader = URLClassLoader(
                arrayOf<URL>(jarfile.toURI().toURL()),
                this.javaClass.classLoader
            )
            val classToLoad = Class.forName(mainclass, true, classLoader)
            val method = classToLoad.getDeclaredMethod("main", Resource::class.java)
            method.invoke(null, this)

//            loadEventHandlers(classToLoad.packageName, classLoader) { method, t: OnServerEvent ->
//                logInfo("LOADING ${t.name}")
//                if (!onServerEventHandlers.containsKey(t.name))
//                    onServerEventHandlers[t.name] = hashSetOf(method)
//                else onServerEventHandlers[t.name]?.add(method)
//            }

            true
        } catch (e: Throwable) {
            logError("[Kotlin-JVM] Exception while loading resource '$name'"
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
                        for (handler in onPlayerConnectHandlers) {
                            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerConnect handler")
                            }) { handler(ev) }
                        }
                    }
                }

                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DISCONNECT -> {
                    val ev = PlayerDisconnectEvent(eventptr)
                    runBlocking {
                        for (handler in onPlayerDisconnectHandlers){
                            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDisconnect handler")
                            }) { handler(ev) }
                        }
                    }
                }

                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_DEATH -> {
                    val ev = PlayerDeathEvent(eventptr)
                    runBlocking {
                        for (handler in onPlayerDeathHandlers){
                            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                logException(throwable, "[Kotlin-JVM] Exception thrown in onPlayerDeath handler")
                            }) { handler(ev) }
                        }
                    }
                }

                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_SERVER_SCRIPT_EVENT -> {
                    val ev = ServerEvent(eventptr)
                    runBlocking {
                        val handlers = onServerEventHandlers[ev.name] ?: return@runBlocking
                        for (handler in handlers){
                            launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                                logException(throwable, "[Kotlin-JVM] Exception thrown in onServerEventHandlers handler")
                            }) {
                                handler.invoke(ev.name, *ev.getArgs(handler))
                            }
                        }
                    }
                }

                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_RESOURCE_START -> {
                    // main() invokation is enough
//                    val ev = ResourceStartEvent(eventptr)
//                    for (handler in onResourceStartHandlers)
//                    {
//                        if(ev.resource != null && ev.resource!!.name == name)
//                        {
//                            runBlocking {
//                                handler()
//                            }
//                        }
//                    }
                }

                else -> {
                    logWarning("[Kotlin-JVM] Unhandled event ${event.type.name}")
                }
            }

            !event.wasCancelled
        } catch (e: Throwable)
        {
            logException(e, "[Kotlin-JVM] Exception when invoking event handler")
            false
        }
    }

    internal val on_tick = CAPIExtra.OnResourceTickFn { resource ->
        runBlocking {
            for (handler in onTickHandlers)
                launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                    logException(throwable, "[Kotlin-JVM] Exception thrown in onTick handler")
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

    // Server event
    private val onServerEventHandlers = hashMapOf<String, HashSet<ServerEvent.Handler>>()
    fun onServerEvent(name: String, func: Function<*>) {
        val handler = ServerEvent.Handler(func)
        if (!onServerEventHandlers.containsKey(name))
            onServerEventHandlers[name] = hashSetOf(handler)
        else onServerEventHandlers[name]?.add(handler)
    }

    // Resource Start
//    private val onResourceStartHandlers = arrayListOf<() -> Unit>()
//    fun onResourceStart(f: () -> Unit) {
//        onResourceStartHandlers.add(f)
//    }

    // Resource Start
    private val onServerStartHandlers = arrayListOf<() -> Unit>()
    fun onServerStart(f: () -> Unit) {
        onServerStartHandlers.add(f)
    }

    // Tick
    private val onTickHandlers = arrayListOf<() -> Unit>()
    fun onTick(f: () -> Unit) {
        onTickHandlers.add(f)
    }
}
