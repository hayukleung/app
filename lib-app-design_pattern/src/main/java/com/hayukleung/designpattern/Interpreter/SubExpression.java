package com.hayukleung.designpattern.Interpreter;

import java.util.HashMap;

/**
 * @author lyq
 */
public class SubExpression extends SymbolExpression {
    public SubExpression(Expression _left, Expression _right) {

        super(_left, _right);

    }

    public int interpreter(HashMap<String, Integer> var) {

        return super.left.interpreter(var) - super.right.interpreter(var);

    }
}
