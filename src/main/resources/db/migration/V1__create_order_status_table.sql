
CREATE TABLE order_status (
    id   INT           NOT NULL,
    name NVARCHAR(50)  NOT NULL,
    description NVARCHAR(255) NULL,
    CONSTRAINT PK_order_status PRIMARY KEY (id),
    CONSTRAINT UQ_order_status_name UNIQUE (name)
);

-- Seed statuses
INSERT INTO order_status (id, name, description) VALUES
    (1, 'PENDING',   'Order has been placed and is awaiting confirmation'),
    (2, 'CONFIRMED', 'Order has been confirmed and is being processed'),
    (3, 'FAILED',    'Order processing has failed'),
    (4, 'CANCELLED', 'Order has been cancelled by the customer or system');

