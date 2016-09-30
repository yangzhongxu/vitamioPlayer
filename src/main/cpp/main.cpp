#include <jni.h>
#include <string>

extern "C"
jint Java_yzx_gogoPlayer_AppApplication_add(JNIEnv *env,jclass appClass,jint a,jint b){
    return a+b;
}