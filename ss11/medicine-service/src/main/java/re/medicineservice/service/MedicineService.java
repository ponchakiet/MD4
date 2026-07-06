package re.medicineservice.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import re.medicineservice.entity.Medicine;
import re.medicineservice.repository.MedicineRepository;

import java.util.concurrent.TimeUnit;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final RedissonClient redissonClient;

    public MedicineService(MedicineRepository medicineRepository, RedissonClient redissonClient) {
        this.medicineRepository = medicineRepository;
        this.redissonClient = redissonClient;
    }

    @Cacheable(value = "medicines", key = "#id")
    public Medicine getMedicineById(Long id) {
        System.out.println("==> Method getMedicineById() RUNNING... Fetching from Database for ID: " + id);

        return medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + id));
    }

    @CacheEvict(value = "medicines", key = "#medicine.id")
    public Medicine updateMedicine(Medicine medicine) {
        System.out.println("==> [DB] Đang cập nhật giá thuốc trong Database cho ID: " + medicine.getId());

        Medicine existingMedicine = medicineRepository.findById(medicine.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc để cập nhật!"));

        existingMedicine.setName(medicine.getName());
        existingMedicine.setPrice(medicine.getPrice());

        return medicineRepository.save(existingMedicine);
    }

    public void sellMedicine(Long medicineId, int quantityToBuy) {
        String lockKey = "lock:medicine:" + medicineId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLockAcquired = false;
        try {
            isLockAcquired = lock.tryLock(3, 5, TimeUnit.SECONDS);

            if (isLockAcquired) {
                System.out.println("==> " + Thread.currentThread().getName() + " đã chiếm được LOCK thành công. Tiến hành kiểm tra kho...");

                Thread.sleep(2000);

                Medicine medicine = medicineRepository.findById(medicineId)
                        .orElseThrow(() -> new RuntimeException("Thuốc không tồn tại!"));

                if (medicine.getQuantity() < quantityToBuy) {
                    throw new RuntimeException("Xin lỗi, thuốc " + medicine.getName() + " chỉ còn " + medicine.getQuantity() + " sản phẩm. Không đủ bán!");
                }

                medicine.setQuantity(medicine.getQuantity() - quantityToBuy);
                medicineRepository.save(medicine);

                System.out.println("==> " + Thread.currentThread().getName() + " THANH TOÁN THÀNH CÔNG! Số lượng còn lại trong kho: " + medicine.getQuantity());

            } else {
                System.out.println("❌ " + Thread.currentThread().getName() + " thất bại: Hệ thống đang bận, vui lòng thử lại sau!");
                throw new RuntimeException("Hệ thống đang nghẽn do có nhiều người cùng mua sản phẩm này. Hãy thử lại!");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Quá trình xử lý bị ngắt quãng.");
        } finally {
            if (isLockAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("==> " + Thread.currentThread().getName() + " đã GIẢI PHÓNG LOCK.");
            }
        }
    }
}
