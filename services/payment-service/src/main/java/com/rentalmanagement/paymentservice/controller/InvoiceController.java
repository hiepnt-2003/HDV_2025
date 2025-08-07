// controller/InvoiceController.java
package com.rentalmanagement.paymentservice.controller;

import com.rentalmanagement.paymentservice.model.Invoice;
import com.rentalmanagement.paymentservice.model.dto.InvoiceCreateRequest;
import com.rentalmanagement.paymentservice.model.dto.InvoiceResponse;
import com.rentalmanagement.paymentservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomerId(customerId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByRoomId(roomId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatus(@PathVariable Invoice.InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    @GetMapping("/period")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByPeriod(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ResponseEntity.ok(invoiceService.getInvoicesByPeriod(year, month));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceCreateRequest request) {
        return new ResponseEntity<>(invoiceService.createInvoice(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InvoiceResponse> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestBody Invoice.InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-monthly/{year}/{month}")
    public ResponseEntity<List<InvoiceResponse>> generateMonthlyInvoices(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(invoiceService.generateMonthlyInvoices(year, month));
    }

    // Thêm các endpoints này vào cuối class InvoiceController (trước closing brace)

    @GetMapping("/revenue/month")
    public ResponseEntity<Double> getRevenueByMonth(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ResponseEntity.ok(invoiceService.getRevenueByMonth(year, month));
    }

    @GetMapping("/revenue/year")
    public ResponseEntity<Double> getRevenueByYear(@RequestParam Integer year) {
        return ResponseEntity.ok(invoiceService.getRevenueByYear(year));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @GetMapping("/due-soon")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesDueSoon() {
        return ResponseEntity.ok(invoiceService.getInvoicesDueSoon());
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(invoiceService.getDashboardStats());
    }
}