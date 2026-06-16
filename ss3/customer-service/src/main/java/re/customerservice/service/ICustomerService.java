package re.customerservice.service;

import re.customerservice.dto.request.CustomerRequestDTO;
import re.customerservice.dto.request.LoginRequestDTO;
import re.customerservice.dto.response.CustomerResponseDTO;

public interface ICustomerService {
    CustomerResponseDTO register(CustomerRequestDTO dto);
    CustomerResponseDTO getById(Long id);
    CustomerResponseDTO login(LoginRequestDTO dto);
}
