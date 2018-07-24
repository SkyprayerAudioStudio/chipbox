/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_sigmabeta_chipbox_backend_vgm_ScannerImpl */

#ifndef _Included_net_sigmabeta_chipbox_backend_vgm_ScannerImpl
#define _Included_net_sigmabeta_chipbox_backend_vgm_ScannerImpl
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getPlatform
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getPlatform
        (JNIEnv *, jobject, jstring);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    fileInfoSetup
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_fileInfoSetup
        (JNIEnv *, jobject, jstring);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    fileInfoGetTrackCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_fileInfoGetTrackCount
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    fileInfoSetTrackNumber
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_fileInfoSetTrackNumber
        (JNIEnv *, jobject, jint);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    fileInfoTeardown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_fileInfoTeardown
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getFileTrackLength
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getFileTrackLength
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getFileIntroLength
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getFileIntroLength
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getFileLoopLength
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getFileLoopLength
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getFileTitle
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getFileTitle
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getFileGameTitle
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getFileGameTitle
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getFilePlatform
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getFilePlatform
        (JNIEnv *, jobject);

/*
 * Class:     net_sigmabeta_chipbox_backend_vgm_ScannerImpl
 * Method:    getFileArtist
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_sigmabeta_chipbox_backend_vgm_ScannerImpl_getFileArtist
        (JNIEnv *, jobject);

jbyteArray get_java_byte_array(JNIEnv *, wchar_t *);
void teardown();

#ifdef __cplusplus
}
#endif
#endif
