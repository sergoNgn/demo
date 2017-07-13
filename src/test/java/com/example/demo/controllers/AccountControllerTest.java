package com.example.demo.controllers;

import com.example.demo.DemoApplicationTests;
import com.example.demo.dto.UserAccountDto;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTest extends DemoApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webAppContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void deposit() throws Exception {
        UserAccountDto userAccountDto = new UserAccountDto(123L, "name", "surname", 150L);
        String json = new Gson().toJson(userAccountDto);
        mockMvc.perform(post("/deposit")
               .accept(MediaType.APPLICATION_JSON)
               .header("remoteAddr", "127.0.0.1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(json))
               .andExpect(status().isOk());
    }

    @Test
    public void withdraw() throws Exception {
        mockMvc.perform(get("/withdraw?amount=50&personalId=123")
               .accept(MediaType.APPLICATION_JSON)
               .header("remoteAddr", "127.0.0.1"))
               .andExpect(status().isOk());
    }

    @Test
    public void getAccountOperations() throws Exception {
        mockMvc.perform(get("/account/operations")
               .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    public void getAccountOperationsByUserId() throws Exception {
        mockMvc.perform(get("/account/operations?personalId=123")
               .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }
}