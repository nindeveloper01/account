package com.api.account.controller;

import com.api.account.model.dto.request.CustomerRequest;
import com.api.account.model.dto.response.CustomerResponse;
import com.api.account.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(CustomerController.class) // Only loads the Controller
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc; // Tool to perform HTTP requests

    @MockBean
    private CustomerService customerService; // Fake version of your service

    @Autowired
    private ObjectMapper objectMapper; // Converts Objects to JSON

    @Test
    void createCustomer_ShouldReturn201() throws Exception {
        // 1. Arrange
        CustomerRequest request = new CustomerRequest("Jane", "jane@email.com", "5551234", LocalDate.now());
        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(1L);
        response.setName("Jane");
        response.setEmail("jane@email.com");
        response.setMobileNumber("5551234");
        response.setCreateDate(LocalDate.now());
        response.setCommunicationAlreadySent(null);

        // Tell the mock service what to return when the controller calls it
        Mockito.when(customerService.save(Mockito.any(CustomerRequest.class))).thenReturn(response);

        // 2. Act & Assert
        mockMvc.perform(post("/api/v1/customers") // Added /v1 here
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane"));
    }
}