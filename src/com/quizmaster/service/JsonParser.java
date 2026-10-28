package com.quizmaster.service;

import java.util.*;

/**
 * Lightweight zero-dependency JSON parser and serializer.
 * Handles simple JSON objects, arrays, strings, numbers, booleans, and nulls.
 * No external libraries required — pure Java.
 */
public final class JsonParser {

    private JsonParser() {}

    // ═══════════════════════════════════════════════════════════════
    //  PARSING
    // ═══════════════════════════════════════════════════════════════

    private static int pos;
    private static String json;

    /**
     * Parses a JSON string into a Map (for objects) or List (for arrays).
     */
    public static Object parse(String input) {
        json = input.trim();
        pos = 0;
        return parseValue();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String input) {
        return (Map<String, Object>) parse(input);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> parseArray(String input) {
        return (List<Object>) parse(input);
    }

    private static Object parseValue() {
        skipWhitespace();
        if (pos >= json.length()) return null;

        char c = json.charAt(pos);
        if (c == '{') return parseObj();
        if (c == '[') return parseArr();
        if (c == '"') return parseString();
        if (c == 't' || c == 'f') return parseBool();
        if (c == 'n') return parseNull();
        return parseNumber();
    }

    private static Map<String, Object> parseObj() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++; // skip '{'
        skipWhitespace();

        if (pos < json.length() && json.charAt(pos) == '}') {
            pos++;
            return map;
        }

        while (pos < json.length()) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            pos++; // skip ':'
            Object value = parseValue();
            map.put(key, value);
            skipWhitespace();
            if (pos < json.length() && json.charAt(pos) == ',') {
                pos++;
            } else {
                break;
            }
        }
        if (pos < json.length() && json.charAt(pos) == '}') pos++;
        return map;
    }

    private static List<Object> parseArr() {
        List<Object> list = new ArrayList<>();
        pos++; // skip '['
        skipWhitespace();

        if (pos < json.length() && json.charAt(pos) == ']') {
            pos++;
            return list;
        }

        while (pos < json.length()) {
            list.add(parseValue());
            skipWhitespace();
            if (pos < json.length() && json.charAt(pos) == ',') {
                pos++;
            } else {
                break;
            }
        }
        if (pos < json.length() && json.charAt(pos) == ']') pos++;
        return list;
    }

    private static String parseString() {
        pos++; // skip opening '"'
        StringBuilder sb = new StringBuilder();
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == '\\' && pos + 1 < json.length()) {
                pos++;
                char escaped = json.charAt(pos);
                switch (escaped) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'u':
                        String hex = json.substring(pos + 1, pos + 5);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                        break;
                    default:   sb.append(escaped); break;
                }
            } else if (c == '"') {
                pos++;
                return sb.toString();
            } else {
                sb.append(c);
            }
            pos++;
        }
        return sb.toString();
    }

    private static Number parseNumber() {
        int start = pos;
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) break;
            pos++;
        }
        String numStr = json.substring(start, pos);
        if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
            return Double.parseDouble(numStr);
        }
        long val = Long.parseLong(numStr);
        if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
            return (int) val;
        }
        return val;
    }

    private static Boolean parseBool() {
        if (json.startsWith("true", pos)) {
            pos += 4;
            return true;
        }
        pos += 5;
        return false;
    }

    private static Object parseNull() {
        pos += 4;
        return null;
    }

    private static void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  SERIALIZATION
    // ═══════════════════════════════════════════════════════════════

    /**
     * Serializes a Map/List/String/Number/Boolean to a JSON string.
     */
    @SuppressWarnings("unchecked")
    public static String toJson(Object obj) {
        return toJson(obj, 0);
    }

    /**
     * Serializes to pretty-printed JSON.
     */
    @SuppressWarnings("unchecked")
    public static String toPrettyJson(Object obj) {
        return toJson(obj, 0, true);
    }

    @SuppressWarnings("unchecked")
    private static String toJson(Object obj, int indent) {
        return toJson(obj, indent, false);
    }

    @SuppressWarnings("unchecked")
    private static String toJson(Object obj, int indent, boolean pretty) {
        if (obj == null) return "null";
        if (obj instanceof String) return escapeString((String) obj);
        if (obj instanceof Number) return obj.toString();
        if (obj instanceof Boolean) return obj.toString();

        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            if (map.isEmpty()) return "{}";

            StringBuilder sb = new StringBuilder();
            sb.append("{");
            String sep = "";
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append(sep);
                if (pretty) {
                    sb.append("\n").append("  ".repeat(indent + 1));
                }
                sb.append(escapeString(entry.getKey()));
                sb.append(pretty ? ": " : ":");
                sb.append(toJson(entry.getValue(), indent + 1, pretty));
                sep = ",";
            }
            if (pretty) {
                sb.append("\n").append("  ".repeat(indent));
            }
            sb.append("}");
            return sb.toString();
        }

        if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            if (list.isEmpty()) return "[]";

            StringBuilder sb = new StringBuilder();
            sb.append("[");
            String sep = "";
            for (Object item : list) {
                sb.append(sep);
                if (pretty) {
                    sb.append("\n").append("  ".repeat(indent + 1));
                }
                sb.append(toJson(item, indent + 1, pretty));
                sep = ",";
            }
            if (pretty) {
                sb.append("\n").append("  ".repeat(indent));
            }
            sb.append("]");
            return sb.toString();
        }

        return escapeString(obj.toString());
    }

    private static String escapeString(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:   sb.append(c);      break;
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════
    //  HELPER ACCESSORS (safe typed access from parsed maps)
    // ═══════════════════════════════════════════════════════════════

    public static String getString(Map<String, Object> map, String key, String defaultVal) {
        Object v = map.get(key);
        return v != null ? v.toString() : defaultVal;
    }

    public static int getInt(Map<String, Object> map, String key, int defaultVal) {
        Object v = map.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) {
            try { return Integer.parseInt((String) v); } catch (NumberFormatException e) { /* fall through */ }
        }
        return defaultVal;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getArray(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof List) return (List<Object>) v;
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getObject(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Map) return (Map<String, Object>) v;
        return new LinkedHashMap<>();
    }
}
