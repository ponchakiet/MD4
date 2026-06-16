package re.customerservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.customerservice.dto.request.CustomerRequestDTO;
import re.customerservice.dto.request.LoginRequestDTO;
import re.customerservice.dto.response.CustomerResponseDTO;
import re.customerservice.entity.Customer;
import re.customerservice.repository.ICustomerRepository;
import re.customerservice.service.ICustomerService;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private final ICustomerRepository customerRepository;


    @Override
    public CustomerResponseDTO register(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());

        customer.setPassword(Base64.getEncoder().encodeToString(dto.getPassword().getBytes()));

        Customer saved = customerRepository.save(customer);
        return new CustomerResponseDTO(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getAddress());
    }

    @Override
    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return new CustomerResponseDTO(customer.getId(), customer.getFullName(), customer.getEmail(), customer.getAddress());
    }

    @Override
    public CustomerResponseDTO login(LoginRequestDTO dto) {
        Customer customer = customerRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email or password incorrect"));

        String encodedPass = Base64.getEncoder().encodeToString(dto.getPassword().getBytes());
        if (!customer.getPassword().equals(encodedPass)) {
            throw new RuntimeException("Email or password incorrect");
        }
        return new CustomerResponseDTO(customer.getId(), customer.getFullName(), customer.getEmail(), customer.getAddress());
    }
}
