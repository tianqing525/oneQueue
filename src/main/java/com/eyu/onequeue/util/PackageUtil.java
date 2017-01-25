package com.eyu.onequeue.util;

import java.util.concurrent.atomic.AtomicLong;

/***
 * @author solq
 */
public class PackageUtil {

    /**
     * 包固定长度
     * */
    public final static int PACK_FIXED_LENG = Long.BYTES + Short.BYTES+ Long.BYTES;
    
    // 用long类型做 id 如果每秒10W处理，可以N天才轮回一次
    private final static AtomicLong sn = new AtomicLong();
    private static long SN_MAX = Long.MAX_VALUE ^ 0x0;

    public static long getSn() {
	long next = sn.getAndIncrement();
	if (next > SN_MAX) {
	    sn.lazySet(0);
	}
	return next;
    }

    public static long getSessionId() {
	return getSn();
    }

    public static void writeLong(int offset, long v, byte[] ret) {
	ret[offset] = (byte) (v >> 56);
	ret[offset + 1] = (byte) (v >> 48);
	ret[offset + 2] = (byte) (v >> 40);
	ret[offset + 3] = (byte) (v >> 32);
	ret[offset + 4] = (byte) (v >> 24);
	ret[offset + 5] = (byte) (v >> 16);
	ret[offset + 6] = (byte) (v >> 8);
	ret[offset + 7] = (byte) (v);
    }

    public static void writeShort(int offset, short v, byte[] ret) {
	ret[offset] = (byte) (v >> 8);
	ret[offset + 1] = (byte) (v);
    }

    public static void writeBytes(int offset, byte[] v, byte[] ret) {
	for (int i = 0; i < v.length; offset++, i++) {
	    ret[offset] = v[i];
	}
    }

    public static long readLong(int offset, byte[] bytes) {

	long s0 = bytes[offset] & 0xFF;
	long s1 = bytes[offset + 1] & 0xFF;
	long s2 = bytes[offset + 2] & 0xFF;
	long s3 = bytes[offset + 3] & 0xFF;
	long s4 = bytes[offset + 4] & 0xFF;
	long s5 = bytes[offset + 5] & 0xFF;
	long s6 = bytes[offset + 6] & 0xFF;
	long s7 = bytes[offset + 7] & 0xFF;
	// 操作顺序不能乱 先 &清理溢出 然后循环位移累加上一次值
	return s0 << 56 | s1 << 48 | s2 << 40 | s3 << 32 | s4 << 24 | s5 << 16 | s6 << 8 | s7;
    }

    public static short readShort(int offset, byte[] bytes) {
	return (short) ((bytes[offset] << 8) & 0xFF | bytes[offset + 1] & 0xFF);
    }

    public static byte[] readBytes(int offset, int len, byte[] v) {
	byte[] ret = new byte[len];
	for (int i = 0; i < len; offset++, i++) {
	    ret[i] = v[offset];
	}
	return ret;
    }

}
