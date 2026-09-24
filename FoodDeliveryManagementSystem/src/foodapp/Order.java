package foodapp;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a single customer order.
 * Each order stores food items and quantities in a Map (foodId -> quantity).
 * Orders themselves are held in a LinkedList inside FoodDeliverySystem
 * because the number of active orders changes dynamically (placed,
 * modified, cancelled) and a LinkedList allows efficient insertion
 * and removal compared to a fixed-size array.
 */
public class Order {
    public enum Status { PLACED, DELIVERED, CANCELLED }

    private int orderId;
    private Customer customer;
    private Map<Integer, Integer> items; // foodId -> quantity
    private Status status;

    public Order(int orderId, Customer customer) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = new LinkedHashMap<>();
        this.status = Status.PLACED;
    }

    public int getOrderId() { return orderId; }
    public Customer getCustomer() { return customer; }
    public Map<Integer, Integer> getItems() { return items; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public void addItem(int foodId, int qty) {
        items.merge(foodId, qty, Integer::sum);
    }

    public void removeItem(int foodId) {
        items.remove(foodId);
    }

    public double calculateTotal(FoodDeliverySystem system) {
        double total = 0.0;
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            FoodItem item = system.searchFoodById(entry.getKey());
            if (item != null) {
                total += item.getPrice() * entry.getValue();
            }
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Order #%d | Customer: %s | Status: %s | Items: %d",
                orderId, customer.getName(), status, items.size());
    }
}
