package com.oskarott.webshoptemplatebackend.dto;

import com.oskarott.webshoptemplatebackend.model.Address;

public record AddressDto(
        String firstName,
        String lastName,
        String company,
        String street,
        String addressLine2,
        String area,
        String postalCode,
        String country,
        String phone
) {
    public static AddressDto from(Address address) {
        if (address == null) return null;
        return new AddressDto(
                address.getFirstName(),
                address.getLastName(),
                address.getCompany(),
                address.getStreet(),
                address.getAddressLine2(),
                address.getArea(),
                address.getPostalCode(),
                address.getCountry(),
                address.getPhone()
        );
    }

    public Address toEntity() {
        Address address = new Address();
        address.setFirstName(firstName);
        address.setLastName(lastName);
        address.setCompany(company);
        address.setStreet(street);
        address.setAddressLine2(addressLine2);
        address.setArea(area);
        address.setPostalCode(postalCode);
        address.setCountry(country);
        address.setPhone(phone);
        return address;
    }
}
