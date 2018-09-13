/*
 * Copyright (c) 2014-2015 XXX, Inc. All Rights Reserved.
 */

package com.study.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author niebiaofei
 *
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static <T> T deserializeConfig(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(
                    "Blank string cannot be deserialized to class");
        }
        try {
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,true);
            T t = objectMapper.readValue(string, clazz);
            return t;
        } catch (IOException e) {
            logger.error(
                    "Error deserializing string: " + string, e);
            throw new IllegalArgumentException(
                    "Error deserializing string: " + string, e);
        }
    }

    public static <T> T deserialize(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(
                    "Blank string cannot be deserialized to class");
        }

        try {
            T t = objectMapper.readValue(string, clazz);
            return t;
        } catch (IOException e) {
            logger.error(
                    "Error deserializing string: " + string, e);
            throw new IllegalArgumentException(
                    "Error deserializing string: " + string, e);
        }
    }

    public static <T> T deserialize(String string, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(string)) {
            throw new IllegalArgumentException(
                    "Blank string cannot be deserialized to class");
        }

        try {
            T t = objectMapper.readValue(string, typeReference);
            return t;
        } catch (IOException e) {
            logger.error(
                    "Error deserializing string: " + string, e);
            throw new IllegalArgumentException(
                    "Error deserializing string: " + string, e);
        }
    }

    public static String serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Null Object cannot be serialized");
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object", e);
            throw new IllegalArgumentException("Error serializing object: " + e.getMessage());
        }
    }

    public static boolean isJSONValid(String string) {
        try {
            objectMapper.readTree(string);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> boolean isJSONValid(String string, Class<T> clazz) {
        if (StringUtils.isBlank(string)) {
            return false;
        }

        try {
            objectMapper.readValue(string, clazz);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
