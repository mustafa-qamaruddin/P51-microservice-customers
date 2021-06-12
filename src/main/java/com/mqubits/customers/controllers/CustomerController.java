package com.mqubits.customers.controllers;

import com.mqubits.customers.models.Customer;
import com.mqubits.customers.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
public class CustomerController {

    @Autowired
    protected CustomerService customerService;

    @PostMapping("/employer")
    ResponseEntity<Map> newEmployer(@RequestBody Customer customer) {
        var employer = customerService.createEmployer(customer);
        return new ResponseEntity<Map>(Map.of("id", employer.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/employee")
    ResponseEntity<Map> newEmployee(@RequestBody Customer customer, @RequestParam(required = true, name = "employerId") String employerId) {
        var employee = customerService.createEmployee(customer, employerId);
        if (employee.isEmpty()) {
            return new ResponseEntity<Map>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Map>(Map.of("id", ((Customer) employee.get()).getId()), HttpStatus.CREATED);
    }

}
