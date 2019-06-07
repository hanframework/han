package org.hanframework.tool.collection;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author liuxin
 * @date 2018/1/22 上午11:02
 */
public class MapTools {


    /**
     * String used to indent the verbose and debug Map prints.
     */
    private static final String INDENT_STRING = "    ";

    /**
     * <code>MapUtils</code> should not normally be instantiated.
     */
    public MapTools() {
    }

    // Type safe getters
    //-------------------------------------------------------------------------


    public static Object getObject(final Map map, final Object key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }


    public static String getString(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }


    public static Boolean getBoolean(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;

                } else if (answer instanceof String) {
                    return new Boolean((String) answer);

                } else if (answer instanceof Number) {
                    Number n = (Number) answer;
                    return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }


    public static Number getNumber(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;

                } else if (answer instanceof String) {
                    try {
                        String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);

                    } catch (ParseException e) {
                        logInfo(e);
                    }
                }
            }
        }
        return null;
    }


    public static Byte getByte(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Byte) {
            return (Byte) answer;
        }
        return new Byte(answer.byteValue());
    }


    public static Short getShort(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Short) {
            return (Short) answer;
        }
        return new Short(answer.shortValue());
    }


    public static Integer getInteger(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return new Integer(answer.intValue());
    }


    public static Long getLong(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Long) {
            return (Long) answer;
        }
        return new Long(answer.longValue());
    }


    public static Float getFloat(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Float) {
            return (Float) answer;
        }
        return new Float(answer.floatValue());
    }


    public static Double getDouble(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Double) {
            return (Double) answer;
        }
        return new Double(answer.doubleValue());
    }


    public static Map getMap(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null && answer instanceof Map) {
                return (Map) answer;
            }
        }
        return null;
    }

    // Type safe getters with default values
    //-------------------------------------------------------------------------


    public static Object getObject(Map map, Object key, Object defaultValue) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                return answer;
            }
        }
        return defaultValue;
    }


    public static String getString(Map map, Object key, String defaultValue) {
        String answer = getString(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Boolean getBoolean(Map map, Object key, Boolean defaultValue) {
        Boolean answer = getBoolean(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Number getNumber(Map map, Object key, Number defaultValue) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Byte getByte(Map map, Object key, Byte defaultValue) {
        Byte answer = getByte(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Short getShort(Map map, Object key, Short defaultValue) {
        Short answer = getShort(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Integer getInteger(Map map, Object key, Integer defaultValue) {
        Integer answer = getInteger(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Long getLong(Map map, Object key, Long defaultValue) {
        Long answer = getLong(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Float getFloat(Map map, Object key, Float defaultValue) {
        Float answer = getFloat(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Double getDouble(Map map, Object key, Double defaultValue) {
        Double answer = getDouble(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    public static Map getMap(Map map, Object key, Map defaultValue) {
        Map answer = getMap(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }


    // Type safe primitive getters
    //-------------------------------------------------------------------------


    public static boolean getBooleanValue(final Map map, final Object key) {
        Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return false;
        }
        return booleanObject.booleanValue();
    }


    public static byte getByteValue(final Map map, final Object key) {
        Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return 0;
        }
        return byteObject.byteValue();
    }


    public static short getShortValue(final Map map, final Object key) {
        Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return 0;
        }
        return shortObject.shortValue();
    }


    public static int getIntValue(final Map map, final Object key) {
        Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return 0;
        }
        return integerObject.intValue();
    }


    public static long getLongValue(final Map map, final Object key) {
        Long longObject = getLong(map, key);
        if (longObject == null) {
            return 0L;
        }
        return longObject.longValue();
    }


    public static float getFloatValue(final Map map, final Object key) {
        Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return 0f;
        }
        return floatObject.floatValue();
    }


    public static double getDoubleValue(final Map map, final Object key) {
        Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return 0d;
        }
        return doubleObject.doubleValue();
    }

    // Type safe primitive getters with default values
    //-------------------------------------------------------------------------


    public static boolean getBooleanValue(final Map map, final Object key, boolean defaultValue) {
        Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return defaultValue;
        }
        return booleanObject.booleanValue();
    }


    public static byte getByteValue(final Map map, final Object key, byte defaultValue) {
        Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return defaultValue;
        }
        return byteObject.byteValue();
    }


    public static short getShortValue(final Map map, final Object key, short defaultValue) {
        Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return defaultValue;
        }
        return shortObject.shortValue();
    }


    public static int getIntValue(final Map map, final Object key, int defaultValue) {
        Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject.intValue();
    }


    public static long getLongValue(final Map map, final Object key, long defaultValue) {
        Long longObject = getLong(map, key);
        if (longObject == null) {
            return defaultValue;
        }
        return longObject.longValue();
    }


    public static float getFloatValue(final Map map, final Object key, float defaultValue) {
        Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return defaultValue;
        }
        return floatObject.floatValue();
    }


    public static double getDoubleValue(final Map map, final Object key, double defaultValue) {
        Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return defaultValue;
        }
        return doubleObject.doubleValue();
    }

    // Conversion methods
    //-------------------------------------------------------------------------


    public static Properties toProperties(final Map map) {
        Properties answer = new Properties();
        if (map != null) {
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }


    public static Map toMap(final ResourceBundle resourceBundle) {
        Enumeration enumeration = resourceBundle.getKeys();
        Map map = new HashMap();

        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }

        return map;
    }



    protected static void logInfo(final Exception ex) {
        System.out.println("INFO: Exception: " + ex);
    }



    private static void printIndent(final PrintStream out, final int indent) {
        for (int i = 0; i < indent; i++) {
            out.print(INDENT_STRING);
        }
    }

    // Misc
    //-----------------------------------------------------------------------


    public static Map invertMap(Map map) {
        Map out = new HashMap(map.size());
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            out.put(entry.getValue(), entry.getKey());
        }
        return out;
    }

    //-----------------------------------------------------------------------

    public static void safeAddToMap(Map map, Object key, Object value) throws NullPointerException {
        if (value == null) {
            map.put(key, "");
        } else {
            map.put(key, value);
        }
    }


    //-----------------------------------------------------------------------


    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }


    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    // Map decorators
    //-----------------------------------------------------------------------


    public static Map synchronizedMap(Map map) {
        return Collections.synchronizedMap(map);
    }


    // SortedMap decorators
    //-----------------------------------------------------------------------


    public static Map synchronizedSortedMap(SortedMap map) {
        return Collections.synchronizedSortedMap(map);
    }

}
