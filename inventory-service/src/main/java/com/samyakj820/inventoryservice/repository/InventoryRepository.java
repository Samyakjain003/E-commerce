package com.samyakj820.inventoryservice.repository;

import com.samyakj820.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Optional<Inventory> findBySkuCode(String skuCode);
}
