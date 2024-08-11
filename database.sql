

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema library_d
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema library_d
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `library_d` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `library_d` ;

-- -----------------------------------------------------
-- Table `library_d`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `library_d`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `phone_number` VARCHAR(50) NOT NULL,
  `library_card_number` VARCHAR(50) NOT NULL,
  `age` INT NULL DEFAULT NULL,
  `fine` DECIMAL(10,2) NULL DEFAULT '0.00',
  PRIMARY KEY (`user_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE UNIQUE INDEX `library_card_number` ON `library_d`.`users` (`library_card_number` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `library_d`.`items`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `library_d`.`items` (
  `item_id` VARCHAR(50) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `author` VARCHAR(255) NULL DEFAULT NULL,
  `value` DECIMAL(10,2) NULL DEFAULT NULL,
  `best_seller` TINYINT(1) NULL DEFAULT '0',
  `reference_only` TINYINT(1) NULL DEFAULT '0',
  `item_type` ENUM('book', 'audio', 'video', 'reference', 'magazine') NOT NULL,
  `copies` INT NULL DEFAULT '1',
  `available` TINYINT(1) NULL DEFAULT '1',
  PRIMARY KEY (`item_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `library_d`.`checkouts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `library_d`.`checkouts` (
  `checkout_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `item_id` VARCHAR(50) NOT NULL,
  `item_type` ENUM('book', 'audio', 'video', 'reference', 'magazine') NOT NULL,
  `checkout_date` DATE NOT NULL,
  `due_date` DATE NOT NULL,
  `returned` TINYINT(1) NULL DEFAULT '0',
  `requested` TINYINT(1) NULL DEFAULT '0',
  `renewed` TINYINT(1) NULL DEFAULT '0',
  `renewal_count` INT NULL DEFAULT '0',
  `is_requested` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`checkout_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 28
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `user_id` ON `library_d`.`checkouts` (`user_id` ASC) VISIBLE;

CREATE INDEX `item_id` ON `library_d`.`checkouts` (`item_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `library_d`.`fines`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `library_d`.`fines` (
  `fine_id` INT NOT NULL AUTO_INCREMENT,
  `checkout_id` INT NOT NULL,
  `fine_amount` DECIMAL(10,2) NOT NULL,
  `paid` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`fine_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `checkout_id` ON `library_d`.`fines` (`checkout_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `library_d`.`requests`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `library_d`.`requests` (
  `request_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `item_id` VARCHAR(50) NOT NULL,
  `request_date` DATE NOT NULL,
  `fulfilled` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`request_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `user_id` ON `library_d`.`requests` (`user_id` ASC) VISIBLE;

CREATE INDEX `item_id` ON `library_d`.`requests` (`item_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `library_d`.`return_requests`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `library_d`.`return_requests` (
  `request_id` INT NOT NULL AUTO_INCREMENT,
  `item_id` VARCHAR(255) NULL DEFAULT NULL,
  `requestor_user_id` INT NULL DEFAULT NULL,
  `request_date` DATE NULL DEFAULT NULL,
  `library_card_number` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`request_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX `item_id` ON `library_d`.`return_requests` (`item_id` ASC) VISIBLE;

CREATE INDEX `requestor_user_id` ON `library_d`.`return_requests` (`requestor_user_id` ASC) VISIBLE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
