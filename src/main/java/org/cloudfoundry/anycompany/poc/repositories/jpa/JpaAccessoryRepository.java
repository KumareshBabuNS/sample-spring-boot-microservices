package org.cloudfoundry.anycompany.poc.repositories.jpa;

import java.util.List;

import org.cloudfoundry.anycompany.poc.domain.Accessory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"in-memory", "mysql", "postgres", "oracle"})
public interface JpaAccessoryRepository extends JpaRepository<Accessory, String> {
	
	  List<Accessory> findBySkuLike(String sku);  
}
