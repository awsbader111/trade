package com.example.trade;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CalculatorController {
    private final Calculator calculator;

    public CalculatorController(Calculator calculator) {
        this.calculator = calculator;
    }

    @GetMapping("/test")
    public String showForm(Model model) {
        model.addAttribute("calcForm", new CalculatorForm());
        return "calculator";
    }

    @PostMapping("/test")
    public String calculate(@ModelAttribute("calcForm") CalculatorForm form, Model model) {
        try {
            int a = form.getA(), b = form.getB();
            String op = form.getOp() == null ? "" : form.getOp();
            switch (op) {
                case "add" -> {
                    model.addAttribute("result", String.valueOf(calculator.add(a, b)));
                    model.addAttribute("symbol", "+");
                }
                case "sub" -> {
                    model.addAttribute("result", String.valueOf(calculator.subtract(a, b)));
                    model.addAttribute("symbol", "−");
                }
                case "mul" -> {
                    model.addAttribute("result", String.valueOf(calculator.multiply(a, b)));
                    model.addAttribute("symbol", "×");
                }
                case "divide" -> {
                    model.addAttribute("result", String.format(Locale.US, "%.2f", calculator.divide(a, b)));
                    model.addAttribute("symbol", "÷");
                }
                default -> model.addAttribute("error", "Please select a valid operation.");
            }
        } catch (IllegalArgumentException | ArithmeticException e) {
            model.addAttribute("error", e.getMessage() == null ? "Arithmetic error" : e.getMessage());
        }
        return "calculator";
    }
}
