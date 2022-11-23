package com.aloumDaum.user.repository;

import com.aloumDaum.user.data.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
    @Query(value = "SELECT * FROM device_details where user_id= ?1 AND is_active=true AND token=?2", nativeQuery = true)
    DeviceEntity getByUserId(String userId,String token);

}
