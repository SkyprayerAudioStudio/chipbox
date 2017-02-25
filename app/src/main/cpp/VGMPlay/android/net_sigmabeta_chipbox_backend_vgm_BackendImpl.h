/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include "../VGMPlay.h"
/* Header for class net_sigmabeta_chipbox_backend_vgm_BackendImpl */

#ifndef _Included_net_sigmabeta_chipbox_backend_vgm_BackendImpl
#define _Included_net_sigmabeta_chipbox_backend_vgm_BackendImpl
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    loadFile
 * Signature: (Ljava/lang/String;IIJJ)V
 */
JNIEXPORT void JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_loadFile
        (JNIEnv *, jobject, jstring, jint, jint, jlong, jlong);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    readNextSamples
 * Signature: ([S)V
 */
JNIEXPORT void JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_readNextSamples
        (JNIEnv *, jobject, jshortArray);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    getMillisPlayed
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_getMillisPlayed
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    seek
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_seek
        (JNIEnv *, jobject, jlong);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    setTempo
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_setTempo
        (JNIEnv *, jobject, jdouble);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    getVoiceCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_getVoiceCount
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    getVoiceName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_getVoiceName
        (JNIEnv *, jobject, jint);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    muteVoice
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_muteVoice
        (JNIEnv *, jobject, jint, jint);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    isTrackOver
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_isTrackOver
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    teardown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_teardown
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_BackendImpl
 * Method:    getLastError
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sigmabeta_chipbox_backend_vgm_BackendImpl_getLastError
        (JNIEnv *, jobject);

int getChannelCountForChipId(int);
void setMutingData(int, CHIP_OPTS *, int, bool);

#ifdef __cplusplus
}
#endif
#endif
