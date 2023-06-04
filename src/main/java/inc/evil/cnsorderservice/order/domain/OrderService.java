package inc.evil.cnsorderservice.order.domain;

import inc.evil.cnsorderservice.book.BookClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookClient bookClient;

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .map(book -> Order.acceptedOrder(book, quantity))
                .defaultIfEmpty(Order.rejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }
}
