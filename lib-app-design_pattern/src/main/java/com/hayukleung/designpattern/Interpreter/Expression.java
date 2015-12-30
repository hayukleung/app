package com.hayukleung.designpattern.Interpreter;

import java.util.HashMap;

public abstract class Expression {

    public abstract int interpreter(HashMap<String, Integer> var);

}