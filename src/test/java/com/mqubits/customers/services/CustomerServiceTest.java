package com.mqubits.customers.services;

import com.mqubits.customers.models.Customer;
import com.mqubits.customers.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceTest {

    private final String TEST_EMAIL_EMPLOYER = "martin-mcfly@gmail.com";
    private final String TEST_EMAIL_EMPLOYEE = "george-mcfly@gmail.com";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    TestTimelineKafkaConsumer testTimelineKafkaConsumer;

    @Autowired
    TestMembershipKafkaConsumer testMembershipKafkaConsumer;

    @BeforeEach
    @AfterAll
    private void removeUsers() {
        customerRepository.deleteAll();
        testTimelineKafkaConsumer.resetLatch();
        testTimelineKafkaConsumer.resetLatch();
    }

    @Test
    void createEmployer() {
        var employer = new Customer();
        employer.setEmail(TEST_EMAIL_EMPLOYER);
        var customer = customerService.createEmployer(employer);
        var employerId = customer.getId();
        var timelineId = customer.getTimeline();

        verifyCountDown(testTimelineKafkaConsumer);
        verifyCountDown(testMembershipKafkaConsumer);

        assertEquals(testTimelineKafkaConsumer.getTimelineDTO().getEmployer(), employerId);
        assertEquals(testTimelineKafkaConsumer.getTimelineDTO().getTimeline(), timelineId);

        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getEmployer(), employerId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getEmployee(), employerId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getTimeline(), timelineId);
    }

    private void verifyCountDown(TestKafkaConsumer consumer) {
        try {
            consumer.getLatch().await(50000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertThat(consumer.getLatch().getCount(), equalTo(0L));
    }

    @Test
    void createEmployee() {
        testMembershipKafkaConsumer.setLatch(2);
        var employer = new Customer();
        employer.setEmail(TEST_EMAIL_EMPLOYER);
        var customer = customerService.createEmployer(employer);
        var employerId = customer.getId();
        var timelineId = customer.getTimeline();

        var employee = new Customer();
        employee.setEmail(TEST_EMAIL_EMPLOYEE);
        var createdEmployee = customerService.createEmployee(employee, employerId);

        assertFalse(createdEmployee.isEmpty());

        var employeeId = ((Customer) createdEmployee.get()).getId();

        verifyCountDown(testMembershipKafkaConsumer);

        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getTimeline(), timelineId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getEmployer(), employerId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(1).getTimeline(), timelineId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(1).getEmployer(), employerId);
    }
}