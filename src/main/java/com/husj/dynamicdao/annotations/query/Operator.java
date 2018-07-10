package com.husj.dynamicdao.annotations.query;

/**
 * @author 胡胜钧
 * @date 7/1 0001.
 */
public enum Operator {

    AND("AND"), OR("OR");

    private String operator;

    Operator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
