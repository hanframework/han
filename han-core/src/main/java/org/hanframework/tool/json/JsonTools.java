package org.hanframework.tool.json;


import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hanframework.tool.exeception.JsonConverException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxin on 17/1/12.
 * 返回快照信息，对Order服务返回的Json信息，转换
 */
public class JsonTools {
    public static final Gson GSON = new Gson();
    private static final Gson LOG_GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static String toJson(Object object) {
        return LOG_GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        T t = GSON.fromJson(json, cls);
        return t;
    }

    public static List parseList(String json) {
        List<Object> objects = null;
        try {
            objects = GSON.fromJson(json, List.class);
        } catch (Exception e) {
            throw new JsonConverException("json转换异常,请检查是否符合Json规范:[ " + json + " ]");
        }
        return objects;
    }

    public static Map parseMap(String json) {
        Map<String, Object> stringObjectMap;
        try {
            stringObjectMap = GSON.fromJson(json, Map.class);
        } catch (Exception e) {
            throw new JsonConverException("json转换异常,请检查是否符合Json规范:[ " + json + " ]");
        }
        return stringObjectMap;
    }

    /**
     * 获取原始数据
     * 将code message data
     * 中的data获取然后解析，如果code为非0,则抛出一个自定义异常,
     *
     * @param resBoby 原始响应
     * @return JSONObject, 可以从里面获取未定义的json字段
     */
    public static JSONObject getMetaData(String resBoby) {
        return new JSONObject(resBoby);
    }

    /**
     * json转
     *
     * @param json
     * @return
     */
    public static JSONObject getGsonObject(String json) {
        JSONObject metaData = null;
        try {
            metaData = new JSONObject(json);
        } catch (JSONException je) {
            throw new JsonConverException(je.getMessage());
        }
        return metaData;
    }

    /**
     * 使用jackJson注解
     *
     * @param targetObj
     * @return
     * @JsonIgnore 此注解用于属性上，作用是进行JSON操作时忽略该属性。
     * @JsonFormat 此注解用于属性上，作用是把Date类型直接转化为想要的格式，
     * 如@JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")。
     * @JsonProperty 此注解用于属性上，作用是把该属性的名称序列化为另外一个名称，
     * 如把trueName属性序列化为name，@JsonProperty("name")。
     */
    public static String toJsonByJackson(Object targetObj) {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(targetObj);
        } catch (Exception e) {
            throw new JsonConverException(e.getMessage());
        }

        return json;
    }

    /**
     * 支持Jackson注解
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T fromJsonByJackson(String json, Class<T> cls) {
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            t = mapper.readValue(json, cls);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

}
