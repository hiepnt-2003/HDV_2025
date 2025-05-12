package com.rentalmanagement.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// Tạo một interface để biểu diễn Customer model từ customer-service
@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    ResponseEntity<Customer> getCustomerById(@PathVariable Long id);

    @GetMapping("/api/customers/search")
    ResponseEntity<List<Customer>> searchCustomers(@RequestParam String term);

    // Lớp tĩnh đại diện cho Customer model từ customer-service
    class Customer {
        private Long id;
        private String fullName;
        private String identificationNumber;
        private String phoneNumber;
        private String email;
        private String address;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getIdentificationNumber() {
            return identificationNumber;
        }

        public void setIdentificationNumber(String identificationNumber) {
            this.identificationNumber = identificationNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}