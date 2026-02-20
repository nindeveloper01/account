package com.api.account.mapper;

import com.api.account.model.Customer;
import com.api.account.model.dto.request.CustomerRequest;
import com.api.account.model.dto.response.CustomerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer fromCustomerRequest(CustomerRequest customerRequest);
    CustomerResponse toCustomerResponse(Customer customer);
}