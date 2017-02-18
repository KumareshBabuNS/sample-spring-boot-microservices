package org.cloudfoundry.anycompany.poc.repositories.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cloudfoundry.anycompany.poc.domain.Accessory;
import org.cloudfoundry.anycompany.poc.domain.RandomIdGenerator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

public class RedisAccessoryRepository implements CrudRepository<Accessory, String> {
    public static final String ACCESSORIES_KEY = "accessories";

    private final RandomIdGenerator idGenerator;
    private final HashOperations<String, String, Accessory> hashOps;

    public RedisAccessoryRepository(RedisTemplate<String, Accessory> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();
        this.idGenerator = new RandomIdGenerator();
    }

    @Override
    public <S extends Accessory> S save(S event) {
        if (event.getId() == null) {
            event.setId(idGenerator.generateId());
        }

        hashOps.put(ACCESSORIES_KEY, event.getId(), event);

        return event;
    }

    @Override
    public <S extends Accessory> Iterable<S> save(Iterable<S> events) {
        List<S> result = new ArrayList<>();

        for (S entity : events) {
            save(entity);
            result.add(entity);
        }

        return result;
    }

    @Override
    public Accessory findOne(String id) {
        return hashOps.get(ACCESSORIES_KEY, id);
    }

    @Override
    public boolean exists(String id) {
        return hashOps.hasKey(ACCESSORIES_KEY, id);
    }

    @Override
    public Iterable<Accessory> findAll() {
        return hashOps.values(ACCESSORIES_KEY);
    }

    @Override
    public Iterable<Accessory> findAll(Iterable<String> ids) {
        return hashOps.multiGet(ACCESSORIES_KEY, convertIterableToList(ids));
    }

    @Override
    public long count() {
        return hashOps.keys(ACCESSORIES_KEY).size();
    }

    @Override
    public void delete(String id) {
        hashOps.delete(ACCESSORIES_KEY, id);
    }

    @Override
    public void delete(Accessory event) {
        hashOps.delete(ACCESSORIES_KEY, event.getId());
    }

    @Override
    public void delete(Iterable<? extends Accessory> events) {
        for (Accessory album : events) {
            delete(album);
        }
    }

    @Override
    public void deleteAll() {
        Set<String> ids = hashOps.keys(ACCESSORIES_KEY);
        for (String id : ids) {
            delete(id);
        }
    }

    private <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T object : iterable) {
            list.add(object);
        }
        return list;
    }
    
    //TODO: RSG
    
    public <T> List<T> findBySku(String sku) {
    	return null;
    }
    
}
