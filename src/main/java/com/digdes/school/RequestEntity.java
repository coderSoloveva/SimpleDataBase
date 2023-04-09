package com.digdes.school;

public class RequestEntity {
    public enum Command {
        INSERT,
        UPDATE,
        SELECT,
        DELETE
    }

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        LESS_OR_EQUALS,
        MORE_OR_EQUALS,
        LESS,
        MORE,
        LIKE,
        ILIKE,
    }

    public static class Disjunction {
        Comparison[] conjunctions;
    }

    public static class Comparison {
        String leftPart;
        Operator operator;
        String rightPart;
    }

    Command command;
    String values;
    Disjunction[] disjunctions;

    public RequestEntity(Command command, String values, Disjunction[] disjunctions) {
        this.command = command;
        this.values = values;
        this.disjunctions = disjunctions;
    }


    public static Operator parseOperator(String operatorString) {
        switch (operatorString.toUpperCase()) {
            case "=":
                return Operator.EQUALS;
            case "!=":
                return Operator.NOT_EQUALS;
            case "<=":
                return Operator.LESS_OR_EQUALS;
            case ">=":
                return Operator.MORE_OR_EQUALS;
            case "<":
                return Operator.LESS;
            case ">":
                return Operator.MORE;
            case "LIKE":
                return Operator.LIKE;
            case "ILIKE":
                return Operator.ILIKE;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operatorString);
        }
    }
}
