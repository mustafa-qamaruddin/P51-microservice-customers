package com.mqubits.customers.services;

import com.mqubits.customers.models.Customer;
import com.mqubits.customers.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerService {

    @Autowired
    protected CustomerRepository customerRepository;

    public Customer createEmployer(Customer customer) {
        var ret = customerRepository.save(customer);
        // Customer Service notifies Timeline Service with New Employer ID + UUID Timeline ID
        // Customer Service notifies Membership Service with New Employer ID + UUID Timeline ID
        return ret;
    }
}
