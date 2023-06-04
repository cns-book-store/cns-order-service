package inc.evil.cnsorderservice.order.domain;

import inc.evil.cnsorderservice.common.AbstractTestContainersIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest extends AbstractTestContainersIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void createRejectedOrder() {
        var rejectedOrder = Order.rejectedOrder("1234567890", 3);
        StepVerifier
                .create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))
                .verifyComplete();
    }
}
