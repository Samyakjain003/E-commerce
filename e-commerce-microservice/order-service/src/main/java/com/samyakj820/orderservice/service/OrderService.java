package com.samyakj820.orderservice.service;

import com.samyakj820.orderservice.dto.InventoryResponse;
import com.samyakj820.orderservice.dto.OrderLineItemsDto;
import com.samyakj820.orderservice.dto.OrderRequest;
import com.samyakj820.orderservice.model.Order;
import com.samyakj820.orderservice.model.OrderLineItems;
import com.samyakj820.orderservice.repository.OrderRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> list = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToOrderLineItems)
                .toList();
        order.setOrderLineItemsList(list);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        //calling inventory service, and place order if product is in stock
        InventoryResponse inventoryResponses[] = webClientBuilder.build().get()
                .uri("http://INVENTORY-SERVICE/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean result = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::getIsInStock);

        if (result) {
            orderRepository.save(order);
            return "Order Placed Successfully!";
        }
        else
            throw new IllegalArgumentException("Product is not in stock, please try again later");
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }
}
