package inc.evil.cnsorderservice.order.web;

import inc.evil.cnsorderservice.order.domain.Order;
import inc.evil.cnsorderservice.order.domain.OrderService;
import inc.evil.cnsorderservice.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private OrderService orderService;

    @Test
    void getAllOrders() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = Order.rejectedOrder(orderRequest.isbn(), orderRequest.quantity());
        given(orderService.getAllOrders()).willReturn(Flux.just(expectedOrder));

        webClient
                .get()
                .uri("/api/v1/orders")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Order.class)
                .value(orders -> {
                    assertThat(orders).hasSize(1);
                    assertThat(orders.get(0).status()).isEqualTo(OrderStatus.REJECTED);
                });
    }

    @Test
    void submitOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = Order.rejectedOrder(orderRequest.isbn(), orderRequest.quantity());
        given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity())).willReturn(Mono.just(expectedOrder));

        webClient
                .post()
                .uri("/api/v1/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class)
                .value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }
}

