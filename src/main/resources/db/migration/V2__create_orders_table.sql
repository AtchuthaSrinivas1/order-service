CREATE TABLE orders (
    id           BIGINT          NOT NULL IDENTITY(1,1),
    product_id   BIGINT          NOT NULL,
    product_name NVARCHAR(255)   NOT NULL,
    price        DECIMAL(18, 2)  NOT NULL,
    quantity     INT             NOT NULL,
    status_id    INT             NOT NULL,
    total_amount DECIMAL(18, 2)  NOT NULL,
    created_at   DATETIME2       NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at   DATETIME2       NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_orders PRIMARY KEY (id),
    CONSTRAINT FK_orders_status FOREIGN KEY (status_id)
        REFERENCES order_status (id)
);


-- INSERT INTO orders (product_id, product_name, price, quantity, status_id, created_at, updated_at) VALUES
--     (101, 'Apple iPhone 15 Pro',        999.99,  1, 2, '2026-05-01 08:00:00', '2026-05-01 08:05:00'),
--     (102, 'Samsung Galaxy S24 Ultra',   1199.99, 1, 1, '2026-05-02 09:30:00', '2026-05-02 09:35:00'),
--     (103, 'Sony WH-1000XM5 Headphones', 349.99,  2, 2, '2026-05-03 10:15:00', '2026-05-03 10:15:00'),
--     (104, 'Apple MacBook Pro 14"',      1999.00, 1, 1, '2026-05-05 11:00:00', '2026-05-05 11:30:00'),
--     (105, 'Dell XPS 15 Laptop',         1749.99, 1, 3, '2026-05-06 12:00:00', '2026-05-06 12:10:00'),
--     (106, 'Logitech MX Master 3 Mouse',  99.99,  3, 2, '2026-05-07 13:45:00', '2026-05-07 13:45:00'),
--     (107, 'Nike Air Max 270',           129.99,  2, 4, '2026-05-08 14:00:00', '2026-05-08 14:05:00'),
--     (108, 'Dyson V15 Vacuum Cleaner',   649.99,  1, 2, '2026-05-10 15:20:00', '2026-05-10 15:25:00'),
--     (109, 'GoPro HERO 12 Black',        399.99,  1, 3, '2026-05-12 16:00:00', '2026-05-12 16:01:00'),
--     (110, 'Kindle Paperwhite',           139.99, 2, 2, '2026-05-15 09:00:00', '2026-05-15 09:00:00');

