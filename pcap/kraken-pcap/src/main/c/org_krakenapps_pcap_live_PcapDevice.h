/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_krakenapps_pcap_live_PcapDevice */

#ifndef _Included_org_krakenapps_pcap_live_PcapDevice
#define _Included_org_krakenapps_pcap_live_PcapDevice
#ifdef __cplusplus
extern "C" {
#endif
#undef org_krakenapps_pcap_live_PcapDevice_MAX_NUMBER_OF_INSTANCE
#define org_krakenapps_pcap_live_PcapDevice_MAX_NUMBER_OF_INSTANCE 255L

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    getDeviceList
 * Signature: ()[Lorg/krakenapps/pcap/live/PcapDevice;
 */
JNIEXPORT jobjectArray JNICALL Java_org_krakenapps_pcap_live_PcapDevice_getDeviceList
  (JNIEnv *, jclass);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    open
 * Signature: (ILjava/lang/String;IZI)V;
 */
JNIEXPORT void JNICALL Java_org_krakenapps_pcap_live_PcapDevice_open
  (JNIEnv *, jobject, jint, jstring, jint, jboolean, jint);
	
/*
 * Class:     org_krakenapps_pcap_live_PcapDeviceInputStream
 * Method:    getPacket
 * Signature: (I)Lorg/krakenapps/pcap/packet/PcapPacket;
 */
JNIEXPORT jobject JNICALL Java_org_krakenapps_pcap_live_PcapDevice_getPacket
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    write
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_org_krakenapps_pcap_live_PcapDevice_write
  (JNIEnv *, jobject, jint, jbyteArray, jint, jint);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    setNonblock
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_krakenapps_pcap_live_PcapDevice_setNonblock
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    isNonblock
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_krakenapps_pcap_live_PcapDevice_isNonblock
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    setFilter
 * Signature: (ILjava/lang/String;II)V
 */
JNIEXPORT void JNICALL Java_org_krakenapps_pcap_live_PcapDevice_setFilter
  (JNIEnv *, jobject, jint, jstring, jint, jint);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    getStat
 * Signature: (I)Lorg/krakenapps/pcap/live/PcapStat;
 */
JNIEXPORT jobject JNICALL Java_org_krakenapps_pcap_live_PcapDevice_getStat
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_krakenapps_pcap_live_PcapDevice_close
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_krakenapps_pcap_live_PcapDevice
 * Method:    getPcapLibVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jobject JNICALL Java_org_krakenapps_pcap_live_PcapDevice_getPcapLibVersion
  (JNIEnv *, jclass);


jobjectArray getAddressBindings(JNIEnv *, pcap_if_t *);
jbyteArray getAddress(JNIEnv *, struct sockaddr *);
jobject makePacket(JNIEnv *, struct pcap_pkthdr *, const u_char *);
jobject makePacketHeader(JNIEnv *, struct pcap_pkthdr *);
jobject makePacketPayload(JNIEnv *, const u_char *, jint);

#ifdef __cplusplus
}
#endif
#endif
