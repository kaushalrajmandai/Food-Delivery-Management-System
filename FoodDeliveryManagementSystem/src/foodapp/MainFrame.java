package foodapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Swing-based graphical user interface for the Food Delivery Management System.
 * Provides tabs for: Menu, Place Order, Manage Orders, and Billing.
 */
public class MainFrame extends JFrame {

    private final FoodDeliverySystem system;

    private DefaultTableModel menuTableModel;
    private JTable menuTable;
    private JComboBox<String> sortModeBox;

    private JComboBox<Customer> customerBox;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;

    private DefaultTableModel ordersTableModel;
    private JTable ordersTable;

    private JTextArea billArea;
    private JTextField billOrderIdField;

    public MainFrame() {
        super("Food Delivery Management System - Team 9");
        system = new FoodDeliverySystem();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Menu", buildMenuPanel());
        tabs.addTab("Place Order", buildOrderPanel());
        tabs.addTab("Manage Orders", buildManageOrdersPanel());
        tabs.addTab("Billing", buildBillingPanel());

        add(tabs);
    }

    // ---------------- Menu Tab ----------------

    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        menuTableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Price (Rs.)", "Restaurant"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        refreshMenuTable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("View order:"));
        sortModeBox = new JComboBox<>(new String[]{"As Added (Array)", "Sorted by ID (TreeMap)"});
        sortModeBox.addActionListener(e -> refreshMenuTable(sortModeBox.getSelectedIndex() == 1));
        top.add(sortModeBox);

        JTextField searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search by Name");
        searchBtn.addActionListener(e -> {
            List<FoodItem> results = system.searchFoodByName(searchField.getText().trim());
            populateMenuTable(results);
        });
        JButton searchIdBtn = new JButton("Search by ID");
        JTextField idField = new JTextField(6);
        searchIdBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                FoodItem item = system.searchFoodById(id);
                menuTableModel.setRowCount(0);
                if (item != null) addFoodRow(item);
                else JOptionPane.showMessageDialog(this, "No food item found with ID " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric ID.");
            }
        });
        JButton resetBtn = new JButton("Show All");
        resetBtn.addActionListener(e -> refreshMenuTable(sortModeBox.getSelectedIndex() == 1));

        top.add(new JLabel("   Name:"));
        top.add(searchField);
        top.add(searchBtn);
        top.add(new JLabel("   ID:"));
        top.add(idField);
        top.add(searchIdBtn);
        top.add(resetBtn);

        JPanel addPanel = buildAddFoodPanel();

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        panel.add(addPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildAddFoodPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder("Add New Food Item"));
        JTextField idF = new JTextField(4);
        JTextField nameF = new JTextField(10);
        JTextField catF = new JTextField(8);
        JTextField priceF = new JTextField(6);
        JTextField restF = new JTextField(10);
        JButton addBtn = new JButton("Add Item");

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idF.getText().trim());
                String name = nameF.getText().trim();
                String cat = catF.getText().trim();
                double price = Double.parseDouble(priceF.getText().trim());
                String rest = restF.getText().trim();

                if (name.isEmpty() || cat.isEmpty() || rest.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                    return;
                }
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative.");
                    return;
                }
                boolean added = system.addFoodItem(new FoodItem(id, name, cat, price, rest));
                if (!added) {
                    JOptionPane.showMessageDialog(this, "Duplicate ID or menu is full.");
                } else {
                    refreshMenuTable(sortModeBox.getSelectedIndex() == 1);
                    idF.setText(""); nameF.setText(""); catF.setText("");
                    priceF.setText(""); restF.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID must be an integer and Price must be numeric.");
            }
        });

        p.add(new JLabel("ID:")); p.add(idF);
        p.add(new JLabel("Name:")); p.add(nameF);
        p.add(new JLabel("Category:")); p.add(catF);
        p.add(new JLabel("Price:")); p.add(priceF);
        p.add(new JLabel("Restaurant:")); p.add(restF);
        p.add(addBtn);
        return p;
    }

    private void refreshMenuTable(boolean sorted) {
        List<FoodItem> items = sorted ? system.getMenuSortedById() : system.getMenuArrayOrder();
        populateMenuTable(items);
    }

    private void populateMenuTable(List<FoodItem> items) {
        menuTableModel.setRowCount(0);
        for (FoodItem item : items) addFoodRow(item);
    }

    private void addFoodRow(FoodItem item) {
        menuTableModel.addRow(new Object[]{
                item.getId(), item.getName(), item.getCategory(),
                String.format("%.2f", item.getPrice()), item.getRestaurant()
        });
    }

    // ---------------- Place Order Tab ----------------

    private JPanel buildOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel custPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        custPanel.setBorder(BorderFactory.createTitledBorder("Customer"));
        customerBox = new JComboBox<>();
        JTextField nameF = new JTextField(10);
        JTextField phoneF = new JTextField(8);
        JTextField addrF = new JTextField(14);
        JButton regBtn = new JButton("Register Customer");
        regBtn.addActionListener(e -> {
            if (nameF.getText().trim().isEmpty() || phoneF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and phone are required.");
                return;
            }
            Customer c = system.registerCustomer(nameF.getText().trim(), phoneF.getText().trim(), addrF.getText().trim());
            customerBox.addItem(c);
            customerBox.setSelectedItem(c);
            nameF.setText(""); phoneF.setText(""); addrF.setText("");
        });
        custPanel.add(new JLabel("Select:")); custPanel.add(customerBox);
        custPanel.add(new JLabel("Name:")); custPanel.add(nameF);
        custPanel.add(new JLabel("Phone:")); custPanel.add(phoneF);
        custPanel.add(new JLabel("Address:")); custPanel.add(addrF);
        custPanel.add(regBtn);

        cartTableModel = new DefaultTableModel(new Object[]{"Food ID", "Name", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartTableModel);

        JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addItemPanel.setBorder(BorderFactory.createTitledBorder("Add Food to Order"));
        JTextField foodIdF = new JTextField(5);
        JTextField qtyF = new JTextField(4);
        JButton addItemBtn = new JButton("Add to Cart");

        final Order[] currentOrder = new Order[1];

        JButton newOrderBtn = new JButton("Start New Order");
        newOrderBtn.addActionListener(e -> {
            Customer selected = (Customer) customerBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Register or select a customer first.");
                return;
            }
            currentOrder[0] = system.placeOrder(selected);
            cartTableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "New order started: #" + currentOrder[0].getOrderId());
            refreshOrdersTable();
        });

        addItemBtn.addActionListener(e -> {
            if (currentOrder[0] == null) {
                JOptionPane.showMessageDialog(this, "Start a new order first.");
                return;
            }
            try {
                int foodId = Integer.parseInt(foodIdF.getText().trim());
                int qty = Integer.parseInt(qtyF.getText().trim());
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                    return;
                }
                FoodItem item = system.searchFoodById(foodId);
                if (item == null) {
                    JOptionPane.showMessageDialog(this, "No such food ID in menu.");
                    return;
                }
                currentOrder[0].addItem(foodId, qty);
                refreshCartTable(currentOrder[0]);
                refreshOrdersTable();
                foodIdF.setText(""); qtyF.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Food ID and quantity must be numeric.");
            }
        });

        addItemPanel.add(new JLabel("Food ID:")); addItemPanel.add(foodIdF);
        addItemPanel.add(new JLabel("Qty:")); addItemPanel.add(qtyF);
        addItemPanel.add(addItemBtn);
        addItemPanel.add(newOrderBtn);

        JPanel north = new JPanel(new GridLayout(2, 1));
        north.add(custPanel);
        north.add(addItemPanel);

        panel.add(north, BorderLayout.NORTH);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshCartTable(Order order) {
        cartTableModel.setRowCount(0);
        for (Map.Entry<Integer, Integer> entry : order.getItems().entrySet()) {
            FoodItem item = system.searchFoodById(entry.getKey());
            if (item == null) continue;
            double subtotal = item.getPrice() * entry.getValue();
            cartTableModel.addRow(new Object[]{
                    entry.getKey(), item.getName(), entry.getValue(), String.format("%.2f", subtotal)
            });
        }
    }

    // ---------------- Manage Orders Tab ----------------

    private JPanel buildManageOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ordersTableModel = new DefaultTableModel(
                new Object[]{"Order ID", "Customer", "Items", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(ordersTableModel);
        refreshOrdersTable();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField orderIdF = new JTextField(6);
        JButton cancelBtn = new JButton("Cancel Order");
        JButton refreshBtn = new JButton("Refresh");

        cancelBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(orderIdF.getText().trim());
                boolean ok = system.cancelOrder(id);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Order #" + id + " cancelled.");
                    refreshOrdersTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Order not found.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid Order ID.");
            }
        });
        refreshBtn.addActionListener(e -> refreshOrdersTable());

        actions.add(new JLabel("Order ID:")); actions.add(orderIdF);
        actions.add(cancelBtn); actions.add(refreshBtn);

        panel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshOrdersTable() {
        ordersTableModel.setRowCount(0);
        for (Order o : system.getOrders()) {
            ordersTableModel.addRow(new Object[]{
                    o.getOrderId(), o.getCustomer().getName(), o.getItems().size(), o.getStatus()
            });
        }
    }

    // ---------------- Billing Tab ----------------

    private JPanel buildBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        billOrderIdField = new JTextField(6);
        JButton genBtn = new JButton("Generate Bill");
        top.add(new JLabel("Order ID:")); top.add(billOrderIdField); top.add(genBtn);

        billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        genBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(billOrderIdField.getText().trim());
                Order order = system.findOrder(id);
                if (order == null) {
                    JOptionPane.showMessageDialog(this, "Order not found.");
                    return;
                }
                if (order.getStatus() == Order.Status.CANCELLED) {
                    billArea.setText("Order #" + id + " was cancelled. No bill generated.");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("===== FOOD DELIVERY BILL =====\n");
                sb.append("Order ID   : ").append(order.getOrderId()).append("\n");
                sb.append("Customer   : ").append(order.getCustomer().getName()).append("\n");
                sb.append("Phone      : ").append(order.getCustomer().getPhone()).append("\n");
                sb.append("--------------------------------\n");
                double total = 0;
                for (Map.Entry<Integer, Integer> entry : order.getItems().entrySet()) {
                    FoodItem item = system.searchFoodById(entry.getKey());
                    if (item == null) continue;
                    double subtotal = item.getPrice() * entry.getValue();
                    total += subtotal;
                    sb.append(String.format("%-18s x%-3d Rs.%8.2f%n", item.getName(), entry.getValue(), subtotal));
                }
                sb.append("--------------------------------\n");
                sb.append(String.format("TOTAL: Rs.%.2f%n", total));
                billArea.setText(sb.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid Order ID.");
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(billArea), BorderLayout.CENTER);
        return panel;
    }
}
