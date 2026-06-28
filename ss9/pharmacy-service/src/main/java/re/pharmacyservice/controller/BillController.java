package re.pharmacyservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bill")
@RefreshScope
public class BillController {

    @Value("${pharmacy.vat-rate:10}")
    private double vatRate;

    @PostMapping
    public String calculateBill(@RequestBody double amount) {
        double tax = amount * (vatRate / 100);
        double total = amount + tax;

        return String.format(
                "Hóa đơn gốc: %.2f | Thuế VAT (%s%%): %.2f | Tổng thanh toán: %.2f",
                amount, vatRate, tax, total
        );
    }
}