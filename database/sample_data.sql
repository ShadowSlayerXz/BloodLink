-- SYNTHETIC demonstration data only. Not real blood banks, not live availability.
USE bloodlink_db;
INSERT INTO blood_banks (bank_name, address, city, contact_number) VALUES
 ('Sample City Blood Bank', '1 Example Road', 'Pune', '0000000001'),
 ('Demo Regional Blood Centre', '22 Test Avenue', 'Mumbai', '0000000002');
INSERT INTO blood_inventory (bank_id, blood_group, available_units, source) VALUES
 (1, 'O+', 10, 'SYNTHETIC SAMPLE'), (1, 'A+', 6, 'SYNTHETIC SAMPLE'),
 (2, 'B+', 4, 'SYNTHETIC SAMPLE');
