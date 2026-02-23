package com.api.account.repository;

import com.api.account.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer alice;

    @BeforeEach
    void setUp() {
        // Prepare data before EVERY test
        alice = new Customer();
        alice.setName("Alice");
        alice.setEmail("alice@example.com");
        alice.setMobileNumber("0987654321");
        alice.setCreateDate(LocalDate.now());
    }
    @Test
    void shouldSaveAndFindCustomer() {
        // Arrange
        Customer customer = new Customer(null, "Alice", "alice@example.com", "0987654321", LocalDate.now(), false);

        // Act
        Customer saved = customerRepository.save(customer);

        // Assert
        Optional<Customer> found = customerRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
    }

    @Test
    void testStorage() {
        customerRepository.save(alice);

        long count = customerRepository.count();
        // This will now pass because we saved inside the setup/test
        assertEquals(1, count);
    }


}