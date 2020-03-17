CREATE DATABASE IF NOT EXISTS `audio_deep_flow`;
use audio_deep_flow;
CREATE TABLE IF NOT EXISTS `streamInformation`(
   `streamId` INT NOT NULL AUTO_INCREMENT,
   `streamName` VARCHAR(128) NOT NULL,
   `servingName` VARCHAR(128) NOT NULL,
   `startTime` DATETIME NOT NULL,
   `cpu` FLOAT NOT NULL,
   `memory` INT NOT NULL,
   `instance` INT NOT NULL,
   `kafkaAddress` VARCHAR(128) NOT NULL,
   `receiverTopics` VARCHAR(128) NOT NULL,
   `sendTopics` VARCHAR(128) NOT NULL,
   `nodePort` INT NOT NULL,
   `httpAddress` VARCHAR(128) NOT NULL,
   PRIMARY KEY ( `streamId` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



