package com.example.trade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudentRestControllerTest {
    @Autowired
    MockMvc mvc;
    private static final String STUDENT = """
            {"sname":"Anna Smith","studentClass":"12/B","boy":false}
            """;

    @Test
    void createReadUpdateDelete() throws Exception {
        String created = mvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON).content(STUDENT))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sname").value("Anna Smith"))
                .andReturn().getResponse().getContentAsString();

        long id = new tools.jackson.databind.ObjectMapper().readTree(created).get("id").asLong();
        mvc.perform(get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentClass").value("12/B"));
        mvc.perform(put("/api/students/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(STUDENT.replace("12/B", "12/A")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentClass").value("12/A"));
        mvc.perform(delete("/api/students/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void invalidInputRejected() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sname\":\"\",\"studentClass\":\"12/A\",\"boy\":null}"))
                .andExpect(status().isBadRequest());
    }
}
