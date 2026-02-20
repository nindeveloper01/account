package com.api.account.service.impl;

import com.api.account.mapper.CustomerMapper;
import com.api.account.model.Customer;
import com.api.account.model.dto.request.CustomerRequest;
import com.api.account.model.dto.response.CustomerResponse;
import com.api.account.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    @DisplayName("Should map, save, and return customer response")
    void save_Successful() {
        // 1. Arrange: Create the Record with data
        CustomerRequest request = new CustomerRequest(
                "John Doe",
                "john@example.com",
                "1234567890",
                LocalDate.now()
        );

        Customer mockCustomer = new Customer(); // Assume this has similar fields
        CustomerResponse mockResponse = new CustomerResponse(); // Assume this is your DTO

        // Mocking behaviors
        when(customerMapper.fromCustomerRequest(request)).thenReturn(mockCustomer);
        when(customerRepository.save(mockCustomer)).thenReturn(mockCustomer);
        when(customerMapper.toCustomerResponse(mockCustomer)).thenReturn(mockResponse);

        // 2. Act
        CustomerResponse actualResponse = customerService.save(request);

        // 3. Assert
        assertNotNull(actualResponse);
        assertEquals(mockResponse, actualResponse);

        // Verify that the repository's save method was called exactly once
        verify(customerRepository, times(1)).save(mockCustomer);
    }
}