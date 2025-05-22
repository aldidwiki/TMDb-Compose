#include <jni.h>
#include <string>

using namespace std;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_aldiprahasta_tmdb_utils_Constant_getApiKey(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxNTBlZjRkN2I0ZDNjOTk1MzUxOGE2ZTJlZDQ5OTI4ZSIsInN1YiI6IjVmZDZkYjUyZDhlMjI1MDA0MTFiMzZlMSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ObyQC30cOIxcfWiFBzp4mFX3BMxsQky6BXnONZtrzQw");
}