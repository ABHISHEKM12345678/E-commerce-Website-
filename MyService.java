package com.example.ecommrece.service;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.ecommrece.dto.Customer;
import com.example.ecommrece.repository.CustomerRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class MyService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JavaMailSender mailSender;

    public String signup(@ModelAttribute Customer customer, ModelMap map) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            map.put("fail", "Email Already Exists");
            return "customer-repository.html";
        } else if (customerRepository.existsByMobile(customer.getMobile())) {
            map.put("fail", "Mobile Number Already Exists");
            return "customer-repository.html";
        } else {
            int otp = new Random().nextInt(100000, 1000000);
            customer.setOtp(otp);
            customer.setPassword(AES.encrypt(customer.getPassword(), "123"));
            customerRepository.save(customer);
            sendmail(customer.getEmail(), customer.getName(), otp);
            map.put("pass", "OTP sent successfully");
            map.put("id", customer.getId());
            return "customer-otp.html";
        }
    }

    public void sendmail(String email, String name, int otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Email Verification with SuperTiles");
            helper.setFrom("abhimabhi2003@gmail.com", "SuperTiles");
            helper.setText("<h1>Hello " + name + ", your OTP is: " + otp + " </h1>", true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String otp(int id, ModelMap map, int otp) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer != null && customer.getOtp() == otp) {
            customer.setVerifedotp(true);
            customerRepository.save(customer);
            map.put("pass", "Account created successfully");
            return "home.html";
        } else {
            map.put("fail", "Invalid OTP");
            map.put("id", id);
            return "customer-otp.html";
        }
    }

    public String login(String email, String password, ModelMap map, HttpSession session) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            map.put("fail", "Incorrect Email");
            return "customer-login.html";
        } else {                       
            if (password.equals(AES.decrypt(customer.getPassword(), "123"))) {
                if (customer.isVerifedotp()) {
                    map.put("pass", "Login Successful");
                    session.setAttribute("customer", customer);
                    return "customerhomepage.html";
                } else {
                    int otp = new Random().nextInt(100000, 1000000);
                    customer.setOtp(otp);
                    customer.setPassword(AES.encrypt(customer.getPassword(), "123"));
                    customerRepository.save(customer);
                    sendmail(customer.getEmail(), customer.getName(), otp);
                    map.put("pass", "OTP sent successfully");
                    map.put("id", customer.getId());
                    return "customer-otp.html";
                }
            } else {
                map.put("fail", "Incorrect Password");
                return "customer-login.html";
            }
        }
    }
    public String sendOtpForReset(String email, ModelMap map) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            map.put("fail", "Email not found!");
            return "forgotpwd.html";
        }

        // Generate Secure OTP
        SecureRandom secureRandom = new SecureRandom();
        int otp = 100000 + secureRandom.nextInt(900000);
        
        customer.setOtp(otp);
        customerRepository.save(customer);
        sendOtpMail(email, customer.getName(), otp);

        map.put("pass", "OTP sent to your email!");
        return "redirect:/resetpwd/" + customer.getId();
    }

    // Reset Password - Validate OTP and Update Password
    public String resetPassword(int id, int otp, String newPassword, ModelMap map) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            map.put("fail", "Invalid User ID!");
            return "resetpwd.html";
        }

        // Debugging: Print stored and entered OTP
        System.out.println("Stored OTP: " + customer.getOtp() + ", Entered OTP: " + otp);

        if (customer.getOtp() != otp) {
            map.put("fail", "Invalid OTP!");
            return "resetpwd.html";
        }

        // Encrypt and Save New Password
        customer.setPassword(AES.encrypt(newPassword, "123")); 
        customerRepository.save(customer);

        map.put("pass", "Password reset successfully! Please login.");
        return "customer-login.html";
    }

    // Send OTP via Email
    private void sendOtpMail(String email, String name, int otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Password Reset OTP");
            helper.setFrom("noreply@ecommerce.com", "E-Commerce Support");
            helper.setText("<h3>Hello " + name + ",</h3><p>Your OTP for password reset is: <strong>" + otp + "</strong></p>", true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
