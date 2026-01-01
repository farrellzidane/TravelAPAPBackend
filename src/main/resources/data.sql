-- Dummy Payment Methods Data
-- This will be automatically executed on application startup

INSERT INTO payment_method (id, method_name, provider, status, created_at, updated_at) VALUES
('pm-va-bca-001', 'Virtual Account', 'BCA', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-va-mandiri-002', 'Virtual Account', 'Mandiri', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-va-bni-003', 'Virtual Account', 'BNI', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-va-bri-004', 'Virtual Account', 'BRI', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-ew-gopay-005', 'E-Wallet', 'GoPay', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-ew-ovo-006', 'E-Wallet', 'OVO', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-ew-dana-007', 'E-Wallet', 'DANA', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-ew-shopeepay-008', 'E-Wallet', 'ShopeePay', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-cc-visa-009', 'Credit Card', 'Visa', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-cc-mastercard-010', 'Credit Card', 'Mastercard', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-qr-qris-011', 'QRIS', 'QRIS', 'Active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('pm-retail-alfamart-012', 'Retail Payment', 'Alfamart', 'Inactive', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
