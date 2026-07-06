package re.pharmacyservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final List<String> notificationLog = new ArrayList<>();

    @GetMapping("/history")
    public ResponseEntity<List<String>> getNotificationHistory() {
        return ResponseEntity.ok(notificationLog);
    }

    @PostMapping("/log")
    public void logNotification(@RequestBody String message) {
        notificationLog.add(new Date() + ": " + message);
    }
}
