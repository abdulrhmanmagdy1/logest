package com.edham.logistics.repository;

import com.edham.logistics.model.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {
}
