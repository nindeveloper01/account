package com.api.account;

import com.api.account.model.dto.request.CustomerRequest;
import com.api.account.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // Forces port 8080
@AutoConfigureMockMvc(addFilters = false)
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        customerRepository.deleteAll(); // Ensures H2 starts empty for every test
    }

    @Test
    void shouldCreateCustomerAndSaveToH2() throws Exception {
        // 1. Arrange: Create a real request
        CustomerRequest request = new CustomerRequest(
                "Full Integration",
                "integration@test.com",
                "000999888",
                LocalDate.now()
        );

        // 2. Act: Call the real Controller -> real Service -> real H2 DB
        mockMvc.perform(post("/api/v1/customers") // Corrected URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                // 3. Assert Response
                .andExpect(status().isCreated()) // Matches your @ResponseStatus(HttpStatus.CREATED)
                .andExpect(jsonPath("$.name").value("Full Integration"));

        // 4. Assert H2 Database: Verify data was actually stored
        assertEquals(1, customerRepository.count());
        System.out.println("TEST PAUSED - Open http://localhost:8080/h2-console now!");
        Thread.sleep(120000); //
    }
    @Test
    void shouldCreateCustomerAndVerifyInH2() throws Exception {
        // 1. Arrange
        CustomerRequest request = new CustomerRequest("Dev Test", "dev@test.com", "999-000", LocalDate.now());

        // 2. Act
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // 3. Assert
        assertEquals(1, customerRepository.count());

        // 4. PAUSE HERE
        System.out.println("TEST PAUSED - Open http://localhost:8080/h2-console now!");
        Thread.sleep(120000); // The JVM will stay alive for 2 minutes
    }
}