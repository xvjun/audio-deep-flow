CREATE DATABASE IF NOT EXISTS `audio_deep_flow`;
use audio_deep_flow;
CREATE TABLE IF NOT EXISTS `jobInformation`(
   `jobId` INT NOT NULL AUTO_INCREMENT,
   `jobName` VARCHAR(128) NOT NULL,
   `rootPath` VARCHAR(128) NOT NULL,
   `hiddenLayers` INT NOT NULL,
   `layersSize` INT NOT NULL,
   `learningRate` FLOAT NOT NULL,
   `epochs` INT NOT NULL,
   `dropoutRate` FLOAT NOT NULL,
   `classSum` INT NOT NULL,
   `dataLength` INT NOT NULL,
   `time` INT NOT NULL,
   `dataShardSum` INT NOT NULL,
   `dataShardReadySum` INT NOT NULL,
   `startTime` DATETIME NOT NULL,
   `isCompleted` TINYINT NOT NULL,
   `cpu` FLOAT NOT NULL,
   `memory` INT NOT NULL,
   PRIMARY KEY ( `jobId` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `modelInformation`(
   `dataId` INT NOT NULL AUTO_INCREMENT,
   `dataName` VARCHAR(128) NOT NULL,
   `hdfsPath` VARCHAR(128) NOT NULL,
   `length` INT NOT NULL,
   `capacity` INT NOT NULL,
   `importTime` DATETIME NOT NULL,
   `isCompleted` TINYINT NOT NULL,
   PRIMARY KEY ( `dataId` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
