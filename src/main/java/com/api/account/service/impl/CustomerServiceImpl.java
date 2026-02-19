package com.api.account.service.impl;

import com.api.account.mapper.CustomerMapper;
import com.api.account.model.Customer;
import com.api.account.model.dto.CustomerRequest;
import com.api.account.model.dto.CustomerResponse;
import com.api.account.repository.CustomerRepository;
import com.api.account.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl  implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    @Override
    public CustomerResponse save(CustomerRequest customerRequest) {
        Customer customer = customerMapper.fromCustomerRequest(customerRequest);
        customerRepository.save(customer);
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomerResponses() {
        // 1. Fetch the list of customers from your service
        List<Customer> customers = customerRepository.findAll();

        // 2. Map the list of entities to CustomerResponse DTOs
        return customers.stream()
                .map(customerMapper::toCustomerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Customer not found "+ id));
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        // Check existence
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Customer not found with id: " + id);
        }

        // Delete from DB
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        // 1. Check if customer exists
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // 2. Update fields (You can also use a MapStruct @MappingTarget here)
        existingCustomer.setName(request.name());
        existingCustomer.setEmail(request.email());
        existingCustomer.setMobileNumber(request.mobileNumber());

        // 3. Save and return
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toCustomerResponse(updatedCustomer);
    }


}
