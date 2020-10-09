package hazard7.altv.kotlin

import hazard7.altv.jvm.AltStringView
import hazard7.altv.jvm.CAPI
import hazard7.altv.jvm.CAPIExtra
import hazard7.altv.kotlin.events.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.internal.MainDispatcherFactory
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

var mainThread = Thread.currentThread()

fun main()
{
    logInfo("[Kotlin-JVM] Kotlin-JVM plugin loaded")

//    Thread.UncaughtExceptionHandler { t, e ->
//        Log.exception(e, "[Kotlin-JVM] Unhandled exception thrown")
//    }

    val script_runtime = CAPIExtra.func.alt_CAPIScriptRuntime_Create(
            create_resource,
            remove_resource,
            on_tick
    )
    CAPI.func.alt_ICore_RegisterScriptRuntime(
            CAPI.core,
            AltStringView("kotlin-jvm").ptr(),
            script_runtime
    )
    CAPI.func.alt_ICore_SubscribeEvent(CAPI.core, CAPI.alt_CEvent_Type.ALT_CEVENT_TYPE_ALL, Event.handler, null)

    logInfo("[Kotlin-JVM] Registered runtime for 'kotlin-jvm' resource type")
}

var create_resource = CAPIExtra.CreateImplFn { runtime, resource ->
    val kotlinResource = Resource(resource)

    CAPIExtra.func.alt_CAPIResource_Impl_Create(
            resource,
            kotlinResource.on_make_client,
            kotlinResource.on_start,
            kotlinResource.on_stop,
            kotlinResource.on_event,
            kotlinResource.on_tick,
            kotlinResource.on_create_base_object,
            kotlinResource.on_remove_base_object
    )
}

var remove_resource = CAPIExtra.DestroyImplFn { runtime, resource ->
    logInfo("[Kotlin-JVM] KOTLIN REMOVE RESOURCE")
}

var on_tick = CAPIExtra.OnRuntimeTickFn { runtime ->
    runBlocking {
        while (!nextTicks.isEmpty())
        {
            // Get all operations
            val ops = nextTicks.toMutableList()
            // clear the queue
            nextTicks.clear()
            // Execute all operations
            for (op in ops) {
                executeNextTickCallback(op)
            }
            // if new nextTick calls were added, process them before ending this tick
        }
    }
}
private val nextTicks = ConcurrentLinkedQueue<Runnable>()

/**
 * Invokes a callback in the next tick or instantly if it's the main thread
 */
fun <T> nextTick(callable: suspend () -> T): Deferred<T> {
    return MainScope().async {
        callable()
    }
}
fun executeNextTickCallback(op: Runnable)
{
    try {
        op.run()
    } catch (e: Throwable) {
        logException(e, "[Kotlin-JVM] Unhandled exception in nextTick")
    }
}

@InternalCoroutinesApi
class MainDispatcher : MainCoroutineDispatcher()//, Delay
{
    override val immediate: MainCoroutineDispatcher
        get() = this

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if(Thread.currentThread() == mainThread)
        {
            logInfo("INSTANT NEXT TICK")
            executeNextTickCallback(block)
        }
        else
            nextTicks.add(block)
    }

//    @InternalCoroutinesApi
//    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
//        TODO("Not yet implemented")
//    }
}

@InternalCoroutinesApi
internal class MainDispatcherFactory : MainDispatcherFactory {
    override val loadPriority: Int get() = Int.MAX_VALUE
    override fun createDispatcher(allFactories: List<MainDispatcherFactory>) = MainDispatcher()
}