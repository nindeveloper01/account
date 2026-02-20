package com.api.account.service;

import com.api.account.model.dto.request.CustomerRequest;
import com.api.account.model.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse save(CustomerRequest customerRequest);

    List<CustomerResponse> getAllCustomerResponses();

    CustomerResponse getCustomer(Long id);

    void deleteCustomer(Long id);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);
}
