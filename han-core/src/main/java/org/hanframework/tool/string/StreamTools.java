package org.hanframework.tool.string;

import org.hanframework.tool.json.JsonTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Package: safety.bankpay.util
 * @Description: 流转换成String
 * @author: liuxin
 * @date: 2017/11/14 下午6:12
 */
public class StreamTools {


    public static <T> T convertStringToObject(BufferedReader reader, Class<T> cls) {
        String var1 = convertStreamToString(reader);
        return JsonTools.fromJson(var1, cls);
    }

    public static String convertStreamToString(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    public static <T> T convertStringToObject(InputStream inputStream, Class<T> cls) {
        String var1 = convertStreamToString(inputStream, "utf8");
        return JsonTools.fromJson(var1, cls);
    }

    public static String convertStreamToString(InputStream is, String charSet) {
        InputStreamReader streamReader = null;
        try {
            streamReader = new InputStreamReader(is, charSet);
        } catch (Exception e) {

        }
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
