package com.mqubits.customers.services;

import com.mqubits.customers.models.dto.MembershipDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.mqubits.customers.services.CustomerService.TOPIC_MEMBERSHIP;

@Component
public class TestMembershipKafkaConsumer extends TestKafkaConsumer {

    private final List<MembershipDTO> membershipDTO = new ArrayList<>();

    @KafkaListener(topics = TOPIC_MEMBERSHIP)
    public void receive(MembershipDTO membershipDTO) {
        LOGGER.info("received payload='{}'", membershipDTO.toString());
        setPayload(membershipDTO.toString());
        this.membershipDTO.add(membershipDTO);
        latch.countDown();
    }

    public List<MembershipDTO> getMembershipDTO() {
        return membershipDTO;
    }
}
