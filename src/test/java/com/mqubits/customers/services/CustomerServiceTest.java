package com.mqubits.customers.services;

import com.mqubits.customers.models.Customer;
import com.mqubits.customers.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
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
    private void removeUsers() {
        customerRepository.deleteAll();
    }

    private void verifyCountDown(TestKafkaConsumer consumer) {
        try {
            consumer.getLatch().await(59, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            consumer.resetLatch();
        }
        assertThat(consumer.getLatch().getCount(), equalTo(0L));
    }

    @Test
    void canSendDTOs() {
        // clear queues
        testMembershipKafkaConsumer.getMembershipDTO().clear();
        testMembershipKafkaConsumer.setLatch(2);

        // create employer
        var employer = new Customer();
        employer.setEmail(TEST_EMAIL_EMPLOYER);
        var customer = customerService.createEmployer(employer);
        var employerId = customer.getId();
        var timelineId = customer.getTimeline();

        // create employee
        var employee = new Customer();
        employee.setEmail(TEST_EMAIL_EMPLOYEE);
        var createdEmployee = customerService.createEmployee(employee, employerId);

        assertFalse(createdEmployee.isEmpty());

        var employeeId = ((Customer) createdEmployee.get()).getId();

        // wait until at least one message is received in kafka
        verifyCountDown(testTimelineKafkaConsumer);

        // verify message of timeline notifications
        assertEquals(testTimelineKafkaConsumer.getTimelineDTO().getEmployer(), employerId);
        assertEquals(testTimelineKafkaConsumer.getTimelineDTO().getTimeline(), timelineId);

        // wait until at least one message is received in kafka
        verifyCountDown(testMembershipKafkaConsumer);

        // verify message of timeline notifications
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().size(), 2);

        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getTimeline(), timelineId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getEmployer(), employerId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(1).getTimeline(), timelineId);
        assertEquals(testMembershipKafkaConsumer.getMembershipDTO().get(1).getEmployer(), employerId);

        assertTrue(
                Objects.equals(testMembershipKafkaConsumer.getMembershipDTO().get(0).getEmployee(), employeeId)
                        || Objects.equals(testMembershipKafkaConsumer.getMembershipDTO().get(1).getEmployee(), employeeId)
        );
    }
}