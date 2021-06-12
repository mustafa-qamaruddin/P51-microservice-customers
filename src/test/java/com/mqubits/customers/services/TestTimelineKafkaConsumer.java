package com.mqubits.customers.services;

import com.mqubits.customers.models.dto.TimelineDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.mqubits.customers.services.CustomerService.TOPIC_TIMELINE;

@Component
public class TestTimelineKafkaConsumer extends TestKafkaConsumer {

    private TimelineDTO timelineDTO;

    @KafkaListener(topics = TOPIC_TIMELINE)
    public void receive(TimelineDTO timelineDTO) {
        LOGGER.info("received payload='{}'", timelineDTO.toString());
        setPayload(timelineDTO.toString());
        this.timelineDTO = timelineDTO;
        latch.countDown();
    }

    public TimelineDTO getTimelineDTO() {
        return timelineDTO;
    }
}
