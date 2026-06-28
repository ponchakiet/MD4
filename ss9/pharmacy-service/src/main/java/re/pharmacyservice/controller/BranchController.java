package re.pharmacyservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BranchController {

    @Value("${app.branch-name}")
    private String branchName;

    @Value("${app.hotline}")
    private String hotline;

    @GetMapping("/info")
    public String getBranchInfo() {
        return "Chào mừng bạn đến với: " + branchName + " - Hotline: " + hotline;
    }
}