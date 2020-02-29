CREATE DATABASE IF NOT EXISTS `audio_deep_flow`;
use audio_deep_flow;
CREATE TABLE IF NOT EXISTS `servingInformation`(
   `servingId` INT NOT NULL AUTO_INCREMENT,
   `servingName` VARCHAR(128) NOT NULL,
   `cpu` FLOAT NOT NULL,
   `memory` INT NOT NULL,
   `instance` INT NOT NULL,
   `modelLocalPath` VARCHAR(128) NOT NULL,
   `startTime` DATETIME NOT NULL,
   `isCompleted` TINYINT NOT NULL,
   PRIMARY KEY ( `servingId` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

