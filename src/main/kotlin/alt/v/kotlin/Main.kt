package alt.v.kotlin

import alt.v.jvm.CAPI
import alt.v.jvm.AltStringView
import alt.v.jvm.CAPIExtra


fun main()
{
    Log.info("[Kotlin-JVM] Kotlin-JVM plugin loaded")

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
    Log.info("[Kotlin-JVM] Registered runtime for 'kotlin-jvm' resource type")
}

var create_resource = CAPIExtra.CreateImplFn { runtime, resource ->
//    Log.info("KOTLIN CREATE RESOURCE")
//    val kotlinResource = Resource()

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
    Log.info("[Kotlin-JVM] KOTLIN REMOVE RESOURCE")
}

var on_tick = CAPIExtra.OnRuntimeTickFn { runtime ->

}

// resources
//fun createResource(infoptr: Pointer): Pointer?
//{
//    val info = CAPI.alt_resource_creation_info_t()
//    info.useMemory(infoptr)
//    Log.info("Loading resource ${info.name.get()}")
//    val resource = Resource(info)
//
//    if(resource.loaded) return resource.ptr
//    else return null
//}
//
//fun removeResource(resource: Pointer) {
//    throw Exception("KOTLIN Remove Resource triggered")
//}
//
//fun onTick() {
//
//}
