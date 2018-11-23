package com.sooner.framework.jdbc.core.toolkit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooner.framework.jdbc.core.MybatisMapperAnnotationBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @Author:Hoo
 * @Date:2018/11/21 16:00
 * @Description:
 */
public class JsonUtils {

    private static final Log logger = LogFactory.getLog(MybatisMapperAnnotationBuilder.class);
    private  static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T serializable(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static <T> T serializable(String json, TypeReference reference) {

        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, reference);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return  null;
        }

    }

    public static String deserializer(Object json) {
        if (json == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return null;
        }

    }
}
