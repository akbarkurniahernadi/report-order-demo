-- Seed ITEM
INSERT INTO item (id, name, price) VALUES
       (1, 'Pen', 5),
       (2, 'Book', 10),
       (3, 'Bag', 30),
       (4, 'Pencil', 3),
       (5, 'Shoe', 45),
       (6, 'Box', 5),
       (7, 'Cap', 25);

-- Seed INVENTORY
INSERT INTO inventory (id, item_id, qty, type) VALUES
       (1, 1, 5, 'T'),
       (2, 2, 10, 'T'),
       (3, 3, 30, 'T'),
       (4, 4, 3, 'T'),
       (5, 5, 45, 'T'),
       (6, 6, 5, 'T'),
       (7, 7, 25, 'T'),
       (8, 5, 7, 'T'),
       (9, 5, 10, 'W');

-- Seed ORDER
INSERT INTO "order" (id, order_no, item_id, qty, price) VALUES
        (1, 'O1', 1, 2, 5),
        (2, 'O2', 2, 3, 10),
        (3, 'O3', 5, 4, 45),
        (4, 'O4', 4, 1, 2),
        (5, 'O5', 5, 2, 45),
        (6, 'O6', 6, 3, 5),
        (7, 'O7', 1, 5, 5),
        (8, 'O8', 2, 1, 10),
        (9, 'O9', 3, 2, 30),
        (10, 'O10', 4, 3, 3);
