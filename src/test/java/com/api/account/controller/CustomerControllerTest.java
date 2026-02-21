package com.api.account.controller;

import com.api.account.model.dto.request.CustomerRequest;
import com.api.account.model.dto.response.CustomerResponse;
import com.api.account.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = CustomerController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_ShouldReturn201() throws Exception {
        CustomerRequest request = new CustomerRequest("Jane", "jane@email.com", "5551234", LocalDate.now());
        CustomerResponse response = new CustomerResponse(1L, "Jane", "jane@email.com", "5551234", LocalDate.now(), false);

        Mockito.when(customerService.save(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane"));
    }

    @Test
    void getCustomer_ShouldReturn200() throws Exception {
        CustomerResponse response = new CustomerResponse(1L, "Jane", "jane@email.com", "5551234", LocalDate.now(), false);

        Mockito.when(customerService.getCustomer(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@email.com"));
    }

    @Test
    void deleteCustomer_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(customerService).deleteCustomer(1L);
    }
}