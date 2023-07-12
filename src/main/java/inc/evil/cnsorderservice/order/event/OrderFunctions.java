package inc.evil.cnsorderservice.order.event;

import inc.evil.cnsorderservice.order.domain.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class OrderFunctions {

    @Bean
    public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(OrderService orderService){
        return orderDispatchedMessages ->
                orderService.consumeOrderDispatchedEvent(orderDispatchedMessages)
                        .doOnNext(order -> log.info("The order with id {} is dispatched", order.id()))
                        .subscribe();
    }
}
