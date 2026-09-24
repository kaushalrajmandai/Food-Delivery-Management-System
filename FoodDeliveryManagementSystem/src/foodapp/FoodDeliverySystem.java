package foodapp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Core application logic for the Food Delivery Management System.
 *
 * Data structures used (as required by the mini project guidelines):
 *  - Array (FoodItem[])        : stores the fixed-size restaurant menu loaded at startup.
 *  - LinkedList<Order>         : maintains the dynamically changing list of customer
 *                                orders (orders are placed, modified and cancelled at runtime).
 *  - HashMap<Integer,FoodItem> : provides O(1) average lookup of a food item by its ID.
 *  - TreeMap<Integer,FoodItem> : keeps the menu automatically sorted by food ID for
 *                                sorted display / reporting.
 */
public class FoodDeliverySystem {

    private static final int MENU_CAPACITY = 50;
    private FoodItem[] menuArray;      // fixed-size array of the base menu
    private int menuCount;

    private HashMap<Integer, FoodItem> foodMap;      // fast search by ID
    private TreeMap<Integer, FoodItem> sortedFoodMap; // sorted view by ID

    private LinkedList<Order> orders;   // dynamic list of orders
    private LinkedList<Customer> customers;

    private int nextOrderId = 1001;
    private int nextCustomerId = 1;

    public FoodDeliverySystem() {
        menuArray = new FoodItem[MENU_CAPACITY];
        menuCount = 0;
        foodMap = new HashMap<>();
        sortedFoodMap = new TreeMap<>();
        orders = new LinkedList<>();
        customers = new LinkedList<>();
        loadSampleMenu();
    }

    // ---------- Menu (Array + HashMap + TreeMap) ----------

    private void loadSampleMenu() {
        addFoodItem(new FoodItem(101, "Margherita Pizza", "Main Course", 249.00, "Napoli House"));
        addFoodItem(new FoodItem(102, "Veg Burger", "Snacks", 99.00, "Burger Point"));
        addFoodItem(new FoodItem(103, "Chicken Biryani", "Main Course", 219.00, "Spice Route"));
        addFoodItem(new FoodItem(104, "Paneer Tikka", "Starter", 179.00, "Spice Route"));
        addFoodItem(new FoodItem(105, "French Fries", "Snacks", 89.00, "Burger Point"));
        addFoodItem(new FoodItem(106, "Cold Coffee", "Beverage", 79.00, "Cafe Mocha"));
        addFoodItem(new FoodItem(107, "Masala Dosa", "Main Course", 129.00, "South Spice"));
        addFoodItem(new FoodItem(108, "Chocolate Brownie", "Dessert", 99.00, "Cafe Mocha"));
    }

    /** Adds a new food item to the array-backed menu and index structures. */
    public boolean addFoodItem(FoodItem item) {
        if (foodMap.containsKey(item.getId())) {
            return false; // duplicate ID not allowed
        }
        if (menuCount >= menuArray.length) {
            return false; // array full
        }
        menuArray[menuCount++] = item;
        foodMap.put(item.getId(), item);
        sortedFoodMap.put(item.getId(), item);
        return true;
    }

    /** Removes a food item from the array and the maps. */
    public boolean removeFoodItem(int foodId) {
        if (!foodMap.containsKey(foodId)) return false;
        for (int i = 0; i < menuCount; i++) {
            if (menuArray[i].getId() == foodId) {
                for (int j = i; j < menuCount - 1; j++) {
                    menuArray[j] = menuArray[j + 1];
                }
                menuArray[--menuCount] = null;
                break;
            }
        }
        foodMap.remove(foodId);
        sortedFoodMap.remove(foodId);
        return true;
    }

    /** HashMap-based fast lookup by food ID. */
    public FoodItem searchFoodById(int foodId) {
        return foodMap.get(foodId);
    }

    /** Linear search through the array by (partial, case-insensitive) name. */
    public List<FoodItem> searchFoodByName(String name) {
        List<FoodItem> results = new ArrayList<>();
        String lower = name.toLowerCase();
        for (int i = 0; i < menuCount; i++) {
            if (menuArray[i].getName().toLowerCase().contains(lower)) {
                results.add(menuArray[i]);
            }
        }
        return results;
    }

    /** Returns the menu in insertion order (array order). */
    public List<FoodItem> getMenuArrayOrder() {
        List<FoodItem> list = new ArrayList<>();
        for (int i = 0; i < menuCount; i++) list.add(menuArray[i]);
        return list;
    }

    /** Returns the menu sorted by food ID (TreeMap order). */
    public List<FoodItem> getMenuSortedById() {
        return new ArrayList<>(sortedFoodMap.values());
    }

    // ---------- Customers ----------

    public Customer registerCustomer(String name, String phone, String address) {
        Customer c = new Customer(nextCustomerId++, name, phone, address);
        customers.add(c);
        return c;
    }

    public LinkedList<Customer> getCustomers() {
        return customers;
    }

    // ---------- Orders (LinkedList) ----------

    public Order placeOrder(Customer customer) {
        Order order = new Order(nextOrderId++, customer);
        orders.add(order);
        return order;
    }

    public boolean cancelOrder(int orderId) {
        for (Order o : orders) {
            if (o.getOrderId() == orderId) {
                o.setStatus(Order.Status.CANCELLED);
                return true;
            }
        }
        return false;
    }

    public boolean modifyOrder(int orderId, int foodId, int newQty) {
        Order order = findOrder(orderId);
        if (order == null) return false;
        if (newQty <= 0) {
            order.removeItem(foodId);
        } else {
            order.getItems().put(foodId, newQty);
        }
        return true;
    }

    public Order findOrder(int orderId) {
        for (Order o : orders) {
            if (o.getOrderId() == orderId) return o;
        }
        return null;
    }

    public LinkedList<Order> getOrders() {
        return orders;
    }

    public double generateBill(int orderId) {
        Order order = findOrder(orderId);
        if (order == null) return -1;
        return order.calculateTotal(this);
    }
}
