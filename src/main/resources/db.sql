CREATE TABLE mart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    image TEXT NOT NULL,
    lat DOUBLE NOT NULL,
    lon DOUBLE NOT NULL,
    address VARCHAR(255),
    INDEX idx_lat_lon (lat, lon)
);

CREATE TABLE user(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    address TEXT NOT NULL
);


CREATE TABLE banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    image TEXT NOT NULL
);


CREATE TABLE category(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    image TEXT NOT NULL
);

CREATE TABLE product(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    categoryId BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price bigint(20) NOT NULL,
    image TEXT NOT NULL
);

CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `martId` bigint(20) NOT NULL,
  `userId` bigint(20) NOT NULL,
  `shipperId` bigint(20) NOT NULL,
  `orderCode` char(45) NOT NULL,
  `ordererName` varchar(255) NOT NULL,
  `ordererPhone` varchar(15) NOT NULL,
  `ordererAddress` text NOT NULL,
  `orderStatus` enum('WAITING','APPROVED','DELIVERING','COMPLETE','CANCEL','REFUSE') NOT NULL DEFAULT 'WAITING',
  `paymentStatus` enum('NOT_YET','PENDING','PAYMENT_SUCCESS','PAYMENT_FAIL') NOT NULL DEFAULT 'NOT_YET',
  `refundStatus` enum('NONE', 'ENTIRE', 'PARTIAL') NOT NULL DEFAULT 'NOT_YET',
  `cartTotal` bigint(20) NOT NULL,
  `discount` bigint(20) NOT NULL,
  `deliveryFee` bigint(20) NOT NULL,
  `couponVolume` bigint(20) NOT NULL,
  `pointVolume` bigint(20) NOT NULL,
  `pointAccumulate` bigint(20) NOT NULL,
  `amount` bigint(20) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderCode_UNIQUE` (`orderCode`)
)

CREATE TABLE order_cancel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orderCode char(45) NOT NULL,
    cancelCode char(45) NOT NULL COMMENT  'merTrxId',
    amountOriginal bigint(20) NOT NULL,
   amountCancel bigint(20) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL
);



CREATE TABLE order_cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orderCode char(45) NOT NULL,
    productId BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    price bigint(20) NOT NULL CHECK (price >= 1),
    quantity INT NOT NULL CHECK (quantity >= 1),
    categoryId BIGINT NOT NULL,
    categoryName VARCHAR(255) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL
);

CREATE TABLE order_cart_items_cancel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orderCode char(45) NOT NULL,
    cancelCode char(45) NOT NULL COMMENT  'merTrxId',
    productId BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    price bigint(20) NOT NULL CHECK (price >= 1),
    quantity INT NOT NULL CHECK (quantity >= 1),
    categoryId BIGINT NOT NULL,
    categoryName VARCHAR(255) NOT NULL,
    amountActual bigint(20) NOT NULL,
    amountCancel bigint(20) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL
);




CREATE TABLE `order_epay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderCode` char(45) NOT NULL,
  `trxId` varchar(45) DEFAULT NULL,
  `merTrxId` varchar(45) DEFAULT NULL,
  `goodsNm` varchar(255) DEFAULT NULL,
  `buyerFirstNm` varchar(255) DEFAULT NULL,
  `buyerLastNm` varchar(255) DEFAULT NULL,
  `amount` varchar(45) DEFAULT NULL,
  `remainAmount` varchar(45) DEFAULT NULL,
  `payType` ENUM("IC", "IS", "DC", "EW", "VA", "QR", "PL", "CW", "NO") DEFAULT NULL,
  `payOption` varchar(45) DEFAULT NULL,
  `bankId` varchar(45) DEFAULT NULL,
  `bankCode` varchar(45) DEFAULT NULL,
  `cardNo` varchar(45) DEFAULT NULL,
  `cardType` varchar(45) DEFAULT NULL,
  `cardTypeValue` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `resultCd` varchar(45) DEFAULT NULL,
  `resultMsg` TEXT DEFAULT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderCode_UNIQUE` (`orderCode`)
)

-- TEST
TRUNCATE `demo-map-app`.`order`;

TRUNCATE `demo-map-app`.`order_cancel`;

TRUNCATE `demo-map-app`.`order_cart_items`;

TRUNCATE `demo-map-app`.`order_cart_items_cancel`;

TRUNCATE `demo-map-app`.`order_epay`;
