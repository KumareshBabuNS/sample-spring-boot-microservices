package org.cloudfoundry.anycompany.poc.web;

import javax.validation.Valid;

import org.cloudfoundry.anycompany.poc.domain.Accessory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accessories")
public class AccessoryController {
    private static final Logger logger = LoggerFactory.getLogger(AccessoryController.class);
    private CrudRepository<Accessory, String> repository;

    @Autowired
    public AccessoryController(CrudRepository<Accessory, String> repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Accessory> accessories() {
        return repository.findAll();
    }
    
    @RequestMapping(value = "/search/{sku}", method = RequestMethod.GET)
    public Iterable<Accessory> accessoriesBySku(@PathVariable String sku) {
        return repository.findAll(); // return repository.findBySku(sku);
    }
    
    
    // default methods

    @RequestMapping(method = RequestMethod.PUT)
    public Accessory add(@RequestBody @Valid Accessory event) {
        logger.info("Adding calendar event " + event.getId());
        return repository.save(event);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Accessory update(@RequestBody @Valid Accessory event) {
        logger.info("Updating calendar event " + event.getId());
        return repository.save(event);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Accessory getById(@PathVariable String id) {
        logger.info("Getting calendar event " + id);
        return repository.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String id) {
        logger.info("Deleting calendar event " + id);
        repository.delete(id);
    }
}