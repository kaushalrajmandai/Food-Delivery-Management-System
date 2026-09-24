package foodapp;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Food Delivery Management System (Mini Project 9).
 * Team 9: Kaushal Rajmandai, Harsh Kumar, Kunwar Bhosle, Saksham.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
