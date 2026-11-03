package com.example.trade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    private Calculator calc;

    @BeforeEach
    void setUp() {
        calc = new Calculator();
    }

    @Test
    @DisplayName("Addition")
    void add() {
        assertEquals(20, calc.add(15, 5));
    }

    @Test
    @DisplayName("Subtraction")
    void subtract() {
        assertEquals(10, calc.subtract(15, 5));
    }

    @Test
    @DisplayName("Negative subtraction")
    void subtractNegative() {
        assertEquals(-5, calc.subtract(5, 10));
    }

    @Test
    @DisplayName("Multiplication")
    void multiply() {
        assertEquals(25, calc.multiply(5, 5));
    }

    @Test
    @DisplayName("Negative multiplication")
    void multiplyNegative() {
        assertEquals(30, calc.multiply(-3, -10));
    }

    @Test
    @DisplayName("Decimal division")
    void divide() {
        assertEquals(2.5, calc.divide(5, 2));
    }

    @Test
    @DisplayName("Division by zero")
    void divideZero() {
        assertThrows(IllegalArgumentException.class, () -> calc.divide(10, 0));
    }

    @Test
    @DisplayName("Adding overflow fails safely")
    void addOverflow() {
        assertThrows(ArithmeticException.class, () -> calc.add(Integer.MAX_VALUE, 1));
    }

    @Test
    @DisplayName("Subtracting underflow fails safely")
    void subtractUnderflow() {
        assertThrows(ArithmeticException.class, () -> calc.subtract(Integer.MIN_VALUE, 1));
    }

    @Test
    @DisplayName("Division precision")
    void divideRepeating() {
        assertEquals(1.0 / 3, calc.divide(1, 3), 1e-15);
    }

    @Test
    @DisplayName("Multiplication by zero")
    void zeroMultiply() {
        assertEquals(0, calc.multiply(-999, 0));
    }
}
