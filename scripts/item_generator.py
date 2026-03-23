import random
import sqlite3
import os

def gerar_item_aleatorio():
    # Adicione novos tipos de armas aqui
    prefixos = ["Espada", "Escudo", "Adaga", "Cajado", "Arco", "Machado", "Lança", "Foice", "Martelo", "Clava", "Besta", "Chicote", "Glaive", "Tridente", "Katana", "Manopla", "Bumerangue", "Lâmina", "Cetro", "Arpão"]
    
    # Adicione novos elementos (isso dá personalidade ao item)
    elementos = ["de Fogo", "de Gelo", "do Trovão", "do Caos", "da Luz", "sombrio(a)", "Venoso(a)", "de Vento", "da Terra", "Ácido(a)", "de Água", "do Espírito", "de Sangue", "da Alma", "de Aço", "de Cristal", "de Sombra", "de Gelo", "de Fogo", "de Raio", "de Vento", "de Terra", "de Água", "do Caos", "da Luz", "sombrio(a)", "Venoso(a)", "do Espírito", "de Sangue", "da Alma", "de Aço", "de Cristal", "de Sombra"]
    

    
    # Ajuste as raridades e as probabilidades (a soma deve ser 100)
    raridades = {
        "Comum": 60,     
        "Raro": 25,
        "Épico": 10,      # Nova categoria!
        "Lendário": 5,
        "Mítico": 3,        # Ficou mais difícil de dropar
        "Exótico": 2        # Categoria super rara, quase impossível de dropar

    }
    
    nome = f"{random.choice(prefixos)} {random.choice(elementos)}"
    raridade = random.choices(list(raridades.keys()), weights=list(raridades.values()))[0]
    
    # Lógica de ataque baseada na raridade
    if raridade == "Comum":
        ataque = random.randint(10, 50)
    elif raridade == "Raro":
        ataque = random.randint(51, 120)
    elif raridade == "Épico":
        ataque = random.randint(121, 200)
    elif raridade == "Lendário":
        ataque = random.randint(201, 300)
    elif raridade == "Mítico":
        ataque = random.randint(301, 500)
    elif raridade == "Exótico":
        ataque = random.randint(501, 1000)

    return (nome, raridade, ataque)

def salvar_no_banco(item, player_id):
    """Conecta e insere o item no inventário do jogador."""
    # Banco está no nível acima da pasta scripts (project root)
    db_path = os.path.join(os.path.dirname(__file__), '..', 'kronus_local.db')
    db_path = os.path.normpath(db_path)
    
    try:
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        # 1. Ativa suporte a chaves estrangeiras (Foreign Keys) no SQLite
        cursor.execute("PRAGMA foreign_keys = ON;")


        # 2. Cria a tabela com a estrutura correta se não existir
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS inventory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                raridade TEXT NOT NULL,
                ataque INTEGER NOT NULL,
                player_id TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (player_id) REFERENCES users(id) ON DELETE CASCADE
    
            )
        ''')

        # 3. Insere o item dropado
        cursor.execute('''
            INSERT INTO inventory (nome, raridade, ataque, player_id)
            VALUES (?, ?, ?, ?)
        ''', (item[0], item[1], item[2], player_id))
        
        conn.commit()
        print(f"[+] SUCCESS: {item[0]} saved to Player {player_id}'s inventory!")
        
    except sqlite3.OperationalError as e:
        print(f"[!] DATABASE ERROR: {e}. (Verify if DBeaver has a lock on the file)")
    finally:
        if 'conn' in locals():
            conn.close()

# --- EXECUÇÃO ---
if __name__ == "__main__":
    import sys
    
    # Pode aceitar player_id via argumento ou usar padrão
    player_id = sys.argv[1] if len(sys.argv) > 1 else None
    
    if not player_id:
        # Tenta usar o primeiro usuário do banco
        db_path = os.path.join(os.path.dirname(__file__), '..', 'kronus_local.db')
        db_path = os.path.normpath(db_path)
        try:
            conn = sqlite3.connect(db_path)
            cursor = conn.cursor()
            cursor.execute("SELECT id FROM users LIMIT 1")
            result = cursor.fetchone()
            if result:
                player_id = result[0]
                print(f"[*] Using first user from database: {player_id}")
            else:
                print("[!] ERROR: No users found in database! Run user_create.py first.")
                exit(1)
            conn.close()
        except Exception as e:
            print(f"[!] ERROR: {e}")
            exit(1)
    
    # Simulação: O player dropou um item
    item_dropado = gerar_item_aleatorio()
    print(f"[*] Monster Dropped: {item_dropado[0]} [{item_dropado[1]}] - Atk: {item_dropado[2]}")
    
    # Salva para o usuario
    salvar_no_banco(item_dropado, player_id)