package com.mqubits.customers.controllers;

import com.mqubits.customers.models.Customer;
import com.mqubits.customers.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class CustomerController {

    @Autowired
    protected CustomerService customerService;

    @PostMapping("/employer")
    Customer newEmployee(@RequestBody Customer customer) {
        return customerService.createEmployer(customer);
    }

}
