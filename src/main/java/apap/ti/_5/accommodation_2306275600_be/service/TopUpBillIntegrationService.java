package apap.ti._5.accommodation_2306275600_be.service;

import apap.ti._5.accommodation_2306275600_be.model.TopUpTransaction;

public interface TopUpBillIntegrationService {
    /**
     * Call Bill service to create a bill for top-up transaction
     * This is a "fire and forget" operation - we don't check if it succeeds or fails
     * 
     * @param topUpTransaction The top-up transaction for which to create a bill
     */
    void createBillForTopUp(TopUpTransaction topUpTransaction);
}
