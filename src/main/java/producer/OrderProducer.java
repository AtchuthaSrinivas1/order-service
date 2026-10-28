package producer;

import com.ecommerce.order_service.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderProducer {
    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Order order) {
        kafkaTemplate.send(
                "order-created",
                order.getId().toString(),
                order
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka send failed", ex);
            } else {
            log.info("Message sent to topic={}, partition={}, offset={}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        }
        });
    }

    /* send() internal
   Application
      │
      ▼
   KafkaTemplate.send()
      │
      ▼
   ProducerRecord
      │
      ▼
  Key Serializer
      │
      ▼
  Value Serializer
      │
      ▼
  Partition Selection
      │
      ▼
  Metadata Lookup
      │
      ▼
  Leader Broker
      │
      ▼
  Write to Disk
      │
      ▼
  Replication (optional)
      │
      ▼
     ACK
      │
      ▼
  CompletableFuture Completed
    * */
}
