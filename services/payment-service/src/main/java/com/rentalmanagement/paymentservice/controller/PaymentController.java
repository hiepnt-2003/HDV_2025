// controller/PaymentController.java
package com.rentalmanagement.paymentservice.controller;

import com.rentalmanagement.paymentservice.model.PaymentHistory;
import com.rentalmanagement.paymentservice.model.dto.PaymentRequest;
import com.rentalmanagement.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentHistory>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentHistory>> getPaymentsByInvoiceId(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(paymentService.getPaymentsByInvoiceId(invoiceId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentHistory>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.getPaymentsByDateRange(startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<PaymentHistory> processPayment(@Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.processPayment(request), HttpStatus.CREATED);
    }

    @GetMapping("/total-paid/{invoiceId}")
    public ResponseEntity<BigDecimal> getTotalPaidByInvoiceId(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(paymentService.getTotalPaidByInvoiceId(invoiceId));
    }

    @GetMapping("/stats/by-method")
    public ResponseEntity<List<Object[]>> getPaymentStatsByMethod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.getPaymentStatsByMethod(startDate, endDate));
    }
}