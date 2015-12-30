package com.hayukleung.designpattern.Interpreter;

import java.util.HashMap;

public class AddExpression extends SymbolExpression {

    public AddExpression(Expression _left, Expression _right) {

        super(_left, _right);

    }

    @Override
    public int interpreter(HashMap<String, Integer> var) {
        return super.left.interpreter(var) + super.right.interpreter(var);
    }

}