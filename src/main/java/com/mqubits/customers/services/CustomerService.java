package com.mqubits.customers.services;

import com.mqubits.customers.models.Customer;
import com.mqubits.customers.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerService {

    private final String TOPIC_TIMELINE = "create-timeline";
    private final String TOPIC_MEMBERSHIP = "create-membership";

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected KafkaProducer kafkaProducer;

    public Customer createEmployer(Customer customer) {
        var timelineUUID = UUID.randomUUID().toString();
        // Create customer
        customer.setTimeline(timelineUUID);
        var ret = customerRepository.save(customer);
        var payload = Map.of(
                "employer", ret.getId(),
                "timeline", timelineUUID,
                "employee", ret.getId()
        );
        // Customer Service notifies Timeline Service with New Employer ID + UUID Timeline ID
        kafkaProducer.send(TOPIC_TIMELINE, payload);
        // Customer Service notifies Membership Service with New Employer ID + UUID Timeline ID
        kafkaProducer.send(TOPIC_MEMBERSHIP, payload);
        return ret;
    }

    public Optional<Object> createEmployee(Customer employee, String employerId) {
        if (customerRepository.findById(employerId).isPresent()) {
            var employer = customerRepository.findById(employerId).get();
            var timeline = employer.getTimeline();
            // Create Employee
            var ret = customerRepository.save(employee);
            var payload = Map.of(
                    "employer", employer.getId(),
                    "timeline", timeline,
                    "employee", ret.getId()
            );
            // Customer Service notifies Membership Service with New Employee ID + Employer ID + Timeline ID
            kafkaProducer.send(TOPIC_MEMBERSHIP, payload);
            return Optional.of(ret);
        }
        return Optional.empty();
    }
}
