package alt.v.module.jvm

import jni.*
import kotlinx.cinterop.*

// An empty file is required for the compiler to correctly name the klib file for the common module
@SymbolName("altvMain")
external fun altvMain() {
    memScoped {
        val javaVMPointer = allocPointerTo<JavaVMVar>()
        val envPointer = allocPointerTo<JNIEnvVar>()
        val javaVmOptions = allocArray<JavaVMOption>(1)
        javaVmOptions[0].optionString = "-Djava.class.path=path".cstr.getPointer(this)
        val args = cValue<JavaVMInitArgs> {
            version = JNI_VERSION_1_8
            nOptions = 1
            options = javaVmOptions
        }

        val rc = JNI_CreateJavaVM(javaVMPointer.ptr, envPointer.ptr as CValuesRef<COpaquePointerVar>/*(void**)&env*/, args.ptr)
        if (rc != JNI_OK) {
            //TODO: error logging cin.get()
            return@memScoped
        }
        println(envPointer.reinterpret<JNINativeInterface_>().GetVersion)
    }
}
