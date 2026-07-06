package re.medicineservice.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.medicineservice.dto.LoginRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        if ("admin".equals(loginRequest.getUsername()) && "123456".equals(loginRequest.getPassword())) {
            session.setAttribute("currentUser", loginRequest.getUsername());

            return ResponseEntity.ok("Đăng nhập thành công! Session ID của bạn là: " + session.getId());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu!");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(HttpSession session) {
        String username = (String) session.getAttribute("currentUser");
        if (username != null) {
            return ResponseEntity.ok("Xin chào : " + username);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Đã đăng xuất thành công và xóa session.");
    }
}