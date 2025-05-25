SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE "order";
TRUNCATE TABLE inventory;
TRUNCATE TABLE item;

ALTER TABLE item ALTER COLUMN id RESTART WITH 1;
ALTER TABLE inventory ALTER COLUMN id RESTART WITH 1;
ALTER TABLE "order" ALTER COLUMN id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO item (name, price) VALUES
       ('Pen', 5),
       ('Book', 10),
       ('Bag', 30),
       ('Pencil', 3),
       ('Shoe', 45),
       ('Box', 5),
       ('Cap', 25);

INSERT INTO inventory (item_id, qty, type) VALUES
       (1, 5, 'T'),
       (2, 10, 'T'),
       (3, 30, 'T'),
       (4, 3, 'T'),
       (5, 45, 'T'),
       (6, 5, 'T'),
       (7, 25, 'T'),
       (5, 7, 'T'),
       (5, 10, 'W');

INSERT INTO "order" (order_no, item_id, qty, price) VALUES
        ('O1', 1, 2, 5),
        ('O2', 2, 3, 10),
        ('O3', 5, 4, 45),
        ('O4', 4, 1, 2),
        ('O5', 5, 2, 45),
        ('O6', 6, 3, 5),
        ('O7', 1, 5, 5),
        ('O8', 2, 1, 10),
        ('O9', 3, 2, 30),
        ('O10', 4, 3, 3);
