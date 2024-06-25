INSERT INTO tool (tool_code, tool_type, brand) VALUES ('CHNS', 'Chainsaw', 'Stihl');
INSERT INTO tool (tool_code, tool_type, brand) VALUES ('LADW', 'Ladder', 'Werner');
INSERT INTO tool (tool_code, tool_type, brand) VALUES ('JAKD', 'Jackhammer', 'DeWalt');
INSERT INTO tool (tool_code, tool_type, brand) VALUES ('JAKR', 'Jackhammer', 'Ridgid');

INSERT INTO rental_cost (tool_type, daily_charge, weekday_charge, weekend_charge, holiday_charge) VALUES ('Ladder', '1.99', true, true, false);
INSERT INTO rental_cost (tool_type, daily_charge, weekday_charge, weekend_charge, holiday_charge) VALUES ('Chainsaw', '1.49', true, false, true);
INSERT INTO rental_cost (tool_type, daily_charge, weekday_charge, weekend_charge, holiday_charge) VALUES ('Jackhammer', '2.99', true, false, false);

