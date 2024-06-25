CREATE TABLE tool (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tool_code VARCHAR(10),
        tool_type VARCHAR(25),
        brand VARCHAR(25)
);

CREATE TABLE rental_cost (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        tool_type VARCHAR(25),
        daily_charge VARCHAR(10),
        weekday_charge boolean,
        weekend_charge boolean,
        holiday_charge boolean
);
