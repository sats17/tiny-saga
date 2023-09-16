//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
//import org.springframework.kafka.test.utils.ContainerTestUtils;
//import org.springframework.test.annotation.DirtiesContext;
//
//@SpringBootTest
//@EmbeddedKafka(topics = "order-topic", partitions = 1)
//@DirtiesContext
//public class AppTest {
//
//    @Autowired
//    private KafkaEventConsumer kafkaEventConsumer;
//
//    @Autowired
//    private EmbeddedKafkaRule embeddedKafka;
//
//    @Test
//    public void testConsumeEvent() throws Exception {
//        // Wait for embedded Kafka to be ready
//        ContainerTestUtils.waitForAssignment(embeddedKafka.getEmbeddedKafka(), 1);
//
//        // Create a test message
//        String message = "Test Message";
//        // Produce the test message to the test topic
//        // You can use a Kafka producer or a KafkaTemplate to do this
//
//        // Wait for the consumer to process the message (you might need to add some delays if necessary)
//        Thread.sleep(1000);
//
//        // Add your assertions to verify the behavior of the consumer
//        // For example:
//        // assert ...
//
//        // Optionally, add assertions to verify that your method produced the expected result
//        // For example:
//        // assert ...
//    }
//}
