package com.orderms.dto;

import java.util.Objects;

/***
 * ProductDTO : Class
 * 
 * DTO for Product Entity -- For Data Transfer
 * 
 */
public class ProductDTO {
	private Integer productId;
	private String productName;
	private String productDesc;
	private String productCategory;
	private Double productPrice;
	private String productImage;
	private Integer productQuantity;

	public ProductDTO(Integer productId, String productName, String productDesc, String productCategory,
			Double productPrice, String productImage, Integer productQuantity) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productDesc = productDesc;
		this.productCategory = productCategory;
		this.productPrice = productPrice;
		this.productImage = productImage;
		this.productQuantity = productQuantity;
	}

	public ProductDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	@Override
	public int hashCode() {
		return Objects.hash(productDesc, productId, productImage, productName, productPrice);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductDTO other = (ProductDTO) obj;
		return Objects.equals(productDesc, other.productDesc) && Objects.equals(productId, other.productId)
				&& Objects.equals(productImage, other.productImage) && Objects.equals(productName, other.productName)
				&& Objects.equals(productPrice, other.productPrice);
	}

	@Override
	public String toString() {
		return "ProductDTO [productId=" + productId + ", productName=" + productName + ", productDesc=" + productDesc
				+ ", productPrice=" + productPrice + ", productImage=" + productImage + "]";
	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

}
