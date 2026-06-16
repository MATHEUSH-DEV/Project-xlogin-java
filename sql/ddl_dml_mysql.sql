-- DDL + DML para MySQL Workbench
-- Projeto: XLogin (Artefato III)
-- Observação: execute em MySQL 5.7+ / 8.0+

DROP DATABASE IF EXISTS `xlogin`;
CREATE DATABASE `xlogin` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `xlogin`;

-- Tabela: users
CREATE TABLE `users` (
  `id` CHAR(36) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `status` ENUM('ACTIVE','SUSPENDED','DELETED') NOT NULL DEFAULT 'ACTIVE',
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  `last_login_at` DATETIME NULL,
  `failed_login_attempts` INT NOT NULL DEFAULT 0,
  `failed_login_reset_at` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_users_username` (`username`),
  UNIQUE KEY `uq_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela: sessions (token como PK)
CREATE TABLE `sessions` (
  `token` VARCHAR(512) NOT NULL,
  `user_id` CHAR(36) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` DATETIME NULL,
  `ip_address` VARCHAR(45) NULL,
  `user_agent` VARCHAR(512) NULL,
  `revoked_at` DATETIME NULL,
  PRIMARY KEY (`token`),
  KEY `idx_sessions_user_id` (`user_id`),
  CONSTRAINT `fk_sessions_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela: login_events
CREATE TABLE `login_events` (
  `id` CHAR(36) NOT NULL,
  `user_id` CHAR(36) NULL,
  `username` VARCHAR(50) NULL,
  `ip_address` VARCHAR(45) NULL,
  `user_agent` VARCHAR(512) NULL,
  `success` TINYINT(1) NOT NULL DEFAULT 0,
  `failure_reason` VARCHAR(255) NULL,
  `attempted_at` DATETIME NULL,
  `duration_ms` INT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` DATETIME NULL,
  PRIMARY KEY (`id`),
  KEY `idx_login_events_user` (`user_id`),
  CONSTRAINT `fk_login_events_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela: characters
CREATE TABLE `characters` (
  `id` CHAR(36) NOT NULL,
  `user_id` CHAR(36) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `race` VARCHAR(50) NULL,
  `class` VARCHAR(50) NULL,
  `created_at` BIGINT NOT NULL,
  `level` INT NOT NULL DEFAULT 1,
  `strength` INT NOT NULL,
  `agility` INT NOT NULL,
  `intelligence` INT NOT NULL,
  `experience` BIGINT NOT NULL,
  `health` INT NOT NULL,
  `mana` INT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_characters_user` (`user_id`),
  CONSTRAINT `fk_characters_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela: abilities
CREATE TABLE `abilities` (
  `id` CHAR(36) NOT NULL,
  `character_id` CHAR(36) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `description` TEXT NULL,
  `mana_cost` INT NOT NULL,
  `damage_multiplier` DOUBLE NOT NULL,
  `cooldown_ms` BIGINT NOT NULL,
  `last_used_time` BIGINT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_abilities_character` (`character_id`),
  CONSTRAINT `fk_abilities_character` FOREIGN KEY (`character_id`) REFERENCES `characters`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- INSERÇÕES EXEMPLO (pelo menos 2 em cada tabela não-associativa)
-- USERS
INSERT INTO `users` (`id`,`username`,`email`,`password_hash`,`status`,`created_at`,`updated_at`,`last_login_at`,`failed_login_attempts`) VALUES
('11111111-1111-1111-1111-111111111111','alice','alice@example.com','$2b$12$abcdefghijklmnopqrstuv', 'ACTIVE', NOW(), NOW(), NOW(), 0),
('22222222-2222-2222-2222-222222222222','bob','bob@example.com','$2b$12$qrstuvwxyzabcdefghij', 'ACTIVE', NOW(), NOW(), NOW(), 1);

-- SESSIONS
INSERT INTO `sessions` (`token`,`user_id`,`created_at`,`expires_at`,`ip_address`,`user_agent`) VALUES
('token-alice-0001','11111111-1111-1111-1111-111111111111', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), '192.168.0.10', 'Mozilla/5.0 (example)'),
('token-bob-0001','22222222-2222-2222-2222-222222222222', NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), '192.168.0.11', 'curl/7.68.0');

-- LOGIN_EVENTS
INSERT INTO `login_events` (`id`,`user_id`,`username`,`ip_address`,`user_agent`,`success`,`failure_reason`,`attempted_at`,`duration_ms`,`expires_at`) VALUES
('le-0001','11111111-1111-1111-1111-111111111111','alice','192.168.0.10','Mozilla/5.0',1,NULL,NOW(),120,DATE_ADD(NOW(), INTERVAL 90 DAY)),
('le-0002',NULL,'unknown_user','203.0.113.7','curl/7.68.0',0,'INVALID_CREDENTIALS',NOW(),80,DATE_ADD(NOW(), INTERVAL 90 DAY));

-- CHARACTERS
INSERT INTO `characters` (`id`,`user_id`,`name`,`race`,`class`,`created_at`,`level`,`strength`,`agility`,`intelligence`,`experience`,`health`,`mana`) VALUES
('c-0001','11111111-1111-1111-1111-111111111111','Aragorn','Humano','Guerreiro',UNIX_TIMESTAMP()*1000,5,25,15,8,4500,150,70),
('c-0002','22222222-2222-2222-2222-222222222222','Luna','Elfo','Bruxo',UNIX_TIMESTAMP()*1000,3,16,14,20,1800,132,90);

-- ABILITIES
INSERT INTO `abilities` (`id`,`character_id`,`name`,`description`,`mana_cost`,`damage_multiplier`,`cooldown_ms`,`last_used_time`) VALUES
('a-0001','c-0001','Golpe Poderoso','Ataque devastador que causa 150% de dano',20,1.5,3000,NULL),
('a-0002','c-0001','Furia Berserker','Ataque selvagem que causa 200% de dano',50,2.0,10000,NULL),
('a-0003','c-0002','Bola de Fogo','Fogo mágico que causa 170% de dano',25,1.7,2500,NULL);

-- Nota: abilities tem 3 inserts (aceitável). Se desejar, adiciono mais.

-- Final
SELECT 'Script executado com sucesso' as status;
