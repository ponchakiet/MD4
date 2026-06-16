package re.userservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.userservice.entity.User;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    public User getUser() {
        return new User(
                1, "Nguyen Van A"
        );
    }
}
