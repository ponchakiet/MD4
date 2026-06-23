package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.CustomerRequestDTO;
import com.ecommerce.customer.dto.CustomerResponseDTO;
import com.ecommerce.customer.dto.LoginRequestDTO;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.exception.CustomerNotFoundException;
import com.ecommerce.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponseDTO registerCustomer(CustomerRequestDTO requestDTO) {
        if (customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String hashedPassword = BCrypt.hashpw(requestDTO.getPassword(), BCrypt.gensalt());

        Customer customer = Customer.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .password(hashedPassword)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponseDTO(savedCustomer);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
        return mapToResponseDTO(customer);
    }

    public Object login(LoginRequestDTO loginRequestDTO) {
        Customer customer = customerRepository.findByEmail(loginRequestDTO.getEmail())
                .orElse(null);

        if (customer != null && BCrypt.checkpw(loginRequestDTO.getPassword(), customer.getPassword())) {
            return mapToResponseDTO(customer);
        }

        return "email or password incorrect";
    }

    private CustomerResponseDTO mapToResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .build();
    }
}
