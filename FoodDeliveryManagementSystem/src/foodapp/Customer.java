package foodapp;

/** Represents a customer who places orders. */
public class Customer {
    private int id;
    private String name;
    private String phone;
    private String address;

    public Customer(int id, String name, String phone, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", id, name, phone, address);
    }
}
