import sqlite3

def iniciar_banco():
    # Conecta ao arquivo de banco de dados (ou cria se não existir)
    conn = sqlite3.connect('kronus_local.db')
    cursor = conn.cursor()

    # Criando a tabela de usuários
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            password TEXT NOT NULL,
            status TEXT DEFAULT 'OFFLINE'
        )
    ''')
    conn.commit()
    return conn

def registrar_usuario(username, password, status='OFFLINE'):
    conn = iniciar_banco()
    cursor = conn.cursor()
    try:
        cursor.execute("INSERT INTO usuarios (username, password, status) VALUES (?, ?, ?)", (username, password, status))
        conn.commit()
        print(f"[SUCCESS] Usuário {username} registrado localmente!")
    except Exception as e:
        print(f"[ERROR] Falha ao registrar: {e}")
    finally:
        conn.close()

# Executando
user = input("Novo username: ")
pwd = input("Nova senha: ")
registrar_usuario(user, pwd)