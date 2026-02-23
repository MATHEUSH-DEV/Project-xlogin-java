import sqlite3
import bcrypt

def iniciar_banco():
    # Conecta ao banco de dados kronus_local.db
    conn = sqlite3.connect('kronus_local.db')
    cursor = conn.cursor()

    # Garante que a tabela existe com os campos corretos [cite: 2026-02-14]
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            email TEXT, 
            password TEXT NOT NULL,
            status INTEGER DEFAULT 0
        )
    ''')
    conn.commit()
    return conn

def registrar_usuario(username, password):
    conn = iniciar_banco()
    cursor = conn.cursor()
    
    # --- CRIPTOGRAFIA (BCrypt) ---
    # Gera o Hash no formato que o Java (jBCrypt) entende ($2b$...) [cite: 2026-02-14]
    salt = bcrypt.gensalt(rounds=12, prefix=b"2a") # Força o prefixo que o Java gosta
    hashed = bcrypt.hashpw(password.encode('utf-8'), salt).decode('utf-8')
    
    try:
        # Status 0 = OFFLINE conforme seu Enum Java [cite: 2026-02-14]
        cursor.execute("INSERT INTO usuarios (username, password, status) VALUES (?, ?, ?)", 
                       (username.strip(), hashed, 0))
        conn.commit()
        print(f"\n[SUCCESS] Usuário '{username}' registrado com Hash!")
        print(f"[HASH]: {hashed}")
    except sqlite3.IntegrityError:
        print(f"\n[ERROR] O usuário '{username}' já existe.")
    except Exception as e:
        print(f"\n[ERROR] Falha ao registrar: {e}")
    finally:
        conn.close()

# Interface de uso
print("=== Kronus Rift: Gerador de Usuários ===")
user = input("Novo username: ")
pwd = input("Nova senha: ")

if user and pwd:
    registrar_usuario(user, pwd)
else:
    print("[ERROR] Preencha todos os campos.")