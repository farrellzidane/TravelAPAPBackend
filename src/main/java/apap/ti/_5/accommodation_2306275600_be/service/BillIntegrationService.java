package apap.ti._5.accommodation_2306275600_be.service;

import apap.ti._5.accommodation_2306275600_be.model.Booking;

public interface BillIntegrationService {
    /**
     * Call Bill service to create a bill for accommodation booking
     * This is a "fire and forget" operation - we don't check if it succeeds or fails
     * 
     * @param booking The booking object for which to create a bill
     */
    void createBillForBooking(Booking booking);
}
