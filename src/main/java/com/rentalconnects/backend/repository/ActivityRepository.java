package com.rentalconnects.backend.repository;

import com.rentalconnects.backend.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {

    List<Activity> findByTenantIdOrderByTimestampDesc(String tenantId);

    List<Activity> findByLandlordIdOrderByTimestampDesc(String landlordId);
}