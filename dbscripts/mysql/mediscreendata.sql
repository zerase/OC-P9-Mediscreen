-- Create table
CREATE TABLE patients (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  last_name VARCHAR(30) NOT NULL,
  first_name VARCHAR(30) NOT NULL,
  date_of_birth DATE NOT NULL,
  gender CHAR(1) NOT NULL,
  address VARCHAR(100) DEFAULT NULL,
  phone_number VARCHAR(15) DEFAULT NULL
);

-- Insert data
INSERT INTO patients (id, last_name, first_name, date_of_birth, gender, address, phone_number)
VALUES
    (1, 'TestNone', 'Test', '1966-12-31', 'F', '1 Brookside St','100-222-3333'),
    (2, 'TestBorderline', 'Test', '1945-06-24', 'M', '2 High St', '200-333-4444'),
    (3, 'TestInDanger', 'Test', '2004-06-18', 'M', '3 Club Road', '300-444-5555'),
    (4, 'TestEarlyOnset', 'Test', '2002-06-28', 'F', '4 Valley Dr', '400-555-6666');
COMMIT;