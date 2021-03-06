-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: trainsystem
-- ------------------------------------------------------
-- Server version	5.6.27-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `credit_card`
--

LOCK TABLES `credit_card` WRITE;
/*!40000 ALTER TABLE `credit_card` DISABLE KEYS */;
INSERT INTO `credit_card` VALUES (1,'VISA','4493829384712361','2017-02-01'),(38,'VISA','2414125125152514','2016-10-01');
/*!40000 ALTER TABLE `credit_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'hugo@gmail.com','hugo',14,28,8,0,20);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `route`
--

LOCK TABLES `route` WRITE;
/*!40000 ALTER TABLE `route` DISABLE KEYS */;
INSERT INTO `route` VALUES (1,6,3,60,4,'\0',NULL,NULL),(2,3,6,60,4,'\0',NULL,NULL),(3,6,7,30,2,'\0',NULL,NULL),(4,7,6,30,2,'\0',NULL,NULL),(5,7,3,30,2,'\0',NULL,NULL),(6,3,7,30,2,'\0',NULL,NULL),(7,1,3,105,7,'\0',NULL,NULL),(8,3,1,105,7,'\0',NULL,NULL),(9,2,3,45,3,'\0',NULL,NULL),(10,3,2,45,3,'\0',NULL,NULL),(11,4,3,105,7,'\0',NULL,NULL),(12,3,4,105,7,'\0',NULL,NULL),(13,5,3,60,4,'\0',NULL,NULL),(14,3,5,60,4,'\0',NULL,NULL),(15,1,2,60,4,'\0',NULL,NULL),(16,2,1,60,4,'\0',NULL,NULL),(17,1,4,210,14,'\0',NULL,NULL),(18,1,5,165,11,'\0',NULL,NULL),(19,2,4,150,10,'\0',NULL,NULL),(20,2,5,105,7,'\0',NULL,NULL),(21,4,1,210,14,'\0',NULL,NULL),(22,4,2,150,10,'\0',NULL,NULL),(23,4,5,45,3,'\0',NULL,NULL),(24,5,1,165,11,'\0',NULL,NULL),(25,5,2,105,7,'\0',NULL,NULL),(26,5,4,45,3,'\0',NULL,NULL),(27,1,6,165,11,'',7,2),(28,1,7,135,9,'',7,6),(29,2,6,105,7,'',9,2),(30,2,7,75,5,'',9,6),(31,4,6,165,11,'',11,2),(32,4,7,135,9,'',11,6),(33,5,6,120,8,'',13,2),(34,5,7,90,6,'',13,6),(35,6,1,165,11,'',1,8),(36,6,2,105,7,'',1,10),(37,6,4,165,11,'',1,12),(38,6,5,120,8,'',1,14),(39,7,1,135,9,'',5,8),(40,7,2,75,5,'',5,10),(41,7,4,135,9,'',5,12),(42,7,5,90,6,'',5,14);
/*!40000 ALTER TABLE `route` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `station`
--

LOCK TABLES `station` WRITE;
/*!40000 ALTER TABLE `station` DISABLE KEYS */;
INSERT INTO `station` VALUES (1,'A'),(2,'A/Central'),(4,'B'),(5,'B/Central'),(6,'C'),(7,'C/Central'),(3,'Central');
/*!40000 ALTER TABLE `station` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `station_stop`
--

LOCK TABLES `station_stop` WRITE;
/*!40000 ALTER TABLE `station_stop` DISABLE KEYS */;
INSERT INTO `station_stop` VALUES (1,6,1,'09:00:00',1,1),(2,7,1,'09:30:00',2,1),(3,3,1,'10:00:00',3,1),(4,6,1,'13:00:00',1,1),(5,7,1,'13:30:00',2,1),(6,3,1,'14:00:00',3,1),(7,6,1,'17:00:00',1,1),(8,7,1,'17:30:00',2,1),(9,3,1,'18:00:00',3,1),(10,3,2,'11:00:00',1,1),(11,7,2,'11:30:00',2,1),(12,6,2,'12:00:00',3,1),(13,3,2,'15:00:00',1,1),(14,7,2,'15:30:00',2,1),(15,6,2,'16:00:00',3,1),(16,3,2,'19:00:00',1,1),(17,7,2,'19:30:00',2,1),(18,6,2,'20:00:00',3,1),(19,6,3,'09:00:00',1,1),(20,7,3,'09:30:00',2,1),(21,6,3,'13:00:00',1,1),(22,7,3,'13:30:00',2,1),(23,6,3,'17:00:00',1,1),(24,7,3,'17:30:00',2,1),(25,7,4,'11:30:00',1,1),(26,6,4,'12:00:00',2,1),(27,7,4,'15:30:00',1,1),(28,6,4,'16:00:00',2,1),(29,7,4,'19:30:00',1,1),(30,6,4,'20:00:00',2,1),(31,7,5,'09:30:00',1,1),(32,3,5,'10:00:00',2,1),(33,7,5,'13:30:00',1,1),(34,3,5,'14:00:00',2,1),(35,7,5,'17:30:00',1,1),(36,3,5,'18:00:00',2,1),(37,3,6,'11:00:00',1,1),(38,7,6,'11:30:00',2,1),(39,3,6,'15:00:00',1,1),(40,7,6,'15:30:00',2,1),(41,3,6,'19:00:00',1,1),(42,7,6,'19:30:00',2,1),(43,1,7,'09:00:00',1,2),(44,2,7,'10:00:00',2,2),(45,3,7,'10:45:00',3,2),(46,1,7,'14:00:00',1,3),(47,2,7,'15:00:00',2,3),(48,3,7,'15:45:00',3,3),(49,1,7,'18:30:00',1,2),(50,2,7,'19:30:00',2,2),(51,3,7,'20:15:00',3,2),(52,3,8,'11:15:00',1,3),(53,2,8,'12:00:00',2,3),(54,1,8,'13:00:00',3,3),(55,3,8,'16:30:00',1,2),(56,2,8,'17:15:00',2,2),(57,1,8,'18:15:00',3,2),(58,3,8,'20:45:00',1,3),(59,2,8,'21:30:00',2,3),(60,1,8,'22:30:00',3,3),(61,2,9,'10:00:00',1,2),(62,3,9,'10:45:00',2,2),(63,2,9,'15:00:00',1,3),(64,3,9,'15:45:00',2,3),(65,2,9,'19:30:00',1,2),(66,3,9,'20:15:00',2,2),(67,3,10,'11:15:00',1,3),(68,2,10,'12:00:00',2,3),(69,3,10,'16:30:00',1,2),(70,2,10,'17:15:00',2,2),(71,3,10,'20:45:00',1,3),(72,2,10,'21:30:00',2,3),(73,4,11,'09:30:00',1,3),(74,5,11,'10:15:00',2,3),(75,3,11,'11:15:00',3,3),(76,4,11,'14:45:00',1,2),(77,5,11,'15:30:00',2,2),(78,3,11,'16:30:00',3,2),(79,4,11,'19:00:00',1,3),(80,5,11,'19:45:00',2,3),(81,3,11,'20:45:00',3,3),(82,3,12,'10:45:00',1,2),(83,5,12,'11:45:00',2,2),(84,4,12,'12:30:00',3,2),(85,3,12,'15:45:00',1,3),(86,5,12,'16:45:00',2,3),(87,4,12,'17:30:00',3,3),(88,3,12,'20:15:00',1,2),(89,5,12,'21:15:00',2,2),(90,4,12,'22:00:00',3,2),(91,5,13,'10:15:00',1,3),(92,3,13,'11:15:00',2,3),(93,5,13,'15:30:00',1,2),(94,3,13,'16:30:00',2,2),(95,5,13,'19:45:00',1,3),(96,3,13,'20:45:00',2,3),(97,3,14,'10:45:00',1,2),(98,5,14,'11:45:00',2,2),(99,3,14,'15:45:00',1,3),(100,5,14,'16:45:00',2,3),(101,3,14,'20:15:00',1,2),(102,5,14,'21:15:00',2,2),(103,1,15,'09:00:00',1,2),(104,2,15,'10:00:00',2,2),(105,1,15,'14:00:00',1,3),(106,2,15,'15:00:00',2,3),(107,1,15,'18:30:00',1,2),(108,2,15,'19:30:00',2,2),(109,2,16,'12:00:00',1,3),(110,1,16,'13:00:00',2,3),(111,2,16,'17:15:00',1,2),(112,1,16,'18:15:00',2,2),(113,2,16,'21:30:00',1,3),(114,1,16,'22:30:00',2,3),(115,1,17,'09:00:00',1,2),(116,2,17,'10:00:00',2,2),(117,3,17,'10:45:00',3,2),(118,5,17,'11:45:00',4,2),(119,4,17,'12:30:00',5,2),(120,1,17,'14:00:00',1,3),(121,2,17,'15:00:00',2,3),(122,3,17,'15:45:00',3,3),(123,5,17,'16:45:00',4,3),(124,4,17,'17:30:00',5,3),(125,1,17,'18:30:00',1,2),(126,2,17,'19:30:00',2,2),(127,3,17,'20:15:00',3,2),(128,5,17,'21:15:00',4,2),(129,4,17,'22:00:00',5,2),(130,1,18,'09:00:00',1,2),(131,2,18,'10:00:00',2,2),(132,3,18,'10:45:00',3,2),(133,5,18,'11:45:00',4,2),(134,1,18,'14:00:00',1,3),(135,2,18,'15:00:00',2,3),(136,3,18,'15:45:00',3,3),(137,5,18,'16:45:00',4,3),(138,1,18,'18:30:00',1,2),(139,2,18,'19:30:00',2,2),(140,3,18,'20:15:00',3,2),(141,5,18,'21:15:00',4,2),(142,2,19,'10:00:00',1,2),(143,3,19,'10:45:00',2,2),(144,5,19,'11:45:00',3,2),(145,4,19,'12:30:00',4,2),(146,2,19,'15:00:00',1,3),(147,3,19,'15:45:00',2,3),(148,5,19,'16:45:00',3,3),(149,4,19,'17:30:00',4,3),(150,2,19,'19:30:00',1,2),(151,3,19,'20:15:00',2,2),(152,5,19,'21:15:00',3,2),(153,4,19,'22:00:00',4,2),(154,2,20,'10:00:00',1,2),(155,3,20,'10:45:00',2,2),(156,5,20,'11:45:00',3,2),(157,2,20,'15:00:00',1,3),(158,3,20,'15:45:00',2,3),(159,5,20,'16:45:00',3,3),(160,2,20,'19:30:00',1,2),(161,3,20,'20:15:00',2,2),(162,5,20,'21:15:00',3,2),(163,4,21,'09:30:00',1,3),(164,5,21,'10:15:00',2,3),(165,3,21,'11:15:00',3,3),(166,2,21,'12:00:00',4,3),(167,1,21,'13:00:00',5,3),(168,4,21,'14:45:00',1,2),(169,5,21,'15:30:00',2,2),(170,3,21,'16:30:00',3,2),(171,2,21,'17:15:00',4,2),(172,1,21,'18:15:00',5,2),(173,4,21,'19:00:00',1,3),(174,5,21,'19:45:00',2,3),(175,3,21,'20:45:00',3,3),(176,2,21,'21:30:00',4,3),(177,1,21,'22:30:00',5,3),(178,4,22,'09:30:00',1,3),(179,5,22,'10:15:00',2,3),(180,3,22,'11:15:00',3,3),(181,2,22,'12:00:00',4,3),(182,4,22,'14:45:00',1,2),(183,5,22,'15:30:00',2,2),(184,3,22,'16:30:00',3,2),(185,2,22,'17:15:00',4,2),(186,4,22,'19:00:00',1,3),(187,5,22,'19:45:00',2,3),(188,3,22,'20:45:00',3,3),(189,2,22,'21:30:00',4,3),(190,4,23,'09:30:00',1,3),(191,5,23,'10:15:00',2,3),(192,4,23,'14:45:00',1,2),(193,5,23,'15:30:00',2,2),(194,4,23,'19:00:00',1,3),(195,5,23,'19:45:00',2,3),(196,5,24,'10:15:00',1,3),(197,3,24,'11:15:00',2,3),(198,2,24,'12:00:00',3,3),(199,1,24,'13:00:00',4,3),(200,5,24,'15:30:00',1,2),(201,3,24,'16:30:00',2,2),(202,2,24,'17:15:00',3,2),(203,1,24,'18:15:00',4,2),(204,5,24,'19:45:00',1,3),(205,3,24,'20:45:00',2,3),(206,2,24,'21:30:00',3,3),(207,1,24,'22:30:00',4,3),(208,5,25,'10:15:00',1,3),(209,3,25,'11:15:00',2,3),(210,2,25,'12:00:00',3,3),(211,5,25,'15:30:00',1,2),(212,3,25,'16:30:00',2,2),(213,2,25,'17:15:00',3,2),(214,5,25,'19:45:00',1,3),(215,3,25,'20:45:00',2,3),(216,2,25,'21:30:00',3,3),(217,5,26,'11:45:00',1,2),(218,4,26,'12:30:00',2,2),(219,5,26,'16:45:00',1,3),(220,4,26,'17:30:00',2,3),(221,5,26,'21:15:00',1,2),(222,4,26,'22:00:00',2,2);
/*!40000 ALTER TABLE `station_stop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
INSERT INTO `ticket` VALUES (32,15,1,'\0','2015-11-08 09:00:00','7374341f-6982-46ba-814e-a9c1b9e6b86c','Z6M0oamtdJ4IC10GgNWyewS0EgKEDHWi6royBLodWMoGLkSJZwBoAKdDR6DONg==');
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `train`
--

LOCK TABLES `train` WRITE;
/*!40000 ALTER TABLE `train` DISABLE KEYS */;
INSERT INTO `train` VALUES (1,100),(2,1),(3,150);
/*!40000 ALTER TABLE `train` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Hugo Sousa','hugo','hugo',1),(2,'Frm','galseth','12345',1),(7,'Hugo Miguel','hugo1','hugo1',38);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-11-08 16:50:50
