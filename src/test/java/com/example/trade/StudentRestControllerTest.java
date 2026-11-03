package com.example.trade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudentRestControllerTest {
    @Autowired
    MockMvc mvc;
    private static final String STUDENT = """
            {"firstName":"Anna","lastName":"Smith","email":"anna@example.com","program":"Computer Science","credits":45}
            """;

    @Test
    void createReadUpdateDelete() throws Exception {
        String location = mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(STUDENT))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.firstName").value("Anna"))
                .andReturn().getResponse().getContentAsString();
        long id = new tools.jackson.databind.ObjectMapper().readTree(location).get("id").asLong();
        mvc.perform(get("/api/students/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.credits").value(45));
        mvc.perform(put("/api/students/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(STUDENT.replace("45", "55"))).andExpect(status().isOk()).andExpect(jsonPath("$.credits").value(55));
        mvc.perform(delete("/api/students/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void invalidInputRejected() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"A\",\"email\":\"bad\",\"program\":\"IT\",\"credits\":-1}"))
                .andExpect(status().isBadRequest());
    }
}
