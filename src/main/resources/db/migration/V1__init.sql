-- Create the tool table
CREATE TABLE tool (
    code VARCHAR(4) PRIMARY KEY NOT NULL,
    type VARCHAR(20) NOT NULL,
    brand VARCHAR(20) NOT NULL
);

-- Insert tool records
INSERT INTO tool (code, type, brand) VALUES ('CHNS', 'Chainsaw', 'Stihl');
INSERT INTO tool (code, type, brand) VALUES ('LADW', 'Ladder', 'Werner');
INSERT INTO tool (code, type, brand) VALUES ('JAKD', 'Jackhammer', 'DeWalt');
INSERT INTO tool (code, type, brand) VALUES ('JAKR', 'Jackhammer', 'Ridgid');

-- Create the charge table
CREATE TABLE charge (
    tool_type VARCHAR(15) PRIMARY KEY NOT NULL,
    daily_charge DECIMAL(4, 2) NOT NULL,
    weekday_charge BOOLEAN NOT NULL,
    weekend_charge BOOLEAN NOT NULL,
    holiday_charge BOOLEAN NOT NULL
);

-- Insert charge records
INSERT INTO charge (tool_type,
                    daily_charge,
                    weekday_charge,
                    weekend_charge,
                    holiday_charge)
VALUES ('Ladder', 1.99, true, true, false);
INSERT INTO charge (tool_type,
                    daily_charge,
                    weekday_charge,
                    weekend_charge,
                    holiday_charge)
VALUES ('Chainsaw', 1.49, true, false, true);
INSERT INTO charge (tool_type,
                    daily_charge,
                    weekday_charge,
                    weekend_charge,
                    holiday_charge)
VALUES ('Jackhammer', 2.99, true, false, false);
