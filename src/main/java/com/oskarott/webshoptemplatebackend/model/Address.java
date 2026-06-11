package com.oskarott.webshoptemplatebackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(length = 150)
    private String company;

    @Column(length = 255)
    private String street;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(length = 150)
    private String area;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(length = 30)
    private String phone;

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCompany() { return company; }
    public String getStreet() { return street; }
    public String getAddressLine2() { return addressLine2; }
    public String getArea() { return area; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setCompany(String company) { this.company = company; }
    public void setStreet(String street) { this.street = street; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public void setArea(String area) { this.area = area; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setCountry(String country) { this.country = country; }
    public void setPhone(String phone) { this.phone = phone; }
}
