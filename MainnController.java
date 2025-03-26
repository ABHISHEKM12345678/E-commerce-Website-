package com.example.ecommrece.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommrece.dto.Cart;
import com.example.ecommrece.dto.CartItem;
import com.example.ecommrece.dto.Customer;
import com.example.ecommrece.dto.Item;
import com.example.ecommrece.dto.Order;
import com.example.ecommrece.dto.OrderItem;
import com.example.ecommrece.dto.Product;
import com.example.ecommrece.dto.Seller;
import com.example.ecommrece.repository.CartRepository;
import com.example.ecommrece.repository.CustomerRepository;
import com.example.ecommrece.repository.OrderRepository;
import com.example.ecommrece.repository.ProductRepository;
import com.example.ecommrece.repository.SellerRepository;
import com.example.ecommrece.service.MyService;
import com.example.ecommrece.service.MyServiceSeller;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainnController {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    MyServiceSeller myServiceSeller;
    
    @Autowired
    MyService myService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SellerRepository sellerRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository; 

    @GetMapping("/")
    public String loadHome() {
        return "home.html";
    }

    @GetMapping("/customer-signup")
    public String signup() {
        return "customer-repository";
    }

    @PostMapping("/customer-signup")
    public String signup(@ModelAttribute Customer customer, ModelMap map) {
        return myService.signup(customer, map);
    }

    @PostMapping("/customer-otp/{id}")
    public String otp(@PathVariable int id, ModelMap map, @RequestParam int otp) {
        return myService.otp(id, map, otp);
    }

    @GetMapping("/customer-login")
    public String login() {
        return "customer-login.html";
    }

    @PostMapping("/customer-login")
    public String login(@RequestParam String email, @RequestParam String password, ModelMap map, HttpSession session) {
        return myService.login(email, password, map, session);
    }

    @GetMapping("/seller-signup")
    public String sellersignup() {
        return "seller-repository.html";
    }

    @PostMapping("/seller-signup")
    public String sellersignup(@ModelAttribute Seller seller, ModelMap map) {
        return myServiceSeller.sellersignup(seller, map);
    }

    @PostMapping("/seller-otp/{id}")
    public String sellerotp(@PathVariable int id, ModelMap map, @RequestParam int otp) {
        return myServiceSeller.sellerotp(id, map, otp);
    }

    @GetMapping("/seller-login")
    public String sellerlogin() {
        return "sellerlogin.html";
    }

    @PostMapping("/seller-login")
    public String sellerlogin(@RequestParam String email, @RequestParam String password, ModelMap map, HttpSession session) {
        return myServiceSeller.sellerlogin(email, password, map, session);
    }

    @GetMapping("/add-product")
    public String addProduct(HttpSession session, ModelMap map) {
        if (session.getAttribute("seller") != null) {
            return "addproduct.html";
        } else {
            map.put("fail", "Invalid session");
            return "sellerlogin.html";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, ModelMap map) {
        session.removeAttribute("customer");
        session.removeAttribute("seller");
        map.put("pass", "Logout successful");
        return "home.html";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product, @RequestParam MultipartFile image, ModelMap map, HttpSession session) throws IOException {
        if (session.getAttribute("seller") != null) {
            byte[] picture = new byte[image.getInputStream().available()];
            image.getInputStream().read(picture);
            product.setPicture(picture);
            Seller seller = (Seller) session.getAttribute("seller");
            product.setSeller(seller);
            productRepository.save(product);
            map.put("pass", "Product added successfully");
            return "sellerhome.html";
        } else {
            map.put("fail", "Invalid session");
            return "sellerlogin.html";
        }
    }

    @GetMapping("/forgotpwd")
    public String showForgotPasswordPage() {
        return "forgotpwd.html";
    }

    @PostMapping("/forgotpwd")
    public String sendOtpForReset(@RequestParam("email") String email, ModelMap map) {
        return myService.sendOtpForReset(email, map);
    }

    @GetMapping("/resetpwd/{id}")
    public String showResetPasswordPage(@PathVariable("id") int id, ModelMap map) {
        map.put("id", id);
        return "resetpwd.html";
    }

    @PostMapping("/resetpwd")
    public String resetPassword(@RequestParam("id") int id, @RequestParam("otp") int otp, @RequestParam("newPassword") String newPassword, ModelMap map) {
        return myService.resetPassword(id, otp, newPassword, map);
    }

    @GetMapping("/manage-products")
    public String manageProducts(HttpSession session, ModelMap map) {
        if (session.getAttribute("seller") != null) {
            Seller seller = (Seller) session.getAttribute("seller");
            List<Product> list = productRepository.findBySeller(seller);
            if (list.isEmpty()) {
                map.put("fail", "No products added yet");
                return "sellerhome.html";
            } else {
                map.put("list", list);
                return "products.html";
            }
        } else {
            map.put("fail", "Incorrect session");
            return "sellerlogin.html";
        }
    }

    @GetMapping("/seller-home")
    public String loadSellerHome(HttpSession session, ModelMap map) {
        if (session.getAttribute("seller") != null) {
            return "sellerhome.html";
        } else {
            map.put("fail", "Incorrect session");
            return "sellerlogin.html";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session, ModelMap map) {
        if (session.getAttribute("seller") != null) {
            productRepository.deleteById(id);
            return "redirect:/manage-products";
        } else {
            map.put("fail", "Incorrect session");
            return "sellerlogin.html";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable int id, HttpSession session, ModelMap map) {
        if (session.getAttribute("seller") != null) {
            Product product = productRepository.findById(id).orElse(null);
            map.put("product", product);
            return "edit-product.html";
        } else {
            map.put("fail", "Incorrect session");
            return "sellerlogin.html";
        }
    }

    @PostMapping("/edit-product")
    public String editProduct(@ModelAttribute Product product, @RequestParam MultipartFile image, ModelMap map, HttpSession session) throws IOException {
        if (session.getAttribute("seller") != null) {
            byte[] picture = new byte[image.getInputStream().available()];
            image.getInputStream().read(picture);
            if (picture.length != 0)
                product.setPicture(picture);
            else
                product.setPicture(productRepository.findById(product.getId()).orElse(null).getPicture());

            Seller seller = (Seller) session.getAttribute("seller");
            product.setSeller(seller);
            productRepository.save(product);
            map.put("pass", "Product updated successfully");
            return "redirect:/manage-products";
        } else {
            map.put("fail", "Incorrect session");
            return "sellerlogin.html";
        }
    }

    @GetMapping("/view-products")
    public String viewProducts(HttpSession session, ModelMap map) {
        if (session.getAttribute("customer") != null) {
            List<Product> list = productRepository.findAll();
            if (list.isEmpty()) {
                map.put("fail", "No products added yet");
                return "customerhomepage.html";
            } else {
                map.put("list", list);
                return "view-product.html";
            }
        } else {
            map.put("fail", "Invalid session");
            return "customer-login.html";
        }
    }

    @GetMapping("/add-cart/{id}")
    public String addToCart(@PathVariable int id, HttpSession session, ModelMap map) {
        if (session.getAttribute("customer") != null) {
            Product product = productRepository.findById(id).orElseThrow();
            Customer customer = (Customer) session.getAttribute("customer");
            if (product.getStock() > 0) {
                Cart cart = customer.getCart();
                List<Item> items = cart.getItems();
                boolean flag = true;
                for (Item item : items) {
                    if (item.getName().equals(product.getName()) && item.getBrand().equals(product.getBrand())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Item item = new Item();
                    item.setBrand(product.getBrand());
                    item.setDescription(product.getDescription());
                    item.setName(product.getName());
                    item.setPicture(product.getPicture());
                    item.setPrice(product.getPrice());
                    item.setQuantity(1);
                    item.setSize(product.getSize());
                    items.add(item);
                    customerRepository.save(customer);
                    session.setAttribute("customer", customer);
                    product.setStock(product.getStock() - 1);
                    productRepository.save(product);

                    map.put("pass", "Product added successfully");
                    return "customerhomepage.html";
                } else {
                    map.put("fail", "Product already in cart");
                    return "customerhomepage.html";
                }
            } else {
                map.put("fail", "Out of stock");
                return "customerhomepage.html";
            }
        } else {
            map.put("fail", "Invalid session");
            return "customer-login.html";
        }
    }

    @GetMapping("/view-cart")
    public String viewCart(HttpSession session, ModelMap map) {
        if (session.getAttribute("customer") != null) {
            Customer customer = (Customer) session.getAttribute("customer");
            List<Item> items = customer.getCart().getItems();
            if (items.isEmpty()) {
                map.put("fail", "No items in cart");
                return "customerhomepage.html";
            } else {
                double sum = items.stream().mapToDouble(i -> (i.getPrice() * i.getQuantity())).sum();
                map.put("tp", sum);
                map.put("items", items);
                return "cart.html";
            }
        } else {
            map.put("fail", "Invalid session");
            return "customer-login.html";
        }
    }


    @PostMapping("/proceed-to-pay")
    public String proceedToPay(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customer-login";
        }
        Cart cart = cartRepository.findByCustomer(customer);
        if (cart == null || cart.getCartItems().isEmpty()) {
            model.addAttribute("message", "Your cart is empty!");
            return "cart";
        }
        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(new ArrayList<>());
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(cartItem.getItem());
            order.getItems().add(orderItem); 
        }
        orderRepository.save(order);
        cartRepository.delete(cart);
        return "redirect:/order-confirmation";
    }

}
