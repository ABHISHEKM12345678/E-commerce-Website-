package com.example.ecommrece.dto;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false) // Linking to Customer
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @Transient // This field is not stored in the database
    private List<Item> items = new ArrayList<>();

    public Cart() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        updateItemsFromCartItems(); // Update items dynamically
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // Converts CartItem list to Item list dynamically
    public void updateItemsFromCartItems() {
        items.clear();
        for (CartItem cartItem : cartItems) {
            items.add(cartItem.getItem());  // ✅ Ensure `CartItem` has `getItem()` method
        }
    }

    @Override
    public String toString() {
        return "Cart{id=" + id + ", customer=" + customer + ", cartItems=" + cartItems + ", items=" + items + "}";
    }
}
