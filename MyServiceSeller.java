package com.example.ecommrece.service;


import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.ecommrece.dto.Seller;
import com.example.ecommrece.repository.ProductRepository;
import com.example.ecommrece.repository.SellerRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;

@Service
public class MyServiceSeller {
	@Autowired
	SellerRepository sellerRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	JavaMailSender mailSender;

	public String sellersignup(@ModelAttribute Seller seller,ModelMap map) {
		if(sellerRepository.existsByEmail(seller.getEmail())) {
			map.put("fail", "Email Alrady Exits");
			return "Seller-repository.html";
		}
		else if(sellerRepository.existsByMobile(seller.getMobile())){
			map.put("fail", "Mobile No Already Exits");
			return "Seller-repository.html";
		}
		else {
			int otp=new Random().nextInt(100000,1000000);
			seller.setOtp(otp);
			seller.setPassword(AES.encrypt(seller.getPassword(), "123"));
			sellerRepository.save(seller);
			sellersendmail(seller.getEmail(),seller.getName(),otp);
			map.put("pass", "otp sent successfully");
			map.put("id", seller.getId());
			return "Seller-otp.html";
		}
  }
	public void sellersendmail(String email,String name,int otp) {
		MimeMessage message= mailSender.createMimeMessage();
		MimeMessageHelper helper= new MimeMessageHelper(message);
		try {
			helper.setTo(email);
			helper.setSubject("Email verification with supertiles");
			helper.setFrom("abhimabhi2003@gmail.com","Superrtiles");
			helper.setText("<h1>Hello "+name+"  your otp is :"+otp+" </h1>",true);
		}
		catch (Exception e) {
			
		}
		mailSender.send(message);
	}
	
	public String sellerotp(int id,ModelMap map,int otp) {
		Seller seller=sellerRepository.findById(id).orElseThrow();
		if(seller.getOtp() == otp) {
			seller.setVerifedotp(true);
			sellerRepository.save(seller);
			map.put("pass", "account created success");
			return "home.html";
		} 
		else {
			map.put("fail", "invalid otp");
			map.put("id", seller.getId());
			return "Seller-otp.html";
		}
		
	}
	
	public String sellerlogin(String email, String password, ModelMap map, HttpSession session) {
			
			Seller seller= sellerRepository.findByEmail(email);
			if (seller ==null) {
            map.put("fail", "Incorrect Email");
			return "sellerlogin.html";
			}
			else {                       
				if (password.equals(AES.decrypt(seller.getPassword(), "123"))) {
					if (seller.isVerifedotp()) {
						map.put("pass", "Login Success");
						session.setAttribute("seller", seller);
						return "sellerhome.html";
					}
					else {
						int otp =new Random().nextInt(100000, 1000000);
						seller.setOtp(otp);
						seller.setPassword(AES.encrypt(seller.getPassword(), "123"));
						sellerRepository.save(seller);
						sellersendmail(seller.getEmail(),seller.getName(),otp);
						map.put("pass", "otp sent successfully");
						map.put("id", seller.getId());
						return "Seller-otp.html";
					}
				}
				else {
					map.put("fail","inncorrect password");
					return "sellerlogin.html";
				}
			
			}
			
			
}
	
}














