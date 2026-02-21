package com.api.account.controller;

import com.api.account.model.dto.request.CustomerRequest;
import com.api.account.model.dto.response.CustomerResponse;
import com.api.account.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // This forces the 201 status
    public CustomerResponse saveCustomer(@RequestBody CustomerRequest request) {
        return customerService.save(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomerResponses();
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse updateCustomer(@PathVariable Long id, @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id,request);
    }

}
