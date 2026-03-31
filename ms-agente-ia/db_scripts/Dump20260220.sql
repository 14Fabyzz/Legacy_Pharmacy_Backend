CREATE DATABASE  IF NOT EXISTS "defaultdb" /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `defaultdb`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: legacy03-fbenjumeau-515c.g.aivencloud.com    Database: defaultdb
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '0040d9b7-0609-11f1-9658-3a569eb7c92f:1-2669,
96383c94-fae0-11f0-a92c-e20d2843d00a:1-319,
b75f8acc-d707-11f0-bc61-92c1712bf46f:1-98,
bac5c330-fbc8-11f0-9fb5-96fe6f86d958:1-1774';

--
-- Table structure for table `auditoria_login`
--

DROP TABLE IF EXISTS `auditoria_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_login` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint DEFAULT NULL,
  `login_intentado` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_evento` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip_origen` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `observaciones` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_usuario_id` (`usuario_id`),
  KEY `idx_login_intentado` (`login_intentado`),
  KEY `idx_tipo_evento` (`tipo_evento`),
  KEY `idx_fecha` (`fecha`),
  CONSTRAINT `fk_auditoria_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `chk_tipo_evento` CHECK ((`tipo_evento` in (_utf8mb4'LOGIN_EXITOSO',_utf8mb4'LOGIN_FALLIDO',_utf8mb4'LOGOUT',_utf8mb4'BLOQUEO_CUENTA',_utf8mb4'DESBLOQUEO_CUENTA')))
) ENGINE=InnoDB AUTO_INCREMENT=429 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria_login`
--

LOCK TABLES `auditoria_login` WRITE;
/*!40000 ALTER TABLE `auditoria_login` DISABLE KEYS */;
INSERT INTO `auditoria_login` VALUES (2,1,'admin','LOGIN_EXITOSO','127.0.0.1','insomnia/12.1.0','2025-12-18 14:39:22','Login exitoso'),(69,1,'admin','LOGIN_EXITOSO','127.0.0.1','insomnia/12.1.0','2026-01-19 19:09:40','Login exitoso'),(74,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-01-27 19:37:18','Login exitoso'),(75,1,'admin','LOGIN_EXITOSO','172.23.0.1','curl/7.81.0','2026-01-27 19:37:34','Login exitoso'),(76,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-01-27 19:38:08','Login exitoso'),(78,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-27 19:38:37','Login exitoso'),(79,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-27 19:51:55','Login exitoso'),(80,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-27 19:57:31','Login exitoso'),(81,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-01-28 12:44:04','Login exitoso'),(82,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-01-28 12:44:23','Login exitoso'),(83,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-01-28 12:44:54','Login exitoso'),(84,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-28 14:30:51','Login exitoso'),(86,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-28 14:46:04','Login exitoso'),(87,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-29 12:02:14','Login exitoso'),(88,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-01-29 12:57:42','Login exitoso'),(89,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-29 13:53:51','Login exitoso'),(90,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-29 13:53:52','Login exitoso'),(92,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-29 15:35:59','Login exitoso'),(93,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-29 16:17:52','Login exitoso'),(94,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-01-30 14:53:16','Login exitoso'),(95,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-01-30 15:25:06','Login exitoso'),(96,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-02 15:25:11','Login exitoso'),(97,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-02 15:25:12','Login exitoso'),(100,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-02 17:48:57','Login exitoso'),(101,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-02 17:48:58','Login exitoso'),(102,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-02-02 18:04:07','Login exitoso'),(103,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 12:25:28','Login exitoso'),(104,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 12:26:08','Login exitoso'),(105,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 12:29:14','Login exitoso'),(106,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 12:46:56','Login exitoso'),(107,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 12:55:45','Login exitoso'),(108,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 13:00:07','Login exitoso'),(109,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 13:00:49','Login exitoso'),(110,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 13:02:27','Login exitoso'),(111,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 13:05:49','Login exitoso'),(112,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 13:12:51','Login exitoso'),(113,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-02-03 13:23:48','Login exitoso'),(114,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-03 13:41:59','Login exitoso'),(115,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-02-03 14:55:15','Login exitoso'),(116,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-03 14:56:01','Login exitoso'),(117,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 14:56:23','Login exitoso'),(118,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 14:56:47','Login exitoso'),(119,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 14:57:09','Login exitoso'),(120,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 14:57:58','Login exitoso'),(121,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 14:59:21','Login exitoso'),(122,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:00:25','Login exitoso'),(123,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:09:07','Login exitoso'),(124,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:10:19','Login exitoso'),(125,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:12:51','Login exitoso'),(126,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:22:19','Login exitoso'),(127,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:23:15','Login exitoso'),(128,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:30:27','Login exitoso'),(129,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:43:56','Login exitoso'),(130,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:44:10','Login exitoso'),(131,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:45:05','Login exitoso'),(132,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:45:14','Login exitoso'),(133,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:45:42','Login exitoso'),(134,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:46:00','Login exitoso'),(135,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:47:54','Login exitoso'),(136,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:48:21','Login exitoso'),(137,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:52:58','Login exitoso'),(138,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:54:02','Login exitoso'),(139,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:54:12','Login exitoso'),(140,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:55:23','Login exitoso'),(141,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:56:31','Login exitoso'),(142,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-03 15:56:50','Login exitoso'),(143,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 12:37:26','Login exitoso'),(144,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:09:29','Login exitoso'),(145,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:09:52','Login exitoso'),(146,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:31:30','Login exitoso'),(147,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:33:02','Login exitoso'),(148,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:47:32','Login exitoso'),(149,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:50:38','Login exitoso'),(150,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:53:31','Login exitoso'),(151,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Microsoft Windows 10.0.26200; es-MX) PowerShell/7.5.4','2026-02-05 13:55:36','Login exitoso'),(152,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 14:47:59','Login exitoso'),(153,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 14:48:00','Login exitoso'),(157,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 14:52:19','Login exitoso'),(158,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 15:10:05','Login exitoso'),(161,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 15:32:10','Login exitoso'),(162,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 18:28:56','Login exitoso'),(164,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 18:43:48','Login exitoso'),(166,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 19:15:51','Login exitoso'),(168,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-05 20:45:00','Login exitoso'),(169,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-02-06 18:23:29','Login exitoso'),(170,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-02-06 18:24:04','Login exitoso'),(172,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 18:24:07','Login exitoso'),(173,1,'admin','LOGIN_EXITOSO','146.190.166.77','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 18:27:18','Login exitoso'),(175,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 19:04:32','Login exitoso'),(176,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 19:04:33','Login exitoso'),(179,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 19:13:40','Login exitoso'),(183,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 19:23:30','Login exitoso'),(184,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 19:23:31','Login exitoso'),(185,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 19:30:09','Login exitoso'),(186,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 19:30:09','Login exitoso'),(188,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 20:39:12','Login exitoso'),(189,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 20:44:49','Login exitoso'),(190,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-06 20:57:53','Login exitoso'),(192,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-08 20:05:13','Login exitoso'),(193,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-08 20:05:14','Login exitoso'),(194,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-08 20:51:35','Login exitoso'),(195,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-08 21:17:47','Login exitoso'),(197,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-08 21:33:56','Login exitoso'),(198,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-08 22:03:47','Login exitoso'),(200,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-09 15:53:13','Login exitoso'),(201,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-09 18:23:16','Login exitoso'),(202,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-09 22:39:11','Login exitoso'),(203,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-09 23:47:16','Login exitoso'),(204,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-09 23:47:32','Login exitoso'),(205,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-09 23:49:36','Login exitoso'),(206,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-09 23:50:17','Login exitoso'),(207,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-09 23:55:53','Login exitoso'),(208,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-09 23:56:35','Login exitoso'),(209,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 00:01:36','Login exitoso'),(210,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 00:03:09','Login exitoso'),(212,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 12:10:51','Login exitoso'),(213,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 14:43:21','Login exitoso'),(214,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 14:45:51','Login exitoso'),(215,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 14:46:53','Login exitoso'),(216,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 14:47:47','Login exitoso'),(217,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 14:50:07','Login exitoso'),(218,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-10 14:53:59','Login exitoso'),(219,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 14:55:09','Login exitoso'),(220,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 16:27:17','Login exitoso'),(221,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 20:55:29','Login exitoso'),(222,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 20:55:33','Login exitoso'),(223,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 20:57:23','Login exitoso'),(224,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 21:53:28','Login exitoso'),(225,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-10 21:53:38','Login exitoso'),(227,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 13:09:52','Login exitoso'),(228,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-11 13:13:43','Login exitoso'),(231,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-11 13:42:28','Login exitoso'),(232,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-11 13:42:29','Login exitoso'),(233,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:09:59','Login exitoso'),(234,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:11:19','Login exitoso'),(235,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:13:08','Login exitoso'),(236,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:15:22','Login exitoso'),(237,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:17:32','Login exitoso'),(238,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:19:20','Login exitoso'),(239,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:20:01','Login exitoso'),(240,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:21:10','Login exitoso'),(241,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:28:23','Login exitoso'),(242,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:29:26','Login exitoso'),(243,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:31:22','Login exitoso'),(244,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:32:23','Login exitoso'),(245,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:34:31','Login exitoso'),(246,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:36:27','Login exitoso'),(247,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:38:07','Login exitoso'),(248,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 15:41:42','Login exitoso'),(250,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-11 16:23:54','Login exitoso'),(251,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-11 18:37:04','Login exitoso'),(252,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-11 19:10:34','Login exitoso'),(253,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:36:40','Login exitoso'),(254,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:37:17','Login exitoso'),(255,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:38:03','Login exitoso'),(256,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-02-11 22:38:25','Login exitoso'),(257,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:47:47','Login exitoso'),(258,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:48:14','Login exitoso'),(259,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:50:29','Login exitoso'),(260,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:52:22','Login exitoso'),(261,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:55:27','Login exitoso'),(262,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 22:57:30','Login exitoso'),(263,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-11 23:03:04','Login exitoso'),(264,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 23:10:02','Login exitoso'),(265,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 23:11:29','Login exitoso'),(266,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-11 23:31:38','Login exitoso'),(267,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 14:59:26','Login exitoso'),(269,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 15:18:28','Login exitoso'),(270,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 15:18:41','Login exitoso'),(271,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 15:19:41','Login exitoso'),(272,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 15:20:52','Login exitoso'),(273,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 15:22:14','Login exitoso'),(274,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 15:39:05','Login exitoso'),(276,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 18:23:40','Login exitoso'),(277,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 18:31:01','Login exitoso'),(278,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 18:37:56','Login exitoso'),(281,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-12 18:44:23','Login exitoso'),(283,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-12 18:44:24','Login exitoso'),(284,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 18:59:34','Login exitoso'),(285,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:22:38','Login exitoso'),(286,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:22:58','Login exitoso'),(287,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:23:20','Login exitoso'),(288,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 19:39:55','Login exitoso'),(289,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:40:27','Login exitoso'),(290,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:41:01','Login exitoso'),(291,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:41:31','Login exitoso'),(292,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:42:12','Login exitoso'),(293,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:44:27','Login exitoso'),(294,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:46:28','Login exitoso'),(295,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 19:46:32','Login exitoso'),(296,1,'admin','LOGIN_EXITOSO','172.23.0.1','python-requests/2.32.5','2026-02-12 20:39:15','Login exitoso'),(297,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 20:48:34','Login exitoso'),(299,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 20:48:44','Login exitoso'),(300,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-12 21:01:34','Login exitoso'),(304,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 21:06:47','Login exitoso'),(305,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-12 22:21:54','Login exitoso'),(306,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 14:43:14','Login exitoso'),(307,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 15:11:06','Login exitoso'),(308,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 15:11:37','Login exitoso'),(309,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 15:42:55','Login exitoso'),(310,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 16:14:45','Login exitoso'),(311,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 16:23:26','Login exitoso'),(312,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 19:06:12','Login exitoso'),(313,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:02:17','Login exitoso'),(315,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:03:13','Login exitoso'),(316,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:10:42','Login exitoso'),(317,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:15:44','Login exitoso'),(318,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:17:37','Login exitoso'),(319,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:33:29','Login exitoso'),(320,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-13 20:37:44','Login exitoso'),(321,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:47:57','Login exitoso'),(322,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 20:48:00','Login exitoso'),(323,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0','2026-02-13 21:11:26','Login exitoso'),(324,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 14:36:12','Login exitoso'),(325,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 19:48:24','Login exitoso'),(326,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 20:23:35','Login exitoso'),(327,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 20:44:35','Login exitoso'),(328,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 20:49:45','Login exitoso'),(329,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 20:59:07','Login exitoso'),(330,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 22:31:18','Login exitoso'),(331,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-15 23:12:33','Login exitoso'),(333,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-16 13:03:02','Login exitoso'),(334,1,'admin','LOGIN_EXITOSO','172.23.0.1','insomnia/12.1.0','2026-02-16 13:44:46','Login exitoso'),(335,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-16 20:33:53','Login exitoso'),(336,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-16 22:33:35','Login exitoso'),(337,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-16 22:52:11','Login exitoso'),(339,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-16 23:44:04','Login exitoso'),(340,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 11:57:31','Login exitoso'),(341,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 12:01:07','Login exitoso'),(342,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 12:02:21','Login exitoso'),(343,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 12:05:58','Login exitoso'),(344,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 12:08:41','Login exitoso'),(345,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 12:32:13','Login exitoso'),(347,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 12:37:55','Login exitoso'),(348,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 12:40:00','Login exitoso'),(349,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 12:41:47','Login exitoso'),(350,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 13:01:10','Login exitoso'),(351,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 13:12:57','Login exitoso'),(352,3,'vendedor01','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 13:13:01','Login exitoso'),(354,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 13:13:21','Login exitoso'),(355,3,'vendedor01','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 13:13:26','Login exitoso'),(357,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 13:18:50','Login exitoso'),(358,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 13:19:32','Login exitoso'),(359,4,'pramirez','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 13:22:05','Login exitoso'),(360,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 14:04:17','Login exitoso'),(361,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 14:51:55','Login exitoso'),(362,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 14:52:55','Login exitoso'),(363,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 14:53:55','Login exitoso'),(364,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 15:41:02','Login exitoso'),(365,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT; Windows NT 10.0; es-MX) WindowsPowerShell/5.1.26100.7705','2026-02-17 15:45:00','Login exitoso'),(366,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:16:37','Login exitoso'),(367,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 18:25:34','Login exitoso'),(368,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:27:54','Login exitoso'),(369,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:28:40','Login exitoso'),(370,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:30:48','Login exitoso'),(371,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:37:25','Login exitoso'),(372,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:38:02','Login exitoso'),(373,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:50:31','Login exitoso'),(374,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:57:31','Login exitoso'),(375,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 18:58:18','Login exitoso'),(376,1,'admin','LOGIN_EXITOSO','172.18.0.1','curl/8.7.1','2026-02-17 19:01:41','Login exitoso'),(377,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 19:20:18','Login exitoso'),(378,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 19:37:01','Login exitoso'),(379,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 20:34:03','Login exitoso'),(380,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 20:41:11','Login exitoso'),(384,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 21:00:00','Login exitoso'),(385,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 21:00:02','Login exitoso'),(386,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 22:21:25','Login exitoso'),(387,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 22:32:41','Login exitoso'),(388,3,'vendedor01','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-17 22:33:18','Login exitoso'),(389,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 15:29:11','Login exitoso'),(390,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 16:30:04','Login exitoso'),(391,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 18:58:44','Login exitoso'),(393,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 19:13:22','Login exitoso'),(394,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 20:25:52','Login exitoso'),(395,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 20:36:32','Login exitoso'),(396,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 20:39:41','Login exitoso'),(397,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 20:39:43','Login exitoso'),(398,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 20:39:43','Login exitoso'),(399,1,'admin','LOGIN_EXITOSO','157.230.154.2','curl/8.7.1','2026-02-18 21:01:45','Login exitoso'),(409,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36','2026-02-18 21:14:22','Login exitoso'),(410,1,'admin','LOGIN_EXITOSO','157.230.154.2','curl/8.7.1','2026-02-18 22:02:08','Login exitoso'),(411,1,'admin','LOGIN_EXITOSO','157.230.154.2','curl/8.7.1','2026-02-18 22:02:59','Login exitoso'),(412,1,'admin','LOGIN_EXITOSO','157.230.154.2','curl/8.7.1','2026-02-18 22:05:02','Login exitoso'),(413,1,'admin','LOGIN_EXITOSO','157.230.154.2','curl/8.7.1','2026-02-18 22:13:07','Login exitoso'),(414,1,'admin','LOGIN_EXITOSO','157.230.154.2','curl/8.7.1','2026-02-19 17:13:21','Login exitoso'),(415,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-19 18:50:31','Login exitoso'),(417,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-19 18:57:19','Login exitoso'),(418,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-19 18:57:25','Login exitoso'),(419,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-19 19:28:23','Login exitoso'),(420,1,'admin','LOGIN_EXITOSO','157.230.154.2','curl/8.7.1','2026-02-19 20:42:30','Login exitoso'),(424,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-19 21:02:55','Login exitoso'),(425,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-19 22:01:28','Login exitoso'),(426,1,'admin','LOGIN_EXITOSO','172.18.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-20 00:10:24','Login exitoso'),(427,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-20 11:52:09','Login exitoso'),(428,1,'admin','LOGIN_EXITOSO','172.23.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36','2026-02-20 12:25:06','Login exitoso');
/*!40000 ALTER TABLE `auditoria_login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `activa` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `idx_categorias_activa` (`activa`),
  CONSTRAINT `chk_categoria_nombre_no_vacio` CHECK ((trim(`nombre`) <> _utf8mb4''))
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (1,'ANALGESICOS','Medicamentos para el dolor y fiebre',1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(2,'ANTIBIOTICOS','Tratamiento de infecciones bacterianas',1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(3,'DERMATOLOGICOS','Cuidado de la piel y afecciones cutáneas',1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(4,'GASTROINTESTINAL','Para acidez, reflujo y dolor estomacal',1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(5,'CUIDADO PERSONAL','Higiene y cuidado diario',1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(6,'GENERAL',NULL,1,'2026-01-26 18:49:55','2026-01-26 18:49:55');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historial_precios`
--

DROP TABLE IF EXISTS `historial_precios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_precios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `precio_anterior` decimal(10,2) NOT NULL,
  `precio_nuevo` decimal(10,2) NOT NULL,
  `costo_promedio_momento` decimal(10,2) DEFAULT NULL,
  `margen_anterior` decimal(5,2) DEFAULT NULL,
  `margen_nuevo` decimal(5,2) DEFAULT NULL,
  `motivo` text COLLATE utf8mb4_unicode_ci,
  `usuario_responsable` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_cambio` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_historial_precios_producto` (`producto_id`),
  CONSTRAINT `fk_historial_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_historial_precios_positivos` CHECK (((`precio_anterior` > 0) and (`precio_nuevo` > 0)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_precios`
--

LOCK TABLES `historial_precios` WRITE;
/*!40000 ALTER TABLE `historial_precios` DISABLE KEYS */;
/*!40000 ALTER TABLE `historial_precios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `laboratorios`
--

DROP TABLE IF EXISTS `laboratorios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `laboratorios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pais` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_laboratorio_nombre_pais` (`nombre`,`pais`),
  KEY `idx_laboratorios_activo` (`activo`),
  KEY `idx_laboratorios_nombre` (`nombre`),
  CONSTRAINT `chk_laboratorio_nombre_no_vacio` CHECK ((trim(`nombre`) <> _utf8mb4''))
) ENGINE=InnoDB AUTO_INCREMENT=3682 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `laboratorios`
--

LOCK TABLES `laboratorios` WRITE;
/*!40000 ALTER TABLE `laboratorios` DISABLE KEYS */;
INSERT INTO `laboratorios` VALUES (1,'GENFAR',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(2,'LA SANTE',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(3,'BAYER',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(4,'MK',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(5,'PFIZER',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(6,'JGB',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(7,'TECNOQUIMICAS',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(8,'HALEON',NULL,NULL,NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(9,'GENERICO',NULL,NULL,NULL,1,'2026-01-26 18:49:55','2026-01-26 18:49:55'),(10,'AG',NULL,NULL,NULL,1,'2026-01-26 18:49:56','2026-01-26 18:49:56'),(13,'OTC',NULL,NULL,NULL,1,'2026-01-26 18:49:57','2026-01-26 18:49:57'),(14,'OPHARM',NULL,NULL,NULL,1,'2026-01-26 18:49:57','2026-01-26 18:49:57'),(15,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-01-26 18:49:57','2026-01-26 18:49:57'),(16,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-01-26 18:49:57','2026-01-26 18:49:57'),(17,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-01-26 18:49:58','2026-01-26 18:49:58'),(18,'ECAR',NULL,NULL,NULL,1,'2026-01-26 18:49:58','2026-01-26 18:49:58'),(19,'LABQUIFAR',NULL,NULL,NULL,1,'2026-01-26 18:49:58','2026-01-26 18:49:58'),(20,'LAPROFF',NULL,NULL,NULL,1,'2026-01-26 18:49:59','2026-01-26 18:49:59'),(21,'COASPHARMA',NULL,NULL,NULL,1,'2026-01-26 18:49:59','2026-01-26 18:49:59'),(22,'MEMPHIS',NULL,NULL,NULL,1,'2026-01-26 18:49:59','2026-01-26 18:49:59'),(23,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-01-26 18:49:59','2026-01-26 18:49:59'),(24,'HARTUNG',NULL,NULL,NULL,1,'2026-01-26 18:50:00','2026-01-26 18:50:00'),(25,'JUHNIOS',NULL,NULL,NULL,1,'2026-01-26 18:50:00','2026-01-26 18:50:00'),(26,'AFR SAS',NULL,NULL,NULL,1,'2026-01-26 18:50:00','2026-01-26 18:50:00'),(27,'ABBOTT',NULL,NULL,NULL,1,'2026-01-26 18:50:01','2026-01-26 18:50:01'),(28,'ALIKIN',NULL,NULL,NULL,1,'2026-01-26 18:50:01','2026-01-26 18:50:01'),(29,'TOP GLOVE',NULL,NULL,NULL,1,'2026-01-26 18:50:01','2026-01-26 18:50:01'),(30,'OSA',NULL,NULL,NULL,1,'2026-01-26 18:50:02','2026-01-26 18:50:02'),(31,'SIEGFRIED',NULL,NULL,NULL,1,'2026-01-26 18:50:02','2026-01-26 18:50:02'),(32,'NOVAMED',NULL,NULL,NULL,1,'2026-01-26 18:50:02','2026-01-26 18:50:02'),(2770,'GENERICO',NULL,NULL,NULL,1,'2026-02-13 23:19:38','2026-02-13 23:19:38'),(2771,'AG',NULL,NULL,NULL,1,'2026-02-13 23:19:38','2026-02-13 23:19:38'),(2772,'GENFAR',NULL,NULL,NULL,1,'2026-02-13 23:19:39','2026-02-13 23:19:39'),(2773,'BAYER',NULL,NULL,NULL,1,'2026-02-13 23:19:39','2026-02-13 23:19:39'),(2774,'OTC',NULL,NULL,NULL,1,'2026-02-13 23:19:40','2026-02-13 23:19:40'),(2775,'OPHARM',NULL,NULL,NULL,1,'2026-02-13 23:19:40','2026-02-13 23:19:40'),(2776,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-13 23:19:40','2026-02-13 23:19:40'),(2777,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-13 23:19:41','2026-02-13 23:19:41'),(2778,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-13 23:19:41','2026-02-13 23:19:41'),(2779,'ECAR',NULL,NULL,NULL,1,'2026-02-13 23:19:42','2026-02-13 23:19:42'),(2780,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-13 23:19:42','2026-02-13 23:19:42'),(2781,'LAPROFF',NULL,NULL,NULL,1,'2026-02-13 23:19:43','2026-02-13 23:19:43'),(2782,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-13 23:19:43','2026-02-13 23:19:43'),(2783,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-13 23:19:43','2026-02-13 23:19:43'),(2784,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-13 23:19:44','2026-02-13 23:19:44'),(2785,'HARTUNG',NULL,NULL,NULL,1,'2026-02-13 23:19:44','2026-02-13 23:19:44'),(2786,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-13 23:19:44','2026-02-13 23:19:44'),(2787,'AFR SAS',NULL,NULL,NULL,1,'2026-02-13 23:19:45','2026-02-13 23:19:45'),(2788,'ABBOTT',NULL,NULL,NULL,1,'2026-02-13 23:19:45','2026-02-13 23:19:45'),(2789,'ALIKIN',NULL,NULL,NULL,1,'2026-02-13 23:19:46','2026-02-13 23:19:46'),(2790,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-13 23:19:46','2026-02-13 23:19:46'),(2791,'OSA',NULL,NULL,NULL,1,'2026-02-13 23:19:46','2026-02-13 23:19:46'),(2792,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-13 23:19:47','2026-02-13 23:19:47'),(2793,'NOVAMED',NULL,NULL,NULL,1,'2026-02-13 23:19:47','2026-02-13 23:19:47'),(2794,'GENERICO',NULL,NULL,NULL,1,'2026-02-14 01:01:34','2026-02-14 01:01:34'),(2795,'AG',NULL,NULL,NULL,1,'2026-02-14 01:01:35','2026-02-14 01:01:35'),(2796,'GENFAR',NULL,NULL,NULL,1,'2026-02-14 01:01:35','2026-02-14 01:01:35'),(2797,'BAYER',NULL,NULL,NULL,1,'2026-02-14 01:01:36','2026-02-14 01:01:36'),(2798,'OTC',NULL,NULL,NULL,1,'2026-02-14 01:01:36','2026-02-14 01:01:36'),(2799,'OPHARM',NULL,NULL,NULL,1,'2026-02-14 01:01:36','2026-02-14 01:01:36'),(2800,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-14 01:01:37','2026-02-14 01:01:37'),(2801,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-14 01:01:37','2026-02-14 01:01:37'),(2802,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-14 01:01:38','2026-02-14 01:01:38'),(2803,'ECAR',NULL,NULL,NULL,1,'2026-02-14 01:01:38','2026-02-14 01:01:38'),(2804,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-14 01:01:38','2026-02-14 01:01:38'),(2805,'LAPROFF',NULL,NULL,NULL,1,'2026-02-14 01:01:39','2026-02-14 01:01:39'),(2806,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-14 01:01:39','2026-02-14 01:01:39'),(2807,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-14 01:01:40','2026-02-14 01:01:40'),(2808,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-14 01:01:40','2026-02-14 01:01:40'),(2809,'HARTUNG',NULL,NULL,NULL,1,'2026-02-14 01:01:40','2026-02-14 01:01:40'),(2810,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-14 01:01:41','2026-02-14 01:01:41'),(2811,'AFR SAS',NULL,NULL,NULL,1,'2026-02-14 01:01:41','2026-02-14 01:01:41'),(2812,'ABBOTT',NULL,NULL,NULL,1,'2026-02-14 01:01:42','2026-02-14 01:01:42'),(2813,'ALIKIN',NULL,NULL,NULL,1,'2026-02-14 01:01:42','2026-02-14 01:01:42'),(2814,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-14 01:01:42','2026-02-14 01:01:42'),(2815,'OSA',NULL,NULL,NULL,1,'2026-02-14 01:01:43','2026-02-14 01:01:43'),(2816,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-14 01:01:43','2026-02-14 01:01:43'),(2817,'NOVAMED',NULL,NULL,NULL,1,'2026-02-14 01:01:43','2026-02-14 01:01:43'),(2818,'GENERICO',NULL,NULL,NULL,1,'2026-02-14 01:32:23','2026-02-14 01:32:23'),(2819,'AG',NULL,NULL,NULL,1,'2026-02-14 01:32:24','2026-02-14 01:32:24'),(2820,'GENFAR',NULL,NULL,NULL,1,'2026-02-14 01:32:24','2026-02-14 01:32:24'),(2821,'BAYER',NULL,NULL,NULL,1,'2026-02-14 01:32:25','2026-02-14 01:32:25'),(2822,'OTC',NULL,NULL,NULL,1,'2026-02-14 01:32:25','2026-02-14 01:32:25'),(2823,'OPHARM',NULL,NULL,NULL,1,'2026-02-14 01:32:26','2026-02-14 01:32:26'),(2824,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-14 01:32:26','2026-02-14 01:32:26'),(2825,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-14 01:32:26','2026-02-14 01:32:26'),(2826,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-14 01:32:27','2026-02-14 01:32:27'),(2827,'ECAR',NULL,NULL,NULL,1,'2026-02-14 01:32:27','2026-02-14 01:32:27'),(2828,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-14 01:32:28','2026-02-14 01:32:28'),(2829,'LAPROFF',NULL,NULL,NULL,1,'2026-02-14 01:32:28','2026-02-14 01:32:28'),(2830,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-14 01:32:28','2026-02-14 01:32:28'),(2831,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-14 01:32:29','2026-02-14 01:32:29'),(2832,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-14 01:32:29','2026-02-14 01:32:29'),(2833,'HARTUNG',NULL,NULL,NULL,1,'2026-02-14 01:32:30','2026-02-14 01:32:30'),(2834,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-14 01:32:30','2026-02-14 01:32:30'),(2835,'AFR SAS',NULL,NULL,NULL,1,'2026-02-14 01:32:30','2026-02-14 01:32:30'),(2836,'ABBOTT',NULL,NULL,NULL,1,'2026-02-14 01:32:31','2026-02-14 01:32:31'),(2837,'ALIKIN',NULL,NULL,NULL,1,'2026-02-14 01:32:31','2026-02-14 01:32:31'),(2838,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-14 01:32:32','2026-02-14 01:32:32'),(2839,'OSA',NULL,NULL,NULL,1,'2026-02-14 01:32:32','2026-02-14 01:32:32'),(2840,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-14 01:32:32','2026-02-14 01:32:32'),(2841,'NOVAMED',NULL,NULL,NULL,1,'2026-02-14 01:32:33','2026-02-14 01:32:33'),(2842,'GENERICO',NULL,NULL,NULL,1,'2026-02-15 00:29:06','2026-02-15 00:29:06'),(2843,'AG',NULL,NULL,NULL,1,'2026-02-15 00:29:06','2026-02-15 00:29:06'),(2844,'GENFAR',NULL,NULL,NULL,1,'2026-02-15 00:29:06','2026-02-15 00:29:06'),(2845,'BAYER',NULL,NULL,NULL,1,'2026-02-15 00:29:07','2026-02-15 00:29:07'),(2846,'OTC',NULL,NULL,NULL,1,'2026-02-15 00:29:07','2026-02-15 00:29:07'),(2847,'OPHARM',NULL,NULL,NULL,1,'2026-02-15 00:29:07','2026-02-15 00:29:07'),(2848,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-15 00:29:07','2026-02-15 00:29:07'),(2849,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-15 00:29:08','2026-02-15 00:29:08'),(2850,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-15 00:29:08','2026-02-15 00:29:08'),(2851,'ECAR',NULL,NULL,NULL,1,'2026-02-15 00:29:08','2026-02-15 00:29:08'),(2852,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-15 00:29:08','2026-02-15 00:29:08'),(2853,'LAPROFF',NULL,NULL,NULL,1,'2026-02-15 00:29:09','2026-02-15 00:29:09'),(2854,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-15 00:29:09','2026-02-15 00:29:09'),(2855,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-15 00:29:09','2026-02-15 00:29:09'),(2856,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-15 00:29:09','2026-02-15 00:29:09'),(2857,'HARTUNG',NULL,NULL,NULL,1,'2026-02-15 00:29:10','2026-02-15 00:29:10'),(2858,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-15 00:29:10','2026-02-15 00:29:10'),(2859,'AFR SAS',NULL,NULL,NULL,1,'2026-02-15 00:29:10','2026-02-15 00:29:10'),(2860,'ABBOTT',NULL,NULL,NULL,1,'2026-02-15 00:29:10','2026-02-15 00:29:10'),(2861,'ALIKIN',NULL,NULL,NULL,1,'2026-02-15 00:29:11','2026-02-15 00:29:11'),(2862,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-15 00:29:11','2026-02-15 00:29:11'),(2863,'OSA',NULL,NULL,NULL,1,'2026-02-15 00:29:11','2026-02-15 00:29:11'),(2864,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-15 00:29:12','2026-02-15 00:29:12'),(2865,'NOVAMED',NULL,NULL,NULL,1,'2026-02-15 00:29:12','2026-02-15 00:29:12'),(2866,'GENERICO',NULL,NULL,NULL,1,'2026-02-15 00:34:57','2026-02-15 00:34:57'),(2867,'AG',NULL,NULL,NULL,1,'2026-02-15 00:34:58','2026-02-15 00:34:58'),(2868,'GENFAR',NULL,NULL,NULL,1,'2026-02-15 00:34:58','2026-02-15 00:34:58'),(2869,'BAYER',NULL,NULL,NULL,1,'2026-02-15 00:34:58','2026-02-15 00:34:58'),(2870,'OTC',NULL,NULL,NULL,1,'2026-02-15 00:34:58','2026-02-15 00:34:58'),(2871,'OPHARM',NULL,NULL,NULL,1,'2026-02-15 00:34:59','2026-02-15 00:34:59'),(2872,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-15 00:34:59','2026-02-15 00:34:59'),(2873,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-15 00:34:59','2026-02-15 00:34:59'),(2874,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-15 00:34:59','2026-02-15 00:34:59'),(2875,'ECAR',NULL,NULL,NULL,1,'2026-02-15 00:35:00','2026-02-15 00:35:00'),(2876,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-15 00:35:00','2026-02-15 00:35:00'),(2877,'LAPROFF',NULL,NULL,NULL,1,'2026-02-15 00:35:00','2026-02-15 00:35:00'),(2878,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-15 00:35:00','2026-02-15 00:35:00'),(2879,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-15 00:35:01','2026-02-15 00:35:01'),(2880,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-15 00:35:01','2026-02-15 00:35:01'),(2881,'HARTUNG',NULL,NULL,NULL,1,'2026-02-15 00:35:01','2026-02-15 00:35:01'),(2882,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-15 00:35:01','2026-02-15 00:35:01'),(2883,'AFR SAS',NULL,NULL,NULL,1,'2026-02-15 00:35:02','2026-02-15 00:35:02'),(2884,'ABBOTT',NULL,NULL,NULL,1,'2026-02-15 00:35:02','2026-02-15 00:35:02'),(2885,'ALIKIN',NULL,NULL,NULL,1,'2026-02-15 00:35:02','2026-02-15 00:35:02'),(2886,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-15 00:35:02','2026-02-15 00:35:02'),(2887,'OSA',NULL,NULL,NULL,1,'2026-02-15 00:35:03','2026-02-15 00:35:03'),(2888,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-15 00:35:03','2026-02-15 00:35:03'),(2889,'NOVAMED',NULL,NULL,NULL,1,'2026-02-15 00:35:03','2026-02-15 00:35:03'),(2890,'GENERICO',NULL,NULL,NULL,1,'2026-02-15 18:31:58','2026-02-15 18:31:58'),(2891,'AG',NULL,NULL,NULL,1,'2026-02-15 18:32:00','2026-02-15 18:32:00'),(2892,'GENFAR',NULL,NULL,NULL,1,'2026-02-15 18:32:04','2026-02-15 18:32:04'),(2893,'BAYER',NULL,NULL,NULL,1,'2026-02-15 18:32:05','2026-02-15 18:32:05'),(2894,'OTC',NULL,NULL,NULL,1,'2026-02-15 18:32:07','2026-02-15 18:32:07'),(2895,'OPHARM',NULL,NULL,NULL,1,'2026-02-15 18:32:09','2026-02-15 18:32:09'),(2896,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-15 18:32:11','2026-02-15 18:32:11'),(2897,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-15 18:32:13','2026-02-15 18:32:13'),(2898,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-15 18:32:15','2026-02-15 18:32:15'),(2899,'ECAR',NULL,NULL,NULL,1,'2026-02-15 18:32:19','2026-02-15 18:32:19'),(2900,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-15 18:32:22','2026-02-15 18:32:22'),(2901,'LAPROFF',NULL,NULL,NULL,1,'2026-02-15 18:32:26','2026-02-15 18:32:26'),(2902,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-15 18:33:03','2026-02-15 18:33:03'),(2903,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-15 18:33:05','2026-02-15 18:33:05'),(2904,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-15 18:33:06','2026-02-15 18:33:06'),(2905,'HARTUNG',NULL,NULL,NULL,1,'2026-02-15 18:33:08','2026-02-15 18:33:08'),(2906,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-15 18:33:10','2026-02-15 18:33:10'),(2907,'AFR SAS',NULL,NULL,NULL,1,'2026-02-15 18:33:12','2026-02-15 18:33:12'),(2908,'ABBOTT',NULL,NULL,NULL,1,'2026-02-15 18:33:14','2026-02-15 18:33:14'),(2909,'ALIKIN',NULL,NULL,NULL,1,'2026-02-15 18:33:17','2026-02-15 18:33:17'),(2910,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-15 18:33:19','2026-02-15 18:33:19'),(2911,'OSA',NULL,NULL,NULL,1,'2026-02-15 18:33:21','2026-02-15 18:33:21'),(2912,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-15 18:33:23','2026-02-15 18:33:23'),(2913,'NOVAMED',NULL,NULL,NULL,1,'2026-02-15 18:33:25','2026-02-15 18:33:25'),(2914,'GENERICO',NULL,NULL,NULL,1,'2026-02-15 19:34:39','2026-02-15 19:34:39'),(2915,'AG',NULL,NULL,NULL,1,'2026-02-15 19:34:41','2026-02-15 19:34:41'),(2916,'GENFAR',NULL,NULL,NULL,1,'2026-02-15 19:34:42','2026-02-15 19:34:42'),(2917,'BAYER',NULL,NULL,NULL,1,'2026-02-15 19:34:45','2026-02-15 19:34:45'),(2918,'OTC',NULL,NULL,NULL,1,'2026-02-15 19:34:47','2026-02-15 19:34:47'),(2919,'OPHARM',NULL,NULL,NULL,1,'2026-02-15 19:34:48','2026-02-15 19:34:48'),(2920,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-15 19:34:50','2026-02-15 19:34:50'),(2921,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-15 19:34:56','2026-02-15 19:34:56'),(2922,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-15 19:35:00','2026-02-15 19:35:00'),(2923,'ECAR',NULL,NULL,NULL,1,'2026-02-15 19:35:02','2026-02-15 19:35:02'),(2924,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-15 19:35:10','2026-02-15 19:35:10'),(2925,'LAPROFF',NULL,NULL,NULL,1,'2026-02-15 19:35:18','2026-02-15 19:35:18'),(2926,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-15 19:35:20','2026-02-15 19:35:20'),(2927,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-15 19:35:23','2026-02-15 19:35:23'),(2928,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-15 19:35:25','2026-02-15 19:35:25'),(2929,'HARTUNG',NULL,NULL,NULL,1,'2026-02-15 19:35:27','2026-02-15 19:35:27'),(2930,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-15 19:35:31','2026-02-15 19:35:31'),(2931,'AFR SAS',NULL,NULL,NULL,1,'2026-02-15 19:35:33','2026-02-15 19:35:33'),(2932,'ABBOTT',NULL,NULL,NULL,1,'2026-02-15 19:35:34','2026-02-15 19:35:34'),(2933,'ALIKIN',NULL,NULL,NULL,1,'2026-02-15 19:35:36','2026-02-15 19:35:36'),(2934,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-15 19:35:39','2026-02-15 19:35:39'),(2935,'OSA',NULL,NULL,NULL,1,'2026-02-15 19:35:41','2026-02-15 19:35:41'),(2936,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-15 19:35:42','2026-02-15 19:35:42'),(2937,'NOVAMED',NULL,NULL,NULL,1,'2026-02-15 19:35:44','2026-02-15 19:35:44'),(2938,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 00:37:26','2026-02-16 00:37:26'),(2939,'AG',NULL,NULL,NULL,1,'2026-02-16 00:37:26','2026-02-16 00:37:26'),(2940,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 00:37:27','2026-02-16 00:37:27'),(2941,'BAYER',NULL,NULL,NULL,1,'2026-02-16 00:37:27','2026-02-16 00:37:27'),(2942,'OTC',NULL,NULL,NULL,1,'2026-02-16 00:37:28','2026-02-16 00:37:28'),(2943,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 00:37:29','2026-02-16 00:37:29'),(2944,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 00:37:29','2026-02-16 00:37:29'),(2945,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 00:37:30','2026-02-16 00:37:30'),(2946,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 00:37:30','2026-02-16 00:37:30'),(2947,'ECAR',NULL,NULL,NULL,1,'2026-02-16 00:37:31','2026-02-16 00:37:31'),(2948,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 00:37:31','2026-02-16 00:37:31'),(2949,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 00:37:32','2026-02-16 00:37:32'),(2950,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 00:37:32','2026-02-16 00:37:32'),(2951,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 00:37:33','2026-02-16 00:37:33'),(2952,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 00:37:33','2026-02-16 00:37:33'),(2953,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 00:37:34','2026-02-16 00:37:34'),(2954,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 00:37:34','2026-02-16 00:37:34'),(2955,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 00:37:35','2026-02-16 00:37:35'),(2956,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 00:37:35','2026-02-16 00:37:35'),(2957,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 00:37:35','2026-02-16 00:37:35'),(2958,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 00:37:36','2026-02-16 00:37:36'),(2959,'OSA',NULL,NULL,NULL,1,'2026-02-16 00:37:37','2026-02-16 00:37:37'),(2960,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 00:37:37','2026-02-16 00:37:37'),(2961,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 00:37:37','2026-02-16 00:37:37'),(2962,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 01:21:52','2026-02-16 01:21:52'),(2963,'AG',NULL,NULL,NULL,1,'2026-02-16 01:21:53','2026-02-16 01:21:53'),(2964,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 01:21:53','2026-02-16 01:21:53'),(2965,'BAYER',NULL,NULL,NULL,1,'2026-02-16 01:21:54','2026-02-16 01:21:54'),(2966,'OTC',NULL,NULL,NULL,1,'2026-02-16 01:21:55','2026-02-16 01:21:55'),(2967,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 01:21:55','2026-02-16 01:21:55'),(2968,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 01:21:56','2026-02-16 01:21:56'),(2969,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 01:21:56','2026-02-16 01:21:56'),(2970,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 01:21:57','2026-02-16 01:21:57'),(2971,'ECAR',NULL,NULL,NULL,1,'2026-02-16 01:21:57','2026-02-16 01:21:57'),(2972,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 01:21:58','2026-02-16 01:21:58'),(2973,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 01:21:59','2026-02-16 01:21:59'),(2974,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 01:21:59','2026-02-16 01:21:59'),(2975,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 01:22:00','2026-02-16 01:22:00'),(2976,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 01:22:00','2026-02-16 01:22:00'),(2977,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 01:22:01','2026-02-16 01:22:01'),(2978,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 01:22:01','2026-02-16 01:22:01'),(2979,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 01:22:02','2026-02-16 01:22:02'),(2980,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 01:22:02','2026-02-16 01:22:02'),(2981,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 01:22:03','2026-02-16 01:22:03'),(2982,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 01:22:04','2026-02-16 01:22:04'),(2983,'OSA',NULL,NULL,NULL,1,'2026-02-16 01:22:04','2026-02-16 01:22:04'),(2984,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 01:22:04','2026-02-16 01:22:04'),(2985,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 01:22:05','2026-02-16 01:22:05'),(2986,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 01:47:43','2026-02-16 01:47:43'),(2987,'AG',NULL,NULL,NULL,1,'2026-02-16 01:47:44','2026-02-16 01:47:44'),(2988,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 01:47:44','2026-02-16 01:47:44'),(2989,'BAYER',NULL,NULL,NULL,1,'2026-02-16 01:47:45','2026-02-16 01:47:45'),(2990,'OTC',NULL,NULL,NULL,1,'2026-02-16 01:47:45','2026-02-16 01:47:45'),(2991,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 01:47:46','2026-02-16 01:47:46'),(2992,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 01:47:47','2026-02-16 01:47:47'),(2993,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 01:47:47','2026-02-16 01:47:47'),(2994,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 01:47:48','2026-02-16 01:47:48'),(2995,'ECAR',NULL,NULL,NULL,1,'2026-02-16 01:47:49','2026-02-16 01:47:49'),(2996,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 01:47:49','2026-02-16 01:47:49'),(2997,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 01:47:50','2026-02-16 01:47:50'),(2998,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 01:47:50','2026-02-16 01:47:50'),(2999,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 01:47:50','2026-02-16 01:47:50'),(3000,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 01:47:51','2026-02-16 01:47:51'),(3001,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 01:47:51','2026-02-16 01:47:51'),(3002,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 01:47:52','2026-02-16 01:47:52'),(3003,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 01:47:53','2026-02-16 01:47:53'),(3004,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 01:47:53','2026-02-16 01:47:53'),(3005,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 01:47:54','2026-02-16 01:47:54'),(3006,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 01:47:54','2026-02-16 01:47:54'),(3007,'OSA',NULL,NULL,NULL,1,'2026-02-16 01:47:54','2026-02-16 01:47:54'),(3008,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 01:47:55','2026-02-16 01:47:55'),(3009,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 01:47:55','2026-02-16 01:47:55'),(3010,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 01:58:25','2026-02-16 01:58:25'),(3011,'AG',NULL,NULL,NULL,1,'2026-02-16 01:58:25','2026-02-16 01:58:25'),(3012,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 01:58:26','2026-02-16 01:58:26'),(3013,'BAYER',NULL,NULL,NULL,1,'2026-02-16 01:58:27','2026-02-16 01:58:27'),(3014,'OTC',NULL,NULL,NULL,1,'2026-02-16 01:58:27','2026-02-16 01:58:27'),(3015,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 01:58:28','2026-02-16 01:58:28'),(3016,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 01:58:29','2026-02-16 01:58:29'),(3017,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 01:58:29','2026-02-16 01:58:29'),(3018,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 01:58:30','2026-02-16 01:58:30'),(3019,'ECAR',NULL,NULL,NULL,1,'2026-02-16 01:58:30','2026-02-16 01:58:30'),(3020,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 01:58:31','2026-02-16 01:58:31'),(3021,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 01:58:31','2026-02-16 01:58:31'),(3022,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 01:58:31','2026-02-16 01:58:31'),(3023,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 01:58:32','2026-02-16 01:58:32'),(3024,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 01:58:32','2026-02-16 01:58:32'),(3025,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 01:58:33','2026-02-16 01:58:33'),(3026,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 01:58:33','2026-02-16 01:58:33'),(3027,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 01:58:34','2026-02-16 01:58:34'),(3028,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 01:58:34','2026-02-16 01:58:34'),(3029,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 01:58:34','2026-02-16 01:58:34'),(3030,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 01:58:35','2026-02-16 01:58:35'),(3031,'OSA',NULL,NULL,NULL,1,'2026-02-16 01:58:35','2026-02-16 01:58:35'),(3032,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 01:58:36','2026-02-16 01:58:36'),(3033,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 01:58:37','2026-02-16 01:58:37'),(3034,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 03:26:14','2026-02-16 03:26:14'),(3035,'AG',NULL,NULL,NULL,1,'2026-02-16 03:26:15','2026-02-16 03:26:15'),(3036,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 03:26:15','2026-02-16 03:26:15'),(3037,'BAYER',NULL,NULL,NULL,1,'2026-02-16 03:26:16','2026-02-16 03:26:16'),(3038,'OTC',NULL,NULL,NULL,1,'2026-02-16 03:26:16','2026-02-16 03:26:16'),(3039,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 03:26:16','2026-02-16 03:26:16'),(3040,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 03:26:16','2026-02-16 03:26:16'),(3041,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 03:26:17','2026-02-16 03:26:17'),(3042,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 03:26:17','2026-02-16 03:26:17'),(3043,'ECAR',NULL,NULL,NULL,1,'2026-02-16 03:26:17','2026-02-16 03:26:17'),(3044,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 03:26:18','2026-02-16 03:26:18'),(3045,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 03:26:18','2026-02-16 03:26:18'),(3046,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 03:26:19','2026-02-16 03:26:19'),(3047,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 03:26:19','2026-02-16 03:26:19'),(3048,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 03:26:19','2026-02-16 03:26:19'),(3049,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 03:26:19','2026-02-16 03:26:19'),(3050,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 03:26:20','2026-02-16 03:26:20'),(3051,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 03:26:20','2026-02-16 03:26:20'),(3052,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 03:26:21','2026-02-16 03:26:21'),(3053,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 03:26:21','2026-02-16 03:26:21'),(3054,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 03:26:22','2026-02-16 03:26:22'),(3055,'OSA',NULL,NULL,NULL,1,'2026-02-16 03:26:22','2026-02-16 03:26:22'),(3056,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 03:26:22','2026-02-16 03:26:22'),(3057,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 03:26:23','2026-02-16 03:26:23'),(3058,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 04:11:19','2026-02-16 04:11:19'),(3059,'AG',NULL,NULL,NULL,1,'2026-02-16 04:11:19','2026-02-16 04:11:19'),(3060,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 04:11:20','2026-02-16 04:11:20'),(3061,'BAYER',NULL,NULL,NULL,1,'2026-02-16 04:11:20','2026-02-16 04:11:20'),(3062,'OTC',NULL,NULL,NULL,1,'2026-02-16 04:11:20','2026-02-16 04:11:20'),(3063,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 04:11:20','2026-02-16 04:11:20'),(3064,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 04:11:21','2026-02-16 04:11:21'),(3065,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 04:11:21','2026-02-16 04:11:21'),(3066,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 04:11:21','2026-02-16 04:11:21'),(3067,'ECAR',NULL,NULL,NULL,1,'2026-02-16 04:11:21','2026-02-16 04:11:21'),(3068,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 04:11:22','2026-02-16 04:11:22'),(3069,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 04:11:22','2026-02-16 04:11:22'),(3070,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 04:11:22','2026-02-16 04:11:22'),(3071,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 04:11:22','2026-02-16 04:11:22'),(3072,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 04:11:23','2026-02-16 04:11:23'),(3073,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 04:11:23','2026-02-16 04:11:23'),(3074,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 04:11:23','2026-02-16 04:11:23'),(3075,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 04:11:24','2026-02-16 04:11:24'),(3076,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 04:11:24','2026-02-16 04:11:24'),(3077,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 04:11:24','2026-02-16 04:11:24'),(3078,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 04:11:24','2026-02-16 04:11:24'),(3079,'OSA',NULL,NULL,NULL,1,'2026-02-16 04:11:25','2026-02-16 04:11:25'),(3080,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 04:11:25','2026-02-16 04:11:25'),(3081,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 04:11:25','2026-02-16 04:11:25'),(3082,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 17:18:52','2026-02-16 17:18:52'),(3083,'AG',NULL,NULL,NULL,1,'2026-02-16 17:18:52','2026-02-16 17:18:52'),(3084,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 17:18:52','2026-02-16 17:18:52'),(3085,'BAYER',NULL,NULL,NULL,1,'2026-02-16 17:18:52','2026-02-16 17:18:52'),(3086,'OTC',NULL,NULL,NULL,1,'2026-02-16 17:18:53','2026-02-16 17:18:53'),(3087,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 17:18:53','2026-02-16 17:18:53'),(3088,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:18:53','2026-02-16 17:18:53'),(3089,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 17:18:54','2026-02-16 17:18:54'),(3090,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 17:18:54','2026-02-16 17:18:54'),(3091,'ECAR',NULL,NULL,NULL,1,'2026-02-16 17:18:54','2026-02-16 17:18:54'),(3092,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 17:18:55','2026-02-16 17:18:55'),(3093,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 17:18:55','2026-02-16 17:18:55'),(3094,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:18:55','2026-02-16 17:18:55'),(3095,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 17:18:56','2026-02-16 17:18:56'),(3096,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 17:18:56','2026-02-16 17:18:56'),(3097,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 17:18:56','2026-02-16 17:18:56'),(3098,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 17:18:57','2026-02-16 17:18:57'),(3099,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 17:18:57','2026-02-16 17:18:57'),(3100,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 17:18:57','2026-02-16 17:18:57'),(3101,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 17:18:58','2026-02-16 17:18:58'),(3102,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 17:18:58','2026-02-16 17:18:58'),(3103,'OSA',NULL,NULL,NULL,1,'2026-02-16 17:18:58','2026-02-16 17:18:58'),(3104,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 17:18:58','2026-02-16 17:18:58'),(3105,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 17:18:59','2026-02-16 17:18:59'),(3106,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 17:40:34','2026-02-16 17:40:34'),(3107,'AG',NULL,NULL,NULL,1,'2026-02-16 17:40:34','2026-02-16 17:40:34'),(3108,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 17:40:35','2026-02-16 17:40:35'),(3109,'BAYER',NULL,NULL,NULL,1,'2026-02-16 17:40:35','2026-02-16 17:40:35'),(3110,'OTC',NULL,NULL,NULL,1,'2026-02-16 17:40:35','2026-02-16 17:40:35'),(3111,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 17:40:36','2026-02-16 17:40:36'),(3112,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:40:36','2026-02-16 17:40:36'),(3113,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 17:40:36','2026-02-16 17:40:36'),(3114,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 17:40:37','2026-02-16 17:40:37'),(3115,'ECAR',NULL,NULL,NULL,1,'2026-02-16 17:40:37','2026-02-16 17:40:37'),(3116,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 17:40:37','2026-02-16 17:40:37'),(3117,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 17:40:37','2026-02-16 17:40:37'),(3118,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:40:38','2026-02-16 17:40:38'),(3119,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 17:40:38','2026-02-16 17:40:38'),(3120,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 17:40:38','2026-02-16 17:40:38'),(3121,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 17:40:39','2026-02-16 17:40:39'),(3122,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 17:40:39','2026-02-16 17:40:39'),(3123,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 17:40:39','2026-02-16 17:40:39'),(3124,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 17:40:40','2026-02-16 17:40:40'),(3125,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 17:40:40','2026-02-16 17:40:40'),(3126,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 17:40:40','2026-02-16 17:40:40'),(3127,'OSA',NULL,NULL,NULL,1,'2026-02-16 17:40:41','2026-02-16 17:40:41'),(3128,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 17:40:41','2026-02-16 17:40:41'),(3129,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 17:40:41','2026-02-16 17:40:41'),(3130,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 17:48:11','2026-02-16 17:48:11'),(3131,'AG',NULL,NULL,NULL,1,'2026-02-16 17:48:11','2026-02-16 17:48:11'),(3132,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 17:48:12','2026-02-16 17:48:12'),(3133,'BAYER',NULL,NULL,NULL,1,'2026-02-16 17:48:12','2026-02-16 17:48:12'),(3134,'OTC',NULL,NULL,NULL,1,'2026-02-16 17:48:12','2026-02-16 17:48:12'),(3135,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 17:48:12','2026-02-16 17:48:12'),(3136,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:48:13','2026-02-16 17:48:13'),(3137,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 17:48:13','2026-02-16 17:48:13'),(3138,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 17:48:13','2026-02-16 17:48:13'),(3139,'ECAR',NULL,NULL,NULL,1,'2026-02-16 17:48:13','2026-02-16 17:48:13'),(3140,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 17:48:14','2026-02-16 17:48:14'),(3141,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 17:48:14','2026-02-16 17:48:14'),(3142,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:48:14','2026-02-16 17:48:14'),(3143,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 17:48:15','2026-02-16 17:48:15'),(3144,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 17:48:15','2026-02-16 17:48:15'),(3145,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 17:48:15','2026-02-16 17:48:15'),(3146,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 17:48:15','2026-02-16 17:48:15'),(3147,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 17:48:16','2026-02-16 17:48:16'),(3148,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 17:48:16','2026-02-16 17:48:16'),(3149,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 17:48:16','2026-02-16 17:48:16'),(3150,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 17:48:17','2026-02-16 17:48:17'),(3151,'OSA',NULL,NULL,NULL,1,'2026-02-16 17:48:17','2026-02-16 17:48:17'),(3152,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 17:48:17','2026-02-16 17:48:17'),(3153,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 17:48:17','2026-02-16 17:48:17'),(3154,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 17:48:22','2026-02-16 17:48:22'),(3155,'AG',NULL,NULL,NULL,1,'2026-02-16 17:48:23','2026-02-16 17:48:23'),(3156,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 17:48:23','2026-02-16 17:48:23'),(3157,'BAYER',NULL,NULL,NULL,1,'2026-02-16 17:48:23','2026-02-16 17:48:23'),(3158,'OTC',NULL,NULL,NULL,1,'2026-02-16 17:48:24','2026-02-16 17:48:24'),(3159,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 17:48:24','2026-02-16 17:48:24'),(3160,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:48:24','2026-02-16 17:48:24'),(3161,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 17:48:24','2026-02-16 17:48:24'),(3162,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 17:48:25','2026-02-16 17:48:25'),(3163,'ECAR',NULL,NULL,NULL,1,'2026-02-16 17:48:25','2026-02-16 17:48:25'),(3164,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 17:48:25','2026-02-16 17:48:25'),(3165,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 17:48:26','2026-02-16 17:48:26'),(3166,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:48:26','2026-02-16 17:48:26'),(3167,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 17:48:26','2026-02-16 17:48:26'),(3168,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 17:48:27','2026-02-16 17:48:27'),(3169,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 17:48:27','2026-02-16 17:48:27'),(3170,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 17:48:27','2026-02-16 17:48:27'),(3171,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 17:48:28','2026-02-16 17:48:28'),(3172,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 17:48:28','2026-02-16 17:48:28'),(3173,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 17:48:28','2026-02-16 17:48:28'),(3174,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 17:48:29','2026-02-16 17:48:29'),(3175,'OSA',NULL,NULL,NULL,1,'2026-02-16 17:48:29','2026-02-16 17:48:29'),(3176,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 17:48:29','2026-02-16 17:48:29'),(3177,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 17:48:29','2026-02-16 17:48:29'),(3178,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 17:59:43','2026-02-16 17:59:43'),(3179,'AG',NULL,NULL,NULL,1,'2026-02-16 17:59:44','2026-02-16 17:59:44'),(3180,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 17:59:44','2026-02-16 17:59:44'),(3181,'BAYER',NULL,NULL,NULL,1,'2026-02-16 17:59:44','2026-02-16 17:59:44'),(3182,'OTC',NULL,NULL,NULL,1,'2026-02-16 17:59:45','2026-02-16 17:59:45'),(3183,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 17:59:45','2026-02-16 17:59:45'),(3184,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:59:45','2026-02-16 17:59:45'),(3185,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 17:59:45','2026-02-16 17:59:45'),(3186,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 17:59:46','2026-02-16 17:59:46'),(3187,'ECAR',NULL,NULL,NULL,1,'2026-02-16 17:59:46','2026-02-16 17:59:46'),(3188,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 17:59:46','2026-02-16 17:59:46'),(3189,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 17:59:47','2026-02-16 17:59:47'),(3190,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 17:59:47','2026-02-16 17:59:47'),(3191,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 17:59:47','2026-02-16 17:59:47'),(3192,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 17:59:48','2026-02-16 17:59:48'),(3193,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 17:59:48','2026-02-16 17:59:48'),(3194,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 17:59:48','2026-02-16 17:59:48'),(3195,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 17:59:49','2026-02-16 17:59:49'),(3196,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 17:59:49','2026-02-16 17:59:49'),(3197,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 17:59:49','2026-02-16 17:59:49'),(3198,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 17:59:50','2026-02-16 17:59:50'),(3199,'OSA',NULL,NULL,NULL,1,'2026-02-16 17:59:50','2026-02-16 17:59:50'),(3200,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 17:59:50','2026-02-16 17:59:50'),(3201,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 17:59:50','2026-02-16 17:59:50'),(3202,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 18:29:17','2026-02-16 18:29:17'),(3203,'AG',NULL,NULL,NULL,1,'2026-02-16 18:29:17','2026-02-16 18:29:17'),(3204,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 18:29:18','2026-02-16 18:29:18'),(3205,'BAYER',NULL,NULL,NULL,1,'2026-02-16 18:29:18','2026-02-16 18:29:18'),(3206,'OTC',NULL,NULL,NULL,1,'2026-02-16 18:29:18','2026-02-16 18:29:18'),(3207,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 18:29:18','2026-02-16 18:29:18'),(3208,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:29:19','2026-02-16 18:29:19'),(3209,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 18:29:19','2026-02-16 18:29:19'),(3210,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 18:29:19','2026-02-16 18:29:19'),(3211,'ECAR',NULL,NULL,NULL,1,'2026-02-16 18:29:19','2026-02-16 18:29:19'),(3212,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 18:29:20','2026-02-16 18:29:20'),(3213,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 18:29:20','2026-02-16 18:29:20'),(3214,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:29:20','2026-02-16 18:29:20'),(3215,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 18:29:20','2026-02-16 18:29:20'),(3216,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 18:29:21','2026-02-16 18:29:21'),(3217,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 18:29:21','2026-02-16 18:29:21'),(3218,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 18:29:21','2026-02-16 18:29:21'),(3219,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 18:29:21','2026-02-16 18:29:21'),(3220,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 18:29:22','2026-02-16 18:29:22'),(3221,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 18:29:22','2026-02-16 18:29:22'),(3222,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 18:29:22','2026-02-16 18:29:22'),(3223,'OSA',NULL,NULL,NULL,1,'2026-02-16 18:29:23','2026-02-16 18:29:23'),(3224,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 18:29:23','2026-02-16 18:29:23'),(3225,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 18:29:23','2026-02-16 18:29:23'),(3226,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 18:35:18','2026-02-16 18:35:18'),(3227,'AG',NULL,NULL,NULL,1,'2026-02-16 18:35:19','2026-02-16 18:35:19'),(3228,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 18:35:19','2026-02-16 18:35:19'),(3229,'BAYER',NULL,NULL,NULL,1,'2026-02-16 18:35:19','2026-02-16 18:35:19'),(3230,'OTC',NULL,NULL,NULL,1,'2026-02-16 18:35:19','2026-02-16 18:35:19'),(3231,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 18:35:20','2026-02-16 18:35:20'),(3232,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:35:20','2026-02-16 18:35:20'),(3233,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 18:35:20','2026-02-16 18:35:20'),(3234,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 18:35:21','2026-02-16 18:35:21'),(3235,'ECAR',NULL,NULL,NULL,1,'2026-02-16 18:35:21','2026-02-16 18:35:21'),(3236,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 18:35:21','2026-02-16 18:35:21'),(3237,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 18:35:21','2026-02-16 18:35:21'),(3238,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:35:22','2026-02-16 18:35:22'),(3239,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 18:35:22','2026-02-16 18:35:22'),(3240,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 18:35:22','2026-02-16 18:35:22'),(3241,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 18:35:23','2026-02-16 18:35:23'),(3242,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 18:35:23','2026-02-16 18:35:23'),(3243,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 18:35:23','2026-02-16 18:35:23'),(3244,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 18:35:24','2026-02-16 18:35:24'),(3245,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 18:35:24','2026-02-16 18:35:24'),(3246,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 18:35:24','2026-02-16 18:35:24'),(3247,'OSA',NULL,NULL,NULL,1,'2026-02-16 18:35:24','2026-02-16 18:35:24'),(3248,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 18:35:25','2026-02-16 18:35:25'),(3249,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 18:35:25','2026-02-16 18:35:25'),(3250,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 18:42:01','2026-02-16 18:42:01'),(3251,'AG',NULL,NULL,NULL,1,'2026-02-16 18:42:01','2026-02-16 18:42:01'),(3252,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 18:42:01','2026-02-16 18:42:01'),(3253,'BAYER',NULL,NULL,NULL,1,'2026-02-16 18:42:01','2026-02-16 18:42:01'),(3254,'OTC',NULL,NULL,NULL,1,'2026-02-16 18:42:02','2026-02-16 18:42:02'),(3255,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 18:42:02','2026-02-16 18:42:02'),(3256,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:42:02','2026-02-16 18:42:02'),(3257,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 18:42:03','2026-02-16 18:42:03'),(3258,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 18:42:03','2026-02-16 18:42:03'),(3259,'ECAR',NULL,NULL,NULL,1,'2026-02-16 18:42:03','2026-02-16 18:42:03'),(3260,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 18:42:03','2026-02-16 18:42:03'),(3261,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 18:42:04','2026-02-16 18:42:04'),(3262,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:42:04','2026-02-16 18:42:04'),(3263,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 18:42:04','2026-02-16 18:42:04'),(3264,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 18:42:05','2026-02-16 18:42:05'),(3265,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 18:42:05','2026-02-16 18:42:05'),(3266,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 18:42:05','2026-02-16 18:42:05'),(3267,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 18:42:05','2026-02-16 18:42:05'),(3268,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 18:42:06','2026-02-16 18:42:06'),(3269,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 18:42:06','2026-02-16 18:42:06'),(3270,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 18:42:06','2026-02-16 18:42:06'),(3271,'OSA',NULL,NULL,NULL,1,'2026-02-16 18:42:07','2026-02-16 18:42:07'),(3272,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 18:42:07','2026-02-16 18:42:07'),(3273,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 18:42:07','2026-02-16 18:42:07'),(3274,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 18:46:30','2026-02-16 18:46:30'),(3275,'AG',NULL,NULL,NULL,1,'2026-02-16 18:46:30','2026-02-16 18:46:30'),(3276,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 18:46:31','2026-02-16 18:46:31'),(3277,'BAYER',NULL,NULL,NULL,1,'2026-02-16 18:46:31','2026-02-16 18:46:31'),(3278,'OTC',NULL,NULL,NULL,1,'2026-02-16 18:46:31','2026-02-16 18:46:31'),(3279,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 18:46:31','2026-02-16 18:46:31'),(3280,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:46:32','2026-02-16 18:46:32'),(3281,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 18:46:32','2026-02-16 18:46:32'),(3282,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 18:46:32','2026-02-16 18:46:32'),(3283,'ECAR',NULL,NULL,NULL,1,'2026-02-16 18:46:33','2026-02-16 18:46:33'),(3284,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 18:46:33','2026-02-16 18:46:33'),(3285,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 18:46:33','2026-02-16 18:46:33'),(3286,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:46:34','2026-02-16 18:46:34'),(3287,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 18:46:34','2026-02-16 18:46:34'),(3288,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 18:46:34','2026-02-16 18:46:34'),(3289,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 18:46:35','2026-02-16 18:46:35'),(3290,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 18:46:35','2026-02-16 18:46:35'),(3291,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 18:46:35','2026-02-16 18:46:35'),(3292,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 18:46:36','2026-02-16 18:46:36'),(3293,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 18:46:36','2026-02-16 18:46:36'),(3294,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 18:46:36','2026-02-16 18:46:36'),(3295,'OSA',NULL,NULL,NULL,1,'2026-02-16 18:46:37','2026-02-16 18:46:37'),(3296,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 18:46:37','2026-02-16 18:46:37'),(3297,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 18:46:37','2026-02-16 18:46:37'),(3298,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 18:47:07','2026-02-16 18:47:07'),(3299,'AG',NULL,NULL,NULL,1,'2026-02-16 18:47:07','2026-02-16 18:47:07'),(3300,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 18:47:07','2026-02-16 18:47:07'),(3301,'BAYER',NULL,NULL,NULL,1,'2026-02-16 18:47:07','2026-02-16 18:47:07'),(3302,'OTC',NULL,NULL,NULL,1,'2026-02-16 18:47:08','2026-02-16 18:47:08'),(3303,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 18:47:08','2026-02-16 18:47:08'),(3304,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:47:08','2026-02-16 18:47:08'),(3305,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 18:47:09','2026-02-16 18:47:09'),(3306,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 18:47:09','2026-02-16 18:47:09'),(3307,'ECAR',NULL,NULL,NULL,1,'2026-02-16 18:47:09','2026-02-16 18:47:09'),(3308,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 18:47:10','2026-02-16 18:47:10'),(3309,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 18:47:10','2026-02-16 18:47:10'),(3310,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:47:10','2026-02-16 18:47:10'),(3311,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 18:47:11','2026-02-16 18:47:11'),(3312,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 18:47:11','2026-02-16 18:47:11'),(3313,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 18:47:11','2026-02-16 18:47:11'),(3314,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 18:47:12','2026-02-16 18:47:12'),(3315,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 18:47:12','2026-02-16 18:47:12'),(3316,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 18:47:12','2026-02-16 18:47:12'),(3317,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 18:47:12','2026-02-16 18:47:12'),(3318,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 18:47:13','2026-02-16 18:47:13'),(3319,'OSA',NULL,NULL,NULL,1,'2026-02-16 18:47:13','2026-02-16 18:47:13'),(3320,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 18:47:13','2026-02-16 18:47:13'),(3321,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 18:47:14','2026-02-16 18:47:14'),(3322,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 18:48:57','2026-02-16 18:48:57'),(3323,'AG',NULL,NULL,NULL,1,'2026-02-16 18:48:58','2026-02-16 18:48:58'),(3324,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 18:48:58','2026-02-16 18:48:58'),(3325,'BAYER',NULL,NULL,NULL,1,'2026-02-16 18:48:58','2026-02-16 18:48:58'),(3326,'OTC',NULL,NULL,NULL,1,'2026-02-16 18:48:58','2026-02-16 18:48:58'),(3327,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 18:48:59','2026-02-16 18:48:59'),(3328,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:48:59','2026-02-16 18:48:59'),(3329,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 18:48:59','2026-02-16 18:48:59'),(3330,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 18:49:00','2026-02-16 18:49:00'),(3331,'ECAR',NULL,NULL,NULL,1,'2026-02-16 18:49:00','2026-02-16 18:49:00'),(3332,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 18:49:00','2026-02-16 18:49:00'),(3333,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 18:49:00','2026-02-16 18:49:00'),(3334,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:49:01','2026-02-16 18:49:01'),(3335,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 18:49:01','2026-02-16 18:49:01'),(3336,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 18:49:01','2026-02-16 18:49:01'),(3337,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 18:49:02','2026-02-16 18:49:02'),(3338,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 18:49:02','2026-02-16 18:49:02'),(3339,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 18:49:02','2026-02-16 18:49:02'),(3340,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 18:49:02','2026-02-16 18:49:02'),(3341,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 18:49:03','2026-02-16 18:49:03'),(3342,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 18:49:03','2026-02-16 18:49:03'),(3343,'OSA',NULL,NULL,NULL,1,'2026-02-16 18:49:03','2026-02-16 18:49:03'),(3344,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 18:49:04','2026-02-16 18:49:04'),(3345,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 18:49:04','2026-02-16 18:49:04'),(3346,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 18:53:17','2026-02-16 18:53:17'),(3347,'AG',NULL,NULL,NULL,1,'2026-02-16 18:53:17','2026-02-16 18:53:17'),(3348,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 18:53:18','2026-02-16 18:53:18'),(3349,'BAYER',NULL,NULL,NULL,1,'2026-02-16 18:53:18','2026-02-16 18:53:18'),(3350,'OTC',NULL,NULL,NULL,1,'2026-02-16 18:53:18','2026-02-16 18:53:18'),(3351,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 18:53:18','2026-02-16 18:53:18'),(3352,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:53:19','2026-02-16 18:53:19'),(3353,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 18:53:19','2026-02-16 18:53:19'),(3354,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 18:53:19','2026-02-16 18:53:19'),(3355,'ECAR',NULL,NULL,NULL,1,'2026-02-16 18:53:20','2026-02-16 18:53:20'),(3356,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 18:53:20','2026-02-16 18:53:20'),(3357,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 18:53:20','2026-02-16 18:53:20'),(3358,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 18:53:21','2026-02-16 18:53:21'),(3359,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 18:53:21','2026-02-16 18:53:21'),(3360,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 18:53:21','2026-02-16 18:53:21'),(3361,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 18:53:21','2026-02-16 18:53:21'),(3362,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 18:53:22','2026-02-16 18:53:22'),(3363,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 18:53:22','2026-02-16 18:53:22'),(3364,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 18:53:22','2026-02-16 18:53:22'),(3365,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 18:53:23','2026-02-16 18:53:23'),(3366,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 18:53:23','2026-02-16 18:53:23'),(3367,'OSA',NULL,NULL,NULL,1,'2026-02-16 18:53:23','2026-02-16 18:53:23'),(3368,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 18:53:24','2026-02-16 18:53:24'),(3369,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 18:53:24','2026-02-16 18:53:24'),(3370,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 19:40:52','2026-02-16 19:40:52'),(3371,'AG',NULL,NULL,NULL,1,'2026-02-16 19:40:53','2026-02-16 19:40:53'),(3372,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 19:40:53','2026-02-16 19:40:53'),(3373,'BAYER',NULL,NULL,NULL,1,'2026-02-16 19:40:53','2026-02-16 19:40:53'),(3374,'OTC',NULL,NULL,NULL,1,'2026-02-16 19:40:54','2026-02-16 19:40:54'),(3375,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 19:40:54','2026-02-16 19:40:54'),(3376,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 19:40:54','2026-02-16 19:40:54'),(3377,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 19:40:55','2026-02-16 19:40:55'),(3378,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 19:40:55','2026-02-16 19:40:55'),(3379,'ECAR',NULL,NULL,NULL,1,'2026-02-16 19:40:55','2026-02-16 19:40:55'),(3380,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 19:40:56','2026-02-16 19:40:56'),(3381,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 19:40:56','2026-02-16 19:40:56'),(3382,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 19:40:56','2026-02-16 19:40:56'),(3383,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 19:40:57','2026-02-16 19:40:57'),(3384,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 19:40:57','2026-02-16 19:40:57'),(3385,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 19:40:57','2026-02-16 19:40:57'),(3386,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 19:40:57','2026-02-16 19:40:57'),(3387,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 19:40:58','2026-02-16 19:40:58'),(3388,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 19:40:58','2026-02-16 19:40:58'),(3389,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 19:40:58','2026-02-16 19:40:58'),(3390,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 19:40:59','2026-02-16 19:40:59'),(3391,'OSA',NULL,NULL,NULL,1,'2026-02-16 19:40:59','2026-02-16 19:40:59'),(3392,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 19:40:59','2026-02-16 19:40:59'),(3393,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 19:41:00','2026-02-16 19:41:00'),(3394,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 19:41:20','2026-02-16 19:41:20'),(3395,'AG',NULL,NULL,NULL,1,'2026-02-16 19:41:20','2026-02-16 19:41:20'),(3396,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 19:41:20','2026-02-16 19:41:20'),(3397,'BAYER',NULL,NULL,NULL,1,'2026-02-16 19:41:21','2026-02-16 19:41:21'),(3398,'OTC',NULL,NULL,NULL,1,'2026-02-16 19:41:21','2026-02-16 19:41:21'),(3399,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 19:41:21','2026-02-16 19:41:21'),(3400,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 19:41:22','2026-02-16 19:41:22'),(3401,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 19:41:22','2026-02-16 19:41:22'),(3402,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 19:41:22','2026-02-16 19:41:22'),(3403,'ECAR',NULL,NULL,NULL,1,'2026-02-16 19:41:23','2026-02-16 19:41:23'),(3404,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 19:41:23','2026-02-16 19:41:23'),(3405,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 19:41:23','2026-02-16 19:41:23'),(3406,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 19:41:24','2026-02-16 19:41:24'),(3407,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 19:41:24','2026-02-16 19:41:24'),(3408,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 19:41:24','2026-02-16 19:41:24'),(3409,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 19:41:24','2026-02-16 19:41:24'),(3410,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 19:41:25','2026-02-16 19:41:25'),(3411,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 19:41:25','2026-02-16 19:41:25'),(3412,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 19:41:25','2026-02-16 19:41:25'),(3413,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 19:41:26','2026-02-16 19:41:26'),(3414,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 19:41:26','2026-02-16 19:41:26'),(3415,'OSA',NULL,NULL,NULL,1,'2026-02-16 19:41:26','2026-02-16 19:41:26'),(3416,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 19:41:27','2026-02-16 19:41:27'),(3417,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 19:41:27','2026-02-16 19:41:27'),(3418,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 20:19:41','2026-02-16 20:19:41'),(3419,'AG',NULL,NULL,NULL,1,'2026-02-16 20:19:42','2026-02-16 20:19:42'),(3420,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 20:19:42','2026-02-16 20:19:42'),(3421,'BAYER',NULL,NULL,NULL,1,'2026-02-16 20:19:42','2026-02-16 20:19:42'),(3422,'OTC',NULL,NULL,NULL,1,'2026-02-16 20:19:42','2026-02-16 20:19:42'),(3423,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 20:19:43','2026-02-16 20:19:43'),(3424,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 20:19:43','2026-02-16 20:19:43'),(3425,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 20:19:43','2026-02-16 20:19:43'),(3426,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 20:19:44','2026-02-16 20:19:44'),(3427,'ECAR',NULL,NULL,NULL,1,'2026-02-16 20:19:44','2026-02-16 20:19:44'),(3428,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 20:19:44','2026-02-16 20:19:44'),(3429,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 20:19:45','2026-02-16 20:19:45'),(3430,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 20:19:45','2026-02-16 20:19:45'),(3431,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 20:19:45','2026-02-16 20:19:45'),(3432,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 20:19:46','2026-02-16 20:19:46'),(3433,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 20:19:46','2026-02-16 20:19:46'),(3434,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 20:19:46','2026-02-16 20:19:46'),(3435,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 20:19:47','2026-02-16 20:19:47'),(3436,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 20:19:47','2026-02-16 20:19:47'),(3437,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 20:19:47','2026-02-16 20:19:47'),(3438,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 20:19:48','2026-02-16 20:19:48'),(3439,'OSA',NULL,NULL,NULL,1,'2026-02-16 20:19:48','2026-02-16 20:19:48'),(3440,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 20:19:48','2026-02-16 20:19:48'),(3441,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 20:19:49','2026-02-16 20:19:49'),(3442,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 20:35:49','2026-02-16 20:35:49'),(3443,'AG',NULL,NULL,NULL,1,'2026-02-16 20:35:49','2026-02-16 20:35:49'),(3444,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 20:35:49','2026-02-16 20:35:49'),(3445,'BAYER',NULL,NULL,NULL,1,'2026-02-16 20:35:50','2026-02-16 20:35:50'),(3446,'OTC',NULL,NULL,NULL,1,'2026-02-16 20:35:50','2026-02-16 20:35:50'),(3447,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 20:35:50','2026-02-16 20:35:50'),(3448,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 20:35:51','2026-02-16 20:35:51'),(3449,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 20:35:51','2026-02-16 20:35:51'),(3450,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 20:35:51','2026-02-16 20:35:51'),(3451,'ECAR',NULL,NULL,NULL,1,'2026-02-16 20:35:52','2026-02-16 20:35:52'),(3452,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 20:35:52','2026-02-16 20:35:52'),(3453,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 20:35:52','2026-02-16 20:35:52'),(3454,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 20:35:53','2026-02-16 20:35:53'),(3455,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 20:35:53','2026-02-16 20:35:53'),(3456,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 20:35:53','2026-02-16 20:35:53'),(3457,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 20:35:53','2026-02-16 20:35:53'),(3458,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 20:35:54','2026-02-16 20:35:54'),(3459,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 20:35:54','2026-02-16 20:35:54'),(3460,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 20:35:54','2026-02-16 20:35:54'),(3461,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 20:35:55','2026-02-16 20:35:55'),(3462,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 20:35:55','2026-02-16 20:35:55'),(3463,'OSA',NULL,NULL,NULL,1,'2026-02-16 20:35:55','2026-02-16 20:35:55'),(3464,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 20:35:56','2026-02-16 20:35:56'),(3465,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 20:35:56','2026-02-16 20:35:56'),(3466,'GENERICO',NULL,NULL,NULL,1,'2026-02-16 21:10:36','2026-02-16 21:10:36'),(3467,'AG',NULL,NULL,NULL,1,'2026-02-16 21:10:37','2026-02-16 21:10:37'),(3468,'GENFAR',NULL,NULL,NULL,1,'2026-02-16 21:10:37','2026-02-16 21:10:37'),(3469,'BAYER',NULL,NULL,NULL,1,'2026-02-16 21:10:37','2026-02-16 21:10:37'),(3470,'OTC',NULL,NULL,NULL,1,'2026-02-16 21:10:38','2026-02-16 21:10:38'),(3471,'OPHARM',NULL,NULL,NULL,1,'2026-02-16 21:10:38','2026-02-16 21:10:38'),(3472,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-16 21:10:38','2026-02-16 21:10:38'),(3473,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-16 21:10:39','2026-02-16 21:10:39'),(3474,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-16 21:10:39','2026-02-16 21:10:39'),(3475,'ECAR',NULL,NULL,NULL,1,'2026-02-16 21:10:39','2026-02-16 21:10:39'),(3476,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-16 21:10:39','2026-02-16 21:10:39'),(3477,'LAPROFF',NULL,NULL,NULL,1,'2026-02-16 21:10:40','2026-02-16 21:10:40'),(3478,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-16 21:10:40','2026-02-16 21:10:40'),(3479,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-16 21:10:40','2026-02-16 21:10:40'),(3480,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-16 21:10:41','2026-02-16 21:10:41'),(3481,'HARTUNG',NULL,NULL,NULL,1,'2026-02-16 21:10:41','2026-02-16 21:10:41'),(3482,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-16 21:10:41','2026-02-16 21:10:41'),(3483,'AFR SAS',NULL,NULL,NULL,1,'2026-02-16 21:10:42','2026-02-16 21:10:42'),(3484,'ABBOTT',NULL,NULL,NULL,1,'2026-02-16 21:10:42','2026-02-16 21:10:42'),(3485,'ALIKIN',NULL,NULL,NULL,1,'2026-02-16 21:10:42','2026-02-16 21:10:42'),(3486,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-16 21:10:43','2026-02-16 21:10:43'),(3487,'OSA',NULL,NULL,NULL,1,'2026-02-16 21:10:43','2026-02-16 21:10:43'),(3488,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-16 21:10:43','2026-02-16 21:10:43'),(3489,'NOVAMED',NULL,NULL,NULL,1,'2026-02-16 21:10:44','2026-02-16 21:10:44'),(3490,'GENERICO',NULL,NULL,NULL,1,'2026-02-17 01:33:30','2026-02-17 01:33:30'),(3491,'AG',NULL,NULL,NULL,1,'2026-02-17 01:33:30','2026-02-17 01:33:30'),(3492,'GENFAR',NULL,NULL,NULL,1,'2026-02-17 01:33:31','2026-02-17 01:33:31'),(3493,'BAYER',NULL,NULL,NULL,1,'2026-02-17 01:33:31','2026-02-17 01:33:31'),(3494,'OTC',NULL,NULL,NULL,1,'2026-02-17 01:33:31','2026-02-17 01:33:31'),(3495,'OPHARM',NULL,NULL,NULL,1,'2026-02-17 01:33:32','2026-02-17 01:33:32'),(3496,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-17 01:33:33','2026-02-17 01:33:33'),(3497,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-17 01:33:33','2026-02-17 01:33:33'),(3498,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-17 01:33:33','2026-02-17 01:33:33'),(3499,'ECAR',NULL,NULL,NULL,1,'2026-02-17 01:33:34','2026-02-17 01:33:34'),(3500,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-17 01:33:34','2026-02-17 01:33:34'),(3501,'LAPROFF',NULL,NULL,NULL,1,'2026-02-17 01:33:35','2026-02-17 01:33:35'),(3502,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-17 01:33:35','2026-02-17 01:33:35'),(3503,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-17 01:33:35','2026-02-17 01:33:35'),(3504,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-17 01:33:36','2026-02-17 01:33:36'),(3505,'HARTUNG',NULL,NULL,NULL,1,'2026-02-17 01:33:37','2026-02-17 01:33:37'),(3506,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-17 01:33:37','2026-02-17 01:33:37'),(3507,'AFR SAS',NULL,NULL,NULL,1,'2026-02-17 01:33:37','2026-02-17 01:33:37'),(3508,'ABBOTT',NULL,NULL,NULL,1,'2026-02-17 01:33:38','2026-02-17 01:33:38'),(3509,'ALIKIN',NULL,NULL,NULL,1,'2026-02-17 01:33:38','2026-02-17 01:33:38'),(3510,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-17 01:33:39','2026-02-17 01:33:39'),(3511,'OSA',NULL,NULL,NULL,1,'2026-02-17 01:33:39','2026-02-17 01:33:39'),(3512,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-17 01:33:40','2026-02-17 01:33:40'),(3513,'NOVAMED',NULL,NULL,NULL,1,'2026-02-17 01:33:41','2026-02-17 01:33:41'),(3514,'GENERICO',NULL,NULL,NULL,1,'2026-02-17 01:54:43','2026-02-17 01:54:43'),(3515,'AG',NULL,NULL,NULL,1,'2026-02-17 01:54:44','2026-02-17 01:54:44'),(3516,'GENFAR',NULL,NULL,NULL,1,'2026-02-17 01:54:45','2026-02-17 01:54:45'),(3517,'BAYER',NULL,NULL,NULL,1,'2026-02-17 01:54:45','2026-02-17 01:54:45'),(3518,'OTC',NULL,NULL,NULL,1,'2026-02-17 01:54:45','2026-02-17 01:54:45'),(3519,'OPHARM',NULL,NULL,NULL,1,'2026-02-17 01:54:46','2026-02-17 01:54:46'),(3520,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-17 01:54:47','2026-02-17 01:54:47'),(3521,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-17 01:54:47','2026-02-17 01:54:47'),(3522,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-17 01:54:48','2026-02-17 01:54:48'),(3523,'ECAR',NULL,NULL,NULL,1,'2026-02-17 01:54:48','2026-02-17 01:54:48'),(3524,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-17 01:54:49','2026-02-17 01:54:49'),(3525,'LAPROFF',NULL,NULL,NULL,1,'2026-02-17 01:54:49','2026-02-17 01:54:49'),(3526,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-17 01:54:50','2026-02-17 01:54:50'),(3527,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-17 01:54:50','2026-02-17 01:54:50'),(3528,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-17 01:54:51','2026-02-17 01:54:51'),(3529,'HARTUNG',NULL,NULL,NULL,1,'2026-02-17 01:54:51','2026-02-17 01:54:51'),(3530,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-17 01:54:52','2026-02-17 01:54:52'),(3531,'AFR SAS',NULL,NULL,NULL,1,'2026-02-17 01:54:52','2026-02-17 01:54:52'),(3532,'ABBOTT',NULL,NULL,NULL,1,'2026-02-17 01:54:53','2026-02-17 01:54:53'),(3533,'ALIKIN',NULL,NULL,NULL,1,'2026-02-17 01:54:53','2026-02-17 01:54:53'),(3534,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-17 01:54:54','2026-02-17 01:54:54'),(3535,'OSA',NULL,NULL,NULL,1,'2026-02-17 01:54:54','2026-02-17 01:54:54'),(3536,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-17 01:54:55','2026-02-17 01:54:55'),(3537,'NOVAMED',NULL,NULL,NULL,1,'2026-02-17 01:54:55','2026-02-17 01:54:55'),(3538,'GENERICO',NULL,NULL,NULL,1,'2026-02-17 02:59:43','2026-02-17 02:59:43'),(3539,'AG',NULL,NULL,NULL,1,'2026-02-17 02:59:43','2026-02-17 02:59:43'),(3540,'GENFAR',NULL,NULL,NULL,1,'2026-02-17 02:59:44','2026-02-17 02:59:44'),(3541,'BAYER',NULL,NULL,NULL,1,'2026-02-17 02:59:44','2026-02-17 02:59:44'),(3542,'OTC',NULL,NULL,NULL,1,'2026-02-17 02:59:44','2026-02-17 02:59:44'),(3543,'OPHARM',NULL,NULL,NULL,1,'2026-02-17 02:59:44','2026-02-17 02:59:44'),(3544,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-17 02:59:45','2026-02-17 02:59:45'),(3545,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-17 02:59:45','2026-02-17 02:59:45'),(3546,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-17 02:59:45','2026-02-17 02:59:45'),(3547,'ECAR',NULL,NULL,NULL,1,'2026-02-17 02:59:46','2026-02-17 02:59:46'),(3548,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-17 02:59:46','2026-02-17 02:59:46'),(3549,'LAPROFF',NULL,NULL,NULL,1,'2026-02-17 02:59:46','2026-02-17 02:59:46'),(3550,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-17 02:59:47','2026-02-17 02:59:47'),(3551,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-17 02:59:47','2026-02-17 02:59:47'),(3552,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-17 02:59:47','2026-02-17 02:59:47'),(3553,'HARTUNG',NULL,NULL,NULL,1,'2026-02-17 02:59:48','2026-02-17 02:59:48'),(3554,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-17 02:59:48','2026-02-17 02:59:48'),(3555,'AFR SAS',NULL,NULL,NULL,1,'2026-02-17 02:59:48','2026-02-17 02:59:48'),(3556,'ABBOTT',NULL,NULL,NULL,1,'2026-02-17 02:59:48','2026-02-17 02:59:48'),(3557,'ALIKIN',NULL,NULL,NULL,1,'2026-02-17 02:59:49','2026-02-17 02:59:49'),(3558,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-17 02:59:49','2026-02-17 02:59:49'),(3559,'OSA',NULL,NULL,NULL,1,'2026-02-17 02:59:49','2026-02-17 02:59:49'),(3560,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-17 02:59:50','2026-02-17 02:59:50'),(3561,'NOVAMED',NULL,NULL,NULL,1,'2026-02-17 02:59:50','2026-02-17 02:59:50'),(3562,'GENERICO',NULL,NULL,NULL,1,'2026-02-17 16:57:12','2026-02-17 16:57:12'),(3563,'AG',NULL,NULL,NULL,1,'2026-02-17 16:57:12','2026-02-17 16:57:12'),(3564,'GENFAR',NULL,NULL,NULL,1,'2026-02-17 16:57:13','2026-02-17 16:57:13'),(3565,'BAYER',NULL,NULL,NULL,1,'2026-02-17 16:57:13','2026-02-17 16:57:13'),(3566,'OTC',NULL,NULL,NULL,1,'2026-02-17 16:57:13','2026-02-17 16:57:13'),(3567,'OPHARM',NULL,NULL,NULL,1,'2026-02-17 16:57:14','2026-02-17 16:57:14'),(3568,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-17 16:57:14','2026-02-17 16:57:14'),(3569,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-17 16:57:14','2026-02-17 16:57:14'),(3570,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-17 16:57:15','2026-02-17 16:57:15'),(3571,'ECAR',NULL,NULL,NULL,1,'2026-02-17 16:57:15','2026-02-17 16:57:15'),(3572,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-17 16:57:15','2026-02-17 16:57:15'),(3573,'LAPROFF',NULL,NULL,NULL,1,'2026-02-17 16:57:16','2026-02-17 16:57:16'),(3574,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-17 16:57:16','2026-02-17 16:57:16'),(3575,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-17 16:57:16','2026-02-17 16:57:16'),(3576,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-17 16:57:17','2026-02-17 16:57:17'),(3577,'HARTUNG',NULL,NULL,NULL,1,'2026-02-17 16:57:17','2026-02-17 16:57:17'),(3578,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-17 16:57:17','2026-02-17 16:57:17'),(3579,'AFR SAS',NULL,NULL,NULL,1,'2026-02-17 16:57:18','2026-02-17 16:57:18'),(3580,'ABBOTT',NULL,NULL,NULL,1,'2026-02-17 16:57:18','2026-02-17 16:57:18'),(3581,'ALIKIN',NULL,NULL,NULL,1,'2026-02-17 16:57:18','2026-02-17 16:57:18'),(3582,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-17 16:57:18','2026-02-17 16:57:18'),(3583,'OSA',NULL,NULL,NULL,1,'2026-02-17 16:57:19','2026-02-17 16:57:19'),(3584,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-17 16:57:19','2026-02-17 16:57:19'),(3585,'NOVAMED',NULL,NULL,NULL,1,'2026-02-17 16:57:19','2026-02-17 16:57:19'),(3586,'GENERICO',NULL,NULL,NULL,1,'2026-02-17 17:30:52','2026-02-17 17:30:52'),(3587,'AG',NULL,NULL,NULL,1,'2026-02-17 17:30:52','2026-02-17 17:30:52'),(3588,'GENFAR',NULL,NULL,NULL,1,'2026-02-17 17:30:52','2026-02-17 17:30:52'),(3589,'BAYER',NULL,NULL,NULL,1,'2026-02-17 17:30:52','2026-02-17 17:30:52'),(3590,'OTC',NULL,NULL,NULL,1,'2026-02-17 17:30:53','2026-02-17 17:30:53'),(3591,'OPHARM',NULL,NULL,NULL,1,'2026-02-17 17:30:53','2026-02-17 17:30:53'),(3592,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-17 17:30:53','2026-02-17 17:30:53'),(3593,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-17 17:30:54','2026-02-17 17:30:54'),(3594,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-17 17:30:54','2026-02-17 17:30:54'),(3595,'ECAR',NULL,NULL,NULL,1,'2026-02-17 17:30:54','2026-02-17 17:30:54'),(3596,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-17 17:30:55','2026-02-17 17:30:55'),(3597,'LAPROFF',NULL,NULL,NULL,1,'2026-02-17 17:30:55','2026-02-17 17:30:55'),(3598,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-17 17:30:55','2026-02-17 17:30:55'),(3599,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-17 17:30:56','2026-02-17 17:30:56'),(3600,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-17 17:30:56','2026-02-17 17:30:56'),(3601,'HARTUNG',NULL,NULL,NULL,1,'2026-02-17 17:30:56','2026-02-17 17:30:56'),(3602,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-17 17:30:57','2026-02-17 17:30:57'),(3603,'AFR SAS',NULL,NULL,NULL,1,'2026-02-17 17:30:57','2026-02-17 17:30:57'),(3604,'ABBOTT',NULL,NULL,NULL,1,'2026-02-17 17:30:57','2026-02-17 17:30:57'),(3605,'ALIKIN',NULL,NULL,NULL,1,'2026-02-17 17:30:58','2026-02-17 17:30:58'),(3606,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-17 17:30:58','2026-02-17 17:30:58'),(3607,'OSA',NULL,NULL,NULL,1,'2026-02-17 17:30:58','2026-02-17 17:30:58'),(3608,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-17 17:30:58','2026-02-17 17:30:58'),(3609,'NOVAMED',NULL,NULL,NULL,1,'2026-02-17 17:30:59','2026-02-17 17:30:59'),(3610,'GENERICO',NULL,NULL,NULL,1,'2026-02-17 18:53:48','2026-02-17 18:53:48'),(3611,'AG',NULL,NULL,NULL,1,'2026-02-17 18:53:48','2026-02-17 18:53:48'),(3612,'GENFAR',NULL,NULL,NULL,1,'2026-02-17 18:53:48','2026-02-17 18:53:48'),(3613,'BAYER',NULL,NULL,NULL,1,'2026-02-17 18:53:49','2026-02-17 18:53:49'),(3614,'OTC',NULL,NULL,NULL,1,'2026-02-17 18:53:49','2026-02-17 18:53:49'),(3615,'OPHARM',NULL,NULL,NULL,1,'2026-02-17 18:53:49','2026-02-17 18:53:49'),(3616,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-17 18:53:50','2026-02-17 18:53:50'),(3617,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-17 18:53:50','2026-02-17 18:53:50'),(3618,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-17 18:53:50','2026-02-17 18:53:50'),(3619,'ECAR',NULL,NULL,NULL,1,'2026-02-17 18:53:51','2026-02-17 18:53:51'),(3620,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-17 18:53:51','2026-02-17 18:53:51'),(3621,'LAPROFF',NULL,NULL,NULL,1,'2026-02-17 18:53:51','2026-02-17 18:53:51'),(3622,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-17 18:53:52','2026-02-17 18:53:52'),(3623,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-17 18:53:52','2026-02-17 18:53:52'),(3624,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-17 18:53:52','2026-02-17 18:53:52'),(3625,'HARTUNG',NULL,NULL,NULL,1,'2026-02-17 18:53:52','2026-02-17 18:53:52'),(3626,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-17 18:53:53','2026-02-17 18:53:53'),(3627,'AFR SAS',NULL,NULL,NULL,1,'2026-02-17 18:53:53','2026-02-17 18:53:53'),(3628,'ABBOTT',NULL,NULL,NULL,1,'2026-02-17 18:53:53','2026-02-17 18:53:53'),(3629,'ALIKIN',NULL,NULL,NULL,1,'2026-02-17 18:53:54','2026-02-17 18:53:54'),(3630,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-17 18:53:54','2026-02-17 18:53:54'),(3631,'OSA',NULL,NULL,NULL,1,'2026-02-17 18:53:54','2026-02-17 18:53:54'),(3632,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-17 18:53:55','2026-02-17 18:53:55'),(3633,'NOVAMED',NULL,NULL,NULL,1,'2026-02-17 18:53:55','2026-02-17 18:53:55'),(3634,'GENERICO',NULL,NULL,NULL,1,'2026-02-17 22:52:03','2026-02-17 22:52:03'),(3635,'AG',NULL,NULL,NULL,1,'2026-02-17 22:52:03','2026-02-17 22:52:03'),(3636,'GENFAR',NULL,NULL,NULL,1,'2026-02-17 22:52:04','2026-02-17 22:52:04'),(3637,'BAYER',NULL,NULL,NULL,1,'2026-02-17 22:52:04','2026-02-17 22:52:04'),(3638,'OTC',NULL,NULL,NULL,1,'2026-02-17 22:52:04','2026-02-17 22:52:04'),(3639,'OPHARM',NULL,NULL,NULL,1,'2026-02-17 22:52:05','2026-02-17 22:52:05'),(3640,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-17 22:52:05','2026-02-17 22:52:05'),(3641,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-17 22:52:05','2026-02-17 22:52:05'),(3642,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-17 22:52:05','2026-02-17 22:52:05'),(3643,'ECAR',NULL,NULL,NULL,1,'2026-02-17 22:52:06','2026-02-17 22:52:06'),(3644,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-17 22:52:06','2026-02-17 22:52:06'),(3645,'LAPROFF',NULL,NULL,NULL,1,'2026-02-17 22:52:06','2026-02-17 22:52:06'),(3646,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-17 22:52:07','2026-02-17 22:52:07'),(3647,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-17 22:52:07','2026-02-17 22:52:07'),(3648,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-17 22:52:07','2026-02-17 22:52:07'),(3649,'HARTUNG',NULL,NULL,NULL,1,'2026-02-17 22:52:07','2026-02-17 22:52:07'),(3650,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-17 22:52:08','2026-02-17 22:52:08'),(3651,'AFR SAS',NULL,NULL,NULL,1,'2026-02-17 22:52:08','2026-02-17 22:52:08'),(3652,'ABBOTT',NULL,NULL,NULL,1,'2026-02-17 22:52:08','2026-02-17 22:52:08'),(3653,'ALIKIN',NULL,NULL,NULL,1,'2026-02-17 22:52:08','2026-02-17 22:52:08'),(3654,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-17 22:52:09','2026-02-17 22:52:09'),(3655,'OSA',NULL,NULL,NULL,1,'2026-02-17 22:52:09','2026-02-17 22:52:09'),(3656,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-17 22:52:09','2026-02-17 22:52:09'),(3657,'NOVAMED',NULL,NULL,NULL,1,'2026-02-17 22:52:10','2026-02-17 22:52:10'),(3658,'GENERICO',NULL,NULL,NULL,1,'2026-02-19 23:21:34','2026-02-19 23:21:34'),(3659,'AG',NULL,NULL,NULL,1,'2026-02-19 23:21:34','2026-02-19 23:21:34'),(3660,'GENFAR',NULL,NULL,NULL,1,'2026-02-19 23:21:34','2026-02-19 23:21:34'),(3661,'BAYER',NULL,NULL,NULL,1,'2026-02-19 23:21:35','2026-02-19 23:21:35'),(3662,'OTC',NULL,NULL,NULL,1,'2026-02-19 23:21:35','2026-02-19 23:21:35'),(3663,'OPHARM',NULL,NULL,NULL,1,'2026-02-19 23:21:36','2026-02-19 23:21:36'),(3664,'ANGLOPHARMA',NULL,NULL,NULL,1,'2026-02-19 23:21:36','2026-02-19 23:21:36'),(3665,'NACIONAL DE QUIMICOS',NULL,NULL,NULL,1,'2026-02-19 23:21:36','2026-02-19 23:21:36'),(3666,'DROGA BLANCA',NULL,NULL,NULL,1,'2026-02-19 23:21:37','2026-02-19 23:21:37'),(3667,'ECAR',NULL,NULL,NULL,1,'2026-02-19 23:21:37','2026-02-19 23:21:37'),(3668,'LABQUIFAR',NULL,NULL,NULL,1,'2026-02-19 23:21:37','2026-02-19 23:21:37'),(3669,'LAPROFF',NULL,NULL,NULL,1,'2026-02-19 23:21:38','2026-02-19 23:21:38'),(3670,'COASPHARMA',NULL,NULL,NULL,1,'2026-02-19 23:21:38','2026-02-19 23:21:38'),(3671,'MEMPHIS',NULL,NULL,NULL,1,'2026-02-19 23:21:38','2026-02-19 23:21:38'),(3672,'CUIDADO DEL BEBE',NULL,NULL,NULL,1,'2026-02-19 23:21:39','2026-02-19 23:21:39'),(3673,'HARTUNG',NULL,NULL,NULL,1,'2026-02-19 23:21:39','2026-02-19 23:21:39'),(3674,'JUHNIOS',NULL,NULL,NULL,1,'2026-02-19 23:21:40','2026-02-19 23:21:40'),(3675,'AFR SAS',NULL,NULL,NULL,1,'2026-02-19 23:21:40','2026-02-19 23:21:40'),(3676,'ABBOTT',NULL,NULL,NULL,1,'2026-02-19 23:21:40','2026-02-19 23:21:40'),(3677,'ALIKIN',NULL,NULL,NULL,1,'2026-02-19 23:21:41','2026-02-19 23:21:41'),(3678,'TOP GLOVE',NULL,NULL,NULL,1,'2026-02-19 23:21:41','2026-02-19 23:21:41'),(3679,'OSA',NULL,NULL,NULL,1,'2026-02-19 23:21:41','2026-02-19 23:21:41'),(3680,'SIEGFRIED',NULL,NULL,NULL,1,'2026-02-19 23:21:42','2026-02-19 23:21:42'),(3681,'NOVAMED',NULL,NULL,NULL,1,'2026-02-19 23:21:42','2026-02-19 23:21:42');
/*!40000 ALTER TABLE `laboratorios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lotes`
--

DROP TABLE IF EXISTS `lotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lotes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `numero_lote` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `cantidad_actual` int NOT NULL DEFAULT '0',
  `costo_compra` decimal(38,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sucursal_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_producto_lote` (`producto_id`,`numero_lote`),
  KEY `idx_lotes_producto` (`producto_id`),
  KEY `idx_lotes_vencimiento` (`fecha_vencimiento`),
  KEY `idx_lotes_cantidad` (`cantidad_actual`),
  CONSTRAINT `fk_lote_producto` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_lote_cantidad_positiva` CHECK ((`cantidad_actual` >= 0)),
  CONSTRAINT `chk_lote_costo_positivo` CHECK ((`costo_compra` > 0)),
  CONSTRAINT `chk_lote_fecha_vencimiento` CHECK ((`fecha_vencimiento` > _utf8mb4'2020-01-01'))
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lotes`
--

LOCK TABLES `lotes` WRITE;
/*!40000 ALTER TABLE `lotes` DISABLE KEYS */;
INSERT INTO `lotes` VALUES (1,1,'L-GEN-001','2027-06-30',37,7500.00,'2026-02-09 20:22:16','2026-02-17 20:45:08',1),(2,2,'L-DOL-001','2026-12-15',30,19200.00,'2026-02-09 20:22:16','2026-02-16 19:10:47',1),(3,3,'L-ADV-001','2028-01-20',25,12500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(4,4,'L-GEN-003','2026-11-30',40,8000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(5,5,'L-ASP-001','2027-02-28',20,11000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(6,6,'L-GEN-002','2026-11-30',20,24000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(7,7,'L-AZI-001','2027-03-15',15,9500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(8,8,'L-CIP-001','2027-08-10',30,15000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(9,9,'L-CEF-001','2026-09-20',25,18000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(10,10,'L-GAS-001','2026-10-05',100,3500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(11,11,'L-PED-001','2026-09-01',48,7200.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(12,12,'L-SAL-001','2027-12-31',50,1800.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(13,13,'L-BUS-001','2027-05-15',30,16500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(14,14,'L-NOX-001','2028-01-01',20,11000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(15,15,'L-DES-001','2027-06-15',100,1500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(16,16,'L-LOR-001','2027-04-20',50,3000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(17,17,'L-VICK-01','2029-01-01',15,12000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(18,18,'L-DER-001','2028-05-10',10,18500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(19,19,'L-CLO-001','2027-11-11',15,4500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(20,20,'L-VOL-001','2028-02-28',12,22000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(21,21,'L-REX-001','2027-08-22',12,14000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(22,22,'L-PRE-001','2029-01-01',33,8500.00,'2026-02-09 20:22:16','2026-02-20 05:33:08',1),(23,23,'L-ALC-001','2030-01-01',24,4500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(24,24,'L-VIT-001','2027-07-30',20,9000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(25,25,'L-ENS-001','2026-12-01',6,48000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(26,26,'L-LOS-001','2027-03-30',40,4000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(27,27,'L-ENL-001','2027-03-30',40,3500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(28,28,'L-MET-001','2027-04-15',40,5000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(29,29,'L-DOL-002','2026-11-20',15,14000.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(30,30,'L-NOV-001','2026-10-10',15,13500.00,'2026-02-09 20:22:16','2026-02-09 20:22:16',1),(37,127,'LOTE-TEST-001','2027-12-31',1000,120.00,'2026-02-09 20:53:17','2026-02-09 20:53:18',1),(77,55,'OEP-0912','2028-03-11',3,3000.00,'2026-02-11 18:26:59','2026-02-20 04:38:05',1),(300,1,'skfsdlkf','2027-02-19',50,200.00,'2026-02-20 00:43:38','2026-02-20 04:38:07',1);
/*!40000 ALTER TABLE `lotes` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'REAL_AS_FLOAT,PIPES_AS_CONCAT,ANSI_QUOTES,IGNORE_SPACE,ONLY_FULL_GROUP_BY,ANSI,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`avnadmin`@`%`*/ /*!50003 TRIGGER `trg_actualizar_precio_referencia` AFTER INSERT ON `lotes` FOR EACH ROW BEGIN
    UPDATE productos 
    SET precio_compra_referencia = NEW.costo_compra
    WHERE id = NEW.producto_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `movimientos`
--

DROP TABLE IF EXISTS `movimientos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lote_id` int NOT NULL,
  `tipo_movimiento` enum('ENTRADA','SALIDA','AJUSTE','TRASLADO','VENCIDO','DEVOLUCION') COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad` int NOT NULL,
  `fecha_movimiento` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `usuario_responsable` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `venta_id` int DEFAULT NULL,
  `sucursal_id` int DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `observacion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `saldo_historico` int DEFAULT NULL COMMENT 'Saldo resultante del lote justo despues de este movimiento',
  PRIMARY KEY (`id`),
  KEY `fk_movimiento_sucursal` (`sucursal_id`),
  KEY `idx_movimientos_lote` (`lote_id`),
  KEY `idx_movimientos_tipo` (`tipo_movimiento`),
  KEY `idx_movimientos_fecha` (`fecha_movimiento`),
  CONSTRAINT `fk_movimiento_lote` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_movimiento_sucursal` FOREIGN KEY (`sucursal_id`) REFERENCES `sucursales` (`id`) ON DELETE SET NULL,
  CONSTRAINT `chk_movimiento_cantidad` CHECK ((`cantidad` <> 0)),
  CONSTRAINT `chk_movimiento_usuario_no_vacio` CHECK ((trim(`usuario_responsable`) <> _utf8mb4''))
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimientos`
--

LOCK TABLES `movimientos` WRITE;
/*!40000 ALTER TABLE `movimientos` DISABLE KEYS */;
INSERT INTO `movimientos` VALUES (2,25,'ENTRADA',6,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(3,18,'ENTRADA',10,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(4,20,'ENTRADA',12,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(5,21,'ENTRADA',12,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(6,7,'ENTRADA',15,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(7,17,'ENTRADA',15,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(8,19,'ENTRADA',15,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(9,29,'ENTRADA',15,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(10,30,'ENTRADA',15,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(11,5,'ENTRADA',20,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(12,6,'ENTRADA',20,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(13,14,'ENTRADA',20,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(14,24,'ENTRADA',20,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(15,23,'ENTRADA',24,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(16,3,'ENTRADA',25,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(17,9,'ENTRADA',25,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(18,2,'ENTRADA',30,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(19,8,'ENTRADA',30,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(20,13,'ENTRADA',30,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(21,4,'ENTRADA',40,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(22,26,'ENTRADA',40,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(23,27,'ENTRADA',40,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(24,28,'ENTRADA',40,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(25,11,'ENTRADA',48,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(26,1,'ENTRADA',50,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(27,12,'ENTRADA',50,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(28,16,'ENTRADA',50,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(29,22,'ENTRADA',50,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(30,10,'ENTRADA',100,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(31,15,'ENTRADA',100,'2026-02-09 20:29:58','ADMIN',NULL,1,NULL,'Inventario Inicial - MegaCarga',NULL),(33,37,'ENTRADA',1000,'2026-02-09 20:53:18','admin',NULL,1,'Test de actualización de precios',NULL,NULL),(34,77,'ENTRADA',10,'2026-02-11 18:27:00','SISTEMA',NULL,1,'Entrada de Mercancía','Factura-12321433',NULL),(35,1,'SALIDA',1,'2026-02-16 00:50:34','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(36,1,'SALIDA',1,'2026-02-16 00:50:37','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(37,77,'SALIDA',1,'2026-02-16 00:55:16','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(38,77,'SALIDA',1,'2026-02-16 00:56:23','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(39,77,'SALIDA',1,'2026-02-16 01:25:28','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(40,77,'SALIDA',1,'2026-02-16 03:29:35','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(41,77,'SALIDA',1,'2026-02-16 03:32:15','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(42,77,'SALIDA',1,'2026-02-16 03:57:17','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(43,77,'SALIDA',1,'2026-02-16 04:14:18','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(44,77,'SALIDA',1,'2026-02-16 04:15:38','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(45,77,'SALIDA',1,'2026-02-16 04:18:13','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(46,77,'SALIDA',1,'2026-02-16 04:21:55','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(47,77,'SALIDA',1,'2026-02-16 04:23:09','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(48,2,'SALIDA',1,'2026-02-16 18:24:58','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(49,2,'SALIDA',1,'2026-02-16 18:30:53','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(50,2,'SALIDA',1,'2026-02-16 18:38:29','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(51,2,'SALIDA',1,'2026-02-16 18:49:43','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(52,2,'SALIDA',1,'2026-02-16 18:54:42','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(53,2,'SALIDA',1,'2026-02-16 19:00:11','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(54,2,'SALIDA',1,'2026-02-16 19:04:08','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(55,2,'SALIDA',1,'2026-02-16 19:05:06','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(56,2,'SALIDA',1,'2026-02-16 19:07:31','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(57,2,'SALIDA',1,'2026-02-16 19:10:47','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(58,77,'SALIDA',-1,'2026-02-17 03:34:52','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(59,77,'SALIDA',-1,'2026-02-17 04:26:48','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(60,77,'SALIDA',-1,'2026-02-17 04:46:14','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(61,1,'SALIDA',-10,'2026-02-17 04:46:17','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(62,1,'SALIDA',-1,'2026-02-17 04:48:25','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,NULL),(63,1,'SALIDA',-1,'2026-02-17 19:54:04','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,38),(64,1,'SALIDA',-1,'2026-02-17 20:45:08','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,37),(65,77,'SALIDA',-1,'2026-02-19 01:58:06','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,6),(66,300,'ENTRADA',100,'2026-02-20 00:43:39','SISTEMA',NULL,1,'Entrada de Mercancía',NULL,NULL),(67,300,'SALIDA',-10,'2026-02-20 02:06:01','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,90),(68,22,'SALIDA',-1,'2026-02-20 03:42:55','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,49),(69,22,'SALIDA',-3,'2026-02-20 04:03:36','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,46),(70,22,'SALIDA',-3,'2026-02-20 04:10:01','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,43),(71,22,'SALIDA',-1,'2026-02-20 04:15:18','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,42),(72,22,'SALIDA',-1,'2026-02-20 04:21:11','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,41),(73,22,'SALIDA',-1,'2026-02-20 04:28:43','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,40),(74,77,'SALIDA',-1,'2026-02-20 04:34:20','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,5),(75,22,'SALIDA',-3,'2026-02-20 04:34:23','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,37),(76,300,'SALIDA',-10,'2026-02-20 04:34:25','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,80),(77,77,'SALIDA',-2,'2026-02-20 04:38:05','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,3),(78,300,'SALIDA',-30,'2026-02-20 04:38:07','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,50),(79,22,'SALIDA',-2,'2026-02-20 05:32:23','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,35),(80,22,'SALIDA',-2,'2026-02-20 05:33:08','admin',NULL,1,'VENTA_SUCURSAL_1',NULL,33);
/*!40000 ALTER TABLE `movimientos` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'REAL_AS_FLOAT,PIPES_AS_CONCAT,ANSI_QUOTES,IGNORE_SPACE,ONLY_FULL_GROUP_BY,ANSI,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`avnadmin`@`%`*/ /*!50003 TRIGGER `trg_validar_cantidad_disponible` BEFORE INSERT ON `movimientos` FOR EACH ROW BEGIN
    DECLARE v_cantidad_actual INT;
    -- Solo validamos si es una SALIDA (cantidad negativa)
    IF NEW.cantidad < 0 THEN
        SELECT cantidad_actual INTO v_cantidad_actual
        FROM lotes WHERE id = NEW.lote_id;
        
        IF v_cantidad_actual + NEW.cantidad < 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Error: Stock insuficiente en el lote para realizar este movimiento';
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'REAL_AS_FLOAT,PIPES_AS_CONCAT,ANSI_QUOTES,IGNORE_SPACE,ONLY_FULL_GROUP_BY,ANSI,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`avnadmin`@`%`*/ /*!50003 TRIGGER `trg_actualizar_cantidad_lote` AFTER INSERT ON `movimientos` FOR EACH ROW BEGIN
    UPDATE lotes
    SET cantidad_actual = cantidad_actual + NEW.cantidad
    WHERE id = NEW.lote_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `principios_activos`
--

DROP TABLE IF EXISTS `principios_activos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `principios_activos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `activo` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `idx_principios_activos_nombre` (`nombre`),
  KEY `idx_principios_activos_activo` (`activo`),
  CONSTRAINT `chk_principio_activo_nombre_no_vacio` CHECK ((trim(`nombre`) <> _utf8mb4''))
) ENGINE=InnoDB AUTO_INCREMENT=1366 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `principios_activos`
--

LOCK TABLES `principios_activos` WRITE;
/*!40000 ALTER TABLE `principios_activos` DISABLE KEYS */;
INSERT INTO `principios_activos` VALUES (1,'ACETAMINOFEN',NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(2,'IBUPROFENO',NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(3,'AMOXICILINA',NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(4,'OMEPRAZOL',NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(5,'ACIDO ACETILSALICILICO',NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(6,'LORATADINA',NULL,1,'2026-01-26 18:36:07','2026-01-26 18:36:07'),(7,'ACICLOVIR',NULL,1,'2026-01-26 18:50:02','2026-01-26 18:50:02'),(8,'ACETATO DE ALUMINIO',NULL,1,'2026-01-26 18:50:03','2026-01-26 18:50:03'),(10,'ACIDO BORICO',NULL,1,'2026-01-26 18:50:03','2026-01-26 18:50:03'),(11,'ACIDO FOLICO',NULL,1,'2026-01-26 18:50:04','2026-01-26 18:50:04'),(12,'ACIDO FUSIDICO',NULL,1,'2026-01-26 18:50:04','2026-01-26 18:50:04'),(13,'ACEITE MINERAL',NULL,1,'2026-01-26 18:50:04','2026-01-26 18:50:04'),(14,'ACETILCISTEINA',NULL,1,'2026-01-26 18:50:05','2026-01-26 18:50:05');
/*!40000 ALTER TABLE `principios_activos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo_interno` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_barras` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_comercial` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `concentracion` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `presentacion` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `registro_invima` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `categoria_id` int NOT NULL,
  `laboratorio_id` int NOT NULL,
  `principio_activo_id` int DEFAULT NULL,
  `precio_compra_referencia` decimal(38,2) DEFAULT NULL,
  `precio_venta_base` decimal(38,2) NOT NULL,
  `iva_porcentaje` decimal(38,2) DEFAULT NULL,
  `margen_minimo_porcentaje` decimal(38,2) DEFAULT NULL,
  `stock_minimo` int DEFAULT '10',
  `es_controlado` tinyint(1) DEFAULT '0',
  `refrigerado` tinyint(1) DEFAULT '0',
  `estado` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `es_fraccionable` tinyint(1) NOT NULL DEFAULT '0',
  `precio_venta_unidad` decimal(38,2) DEFAULT NULL,
  `unidades_por_caja` int NOT NULL DEFAULT '1',
  `unidades_por_blister` int DEFAULT NULL,
  `tipo` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'TANGIBLE',
  `precio_venta_blister` decimal(38,2) DEFAULT NULL,
  `porcentaje_ganancia` decimal(38,2) NOT NULL,
  `precio_venta_total` decimal(38,2) NOT NULL,
  `imagen_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imagen_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo_interno` (`codigo_interno`),
  UNIQUE KEY `codigo_barras` (`codigo_barras`),
  KEY `idx_productos_nombre` (`nombre_comercial`),
  KEY `idx_productos_codigo_barras` (`codigo_barras`),
  KEY `idx_productos_codigo_interno` (`codigo_interno`),
  KEY `idx_productos_principio_activo` (`principio_activo_id`),
  KEY `idx_productos_categoria` (`categoria_id`),
  KEY `idx_productos_laboratorio` (`laboratorio_id`),
  KEY `idx_productos_estado` (`estado`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_producto_laboratorio` FOREIGN KEY (`laboratorio_id`) REFERENCES `laboratorios` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_producto_principio_activo` FOREIGN KEY (`principio_activo_id`) REFERENCES `principios_activos` (`id`) ON DELETE SET NULL,
  CONSTRAINT `chk_producto_iva` CHECK (((`iva_porcentaje` >= 0) and (`iva_porcentaje` <= 100))),
  CONSTRAINT `chk_producto_margen_minimo` CHECK ((`margen_minimo_porcentaje` >= 0)),
  CONSTRAINT `chk_producto_nombre_no_vacio` CHECK ((trim(`nombre_comercial`) <> _utf8mb4'')),
  CONSTRAINT `chk_producto_precio_venta_positivo` CHECK ((`precio_venta_base` > 0)),
  CONSTRAINT `chk_producto_stock_minimo` CHECK ((`stock_minimo` >= 0)),
  CONSTRAINT `productos_chk_1` CHECK ((`tipo` in (_utf8mb4'TANGIBLE',_utf8mb4'SERVICIO')))
) ENGINE=InnoDB AUTO_INCREMENT=4376 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'GEN-001','7702605100101','Acetaminofén 500mg Genfar',NULL,'CAJA X 100 TAB',NULL,1,1,1,200.00,290.00,0.00,NULL,20,0,0,NULL,'2026-02-09 20:22:16','2026-02-19 19:43:39',1,50.00,100,10,'TANGIBLE',500.00,45.00,290.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275737/lqgajghryhtpkmmwwjby.jpg','lqgajghryhtpkmmwwjby'),(2,'DOL-001','7702123456789','Dolex Forte Caja x 24',NULL,'CAJA X 24 TAB',NULL,1,3,2,19200.00,24000.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:36:51',1,1000.00,24,4,'TANGIBLE',4000.00,25.00,24000.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771274208/ztsmxosvsrtbubkd6fjn.png','ztsmxosvsrtbubkd6fjn'),(3,'ADV-001','7702055123456','Advil Max 400mg Capsulas',NULL,'CAJA X 10 CAP',NULL,1,4,3,12500.00,16250.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 14:05:32',1,1650.00,10,0,'TANGIBLE',0.00,30.00,16250.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771268730/ci4r2h09yy6oesrlwscu.png','ci4r2h09yy6oesrlwscu'),(4,'GEN-003','7702605100999','Ibuprofeno 400mg Genfar',NULL,'CAJA X 50 TAB',NULL,1,1,3,8000.00,11200.00,0.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,250.00,50,10,'TANGIBLE',2500.00,40.00,11200.00,NULL,NULL),(5,'ASP-001','7702001002003','Aspirina Efervescente x 12',NULL,'CAJA X 12 SOBRES',NULL,1,2,NULL,11000.00,14300.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:21:33',1,1200.00,12,0,'TANGIBLE',0.00,30.00,14300.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273288/myivicxoyzzdmmwvtgpn.png','myivicxoyzzdmmwvtgpn'),(6,'GEN-002','7702605100309','Amoxicilina 500mg Genfar',NULL,'CAJA X 50 CAP',NULL,2,1,4,24000.00,33600.00,19.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:21:05',1,800.00,50,10,'TANGIBLE',8000.00,40.00,39984.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273263/t8guo8ruzpyj9epckkfh.jpg','t8guo8ruzpyj9epckkfh'),(7,'AZI-001','7703153023456','Azitromicina 500mg La Sante',NULL,'CAJA X 3 TAB',NULL,2,2,5,9500.00,12825.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:21:52',1,4300.00,3,0,'TANGIBLE',0.00,35.00,12825.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273308/cahifzj4xugxezulklxm.png','cahifzj4xugxezulklxm'),(8,'CIP-001','7702605123123','Ciprofloxacino 500mg Genfar',NULL,'CAJA X 10 TAB',NULL,2,1,NULL,15000.00,21000.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:22:55',1,2100.00,10,0,'TANGIBLE',0.00,40.00,21000.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273371/orxrhhz8ghou8epfq7ov.png','orxrhhz8ghou8epfq7ov'),(9,'CEF-001','7703038000111','Cefalexina 500mg Laproff',NULL,'CAJA X 20 CAP',NULL,2,5,NULL,18000.00,24300.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:22:29',1,1250.00,20,10,'TANGIBLE',12500.00,35.00,24300.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273348/zltmplmgtgcziqch3uwg.png','zltmplmgtgcziqch3uwg'),(10,'GAS-001','7702605100505','Omeprazol 20mg Cápsulas',NULL,'CAJA X 30 CAP',NULL,5,1,6,3500.00,7000.00,0.00,NULL,15,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,250.00,30,10,'TANGIBLE',2500.00,100.00,7000.00,NULL,NULL),(11,'PED-001','7702055987654','Pedialyte 30 Fresa 500ml',NULL,'FRASCO 500ML',NULL,5,6,NULL,7200.00,9000.00,19.00,NULL,12,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,10710.00,1,0,'TANGIBLE',0.00,25.00,10710.00,NULL,NULL),(12,'SAL-001','7702001005001','Sal de Frutas Lua Sobre',NULL,'CAJA X 6 SOBRES',NULL,5,5,NULL,1800.00,2340.00,0.00,NULL,20,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,400.00,6,0,'TANGIBLE',0.00,30.00,2340.00,NULL,NULL),(13,'BUS-001','7702123000222','Buscapina Compositum',NULL,'CAJA X 20 GRAGEAS',NULL,5,3,NULL,16500.00,21120.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:22:10',1,1100.00,20,10,'TANGIBLE',11000.00,28.00,21120.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273328/vwzhhmfaihgx3unlfkwz.png','vwzhhmfaihgx3unlfkwz'),(14,'NOX-001','7702002002002','Noraver Gripa Capsulas',NULL,'CAJA X 12 CAP',NULL,6,5,NULL,11000.00,14300.00,0.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,1200.00,12,0,'TANGIBLE',0.00,30.00,14300.00,NULL,NULL),(15,'DES-001','7702605000444','Descongel Gripa Sobre',NULL,'SOBRE INDIVIDUAL',NULL,6,1,NULL,1500.00,2025.00,0.00,NULL,20,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:26:08',0,2050.00,1,0,'TANGIBLE',0.00,35.00,2025.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273566/zzzxku7bazlxrrvaz8lz.png','zzzxku7bazlxrrvaz8lz'),(16,'LOR-001','7702605100888','Loratadina 10mg Genfar',NULL,'CAJA X 10 TAB',NULL,6,1,NULL,3000.00,5400.00,0.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,550.00,10,0,'TANGIBLE',0.00,80.00,5400.00,NULL,NULL),(17,'VICK-01','7702123000999','Vick Vaporub 50g',NULL,'POTE 50G',NULL,6,7,NULL,12000.00,15000.00,19.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,17850.00,1,0,'TANGIBLE',0.00,25.00,17850.00,NULL,NULL),(18,'DERM-001','7702123010999','Acid Mantle Loción x 120ml',NULL,'FRASCO X 120ML',NULL,3,2,NULL,18500.00,24050.00,19.00,NULL,3,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,28619.50,1,0,'TANGIBLE',0.00,30.00,28619.50,NULL,NULL),(19,'CLO-001','7702605000777','Clotrimazol Crema 1%',NULL,'TUBO 40G',NULL,3,1,NULL,4500.00,6750.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:23:16',0,6750.00,1,0,'TANGIBLE',0.00,50.00,6750.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273392/kh73uzpyg4kuef7bsm3b.png','kh73uzpyg4kuef7bsm3b'),(20,'VOL-001','7702123000555','Voltaren Emulgel 60g',NULL,'TUBO 60G',NULL,3,3,NULL,22000.00,27500.00,19.00,NULL,3,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,32725.00,1,0,'TANGIBLE',0.00,25.00,32725.00,NULL,NULL),(21,'REX-001','7702005001234','Rexona Clinical Mujer Crema',NULL,'CREMA 48G',NULL,4,5,NULL,14000.00,17500.00,19.00,NULL,6,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,20825.00,1,0,'TANGIBLE',0.00,25.00,20825.00,NULL,NULL),(22,'PRE-001','7702001001001','Preservativos Today x 3',NULL,'CAJA X 3 UNID',NULL,4,8,NULL,8500.00,11900.00,19.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,4750.00,3,0,'TANGIBLE',0.00,40.00,14161.00,NULL,NULL),(23,'ALC-001','7701001001001','Alcohol Antiséptico 700ml','700ml','BOTELLA 700ML',NULL,4,9,5,4500.00,6075.00,19.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-16 15:20:45',0,7229.25,1,0,'TANGIBLE',0.00,35.00,7229.25,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771273243/pfobdx0tamy8goqyhnzf.png','pfobdx0tamy8goqyhnzf'),(24,'VIT-001','7702002005005','Vitamina C MK 500mg',NULL,'CAJA X 100 TAB',NULL,7,5,NULL,9000.00,12600.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,150.00,100,10,'TANGIBLE',1500.00,40.00,12600.00,NULL,NULL),(25,'ENS-001','7702005009009','Ensure Polvo Vainilla 400g',NULL,'LATA 400G',NULL,5,6,NULL,48000.00,55200.00,19.00,NULL,2,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,65700.00,1,0,'TANGIBLE',0.00,15.00,65688.00,NULL,NULL),(26,'LOS-001','7702605100606','Losartán 50mg Genfar',NULL,'CAJA X 30 TAB',NULL,7,1,NULL,4000.00,6400.00,0.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,250.00,30,15,'TANGIBLE',3750.00,60.00,6400.00,NULL,NULL),(27,'ENL-001','7702605100707','Enalapril 20mg Genfar',NULL,'CAJA X 30 TAB',NULL,7,1,NULL,3500.00,5950.00,0.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,200.00,30,10,'TANGIBLE',2000.00,70.00,5950.00,NULL,NULL),(28,'MET-001','7702605100808','Metformina 850mg Genfar',NULL,'CAJA X 30 TAB',NULL,7,1,NULL,5000.00,7500.00,0.00,NULL,10,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',1,250.00,30,10,'TANGIBLE',2500.00,50.00,7500.00,NULL,NULL),(29,'DOL-002','7702123456790','Dolex Niños Jarabe 90ml',NULL,'FRASCO 90ML',NULL,1,3,1,14000.00,18200.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,18200.00,1,0,'TANGIBLE',0.00,30.00,18200.00,NULL,NULL),(30,'NOV-001','7703001001001','Noxpirin Niños Jarabe',NULL,'FRASCO 60ML',NULL,6,5,NULL,13500.00,17550.00,0.00,NULL,5,0,0,NULL,'2026-02-09 20:22:16','2026-02-09 20:22:16',0,17550.00,1,0,'TANGIBLE',0.00,30.00,17550.00,NULL,NULL),(31,'586','0.770303807','Aciclovir 800Mg X Unidad En Tableta Laproff','800Mg','Tableta','INVIMA 2023M-0019452',6,9,7,946.00,1.40,0.00,47.99,15,0,0,'Activo','2026-02-09 20:32:00','2026-02-09 20:32:00',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(32,'1159','0.770656902','Aciclovir Tableta 800Mg X Unidad','800Mg','Tableta','INVIMA 2024M-0005612',6,10,7,540.00,900.00,0.00,66.67,10,0,0,'Inactivo','2026-02-09 20:32:00','2026-02-16 16:03:09',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275787/amfxvk9qqsho8zpsh3rq.png','amfxvk9qqsho8zpsh3rq'),(33,'1656','0.77026051','Aciclovir Unguento Al 5 X 15 Mg','5%','Ungüento','INVIMA 2022M-0123456',6,1,7,7.23,9.40,19.00,45.17,20,0,0,'Activo','2026-02-09 20:32:01','2026-02-19 19:33:25',0,11.19,1,NULL,'TANGIBLE',0.00,30.00,11.19,NULL,NULL),(34,'1756','0.770212301','Acid Mante Baby X 30Gr','30Gr','Crema','INVIMA 2025M-0044556',6,3,NULL,15.69,16.30,19.00,3887.83,12,0,0,'Inactivo','2026-02-09 20:32:01','2026-02-16 16:03:45',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275821/smngyezl7xfi84adhwtn.png','smngyezl7xfi84adhwtn'),(35,'3007','.ACD','Acid Mantle 400 Gr','400 Gr','Frasco','INVIMA 2023M-0099887',6,13,8,23.50,32.50,0.00,38.30,25,0,0,'Activo','2026-02-09 20:32:01','2026-02-16 16:03:58',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275835/hvbgn7hjtolqgebolnoe.png','hvbgn7hjtolqgebolnoe'),(36,'1139','0.7702123','Acid Mantle Locion X 120Ml','120Ml','Loción','INVIMA 2024M-1122334',6,3,8,16.40,20.50,0.00,25.00,18,0,0,'Inactivo','2026-02-09 20:32:02','2026-02-09 20:32:02',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(38,'1806','0.770703551','Acido Acetilsalicico 100 Mg X 10 Ophalac','100 Mg','Tableta','INVIMA 2023M-9988776',6,14,5,1.20,2.00,0.00,66666.67,50,0,0,'Activo','2026-02-09 20:32:02','2026-02-09 20:32:02',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(39,'2990','0.770354665','Acido Acetilsalicilico 100 Mg X 10 Tab','100 Mg','Tableta','INVIMA 2024M-4433221',6,9,5,2.07,3.50,0.00,69.23,40,0,0,'Activo','2026-02-09 20:32:03','2026-02-09 20:32:03',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(41,'55','.','Acido Acetilsalicilico 100Mg Cj X 250 Tabletas','100Mg','Caja','INVIMA 2025M-5544332',6,15,5,28.50,46.50,0.00,63.16,20,0,0,'Inactivo','2026-02-09 20:32:03','2026-02-09 20:32:03',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(42,'1876','0.7703714','Acido Borico X 250 Gra','250 Gra','Bolsa','INVIMA 2023M-7788990',6,16,10,2.20,3.50,0.00,59.09,30,0,0,'Activo','2026-02-09 20:32:04','2026-02-09 20:32:04',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(43,'1887','0.106','Acido Borico X 10 Gr Nal. De Quimicos','10 Gr','Sobre','INVIMA 2024M-6655443',6,17,10,212.00,500.00,0.00,135.85,45,0,0,'Activo','2026-02-09 20:32:04','2026-02-09 20:32:04',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(49,'584','0.770303805','Acido Folico 1Mg Blister X 10 Tabletas Laproff','1Mg','Blister','INVIMA 2024M-9900223',6,9,11,651.00,1.20,0.00,84.33,30,0,0,'Activo','2026-02-09 20:32:07','2026-02-09 20:32:07',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(51,'2283','0.770371203','Acido Fusidico 2 X 15 Gr Cohaspharma','2%','Tubo','INVIMA 2025M-7788445',6,21,12,9.50,9.50,0.00,0.00,10,0,0,'Inactivo','2026-02-09 20:32:07','2026-02-09 20:32:07',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(53,'662','0.77044121','Acido Fusidico 2% Crema Topica X 15Gr','2%','Crema','INVIMA 2024M-0099554',6,22,12,5.70,9.50,0.00,66.67,18,0,0,'Activo','2026-02-09 20:32:08','2026-02-09 20:32:08',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,NULL,NULL),(54,'534','.572625-A','Aceite Jhonsons Baby X 50Ml Jhonson Y Jhonson','50Ml','Frasco','INVIMA 2022M-6655443',6,23,11,7.49,9.74,19.00,30.84,15,0,0,'Activo','2026-02-09 20:32:08','2026-02-19 19:07:45',0,11.59,1,NULL,'TANGIBLE',0.00,30.00,11.59,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275121/sytkiyfjosizbrcvg4cg.png','sytkiyfjosizbrcvg4cg'),(55,'7503030935050','7503030935050','Aceite Julius Baby X 40Ml','40Ml','Frasco','INVIMA 2025M-2233445',6,24,6,3000.00,3900.00,19.00,7142.86,20,0,0,'Activo','2026-02-09 20:32:09','2026-02-16 14:01:43',0,4641.00,1,NULL,'TANGIBLE',0.00,30.00,4641.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771268501/khljfvrl9mipojml4qf9.png','khljfvrl9mipojml4qf9'),(59,'2259','0.770999008','Aceite Para Bebe Julies X 80 Ml','80 Ml','Frasco','INVIMA 2025M-9900223',6,26,NULL,5.50,5.50,0.00,0.00,10,0,0,'Inactivo','2026-02-09 20:32:10','2026-02-16 15:52:30',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275148/dstioomeyjrz0sqoq9r4.jpg','dstioomeyjrz0sqoq9r4'),(60,'2539','0.77091544','Aceite Para Bebe Julius 50 Ml','50 Ml','Frasco','INVIMA 2023M-1122554',6,23,NULL,1.65,3.60,0.00,118.18,15,0,0,'Activo','2026-02-09 20:32:10','2026-02-16 15:52:48',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275164/ypxd4khr9ib0am75wfmw.jpg','ypxd4khr9ib0am75wfmw'),(62,'2083','0.770371401','Aceite Recino 25 Cc','25 Cc','Frasco','INVIMA 2022M-1100229',6,27,NULL,2.00,2.00,0.00,0.00,10,0,0,'Inactivo','2026-02-09 20:32:11','2026-02-16 15:53:00',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275178/d0f5lj1pcfmgrbdmrvsk.png','d0f5lj1pcfmgrbdmrvsk'),(63,'2193','0.770724298','Aceite Sensual Vainilla X 25 Ml','25 Ml','Frasco','INVIMA 2025M-5544332',6,28,NULL,8.00,12.00,0.00,50.00,15,0,0,'Inactivo','2026-02-09 20:32:11','2026-02-16 15:53:14',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275191/skxqyjsadq8q5oltbkos.jpg','skxqyjsadq8q5oltbkos'),(65,'1370','0.77051373','Aceite Suave Osito 50 Ml','50 Ml','Frasco','INVIMA 2024M-6655443',6,30,NULL,2.70,3.40,19.00,25925.93,20,0,0,'Activo','2026-02-09 20:32:12','2026-02-16 15:53:25',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275203/hxxkt3ntwpds8mxxoczf.jpg','hxxkt3ntwpds8mxxoczf'),(66,'1407','0.770735506','Acemuk 600 Acetilcisteina 600 Mg X 30 Comprimidos Efervescentes','','','INVIMA 2022M-2233445',6,31,14,1.73,2.25,19.00,44450.87,25,0,0,'Activo','2026-02-09 20:32:12','2026-02-19 19:32:39',0,2.68,1,NULL,'TANGIBLE',0.00,30.00,2.68,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275215/pa5qg8vmdfyltsudh1hj.png','pa5qg8vmdfyltsudh1hj'),(68,'3064','0.770595989','Acetaminofen Cafeina X 1 Unid.','1 Unid','Tableta','INVIMA 2023M-8877665',6,9,1,686.00,1.60,0.00,133.24,15,0,0,'Activo','2026-02-09 20:32:13','2026-02-16 16:02:35',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275753/xwb0u1gcrkdoywpv5kps.jpg','xwb0u1gcrkdoywpv5kps'),(72,'1801','0.770303816','Acetaminofen / Codeina X 10 Tab.Laprof','10 tab','Tableta','INVIMA 2023M-1122554',6,9,1,4.32,5.62,19.00,85.23,10,1,0,'Activo','2026-02-09 20:32:14','2026-02-19 19:28:51',0,6.69,1,NULL,'TANGIBLE',0.00,30.00,6.69,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275233/r2o48upvwsvw4esig899.png','r2o48upvwsvw4esig899'),(75,'2604','0.770354633','Acetaminofen 150Mg/5Ml Novamed','150Mg/5Ml','Jarabe','INVIMA 2025M-7788445',6,32,1,3.43,8.50,0.00,147.81,10,0,0,'Activo','2026-02-09 20:32:15','2026-02-16 15:56:03',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275359/j1nlv4brylf1zyqrwoei.png','j1nlv4brylf1zyqrwoei'),(76,'165','0.770371204','Acetaminofen 500Mg Blister X 10 Unidades','500Mg','Blister','INVIMA 2023M-3322110',6,9,1,598.00,1.00,0.00,67.22,50,0,0,'Activo','2026-02-09 20:32:15','2026-02-16 15:56:35',0,NULL,1,NULL,'TANGIBLE',NULL,30.00,0.00,'http://res.cloudinary.com/dfwwwclla/image/upload/v1771275393/oip1p66aqolt5aixttg7.jpg','oip1p66aqolt5aixttg7'),(127,'TEST-PRECIO-001','7777777777777','Producto Test Precios',NULL,NULL,NULL,1,1,NULL,120.00,156.00,19.00,NULL,10,0,0,'ACTIVO','2026-02-09 15:53:17','2026-02-09 15:53:18',1,50.00,100,10,'TANGIBLE',500.00,30.00,185.64,NULL,NULL),(1400,'1112','0.77022153','Aceite Julius Baby X 40Ml','40Ml','Frasco','INVIMA 2025M-2233445',6,24,NULL,2.80,3.00,0.00,7142.86,20,0,0,'Activo','2026-02-11 23:36:48','2026-02-11 23:36:48',0,NULL,1,NULL,'TANGIBLE',NULL,0.00,0.00,NULL,NULL),(2607,'7707355053318','7707355053318','Paracetamol 500mg Tabletas','500mg','Tabletas','',1,1,1,25000.00,32500.00,19.00,NULL,6,0,0,'ACTIVO','2026-02-13 15:15:11','2026-02-13 15:15:11',1,3250.00,12,4,'TANGIBLE',13000.00,30.00,38675.00,NULL,NULL),(2608,'019309123','','Paracetamol 200mg Tabletas','','','',1,1,1,1000.00,1100.00,19.00,NULL,10,0,0,'ACTIVO','2026-02-13 15:45:02','2026-02-13 15:45:02',0,1309.00,1,NULL,'TANGIBLE',0.00,10.00,1309.00,NULL,NULL),(2610,'PROD003','12323','Ibuprofeno 400mg','','','',1,1,1,100100.00,130130.00,19.00,NULL,10,0,0,'ACTIVO','2026-02-13 16:16:59','2026-02-13 16:16:59',0,154854.70,1,NULL,'TANGIBLE',0.00,30.00,154854.70,NULL,NULL),(2611,'1000','21321321','Ibuprofeno 400mg','','','',1,1,1,98000.00,127400.00,19.00,NULL,10,0,0,'ACTIVO','2026-02-13 16:26:16','2026-02-13 16:26:16',0,151606.00,1,NULL,'TANGIBLE',0.00,30.00,151606.00,NULL,NULL);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `idx_nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMINISTRADOR','Usuario con acceso completo al sistema. Puede gestionar usuarios, roles, inventario, ventas y reportes.'),(2,'VENDEDOR','Usuario con acceso para realizar ventas y consultar inventario. No puede modificar configuraciones del sistema.'),(3,'SISTEMA','Usuario del sistema para operaciones automatizadas y tareas programadas.');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sucursales`
--

DROP TABLE IF EXISTS `sucursales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sucursales` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ciudad` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` text COLLATE utf8mb4_unicode_ci,
  `activa` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `idx_sucursales_activa` (`activa`),
  KEY `idx_sucursales_ciudad` (`ciudad`),
  CONSTRAINT `chk_sucursal_ciudad_no_vacio` CHECK ((trim(`ciudad`) <> _utf8mb4'')),
  CONSTRAINT `chk_sucursal_nombre_no_vacio` CHECK ((trim(`nombre`) <> _utf8mb4''))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sucursales`
--

LOCK TABLES `sucursales` WRITE;
/*!40000 ALTER TABLE `sucursales` DISABLE KEYS */;
INSERT INTO `sucursales` VALUES (1,'Sucursal Centro','Bogotá','Cra 7 #12',1,'2025-12-12 03:12:13','2025-12-12 03:12:13'),(2,'Sucursal Sur','Armenia','Cr 14 #20-85',1,'2026-02-14 18:42:02','2026-02-14 18:42:02');
/*!40000 ALTER TABLE `sucursales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cedula` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `login` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol_id` bigint NOT NULL,
  `estado` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `intentos_fallidos` int DEFAULT '0',
  `fecha_bloqueo` datetime DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ultimo_acceso` datetime DEFAULT NULL,
  `sucursal_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cedula` (`cedula`),
  UNIQUE KEY `login` (`login`),
  KEY `idx_login` (`login`),
  KEY `idx_cedula` (`cedula`),
  KEY `idx_estado` (`estado`),
  KEY `idx_rol_id` (`rol_id`),
  CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `chk_estado` CHECK ((`estado` in (_utf8mb4'ACTIVO',_utf8mb4'INACTIVO',_utf8mb4'BLOQUEADO')))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Axel - Admin','1234567890','admin','$2a$10$2.lYAVbuxi/XDW/FxmGfD.NQdZRTU9KKLFqNgtaaBvSriGGC9Hawi',1,'ACTIVO',0,NULL,'2025-12-18 09:31:09','2026-02-20 17:25:30','2026-02-20 12:25:06',NULL),(2,'Branch User 8406','998406','user8406','$2a$10$HMOFyJ4vjRuttYynDU2QMOSBV5Gf2hRsnEnUxrNhGE5Jh1pv7XmUK',2,'ACTIVO',0,NULL,'2026-02-12 20:39:18','2026-02-12 20:39:18',NULL,1),(3,'Test Vendedor 01 - Actualizado','1234567891','vendedor01','$2a$10$ovvpiaRpQMG/YztfHi3XeewkLnDcq0zHM2mjXGQNCilfQOarHSm7S',2,'ACTIVO',0,NULL,'2026-02-17 12:37:59','2026-02-17 22:33:18','2026-02-17 22:33:18',2),(4,'Pedro Ramirez','5555555555','pramirez','$2a$10$Er3ao4dJR/bUstAW05Alw.qsXGlNs/I4HH/pj.7o1mFPk3W2Uz6Be',2,'INACTIVO',0,NULL,'2026-02-17 12:41:50','2026-02-17 22:21:40','2026-02-17 13:22:05',3);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_control_precios`
--

DROP TABLE IF EXISTS `v_control_precios`;
/*!50001 DROP VIEW IF EXISTS `v_control_precios`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_control_precios` AS SELECT 
 1 AS `id`,
 1 AS `nombre_comercial`,
 1 AS `precio_actual`,
 1 AS `costo_promedio`,
 1 AS `margen_minimo_porcentaje`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_historial_precios`
--

DROP TABLE IF EXISTS `v_historial_precios`;
/*!50001 DROP VIEW IF EXISTS `v_historial_precios`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_historial_precios` AS SELECT 
 1 AS `id`,
 1 AS `nombre_comercial`,
 1 AS `precio_anterior`,
 1 AS `precio_nuevo`,
 1 AS `usuario_responsable`,
 1 AS `fecha_cambio`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_movimientos_detalle`
--

DROP TABLE IF EXISTS `v_movimientos_detalle`;
/*!50001 DROP VIEW IF EXISTS `v_movimientos_detalle`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_movimientos_detalle` AS SELECT 
 1 AS `id`,
 1 AS `fecha_movimiento`,
 1 AS `tipo_movimiento`,
 1 AS `cantidad`,
 1 AS `nombre_comercial`,
 1 AS `numero_lote`,
 1 AS `observaciones`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_productos_detalle`
--

DROP TABLE IF EXISTS `v_productos_detalle`;
/*!50001 DROP VIEW IF EXISTS `v_productos_detalle`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_productos_detalle` AS SELECT 
 1 AS `id`,
 1 AS `codigo_interno`,
 1 AS `nombre_comercial`,
 1 AS `principio_activo`,
 1 AS `laboratorio`,
 1 AS `stock_total`,
 1 AS `proximo_vencimiento`,
 1 AS `precio_venta_base`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_productos_proximos_vencer`
--

DROP TABLE IF EXISTS `v_productos_proximos_vencer`;
/*!50001 DROP VIEW IF EXISTS `v_productos_proximos_vencer`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_productos_proximos_vencer` AS SELECT 
 1 AS `producto_id`,
 1 AS `nombre_comercial`,
 1 AS `numero_lote`,
 1 AS `fecha_vencimiento`,
 1 AS `cantidad_actual`,
 1 AS `dias_para_vencer`,
 1 AS `estado_vencimiento`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_semaforo_vencimientos`
--

DROP TABLE IF EXISTS `v_semaforo_vencimientos`;
/*!50001 DROP VIEW IF EXISTS `v_semaforo_vencimientos`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_semaforo_vencimientos` AS SELECT 
 1 AS `numero_lote`,
 1 AS `producto_id`,
 1 AS `codigo_interno`,
 1 AS `nombre_comercial`,
 1 AS `laboratorio`,
 1 AS `fecha_vencimiento`,
 1 AS `cantidad_actual`,
 1 AS `dias_restantes`,
 1 AS `color_alerta`,
 1 AS `accion_sugerida`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_stock_productos`
--

DROP TABLE IF EXISTS `v_stock_productos`;
/*!50001 DROP VIEW IF EXISTS `v_stock_productos`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_stock_productos` AS SELECT 
 1 AS `producto_id`,
 1 AS `codigo_interno`,
 1 AS `codigo_barras`,
 1 AS `nombre_comercial`,
 1 AS `concentracion`,
 1 AS `presentacion`,
 1 AS `precio_venta_base`,
 1 AS `precio_venta_total`,
 1 AS `precio_venta_unidad`,
 1 AS `precio_venta_blister`,
 1 AS `iva_porcentaje`,
 1 AS `stock_minimo`,
 1 AS `es_fraccionable`,
 1 AS `unidades_por_caja`,
 1 AS `refrigerado`,
 1 AS `es_controlado`,
 1 AS `imagen_url`,
 1 AS `laboratorio_nombre`,
 1 AS `categoria_nombre`,
 1 AS `principio_activo_nombre`,
 1 AS `stock_total`,
 1 AS `proximo_vencimiento`,
 1 AS `nivel_stock`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping events for database 'defaultdb'
--

--
-- Dumping routines for database 'defaultdb'
--
/*!50003 DROP PROCEDURE IF EXISTS `actualizar_precio_producto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`avnadmin`@`%` PROCEDURE `actualizar_precio_producto`(
    IN p_producto_id INT, 
    IN p_precio_nuevo DECIMAL(10, 2), 
    IN p_usuario VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, 
    IN p_motivo TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
)
BEGIN
    DECLARE v_precio_anterior DECIMAL(10, 2);
    DECLARE v_costo_promedio DECIMAL(10, 2);
    DECLARE v_margen_anterior DECIMAL(5, 2);
    DECLARE v_margen_nuevo DECIMAL(5, 2);

    IF p_precio_nuevo <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El precio debe ser mayor a cero';
    END IF;

    SELECT p.precio_venta_base, IFNULL((SELECT SUM(l.costo_compra * l.cantidad_actual) / SUM(l.cantidad_actual) FROM lotes l WHERE l.producto_id = p.id AND l.cantidad_actual > 0), p.precio_compra_referencia)
    INTO v_precio_anterior, v_costo_promedio
    FROM productos p WHERE p.id = p_producto_id;

    IF v_costo_promedio IS NOT NULL AND v_costo_promedio > 0 THEN
        SET v_margen_anterior = ((v_precio_anterior - v_costo_promedio) / v_costo_promedio) * 100;
        SET v_margen_nuevo = ((p_precio_nuevo - v_costo_promedio) / v_costo_promedio) * 100;
    END IF;

    UPDATE productos SET precio_venta_base = p_precio_nuevo WHERE id = p_producto_id;

    INSERT INTO historial_precios (producto_id, precio_anterior, precio_nuevo, costo_promedio_momento, margen_anterior, margen_nuevo, motivo, usuario_responsable) 
    VALUES (p_producto_id, v_precio_anterior, p_precio_nuevo, v_costo_promedio, v_margen_anterior, v_margen_nuevo, IFNULL(p_motivo, 'Ajuste de precio'), p_usuario);

    SELECT 'Precio actualizado exitosamente' AS mensaje;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ajustar_inventario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`avnadmin`@`%` PROCEDURE `ajustar_inventario`(
    IN p_lote_id INT, 
    IN p_cantidad_nueva INT, 
    IN p_usuario VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, 
    IN p_motivo TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
)
BEGIN
    DECLARE v_cantidad_actual INT;
    DECLARE v_diferencia INT;
    SELECT cantidad_actual INTO v_cantidad_actual FROM lotes WHERE id = p_lote_id;

    IF v_cantidad_actual IS NULL THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lote no encontrado'; END IF;
    SET v_diferencia = p_cantidad_nueva - v_cantidad_actual;

    IF v_diferencia != 0 THEN
        INSERT INTO movimientos (lote_id, tipo_movimiento, cantidad, usuario_responsable, observaciones) 
        VALUES (p_lote_id, 'AJUSTE', v_diferencia, p_usuario, CONCAT('Ajuste de inventario: ', ABS(v_diferencia), ' unidades. Motivo: ', p_motivo));
        SELECT 'Inventario ajustado exitosamente' AS mensaje;
    ELSE
        SELECT 'No hay cambios en el inventario' AS mensaje;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `registrar_entrada_mercancia` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'REAL_AS_FLOAT,PIPES_AS_CONCAT,ANSI_QUOTES,IGNORE_SPACE,ONLY_FULL_GROUP_BY,ANSI,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER="avnadmin"@"%" PROCEDURE "registrar_entrada_mercancia"(
    IN p_producto_id INT,
    IN p_numero_lote VARCHAR(50),
    IN p_cantidad_comprada INT,  -- Cantidad en CAJAS (o unidades si unidades_por_caja=1)
    IN p_costo_compra DECIMAL(10, 2), -- Costo TOTAL de la compra
    IN p_fecha_vencimiento DATE,
    IN p_usuario VARCHAR(50),
    IN p_sucursal_id INT,
    IN p_observaciones TEXT
)
BEGIN
    DECLARE v_unidades_por_caja INT DEFAULT 1;
    DECLARE v_cantidad_real INT;
    DECLARE v_costo_unitario DECIMAL(18, 4);

    -- 1. Obtener factor de conversión del producto
    SELECT COALESCE(unidades_por_caja, 1) INTO v_unidades_por_caja
    FROM productos 
    WHERE id = p_producto_id;

    -- 2. Calcular cantidad real en unidades mínimas
    SET v_cantidad_real = p_cantidad_comprada * v_unidades_por_caja;

    -- 3. Calcular costo unitario (Costo Total / Unidades Totales)
    -- Evitar división por cero
    IF v_cantidad_real > 0 THEN
        SET v_costo_unitario = p_costo_compra / v_cantidad_real;
    ELSE
        SET v_costo_unitario = 0;
    END IF;

    -- 4. Insertar el Lote con las UNIDADES y COSTO UNITARIO
    -- Se corrigió para coincidir con la entidad (solo cantidad_actual)
    INSERT INTO lotes (
        producto_id, 
        numero_lote, 
        fecha_vencimiento, 
        cantidad_actual,  -- Usamos este valor como inicial también
        costo_compra, -- Guardamos el costo UNITARIO calculado
        sucursal_id
    ) VALUES (
        p_producto_id, 
        p_numero_lote, 
        p_fecha_vencimiento, 
        v_cantidad_real, -- Cantidad Actual = Cantidad Inicial calculada 
        v_costo_unitario, 
        p_sucursal_id
    );

    -- 5. Registrar Movimiento de Entrada
    INSERT INTO movimientos (
        lote_id, 
        tipo_movimiento, 
        cantidad, 
        usuario_responsable, 
        sucursal_id, 
        observaciones
    ) VALUES (
        LAST_INSERT_ID(), 
        'ENTRADA', 
        v_cantidad_real, 
        p_usuario, 
        p_sucursal_id, 
        p_observaciones
    );

    -- Retornar resultado
    SELECT 'OK' as estado, 'Lote registrado correctamente' as mensaje, 
           v_cantidad_real as cantidad_unidades, v_costo_unitario as costo_unitario;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `registrar_salida_mercancia` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'REAL_AS_FLOAT,PIPES_AS_CONCAT,ANSI_QUOTES,IGNORE_SPACE,ONLY_FULL_GROUP_BY,ANSI,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER="avnadmin"@"%" PROCEDURE "registrar_salida_mercancia"(
    IN p_producto_id INT,
    IN p_cantidad INT, -- Cantidad solicitada (Cajas o Unidades según flag)
    IN p_usuario VARCHAR(50),
    IN p_sucursal_id INT,
    IN p_venta_id INT,
    IN p_observaciones TEXT,
    IN p_es_venta_por_caja BOOLEAN -- NUEVO PARAMETRO
)
BEGIN
    DECLARE v_cantidad_restante INT;
    DECLARE v_lote_id INT;
    DECLARE v_cantidad_lote INT;
    DECLARE v_descuento INT;
    DECLARE v_unidades_por_caja INT DEFAULT 1;
    DECLARE v_cantidad_a_descontar_total INT;
    
    -- Cursor para iterar lotes FIFO/FEFO (Primero vence, primero sale)
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur_lotes CURSOR FOR 
        SELECT id, cantidad_actual 
        FROM lotes 
        WHERE producto_id = p_producto_id 
          AND cantidad_actual > 0 
          AND fecha_vencimiento > CURDATE()
        ORDER BY fecha_vencimiento ASC;
        
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 1. Determinar cuántas UNIDADES realmente vamos a descontar
    IF p_es_venta_por_caja = TRUE THEN
        -- Buscar factor de conversión
        SELECT COALESCE(unidades_por_caja, 1) INTO v_unidades_por_caja
        FROM productos 
        WHERE id = p_producto_id;
        
        SET v_cantidad_a_descontar_total = p_cantidad * v_unidades_por_caja;
    ELSE
        -- Venta por unidad (o producto no fraccionable tratado como unidad)
        SET v_cantidad_a_descontar_total = p_cantidad;
    END IF;

    SET v_cantidad_restante = v_cantidad_a_descontar_total;

    -- 2. Validar Stock Total disponible antes de empezar
    IF (SELECT SUM(cantidad_actual) FROM lotes WHERE producto_id = p_producto_id) < v_cantidad_restante THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente para realizar la venta';
    END IF;

    -- 3. Iterar lotes y descontar
    OPEN cur_lotes;

    read_loop: LOOP
        FETCH cur_lotes INTO v_lote_id, v_cantidad_lote;
        
        IF done THEN
            LEAVE read_loop;
        END IF;

        IF v_cantidad_restante > 0 THEN
            IF v_cantidad_lote >= v_cantidad_restante THEN
                -- El lote alcanza para todo lo que falta
                SET v_descuento = v_cantidad_restante;
            ELSE
                -- El lote se agota, tomamos todo lo que tiene
                SET v_descuento = v_cantidad_lote;
            END IF;

            -- NO hacemos UPDATE manual aquí, el trigger lo hará
            -- Registrar Movimiento con cantidad NEGATIVA para que el trigger funcione
            INSERT INTO movimientos (
                lote_id, tipo_movimiento, cantidad, usuario_responsable, sucursal_id, venta_id, observaciones
            ) VALUES (
                v_lote_id, 'SALIDA', -v_descuento, p_usuario, p_sucursal_id, p_venta_id, p_observaciones
            );

            SET v_cantidad_restante = v_cantidad_restante - v_descuento;
        ELSE
            LEAVE read_loop;
        END IF;
    END LOOP;

    CLOSE cur_lotes;

    -- Validación final
    IF v_cantidad_restante > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error de consistencia: No se pudo descontar el total requerido.';
    END IF;

    -- Retornar info de lotes afectados (simplificado)
    SELECT id as lote_id, v_cantidad_a_descontar_total as total_descontado FROM lotes WHERE producto_id = p_producto_id limit 1; 

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `reporte_rentabilidad_producto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`avnadmin`@`%` PROCEDURE `reporte_rentabilidad_producto`(IN p_producto_id INT, IN p_fecha_desde DATE, IN p_fecha_hasta DATE)
BEGIN
    SELECT p.id AS producto_id, p.nombre_comercial AS nombre_producto, SUM(ABS(m.cantidad)) AS cantidad_vendida,
        ROUND(SUM(ABS(m.cantidad) * l.costo_compra), 2) AS costo_total,
        ROUND(SUM(ABS(m.cantidad) * p.precio_venta_base), 2) AS ingreso_total,
        ROUND(SUM(ABS(m.cantidad) * (p.precio_venta_base - l.costo_compra)), 2) AS ganancia_total,
        ROUND(AVG((p.precio_venta_base - l.costo_compra) / l.costo_compra * 100), 2) AS margen_promedio
    FROM movimientos m JOIN lotes l ON m.lote_id = l.id JOIN productos p ON l.producto_id = p.id
    WHERE m.tipo_movimiento = 'SALIDA'
      AND (p_producto_id IS NULL OR p.id = p_producto_id)
      AND (p_fecha_desde IS NULL OR DATE(m.fecha_movimiento) >= p_fecha_desde)
      AND (p_fecha_hasta IS NULL OR DATE(m.fecha_movimiento) <= p_fecha_hasta)
    GROUP BY p.id, p.nombre_comercial ORDER BY ganancia_total DESC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `v_control_precios`
--

/*!50001 DROP VIEW IF EXISTS `v_control_precios`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`avnadmin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_control_precios` AS select `p`.`id` AS `id`,`p`.`nombre_comercial` AS `nombre_comercial`,`p`.`precio_venta_base` AS `precio_actual`,round(ifnull((select (sum((`l2`.`costo_compra` * `l2`.`cantidad_actual`)) / sum(`l2`.`cantidad_actual`)) from `lotes` `l2` where ((`l2`.`producto_id` = `p`.`id`) and (`l2`.`cantidad_actual` > 0))),`p`.`precio_compra_referencia`),2) AS `costo_promedio`,`p`.`margen_minimo_porcentaje` AS `margen_minimo_porcentaje` from `productos` `p` where (`p`.`estado` = 'ACTIVO') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_historial_precios`
--

/*!50001 DROP VIEW IF EXISTS `v_historial_precios`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`avnadmin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_historial_precios` AS select `h`.`id` AS `id`,`p`.`nombre_comercial` AS `nombre_comercial`,`h`.`precio_anterior` AS `precio_anterior`,`h`.`precio_nuevo` AS `precio_nuevo`,`h`.`usuario_responsable` AS `usuario_responsable`,`h`.`fecha_cambio` AS `fecha_cambio` from (`historial_precios` `h` join `productos` `p` on((`h`.`producto_id` = `p`.`id`))) order by `h`.`fecha_cambio` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_movimientos_detalle`
--

/*!50001 DROP VIEW IF EXISTS `v_movimientos_detalle`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`avnadmin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_movimientos_detalle` AS select `m`.`id` AS `id`,`m`.`fecha_movimiento` AS `fecha_movimiento`,`m`.`tipo_movimiento` AS `tipo_movimiento`,`m`.`cantidad` AS `cantidad`,`p`.`nombre_comercial` AS `nombre_comercial`,`lt`.`numero_lote` AS `numero_lote`,`m`.`observaciones` AS `observaciones` from ((`movimientos` `m` join `lotes` `lt` on((`m`.`lote_id` = `lt`.`id`))) join `productos` `p` on((`lt`.`producto_id` = `p`.`id`))) order by `m`.`fecha_movimiento` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_productos_detalle`
--

/*!50001 DROP VIEW IF EXISTS `v_productos_detalle`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`avnadmin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_productos_detalle` AS select `p`.`id` AS `id`,`p`.`codigo_interno` AS `codigo_interno`,`p`.`nombre_comercial` AS `nombre_comercial`,`pa`.`nombre` AS `principio_activo`,`l`.`nombre` AS `laboratorio`,ifnull(sum(`lt`.`cantidad_actual`),0) AS `stock_total`,min(`lt`.`fecha_vencimiento`) AS `proximo_vencimiento`,`p`.`precio_venta_base` AS `precio_venta_base` from (((`productos` `p` left join `lotes` `lt` on(((`p`.`id` = `lt`.`producto_id`) and (`lt`.`cantidad_actual` > 0)))) left join `laboratorios` `l` on((`p`.`laboratorio_id` = `l`.`id`))) left join `principios_activos` `pa` on((`p`.`principio_activo_id` = `pa`.`id`))) group by `p`.`id`,`l`.`nombre`,`pa`.`nombre` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_productos_proximos_vencer`
--

/*!50001 DROP VIEW IF EXISTS `v_productos_proximos_vencer`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`avnadmin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_productos_proximos_vencer` AS select `p`.`id` AS `producto_id`,`p`.`nombre_comercial` AS `nombre_comercial`,`lt`.`numero_lote` AS `numero_lote`,`lt`.`fecha_vencimiento` AS `fecha_vencimiento`,`lt`.`cantidad_actual` AS `cantidad_actual`,(to_days(`lt`.`fecha_vencimiento`) - to_days(curdate())) AS `dias_para_vencer`,(case when (`lt`.`fecha_vencimiento` < curdate()) then 'VENCIDO' when ((to_days(`lt`.`fecha_vencimiento`) - to_days(curdate())) <= 90) then 'PROXIMO' else 'OK' end) AS `estado_vencimiento` from (`lotes` `lt` join `productos` `p` on((`lt`.`producto_id` = `p`.`id`))) where ((`lt`.`cantidad_actual` > 0) and (`p`.`estado` = 'ACTIVO')) order by `lt`.`fecha_vencimiento` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_semaforo_vencimientos`
--

/*!50001 DROP VIEW IF EXISTS `v_semaforo_vencimientos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`avnadmin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_semaforo_vencimientos` AS select `lt`.`numero_lote` AS `numero_lote`,`p`.`id` AS `producto_id`,`p`.`codigo_interno` AS `codigo_interno`,`p`.`nombre_comercial` AS `nombre_comercial`,`l`.`nombre` AS `laboratorio`,`lt`.`fecha_vencimiento` AS `fecha_vencimiento`,`lt`.`cantidad_actual` AS `cantidad_actual`,(to_days(`lt`.`fecha_vencimiento`) - to_days(curdate())) AS `dias_restantes`,(case when ((to_days(`lt`.`fecha_vencimiento`) - to_days(curdate())) <= 0) then 'ROJO' when ((to_days(`lt`.`fecha_vencimiento`) - to_days(curdate())) <= 90) then 'AMARILLO' else 'VERDE' end) AS `color_alerta`,(case when ((to_days(`lt`.`fecha_vencimiento`) - to_days(curdate())) <= 0) then 'Retirar inmediata' when ((to_days(`lt`.`fecha_vencimiento`) - to_days(curdate())) <= 90) then 'Impulsar salida' else 'Normal' end) AS `accion_sugerida` from ((`lotes` `lt` join `productos` `p` on((`lt`.`producto_id` = `p`.`id`))) join `laboratorios` `l` on((`p`.`laboratorio_id` = `l`.`id`))) where (`lt`.`cantidad_actual` > 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_stock_productos`
--

/*!50001 DROP VIEW IF EXISTS `v_stock_productos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`avnadmin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_stock_productos` AS select `p`.`id` AS `producto_id`,`p`.`codigo_interno` AS `codigo_interno`,`p`.`codigo_barras` AS `codigo_barras`,`p`.`nombre_comercial` AS `nombre_comercial`,`p`.`concentracion` AS `concentracion`,`p`.`presentacion` AS `presentacion`,`p`.`precio_venta_base` AS `precio_venta_base`,`p`.`precio_venta_total` AS `precio_venta_total`,`p`.`precio_venta_unidad` AS `precio_venta_unidad`,`p`.`precio_venta_blister` AS `precio_venta_blister`,`p`.`iva_porcentaje` AS `iva_porcentaje`,`p`.`stock_minimo` AS `stock_minimo`,`p`.`es_fraccionable` AS `es_fraccionable`,`p`.`unidades_por_caja` AS `unidades_por_caja`,`p`.`refrigerado` AS `refrigerado`,`p`.`es_controlado` AS `es_controlado`,`p`.`imagen_url` AS `imagen_url`,`l`.`nombre` AS `laboratorio_nombre`,`c`.`nombre` AS `categoria_nombre`,`pa`.`nombre` AS `principio_activo_nombre`,coalesce(sum(`lt`.`cantidad_actual`),0) AS `stock_total`,min(`lt`.`fecha_vencimiento`) AS `proximo_vencimiento`,(case when (coalesce(sum(`lt`.`cantidad_actual`),0) = 0) then 'SIN_STOCK' when (coalesce(sum(`lt`.`cantidad_actual`),0) <= `p`.`stock_minimo`) then 'BAJO' else 'OPTIMO' end) AS `nivel_stock` from ((((`productos` `p` left join `laboratorios` `l` on((`p`.`laboratorio_id` = `l`.`id`))) left join `categorias` `c` on((`p`.`categoria_id` = `c`.`id`))) left join `principios_activos` `pa` on((`p`.`principio_activo_id` = `pa`.`id`))) left join `lotes` `lt` on(((`p`.`id` = `lt`.`producto_id`) and (`lt`.`cantidad_actual` > 0)))) group by `p`.`id`,`p`.`codigo_interno`,`p`.`codigo_barras`,`p`.`nombre_comercial`,`p`.`concentracion`,`p`.`presentacion`,`p`.`precio_venta_base`,`p`.`precio_venta_total`,`p`.`precio_venta_unidad`,`p`.`precio_venta_blister`,`p`.`iva_porcentaje`,`p`.`stock_minimo`,`p`.`es_fraccionable`,`p`.`unidades_por_caja`,`p`.`refrigerado`,`p`.`es_controlado`,`p`.`imagen_url`,`l`.`nombre`,`c`.`nombre`,`pa`.`nombre` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-20 13:49:59
