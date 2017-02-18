package org.cloudfoundry.anycompany.poc.repositories.mongodb;

import java.util.List;

import org.cloudfoundry.anycompany.poc.domain.Accessory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongodb")
public interface MongoAccessoryRepository extends MongoRepository<Accessory, String> {
	@Query("select e from Accessory e where e.sku like %?1")
	  List<Accessory> findBySku(String sku);
}