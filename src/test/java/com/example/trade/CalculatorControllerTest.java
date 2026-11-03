package com.example.trade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
@Import(Calculator.class)
class CalculatorControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("GET /test loads calculator form")
    void getForm() throws Exception {
        mvc.perform(get("/test")).andExpect(status().isOk())
                .andExpect(view().name("calculator"))
                .andExpect(model().attributeExists("calcForm"))
                .andExpect(model().attribute("calcForm", hasProperty("op", is("add"))));
    }

    @Test
    @DisplayName("POST addition binds fields")
    void addition() throws Exception {
        mvc.perform(post("/test").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("a", "12").param("b", "8").param("op", "add"))
                .andExpect(status().isOk()).andExpect(view().name("calculator"))
                .andExpect(model().attribute("result", "20"))
                .andExpect(model().attribute("symbol", "+"))
                .andExpect(model().attribute("calcForm", hasProperty("a", is(12))));
    }

    @Test
    @DisplayName("POST decimal division uses a period and 2 digits")
    void division() throws Exception {
        mvc.perform(post("/test").param("a", "5").param("b", "2").param("op", "divide"))
                .andExpect(status().isOk()).andExpect(model().attribute("result", "2.50"));
    }

    @Test
    @DisplayName("POST division by zero gives message")
    void zeroDivision() throws Exception {
        mvc.perform(post("/test").param("a", "10").param("b", "0").param("op", "divide"))
                .andExpect(status().isOk()).andExpect(model().attributeDoesNotExist("result"))
                .andExpect(model().attribute("error", "You cannot divide by zero!"));
    }

    @Test
    @DisplayName("POST unknown operation gives error")
    void wrongOperation() throws Exception {
        mvc.perform(post("/test").param("a", "2").param("b", "3").param("op", "nope"))
                .andExpect(status().isOk()).andExpect(model().attributeExists("error"));
    }
}
