package com.example.ecommrece.dto;


import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Item {
	@Id
	@GeneratedValue
	int id;
	String name;
	String brand;
	String size;
	String description;
	int quantity;
	double price;
	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	byte[] picture;

	@ManyToOne
	Seller seller;
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", brand=" + brand + ", size=" + size + ", description="
				+ description + ", quantity=" + quantity + ", price=" + price + ", picture=" + Arrays.toString(picture)
				+ ", seller=" + seller + "]";
	}

	public String base64Image() {
		return Base64.encodeBase64String(picture);
	}



	
}

