package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Table {
    public List<Map<String, Object>> data;

    public Table(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Table() {
        data = new ArrayList<>();
    }

    public List<Map<String, Object>> execute(String request) throws Exception {
        List<Map<String, Object>> rows = null;
        RequestEntity requestEntity = Parser.parse(request);
        if (requestEntity.command == RequestEntity.Command.INSERT) {
            rows = insert(requestEntity);
        } else if (requestEntity.command == RequestEntity.Command.UPDATE) {
            rows = update(requestEntity);
        } else if (requestEntity.command == RequestEntity.Command.SELECT) {
            rows = select(requestEntity);
        } else if (requestEntity.command == RequestEntity.Command.DELETE) {
            rows = delete(requestEntity);
        }
        return rows;
    }

    private List<Map<String, Object>> insert(RequestEntity request) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        String[] pairs = request.values.split(",");
        for (int i = 0; i < pairs.length; i++) {
            String key = getKey(pairs[i], "=");
            Object value = getValue(pairs[i], "=");
            if (value != null) {
                row.put(key, value);
            }
        }
        if (row.size() != 0) {
            data.add(row);
        } else {
            throw new Exception("Не найдено значений для добавления");
        }
        rows.add(row);
        return rows;
    }

    private List<Map<String, Object>> delete(RequestEntity request) throws Exception {
        List<Map<String, Object>> rows = select(request);
        data.removeAll(rows);
        return rows;
    }

    private List<Map<String, Object>> update(RequestEntity request) throws Exception {
        List<Map<String, Object>> rows = select(request);
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            String[] pairs = request.values.split(",");
            for (int j = 0; j < pairs.length; j++) {
                String key = getKey(pairs[j], "=");
                Object value = getValue(pairs[j], "=");
                if (value != null) {
                    row.put(key, value);
                } else {
                    row.remove(key);
                }
            }
        }
        return rows;
    }

    private List<Map<String, Object>> select(RequestEntity request) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            if (isRightString(row, request.disjunctions)) {
                rows.add(row);
            }
        }
        return rows;
    }

    public void print() {
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            keys.addAll(row.keySet());
        }
        List<String> orderKeys = new ArrayList<>(keys);
        for (int k = 0; k < orderKeys.size(); k++) {
            String key = orderKeys.get(k);
            System.out.printf("| %10s ", key);
        }
        if (orderKeys.size() > 0) {
            String del = "-".repeat(orderKeys.size() * 13 + 1);
            System.out.println();
            System.out.println(del);
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> row = data.get(i);
                for (int k = 0; k < orderKeys.size(); k++) {
                    String key = orderKeys.get(k);
                    Object object = row.get(key);
                    System.out.printf("| %10s ", object);
                }
                System.out.println('|');
            }
            System.out.println(del);
        } else {
            System.out.println("Table is empty");
            System.out.println();
        }
    }

    private boolean isRightString(Map<String, Object> row, RequestEntity.Disjunction[] disjunctions) throws Exception {
        boolean isRightString = false;
        for (int i = 0; i < disjunctions.length; i++) {
            if (isRightString(row, disjunctions[i].conjunctions)) {
                isRightString = true;
                break;
            }
        }
        return isRightString;
    }

    private boolean isRightString(Map<String, Object> row, RequestEntity.Comparison[] conjunctions) throws Exception {
        boolean isRightString = true;
        for (int i = 0; i < conjunctions.length; i++) {
            if (!isRightString(row, conjunctions[i])) {
                isRightString = false;
                break;
            }
        }
        return isRightString;
    }

    private boolean isRightString(Map<String, Object> row, RequestEntity.Comparison comparison) throws Exception {
        if (comparison.operator == null) return true;
        boolean isRightString = false;
        Object key = getValue(comparison.leftPart);
        Object value = getValue(comparison.rightPart);
        if (key instanceof String) {
            Object cell = row.get(key);
            if (cell != null) {
                isRightString = comparison(comparison.operator, cell, value);
            }
        } else {
            isRightString = comparison(comparison.operator, key, value);
        }
        return isRightString;
    }

    private static boolean comparison(RequestEntity.Operator operator, Object key, Object value) {
        boolean isRight = false;
        switch (operator) {
            case EQUALS:
                if (key == null && value == null) {
                    isRight = true;
                } else if (key == null || value == null) {
                    isRight = false;
                } else {
                    isRight = key.equals(value);
                }
                break;
            case NOT_EQUALS:
                if (key == null && value == null) {
                    isRight = false;
                } else if (key == null || value == null) {
                    isRight = true;
                } else {
                    isRight = !key.equals(value);
                }
                break;
            case LIKE:
                if (key instanceof String && value instanceof String) {
                    isRight = ((String) key).matches(getPattern((String) value));
                }
                break;
            case ILIKE:
                if (key instanceof String && value instanceof String) {
                    isRight = matchesIgnoreCase((String) key, getPattern((String) value));
                }
                break;
            case LESS:
                if (key instanceof Double && value instanceof Double) {
                    isRight =  (Double)key <  (Double)value;
                } else if (key instanceof Long && value instanceof Long) {
                    isRight = (Long)key <  (Long)value;
                }
                break;
            case LESS_OR_EQUALS:
                if (key instanceof Double && value instanceof Double) {
                    isRight =  (Double)key <=  (Double)value;
                } else if (key instanceof Long && value instanceof Long) {
                    isRight = (Long)key <=  (Long)value;
                }
                break;
            case MORE:
                if (key instanceof Double && value instanceof Double) {
                    isRight =  (Double)key >  (Double)value;
                } else if (key instanceof Long && value instanceof Long) {
                    isRight = (Long)key >  (Long)value;
                }
                break;
            case MORE_OR_EQUALS:
                if (key instanceof Double && value instanceof Double) {
                    isRight =  (Double)key >=  (Double)value;
                } else if (key instanceof Long && value instanceof Long) {
                    isRight =  (Long)key >=  (Long)value;
                }
                break;
        }
        return isRight;
    }

    private static boolean matchesIgnoreCase(String source, String target) {
        source = source.toUpperCase(Locale.ROOT);
        target = target.toUpperCase(Locale.ROOT);
        return source.matches(target);
    }

    private static String getPattern(String str) {
        return str.replaceAll("%", ".*");
    }

    private static String getKey(String pair, String regex) throws Exception {
        String key = pair.split(regex)[0].trim();
        if (!key.matches("^'.+'$")) {
            throw new Exception("Ключ " + key + " не корректен");
        } else {
            key = key.replaceAll("'", "");
        }
        return key;
    }

    private static Object getValue(String pair, String regex) throws Exception {
        if (pair == null) return null;
        String[] parts = pair.split(regex);
        if (parts.length != 2) {
            throw new Exception("Пара ключ значения неверного формата \"" + pair + "\"");
        }
        String valueString = parts[1];
        Object value = getValue(valueString);
        return value;
    }

    private static Object getValue(String str) throws Exception {
        if (str == null) return null;
        str = str.trim();
        Object value;
        if (str.matches("^'.*'$")) {
            value = str.replaceAll("'", "");
        } else if (str.matches("TRUE")) {
            value = true;
        } else if (str.matches("FALSE")) {
            value = false;
        } else if (str.matches("NULL")) {
            value = null;
        } else {
            try {
                if (str.contains(".")) {
                    value = Double.valueOf(str);
                } else {
                    value = Long.valueOf(str);
                }
            } catch (Exception e) {
                throw new Exception("Значение \"" + str + "\" некорректно");
            }
        }
        return value;
    }
}
