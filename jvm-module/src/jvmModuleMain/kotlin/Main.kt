package alt.v.module.jvm

import jni.*
import kotlinx.cinterop.*

// An empty file is required for the compiler to correctly name the klib file for the common module
@SymbolName("altvMain")
external fun altvMain() {
    memScoped {
        val javaVMPointer = allocPointerTo<JavaVMVar>()
        val envPointer = allocPointerTo<JNIEnvVar>()
        val args = cValue<JavaVMInitArgs> {
        }
        val rc = JNI_CreateJavaVM(javaVMPointer.ptr, envPointer.ptr as CValuesRef<COpaquePointerVar>/*(void**)&env*/, args.ptr)
        if (rc != JNI_OK) {
            //TODO: error logging cin.get()
            return@memScoped
        }
        println(envPointer.reinterpret<JNINativeInterface_>().GetVersion)
    }
}
