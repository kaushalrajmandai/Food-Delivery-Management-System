Food Delivery Management System - Mini Project 9 (Java Programming)
Team 9: Kaushal Rajmandai, Harsh Kumar, Kunwar Bhosle, Saksham

HOW TO COMPILE AND RUN
-----------------------
1. Make sure JDK 8+ is installed (check with: java -version, javac -version)
2. Open a terminal in this folder (FoodDeliveryManagementSystem/)
3. Compile:
     javac -d bin src/foodapp/*.java
4. Run:
     java -cp bin foodapp.Main

FOLDER STRUCTURE
-----------------------
src/foodapp/FoodItem.java            - Food item entity class
src/foodapp/Customer.java            - Customer entity class
src/foodapp/Order.java               - Order entity class (uses Map for items)
src/foodapp/FoodDeliverySystem.java  - Core logic: Array, LinkedList, HashMap, TreeMap
src/foodapp/MainFrame.java           - Swing GUI (Menu, Place Order, Manage Orders, Billing tabs)
src/foodapp/Main.java                - Application entry point
