package com.api.account.repository;

import com.api.account.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Starts an embedded H2 database
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveAndFindCustomer() {
        // 1. Arrange: Create a new Entity
        Customer customer = new Customer();
        customer.setName("Alice");
        customer.setEmail("alice@example.com");
        customer.setMobileNumber("0987654321");
        customer.setCreateDate(LocalDate.now());

        // 2. Act: Save to H2
        Customer savedCustomer = customerRepository.save(customer);

        // 3. Assert: Check if it exists in H2
        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getCustomerId());

        assertTrue(foundCustomer.isPresent());
        assertEquals("alice@example.com", foundCustomer.get().getEmail());
    }
    @Test
    void testStorage() {
        // ... run your test ...

        // Check the database count
        long count = customerRepository.count();
        System.out.println("Current customers in H2: " + count);

        assertEquals(1, count);
    }
}