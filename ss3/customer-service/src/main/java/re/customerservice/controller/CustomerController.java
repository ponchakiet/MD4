package re.customerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.customerservice.dto.request.CustomerRequestDTO;
import re.customerservice.dto.request.LoginRequestDTO;
import re.customerservice.dto.response.CustomerResponseDTO;
import re.customerservice.service.ICustomerService;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    @Autowired
    private ICustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDTO> register(@RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.register(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PutMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        try {
            return ResponseEntity.ok(customerService.login(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
