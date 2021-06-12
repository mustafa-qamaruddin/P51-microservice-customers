package com.mqubits.customers.services;

import com.mqubits.customers.models.Customer;
import com.mqubits.customers.models.dto.MembershipDTO;
import com.mqubits.customers.models.dto.TimelineDTO;
import com.mqubits.customers.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerService {

    public final static String TOPIC_TIMELINE = "create-timeline";
    public final static String TOPIC_MEMBERSHIP = "create-membership";

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected KafkaProducer kafkaProducer;

    public Customer createEmployer(Customer customer) {
        var timelineUUID = UUID.randomUUID().toString();
        // Create customer
        customer.setTimeline(timelineUUID);
        var ret = customerRepository.save(customer);
        // Customer Service notifies Timeline Service with New Employer ID + UUID Timeline ID
        var timelineDto = new TimelineDTO(ret.getId(), timelineUUID);
        kafkaProducer.send(TOPIC_TIMELINE, timelineDto);
        // Customer Service notifies Membership Service with New Employer ID + UUID Timeline ID + Employee ID
        var membershipDto = new MembershipDTO(ret.getId(), ret.getId(), timelineUUID);
        kafkaProducer.send(TOPIC_MEMBERSHIP, membershipDto);
        return ret;
    }

    public Optional<Object> createEmployee(Customer employee, String employerId) {
        if (customerRepository.findById(employerId).isPresent()) {
            var employer = customerRepository.findById(employerId).get();
            var timeline = employer.getTimeline();
            // Create Employee
            var ret = customerRepository.save(employee);
            // Customer Service notifies Membership Service with New Employee ID + Employer ID + Timeline ID
            var membershipDto = new MembershipDTO(employer.getId(), ret.getId(), timeline);
            kafkaProducer.send(TOPIC_MEMBERSHIP, membershipDto);
            return Optional.of(ret);
        }
        return Optional.empty();
    }
}
