DROP DATABASE IF EXISTS `deplag`;
CREATE DATABASE `deplag`;
USE `deplag`;

CREATE TABLE `documents` (
  `pk_id`       INTEGER 
                NOT NULL AUTO_INCREMENT,
  `identifier`	VARCHAR(70)
                NOT NULL,
  `hash`	    VARCHAR(70),
  `url`	        VARCHAR(300),
  UNIQUE ( `identifier` ),
  PRIMARY KEY ( `pk_id`  )
) ENGINE=INNODB;

CREATE TABLE `chunks` (
  `chunk_id_in_document`  INTEGER 
                NOT NULL,
  `fk_document_id`  INTEGER 
                NOT NULL,
  `content`     BLOB,
  UNIQUE (`chunk_id_in_document`, `fk_document_id` ),
  FOREIGN KEY ( `fk_document_id` ) 
                REFERENCES `documents` (`pk_id`)
                ON DELETE CASCADE
) ENGINE=INNODB;