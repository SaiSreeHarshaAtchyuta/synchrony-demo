CREATE TABLE GroceryItem (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    inventory INT NOT NULL
);


CREATE TABLE Orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orderDate TIMESTAMP NOT NULL
);


CREATE TABLE OrderItem (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orderId BIGINT NOT NULL,
    itemId BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (orderId) REFERENCES Orders(id),
    FOREIGN KEY (itemId) REFERENCES GroceryItem(id)
);
