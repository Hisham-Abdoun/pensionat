MERGE INTO rooms (room_number, room_type, extra_beds, price_per_night)
    KEY(room_number)
    VALUES (101, 'ENKEL', 0, 500.0);

MERGE INTO rooms (room_number, room_type, extra_beds, price_per_night)
    KEY(room_number)
    VALUES (102, 'DUBBEL', 0, 750.0);

MERGE INTO rooms (room_number, room_type, extra_beds, price_per_night)
    KEY(room_number)
    VALUES (103, 'DUBBEL', 1, 980.0);

MERGE INTO rooms (room_number, room_type, extra_beds, price_per_night)
    KEY(room_number)
    VALUES (104, 'ENKEL', 2, 1200.0);

MERGE INTO rooms (room_number, room_type, extra_beds, price_per_night)
    KEY(room_number)
    VALUES (105, 'DUBBEL', 0, 1100.0);

MERGE INTO rooms (room_number, room_type, extra_beds, price_per_night)
    KEY(room_number)
    VALUES (106, 'ENKEL', 0, 1000.0);