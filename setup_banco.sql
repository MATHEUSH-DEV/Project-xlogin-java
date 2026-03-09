-- ============================================================
-- 🗄️ SCRIPT DE SETUP - KRONUS RIFT MMORPG
-- ============================================================
-- Executa tudo isso no MySQL Workbench

-- 1️⃣ Criar banco de dados
CREATE DATABASE IF NOT EXISTS kronus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2️⃣ Criar usuário
CREATE USER IF NOT EXISTS 'kronus_user'@'localhost' IDENTIFIED BY 'senha_forte';
GRANT ALL PRIVILEGES ON kronus.* TO 'kronus_user'@'localhost';
FLUSH PRIVILEGES;

-- 3️⃣ Usar o banco
USE kronus;

-- 4️⃣ Criar tabela de usuários
CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5️⃣ Criar tabela de personagens
CREATE TABLE IF NOT EXISTS characters (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  race VARCHAR(50) NOT NULL,
  clazz VARCHAR(50) NOT NULL,
  level INT DEFAULT 1,
  strength INT DEFAULT 18,
  agility INT DEFAULT 12,
  intelligence INT DEFAULT 10,
  health INT DEFAULT 100,
  mana INT DEFAULT 50,
  experience BIGINT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_user_name (user_id, name),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6️⃣ Inserir usuário de teste (se não existir)
INSERT INTO usuarios (username, password, email) 
VALUES ('teteu', 'senha123', 'teteu@example.com') 
ON DUPLICATE KEY UPDATE id=id;

-- 7️⃣ Verificar dados
SELECT '✓ Banco kronus criado!' as status;
SELECT '✓ Usuário kronus_user criado!' as status;
SELECT '✓ Tabela characters criada!' as status;
SELECT '✓ Tabela usuarios criada!' as status;

-- 8️⃣ Listar dados
SELECT '--- USUÁRIOS NO BANCO ---' as info;
SELECT * FROM usuarios;

SELECT '--- PERSONAGENS NO BANCO ---' as info;
SELECT * FROM characters;

-- 9️⃣ Info de conexão
SELECT '✓ ============================================' as setup_info
UNION ALL SELECT '✓ INFORMAÇÕES DE CONEXÃO:'
UNION ALL SELECT '✓ Host: localhost:3306'
UNION ALL SELECT '✓ Banco: kronus'
UNION ALL SELECT '✓ Usuário: kronus_user'
UNION ALL SELECT '✓ Senha: senha_forte'
UNION ALL SELECT '✓ JDBC URL: jdbc:mysql://localhost:3306/kronus'
UNION ALL SELECT '✓ ============================================';
