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
import kotlin.reflect.KFunction


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
        } catch (e: Throwable) {
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
            if (mainclass == null) {
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
            logError(
                "[Kotlin-JVM] Exception while loading resource '$name'"
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
        true
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
    internal val onPlayerConnectHandlers = arrayListOf<(PlayerConnectEvent) -> Boolean>()
    fun onPlayerConnect(f: (PlayerConnectEvent) -> Boolean) {
        onPlayerConnectHandlers.add(f)
    }

    internal val onPlayerDisconnectHandlers = arrayListOf<(PlayerDisconnectEvent) -> Boolean>()
    fun onPlayerDisconnect(f: (PlayerDisconnectEvent) -> Boolean) {
        onPlayerDisconnectHandlers.add(f)
    }

    internal val onPlayerDiedHandlers = arrayListOf<(PlayerDiedEvent) -> Boolean>()
    fun onPlayerDied(f: (PlayerDiedEvent) -> Boolean) {
        onPlayerDiedHandlers.add(f)
    }

    internal val onPlayerRecievedDamageHandlers = arrayListOf<(PlayerRecievedDamageEvent) -> Boolean>()
    fun onPlayerRecievedDamage(f: (PlayerRecievedDamageEvent) -> Boolean) {
        onPlayerRecievedDamageHandlers.add(f)
    }

    internal val onPlayerEnteredVehicleHandlers = arrayListOf<(PlayerEnteredVehicleEvent) -> Boolean>()
    fun onPlayerEnteredVehicle(f: (PlayerEnteredVehicleEvent) -> Boolean) {
        onPlayerEnteredVehicleHandlers.add(f)
    }

    internal val onPlayerLeftVehicleHandlers = arrayListOf<(PlayerLeftVehicleEvent) -> Boolean>()
    fun onPlayerLeftVehicle(f: (PlayerLeftVehicleEvent) -> Boolean) {
        onPlayerLeftVehicleHandlers.add(f)
    }

    internal val onServerEventHandlers = hashMapOf<String, HashSet<ServerEvent.Handler>>()
    fun onServerEvent(name: String, func: Function<*>) {
        val handler = ServerEvent.Handler(func)
        if (!onServerEventHandlers.containsKey(name))
            onServerEventHandlers[name] = hashSetOf(handler)
        else onServerEventHandlers[name]?.add(handler)
    }
    fun onServerEvent(name: String, func: KFunction<*>) {
        val handler = ServerEvent.Handler(func)
        if (!onServerEventHandlers.containsKey(name))
            onServerEventHandlers[name] = hashSetOf(handler)
        else onServerEventHandlers[name]?.add(handler)
    }

    internal val onClientEventHandlers = hashMapOf<String, HashSet<ClientEvent.Handler>>()
    fun onClientEvent(event: String, func: Function<*>) {
        val handler = ClientEvent.Handler(func)
        if (!onClientEventHandlers.containsKey(event))
            onClientEventHandlers[event] = hashSetOf(handler)
        else onClientEventHandlers[event]?.add(handler)
    }
    fun onClientEvent(event: String, func: KFunction<*>) {
        val handler = ClientEvent.Handler(func)
        if (!onClientEventHandlers.containsKey(event))
            onClientEventHandlers[event] = hashSetOf(handler)
        else onClientEventHandlers[event]?.add(handler)
    }

    internal val onConsoleCommandHandlers = hashMapOf<String, HashSet<(List<String>) -> Unit>>()
    fun onConsoleCommand(command: String, f: (List<String>) -> Unit) {
        if (!onConsoleCommandHandlers.containsKey(command))
            onConsoleCommandHandlers[command] = hashSetOf(f)
        else onConsoleCommandHandlers[command]?.add(f)
    }

    // Tick
    internal val onTickHandlers = arrayListOf<() -> Unit>()
    fun onTick(f: () -> Unit) {
        onTickHandlers.add(f)
    }
}
