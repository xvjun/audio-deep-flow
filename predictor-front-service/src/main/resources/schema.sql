CREATE DATABASE IF NOT EXISTS `audio_deep_flow`;
use audio_deep_flow;
CREATE TABLE IF NOT EXISTS `predictorInformation`(
   `id` VARCHAR(128) NOT NULL,
   `localtion` INT NOT NULL,
   `time` DATETIME NOT NULL,
   `label` INT NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


