package foodapp;

/**
 * Represents a food item offered by a restaurant.
 * This is one of the core "real-world entity -> class" mappings
 * required by the Java Programming mini project guidelines.
 */
public class FoodItem {
    private int id;
    private String name;
    private String category;
    private double price;
    private String restaurant;

    public FoodItem(int id, String name, String category, double price, String restaurant) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.restaurant = restaurant;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getRestaurant() { return restaurant; }

    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("[%d] %-18s | %-10s | Rs.%-8.2f | %s",
                id, name, category, price, restaurant);
    }
}
