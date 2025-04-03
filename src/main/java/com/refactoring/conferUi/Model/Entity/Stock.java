package com.refactoring.conferUi.Model.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class Stock {

    @EmbeddedId
    private StockId stockId;

    private Integer quantity;

    public Stock() {
    }

    public Stock(StockId stockId, Integer quantity) {
        this.stockId = stockId;
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public StockId getStockId() {
        return stockId;
    }

    public void setStockId(StockId stockId) {
        this.stockId = stockId;
    }
}
