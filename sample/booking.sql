-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema booking
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema booking
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `booking` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `booking` ;

-- -----------------------------------------------------
-- Table `booking`.`admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`admin` (
  `admin_id` TINYINT NOT NULL,
  `name` TINYTEXT NOT NULL,
  `password` TINYTEXT NOT NULL,
  PRIMARY KEY (`admin_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`customer` (
  `customer_id` TINYINT NOT NULL,
  `name` TINYTEXT NOT NULL,
  `address` TINYTEXT NOT NULL,
  `phone_number` TINYTEXT NOT NULL,
  `account_number` TINYTEXT NOT NULL,
  `email` TINYTEXT NOT NULL,
  `password` TINYTEXT NOT NULL,
  PRIMARY KEY (`customer_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`theater`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`theater` (
  `room_id` TINYINT NOT NULL,
  `capacity` TINYINT NOT NULL,
  `room_number` TINYINT NOT NULL,
  PRIMARY KEY (`room_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`movie` (
  `movie_id` TINYINT NOT NULL,
  `room_id` TINYINT NOT NULL,
  `start_time` TIMESTAMP NOT NULL,
  `duration` TINYTEXT NOT NULL,
  `genre` TINYTEXT NOT NULL,
  `avaliable_seats` TINYINT NOT NULL,
  `movie_name` VARCHAR(45) NOT NULL,
  `price` TINYINT NOT NULL,
  PRIMARY KEY (`movie_id`),
  INDEX `fk_room_id_movie_idx` (`room_id` ASC) VISIBLE,
  CONSTRAINT `fk_room_id_movie`
    FOREIGN KEY (`room_id`)
    REFERENCES `booking`.`theater` (`room_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`payment_account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`payment_account` (
  `payment_id` TINYINT NOT NULL,
  `customer_id` TINYINT NOT NULL,
  `account_number` TINYTEXT NOT NULL,
  `card_info` TINYTEXT NOT NULL,
  PRIMARY KEY (`payment_id`),
  INDEX `fk_customer_id_payment_idx` (`customer_id` ASC) VISIBLE,
  CONSTRAINT `fk_customer_id_payment`
    FOREIGN KEY (`customer_id`)
    REFERENCES `booking`.`customer` (`customer_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`seat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`seat` (
  `seat_id` TINYINT NOT NULL,
  `room_id` TINYINT NOT NULL,
  `movie_id` TINYINT NOT NULL,
  `row` TINYINT NOT NULL,
  `column` TINYINT NOT NULL,
  `occupied` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`seat_id`),
  INDEX `fk_movie_id_seat_idx` (`movie_id` ASC) VISIBLE,
  INDEX `fk_room_id_seat_idx` (`room_id` ASC) VISIBLE,
  CONSTRAINT `fk_movie_id_seat`
    FOREIGN KEY (`movie_id`)
    REFERENCES `booking`.`movie` (`movie_id`),
  CONSTRAINT `fk_room_id_seat`
    FOREIGN KEY (`room_id`)
    REFERENCES `booking`.`theater` (`room_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`transaction` (
  `ticket_id` TINYINT NOT NULL,
  `customer_id` TINYINT NOT NULL,
  `movie_id` TINYINT NOT NULL,
  `seat` TINYINT NOT NULL,
  `price` TINYTEXT NOT NULL,
  PRIMARY KEY (`ticket_id`),
  INDEX `fk_customer_id_t_idx` (`customer_id` ASC) VISIBLE,
  INDEX `fk_movie_id_t_idx` (`movie_id` ASC) VISIBLE,
  CONSTRAINT `fk_customer_id_transaction`
    FOREIGN KEY (`customer_id`)
    REFERENCES `booking`.`customer` (`customer_id`),
  CONSTRAINT `fk_movie_id_transaction`
    FOREIGN KEY (`movie_id`)
    REFERENCES `booking`.`movie` (`movie_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
