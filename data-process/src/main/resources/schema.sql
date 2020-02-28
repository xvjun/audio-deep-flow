CREATE DATABASE IF NOT EXISTS `audio_deep_flow`;
use audio_deep_flow;
CREATE TABLE IF NOT EXISTS `dataInformation`(
   `dataId` INT NOT NULL AUTO_INCREMENT,
   `dataName` VARCHAR(128) NOT NULL,
   `hdfsPath` VARCHAR(128) NOT NULL,
   `length` INT NOT NULL,
   `capacity` INT NOT NULL,
   `importTime` DATETIME NOT NULL,
   `isCompleted` TINYINT NOT NULL,
   PRIMARY KEY ( `dataId` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

