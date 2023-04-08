package com.digdes.school;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static String regex = "("
            + String.join("|", getNames(RequestEntity.Command.class))
            + ")\s*(VALUES(.+?))?(?:WHERE(.+)|$)";

    private static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public static RequestEntity parse(String request) throws Exception {
        RequestEntity requestEntity = null;
        request = upperCaseAndRemoveSpaces(request);
        List<String> titles = getTitles(request);
        request = removeTitles(request);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(request);
        if (matcher.find()) {
            RequestEntity.Command command = RequestEntity.Command.valueOf(matcher.group(1));
            String values = matcher.group(3) != null ? matcher.group(3) : "";
            String restrictions = matcher.group(4) != null ? matcher.group(4) : "";
            String[] strings = {values, restrictions};
            insertTitles(titles, strings);
            values = strings[0];
            restrictions = strings[1];
            RequestEntity.Disjunction[] disjunctions;
            disjunctions = parseRestrictions(restrictions);
            requestEntity = new RequestEntity(command, values, disjunctions);
        } else {
            throw new Exception("Строка " + request + " не соответствует формату запроса");
        }
        return requestEntity;
    }

    private static RequestEntity.Disjunction[] parseRestrictions(String restrictions) {
        List<String> titles = getTitles(restrictions);
        restrictions = removeTitles(restrictions);
        String[] conjunctions = restrictions.split("OR");
        RequestEntity.Disjunction[] disjunctions = new RequestEntity.Disjunction[conjunctions.length];
        insertTitles(titles, conjunctions);
        for (int i = 0; i < conjunctions.length; i++) {
            disjunctions[i] = new RequestEntity.Disjunction();
            disjunctions[i].conjunctions = parseConjunction(conjunctions[i]);
        }
        return disjunctions;
    }

    private static RequestEntity.Comparison[] parseConjunction(String conjunction) {
        List<String> titles = getTitles(conjunction);
        conjunction = removeTitles(conjunction);
        String[] comparisonStrings = conjunction.split("AND");
        insertTitles(titles, comparisonStrings);
        RequestEntity.Comparison[] comparisons = new RequestEntity.Comparison[comparisonStrings.length];
        for (int i = 0; i < comparisons.length; i++) {
            comparisons[i] = parseComparison(comparisonStrings[i]);
        }
        return comparisons;
    }

    private static RequestEntity.Comparison parseComparison(String comparisonString) {
        RequestEntity.Comparison comparison = new RequestEntity.Comparison();
        List<String> titles = getTitles(comparisonString);
        comparisonString = removeTitles(comparisonString);
        Pattern pattern = Pattern.compile("(.*?)(<|>|!=|<=|>=|=|I?LIKE)(.*)");
        Matcher matcher = pattern.matcher(comparisonString);
        if (matcher.find()) {
            String leftPart = matcher.group(1);
            String operator = matcher.group(2);
            String rightPart = matcher.group(3);
            String[] sourceArray =  {leftPart, operator, rightPart};
            insertTitles(titles, sourceArray);
            comparison.leftPart = sourceArray[0];
            comparison.operator = RequestEntity.parseOperator(sourceArray[1]);
            comparison.rightPart = sourceArray[2];
        }
        return comparison;
    }


    private static String removeTitles(String str) {
        if (str == null) return str;
        return str.replaceAll("'.*?'", "''");
    }

    private static void insertTitles(List<String> titles, String[] strings) {
        int j = 0;
        int index = 0;
        for (int i = 0; i < titles.size() && j < strings.length; ) {
            int indexQuotes = strings[j].indexOf("''", index);
            if (strings[j] != null && indexQuotes != -1) {
                String firstPart = strings[j].substring(0, indexQuotes);
                String secondPart = strings[j].substring(indexQuotes);
                index = indexQuotes + 2;
                strings[j] = firstPart + secondPart.replaceFirst("''", titles.get(i));
                i++;
            } else {
                j++;
                index = 0;
            }
        }
    }

    private static List<String> getTitles(String str) {
        List<String> values = new ArrayList<>();
        if (str != null) {
            Pattern pattern = Pattern.compile("'.*?'");
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                values.add(matcher.group());
            }
        }
        return values;
    }

    private static String upperCaseAndRemoveSpaces(String str) {
        if (str == null) return str;
        StringBuilder target = new StringBuilder();
        boolean isQuoteOpen = false;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\'') {
                isQuoteOpen = !isQuoteOpen;
                target.append('\'');
            } else if (!isQuoteOpen) {
                if (String.valueOf(str.charAt(i)).matches("\s")) {
                    if (i > 0 && !String.valueOf(str.charAt(i - 1)).matches("\s")) {
                        target.append(str.charAt(i));
                    }
                } else {
                    target.append(String.valueOf(str.charAt(i)).toUpperCase(Locale.ROOT));
                }
            } else {
                target.append(str.charAt(i));
            }
        }
        return target.toString();
    }
}
