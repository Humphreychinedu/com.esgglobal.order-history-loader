package com.esgglobal.order_history_loader.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("customer")
    private String customer;
    @JsonProperty("items")
    private List<ItemsDTO> items;
    @JsonProperty("status")
    private String status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ItemsDTO {
        @JsonProperty("product")
        private String product;
        @JsonProperty("quantity")
        private Integer quantity;
        @JsonProperty("price")
        private Double price;
    }
}
