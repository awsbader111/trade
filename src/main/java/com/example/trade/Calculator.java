package com.example.trade;

import org.springframework.stereotype.Component;

@Component
public class Calculator {
    public int add(int a, int b) {
        return Math.addExact(a, b);
    }

    public int subtract(int a, int b) {
        return Math.subtractExact(a, b);
    }

    public int multiply(int a, int b) {
        return Math.multiplyExact(a, b);
    }

    public double divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("You cannot divide by zero!");
        return (double) a / b;
    }
}
