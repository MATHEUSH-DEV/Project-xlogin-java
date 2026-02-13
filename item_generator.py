import random
import sqlite3

def gerar_item_aleatorio():
    prefixos = ["Espada", "Escudo", "Adaga", "Cajado"]
    elementos = ["de Fogo", "de Gelo", "do Trovão", "do Caos"]
    raridades = {
        "Comum": 70,
        "Raro": 20,
        "Lendário": 10
    }
    
    nome = f"{random.choice(prefixos)} {random.choice(elementos)}"
    raridade = random.choices(list(raridades.keys()), weights=list(raridades.values()))[0]
    ataque = random.randint(10, 50) if raridade == "Comum" else random.randint(51, 200)

    return (nome, raridade, ataque)

# Simulando a criação de um "Drop" de monstro
item = gerar_item_aleatorio()
print(f"[*] O monstro dropou: {item[0]} [{item[1]}] - Atk: {item[2]}")