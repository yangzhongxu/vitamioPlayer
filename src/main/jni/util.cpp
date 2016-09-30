#include <jni.h>
#include <stddef.h>

jstring get_apk_signature(JNIEnv *env, jobject context_object) {
    jclass context_class = env->GetObjectClass(context_object);
    jmethodID methodId = env->GetMethodID(context_class, "getPackageManager",
                                          "()Landroid/content/pm/PackageManager;");
    jobject package_manager_object = env->CallObjectMethod(context_object, methodId);
    if (package_manager_object == NULL) {
        return NULL;
    }
    methodId = env->GetMethodID(context_class, "getPackageName", "()Ljava/lang/String;");
    jstring package_name_string = (jstring) env->CallObjectMethod(context_object, methodId);
    if (package_name_string == NULL) {
        return NULL;
    }
    env->DeleteLocalRef(context_class);
    jclass pack_manager_class = env->GetObjectClass(package_manager_object);
    methodId = env->GetMethodID(pack_manager_class, "getPackageInfo",
                                "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    env->DeleteLocalRef(pack_manager_class);
    jobject package_info_object = env->CallObjectMethod(package_manager_object, methodId,
                                                        package_name_string,
                                                        64);
    if (package_info_object == NULL) {
        return NULL;
    }
    env->DeleteLocalRef(package_manager_object);
    jclass package_info_class = env->GetObjectClass(package_info_object);
    jfieldID fieldId = env->GetFieldID(package_info_class, "signatures",
                                       "[Landroid/content/pm/Signature;");
    env->DeleteLocalRef(package_info_class);
    jobjectArray signature_object_array = (jobjectArray) env->GetObjectField(
            package_info_object,
            fieldId);
    if (signature_object_array == NULL) {
        return NULL;
    }
    jobject signature_object = env->GetObjectArrayElement(signature_object_array, 0);
    env->DeleteLocalRef(package_info_object);
    jclass signature_class = env->GetObjectClass(signature_object);
    methodId = env->GetMethodID(signature_class, "toCharsString", "()Ljava/lang/String;");
    env->DeleteLocalRef(signature_class);
    jstring signature_string = (jstring) env->CallObjectMethod(signature_object, methodId);
    return signature_string;
}