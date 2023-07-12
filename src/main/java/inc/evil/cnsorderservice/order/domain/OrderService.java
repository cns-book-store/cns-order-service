package inc.evil.cnsorderservice.order.domain;

import inc.evil.cnsorderservice.book.BookClient;
import inc.evil.cnsorderservice.order.event.OrderAcceptedMessage;
import inc.evil.cnsorderservice.order.event.OrderDispatchedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookClient bookClient;
    private final StreamBridge streamBridge;

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .map(book -> Order.acceptedOrder(book, quantity))
                .defaultIfEmpty(Order.rejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderAcceptedEvent);
    }

    private void publishOrderAcceptedEvent(Order order) {
        if (!order.status().isAccepted()) {
            return;
        }
        OrderAcceptedMessage orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        log.info("Sending order accepted event with id: {}", order.id());
        boolean result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        log.info("Result of sending data for order with id {}: {}", order.id(), result);

    }

    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> orderDispatchedMessages) {
        return orderDispatchedMessages
                .flatMap(message -> orderRepository.findById(message.orderId()))
                .map(Order::dispatchedOrder)
                .flatMap(orderRepository::save);
    }
}
