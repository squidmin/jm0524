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

DROP TABLE IF EXISTS charge;

CREATE TABLE charge (
    tool_type VARCHAR(15) PRIMARY KEY NOT NULL,
    daily_charge DECIMAL(4, 2) NOT NULL,
    weekday_charge BOOLEAN NOT NULL,
    weekend_charge BOOLEAN NOT NULL,
    holiday_charge BOOLEAN NOT NULL
);

-- Add the new column with a default value to avoid nulls
-- ALTER TABLE charge ADD COLUMN IF NOT EXISTS tool_type VARCHAR(255) DEFAULT 'default_value' NOT NULL;

-- Optionally, if the column is already added, you can update the null values
-- UPDATE charge SET tool_type = 'default_value' WHERE tool_type IS NULL;

-- Optionally remove the default constraint if not needed after setting the initial values
-- ALTER TABLE charge ALTER COLUMN tool_type DROP DEFAULT;

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
