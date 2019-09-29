package alt.v.kotlin

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.jvm.CAPIExtra
import alt.v.kotlin.events.Event
import alt.v.kotlin.events.PlayerConnectEvent
import jnr.ffi.Pointer
import jnr.ffi.Struct
//import alt.v.kotlin.events.Event
//import alt.v.kotlin.events.PlayerConnectEvent
//import alt.v.kotlin.events.PlayerDisconnectEvent
import java.io.File
import java.net.URLClassLoader
import java.net.URL


class Resource {
    companion object {
        internal val ptrmap = HashMap<Pointer?, Resource>()
    }

    internal val on_make_client = CAPIExtra.MakeClientFn { resource, info, files ->

        true
    }

    internal val on_start = CAPIExtra.StartFn { resource ->
        ptrmap[resource] = this

        // Load jar
        val nameview = AltStringView(CAPI.func.alt_IResource_GetName(resource))
        val name = nameview.str()
        nameview.free()
        val mainview = AltStringView(CAPI.func.alt_IResource_GetMain(resource))
        val main = mainview.str()
        mainview.free()

        val jarfile = File("resources/$name/$name.jar")
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
                val classToLoad = Class.forName(main, true, child)
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
        Log.info("EVENT")

        try {
            val event = Event(eventptr)

            // CEvent::Type enum field seem to be 16-bits, tested on windows
//            Log.info("EVENT TYPE ${event.capiType.intValue()}")
//            Log.info("EVENT TYPE ${event.capiType.get()}")

            when (event.capiType.get() ?: throw NullPointerException())
            {
                CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_PLAYER_CONNECT -> {
                    for (handler in onPlayerConnectHandlers)
                        handler(PlayerConnectEvent(eventptr))
                }
            }
        } catch (e: Exception)
        {
            Log.exception(e)
        }

        true
    }

    internal val on_tick = CAPIExtra.OnResourceTickFn { resource ->

    }

    internal val on_create_base_object = CAPIExtra.OnCreateBaseObjectFn { resource, obj ->

    }

    internal val on_remove_base_object = CAPIExtra.OnRemoveBaseObjectFn { resource, obj ->

    }

    private val pointer: Pointer
    constructor(pointer: Pointer)
    {
        this.pointer = pointer
    }


    ////// Events //////
    val onPlayerConnectHandlers = arrayListOf<(PlayerConnectEvent) -> Boolean>()
    fun onPlayerConnect(f: (PlayerConnectEvent) -> Boolean) {
        onPlayerConnectHandlers.add(f)
    }
}

//internal val resources = mutableMapOf<String, Resource>()

//class Resource {
//    internal var ptr: Pointer? = null
//    internal var loaded = false
//
//    //internal constructor(resourceptr: Pointer) { ptr = resourceptr }
//    internal constructor(info: CAPI.alt_resource_creation_info_t)
//    {
//        ptr = CAPI.func.alt_script_runtime_create_resource(
//                Struct.getMemory(info),
//                on_event,
//                on_tick
//        )
//
//        // Load jar
//        val name = info.name.get()
//        val main = info.main.get()
//
//        val jarfile = File("resources/$name/$name.jar")
//        if (!jarfile.isFile) {
//            Log.error("[Kotlin-JVM] Could not open '${jarfile.absolutePath}'")
//            return
//        }
//
//        try {
//            val child = URLClassLoader(
//                    arrayOf<URL>(jarfile.toURI().toURL()),
//                    this.javaClass.classLoader
//            )
//            val classToLoad = Class.forName(main, true, child)
//            //val method = classToLoad.getDeclaredMethod("main", Resource::class.java)
//            //method.invoke(null, this)
//            val method = classToLoad.getDeclaredMethod("main")
//            method.invoke(null)
//        } catch (e: Exception) {
//            Log.error("[Kotlin-JVM] Exception while loading resource '$name'"
//                    + "\n\t Message: " + e.localizedMessage
//                    + "\n\t Cause: " + e.cause
//                    + "\n\t Ext: " + e.toString()
//                    //+"\n\tStack trace: "
//            )
//            e.printStackTrace()
//        }
//
//        resources[info.name.get()] = this
//        loaded = true
//    }
//
//    /*
//    private val onPlayerConnectHandlers = mutableListOf<(PlayerConnectEvent) -> Boolean>()
//    fun onPlayerConnect(f: (PlayerConnectEvent) -> Boolean) {
//        Log.info("KOTLIN RESOURCE ONPLAYERCONNECT SUBSCRIBED")
//        onPlayerConnectHandlers.add(f)
//    }
//
//    private val onPlayerDisconnectHandlers = mutableListOf<(PlayerDisconnectEvent) -> Boolean>()
//    fun onPlayerDisconnect(f: (PlayerDisconnectEvent) -> Boolean) { onPlayerDisconnectHandlers.add(f) }*/
//
//    private val on_event = CAPI.alt_resource_on_event_callback_t(fun(ptr: Pointer): Boolean {
//        val event = Event(ptr)
//        Log.info("KOTLIN Resource Event triggered")
//
//        /*when (event.type) {
//            CAPI.alt_event_type_t.PLAYER_CONNECT -> {
//                for (f in onPlayerConnectHandlers) if(!f(PlayerConnectEvent(event))) return false
//            }
//            CAPI.alt_event_type_t.PLAYER_DISCONNECT -> {
//                for (f in onPlayerDisconnectHandlers) if(!f(PlayerDisconnectEvent(event))) return false
//            }
//        }*/
//
//        return true
//    })
//
//    private val onTickCallbacks = mutableListOf<() -> Unit>()
//    fun onTick(f: () -> Unit) { onTickCallbacks.add(f) }
//    private val on_tick = CAPI.alt_resource_on_tick_callback_t { for (f in onTickCallbacks) f() }

//}