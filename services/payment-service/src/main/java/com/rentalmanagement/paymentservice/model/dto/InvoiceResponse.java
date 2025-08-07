// model/dto/InvoiceResponse.java
package com.rentalmanagement.paymentservice.model.dto;

import com.rentalmanagement.paymentservice.model.Invoice;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private Long roomId;
    private String roomNumber;
    private Long checkInId;

    // Thông tin thời gian
    private Integer billingPeriodMonth;
    private Integer billingPeriodYear;
    private LocalDate issueDate;
    private LocalDate dueDate;

    // Thông tin tiền thuê
    private BigDecimal monthlyRent;

    // Thông tin điện
    private BigDecimal electricityPreviousReading;
    private BigDecimal electricityCurrentReading;
    private BigDecimal electricityUsage;
    private BigDecimal electricityUnitPrice;
    private BigDecimal electricityAmount;

    // Thông tin nước
    private BigDecimal waterPreviousReading;
    private BigDecimal waterCurrentReading;
    private BigDecimal waterUsage;
    private BigDecimal waterUnitPrice;
    private BigDecimal waterAmount;

    // Các khoản phí
    private BigDecimal serviceFee;
    private BigDecimal internetFee;
    private BigDecimal parkingFee;
    private BigDecimal otherFees;
    private String otherFeesDescription;

    // Tổng tiền
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    // Trạng thái
    private Invoice.InvoiceStatus status;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String notes;

    // Convert từ Invoice entity
    public static InvoiceResponse fromEntity(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setCustomerId(invoice.getCustomerId());
        response.setCustomerName(invoice.getCustomerName());
        response.setRoomId(invoice.getRoomId());
        response.setRoomNumber(invoice.getRoomNumber());
        response.setCheckInId(invoice.getCheckInId());

        response.setBillingPeriodMonth(invoice.getBillingPeriodMonth());
        response.setBillingPeriodYear(invoice.getBillingPeriodYear());
        response.setIssueDate(invoice.getIssueDate());
        response.setDueDate(invoice.getDueDate());

        response.setMonthlyRent(invoice.getMonthlyRent());

        response.setElectricityPreviousReading(invoice.getElectricityPreviousReading());
        response.setElectricityCurrentReading(invoice.getElectricityCurrentReading());
        response.setElectricityUsage(invoice.getElectricityUsage());
        response.setElectricityUnitPrice(invoice.getElectricityUnitPrice());
        response.setElectricityAmount(invoice.getElectricityAmount());

        response.setWaterPreviousReading(invoice.getWaterPreviousReading());
        response.setWaterCurrentReading(invoice.getWaterCurrentReading());
        response.setWaterUsage(invoice.getWaterUsage());
        response.setWaterUnitPrice(invoice.getWaterUnitPrice());
        response.setWaterAmount(invoice.getWaterAmount());

        response.setServiceFee(invoice.getServiceFee());
        response.setInternetFee(invoice.getInternetFee());
        response.setParkingFee(invoice.getParkingFee());
        response.setOtherFees(invoice.getOtherFees());
        response.setOtherFeesDescription(invoice.getOtherFeesDescription());

        response.setSubtotal(invoice.getSubtotal());
        response.setDiscountAmount(invoice.getDiscountAmount());
        response.setTotalAmount(invoice.getTotalAmount());

        response.setStatus(invoice.getStatus());
        response.setPaymentDate(invoice.getPaymentDate());
        response.setPaymentMethod(invoice.getPaymentMethod());
        response.setNotes(invoice.getNotes());

        return response;
    }
}