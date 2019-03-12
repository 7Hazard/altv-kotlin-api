package alt.v.kotlin

import alt.v.jvm.CAPI
import jnr.ffi.Pointer

fun main()
{
    Log.info("[Kotlin-JVM] Kotlin-JVM plugin loaded")

    val scriptRuntime = CAPI.func.alt_script_runtime_create(
            { info -> createResource(info) },
            { resource -> removeResource(resource) },
            { onTick() }
    )
    CAPI.func.alt_server_register_script_runtime(CAPI.server, "kotlin-jvm", scriptRuntime)
}

// resources
fun createResource(infoptr: Pointer): Pointer?
{
    val info = CAPI.alt_resource_creation_info_t()
    info.useMemory(infoptr)
    Log.info("Loading resource ${info.name.get()}")
    val resource = Resource(info)

    if(resource.loaded) return resource.ptr
    else return null
}

fun removeResource(resource: Pointer) {
    throw Exception("KOTLIN Remove Resource triggered")
}

fun onTick() {

}
