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
    price decimal(9,2) NOT NULL,
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
  `refundStatus` enum('NONE','REQUEST_REFUND','REFUND_APPROVED','REFUND_REFUSED') NOT NULL DEFAULT 'NONE',
  `cancelStatus` enum('NONE','ALL','APART') NOT NULL DEFAULT 'NONE',
  `cartData` json NOT NULL,
  `cartTotal` decimal(9,2) NOT NULL,
  `discount` decimal(9,2) NOT NULL,
  `deliveryFee` decimal(9,2) NOT NULL,
  `couponVolume` decimal(9,2) NOT NULL,
  `pointVolume` decimal(9,2) NOT NULL,
  `pointAccumulate` decimal(9,2) NOT NULL,
  `amount` decimal(9,2) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderCode_UNIQUE` (`orderCode`)
)


CREATE TABLE `order_cancel_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderCode` char(45) NOT NULL,
  `productId` varchar(45) DEFAULT NULL,
   `price`` decimal(9,2) NOT NULL,
  `quantity` varchar(45) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
)

CREATE TABLE `order_payment_epay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderCode` char(45) NOT NULL,
  `trxId` varchar(45) DEFAULT NULL,
  `merTrxId` varchar(45) DEFAULT NULL,
  `goodsNm` varchar(255) DEFAULT NULL,
  `buyerFirstNm` varchar(255) DEFAULT NULL,
  `buyerLastNm` varchar(255) DEFAULT NULL,
  `amount` varchar(45) DEFAULT NULL,
  `remainAmount` varchar(45) DEFAULT NULL,
  `payType` ENUM("IC", "IS", "DC", "EW", "VA", "QR", "PL", "CW") DEFAULT NULL,
  `payOption` varchar(45) DEFAULT NULL,
  `bankId` varchar(45) DEFAULT NULL,
  `bankCode` varchar(45) DEFAULT NULL,
  `cardNo` varchar(45) DEFAULT NULL,
  `cardType` varchar(45) DEFAULT NULL,
  `cardTypeValue` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `resultCd` varchar(45) DEFAULT NULL,
  `resultMsg` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderCode_UNIQUE` (`orderCode`)
)