package com.eyu.onequeue.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import com.eyu.common.utils.codec.ZlibUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SerialUtil {

    public final static ObjectMapper MAPPER_CONVERT = new ObjectMapper();

    public static byte[] zip(byte[] src) {
	return ZlibUtils.zip(src);
    }

    public static byte[] unZip(byte[] src) {
	return ZlibUtils.unzip(src, 30, TimeUnit.SECONDS);
    }

    public static <T> T readValueAsFile(String fileName, Class<T> clz) {
	try {
	    FileUtil.createDirs(fileName);
	    return MAPPER_CONVERT.readValue(new File(fileName), clz);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static void writeValueAsFile(String fileName, Object value) {
	try {
	    FileUtil.createDirs(fileName);
	    MAPPER_CONVERT.writeValue(new File(fileName), value);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static <T> List<T> readArray(byte[] src, Class<T> clz) {
	if (src == null) {
	    return null;
	}
	try {
	    CollectionLikeType type = TypeFactory.defaultInstance().constructCollectionType(LinkedList.class, clz);
	    return MAPPER_CONVERT.readValue(src, type);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static <T> T map2Object(Map mapData, TypeReference<T> tr) {
	if (mapData == null) {
	    return (T) mapData;
	}
	try {
	    if (TypeUtils.isInstance(mapData, tr.getType())) {
		return (T) mapData;
	    }
	    return MAPPER_CONVERT.convertValue(mapData, tr);
	} catch (Exception e) {
	    FormattingTuple message = MessageFormatter.format("将MAP[{}]转换为[{}]时出现异常", new Object[] { mapData, tr });
	    throw new RuntimeException(message.getMessage(), e);
	}
    }

    public static <T> T readValue(byte[] src, Class<T> valueType) {
	if (src == null) {
	    return null;
	}
	try {
	    return MAPPER_CONVERT.readValue(src, valueType);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static <T> T readValue(byte[] src, TypeReference<T> valueTypeRef) {
	if (src == null) {
	    return null;
	}
	try {
	    return MAPPER_CONVERT.readValue(src, valueTypeRef);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static <T> T readZipValue(byte[] src, Class<T> valueType) {
	if (src == null) {
	    return null;
	}
	try {
	    return MAPPER_CONVERT.readValue(ZlibUtils.unzip(src, 30, TimeUnit.SECONDS), valueType);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static byte[] writeValueAsBytes(Object obj) {
	try {
	    return MAPPER_CONVERT.writeValueAsBytes(obj);
	} catch (JsonProcessingException e) {
	    throw new RuntimeException(e);
	}
    }

    public static byte[] writeValueAsZipBytes(Object obj) {
	try {
	    return ZlibUtils.zip(MAPPER_CONVERT.writeValueAsBytes(obj));
	} catch (JsonProcessingException e) {
	    throw new RuntimeException(e);
	}
    }

    public static String writeValueAsString(Object obj) {
	try {
	    return MAPPER_CONVERT.writeValueAsString(obj);
	} catch (JsonProcessingException e) {
	    throw new RuntimeException(e);
	}
    }

}
