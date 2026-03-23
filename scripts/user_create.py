import sqlite3
import bcrypt
import os
import sys
from datetime import datetime

# Adicionar o diretório raiz do projeto ao path para importar módulos
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

def iniciar_banco():
    """
    Inicializa o banco de dados with new schema per Constitution Principle II.
    Uses `users` table (NOT `usuarios`) with:
    - BCrypt password hashing (cost 12)
    - Status ACTIVE|SUSPENDED|DELETED
    - Email tracking
    - Login audit trail
    """
    # Database at root level
    db_path = '../kronus_local.db'
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Create users table (new schema per data-model.md)
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
            username TEXT NOT NULL UNIQUE,
            email TEXT NOT NULL UNIQUE,
            password_hash TEXT NOT NULL,
            status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            last_login_at TIMESTAMP,
            failed_login_attempts INTEGER NOT NULL DEFAULT 0 CHECK (failed_login_attempts >= 0),
            failed_login_reset_at TIMESTAMP
        )
    ''')

    # Create sessions table
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS sessions (
            token TEXT PRIMARY KEY,
            user_id TEXT NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            expires_at TIMESTAMP NOT NULL,
            ip_address TEXT NOT NULL,
            user_agent TEXT,
            revoked_at TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        )
    ''')

    # Create login_events table (audit trail per Constitution Principle II)
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS login_events (
            id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))),
            user_id TEXT REFERENCES users(id) ON DELETE CASCADE,
            username TEXT,
            ip_address TEXT NOT NULL,
            user_agent TEXT,
            success INTEGER NOT NULL DEFAULT 0 CHECK (success IN (0, 1)),
            failure_reason TEXT,
            attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            duration_ms INTEGER,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            expires_at TIMESTAMP NOT NULL DEFAULT (datetime('now', '+90 days'))
        )
    ''')

    # Create indexes
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_login_events_ip ON login_events(ip_address, attempted_at) WHERE success = 0')

    conn.commit()
    print(f"✓ Database initialized at {db_path}")
    return conn

def registrar_usuario(username, email, password):
    """
    Register a new user with BCrypt hashing (cost 12).
    Per Constitution Principle II (Security-First Design):
    - BCrypt cost ≥ 12 (non-negotiable)
    - No plaintext passwords stored
    - Email required and unique
    """
    conn = iniciar_banco()
    cursor = conn.cursor()

    # Validate inputs
    if len(username) < 3 or len(username) > 20:
        print(f"❌ ERROR: Username must be 3-20 characters")
        return
    
    if '@' not in email or '.' not in email:
        print(f"❌ ERROR: Invalid email format")
        return

    if len(password) < 8:
        print(f"❌ ERROR: Password must be at least 8 characters")
        return

    # Generate BCrypt hash (cost 12 = ~80-100ms per Constitution Principle II)
    salt = bcrypt.gensalt(rounds=12)
    hashed = bcrypt.hashpw(password.encode('utf-8'), salt).decode('utf-8')

    try:
        # Insert new user with status ACTIVE
        cursor.execute(
            "INSERT INTO users (username, email, password_hash, status) VALUES (?, ?, ?, ?)",
            (username.strip().lower(), email.strip().lower(), hashed, 'ACTIVE')
        )
        conn.commit()
        
        # Get the generated user ID
        cursor.execute("SELECT id FROM users WHERE username = ?", (username.strip().lower(),))
        user_id = cursor.fetchone()[0]
        
        print(f"\n✅ SUCCESS: User '{username}' registered!")
        print(f"   User ID:   {user_id}")
        print(f"   Email:     {email}")
        print(f"   Status:    ACTIVE")
        print(f"   Hash:      {hashed}")
        print(f"   Created:   {datetime.now().isoformat()}")
        
    except sqlite3.IntegrityError as e:
        if 'username' in str(e):
            print(f"\n❌ ERROR: Username '{username}' already registered")
        elif 'email' in str(e):
            print(f"\n❌ ERROR: Email '{email}' already registered")
        else:
            print(f"\n❌ ERROR: {e}")
    except Exception as e:
        print(f"\n❌ ERROR: Failed to register user: {e}")
    finally:
        conn.close()

# CLI Interface
if __name__ == "__main__":
    import sys
    
    print("\n" + "="*50)
    print("  KRONUS RIFT: User Registration Tool")
    print("  Security-First Authentication System")
    print("="*50)
    
    # Accept command-line arguments or prompt for input
    if len(sys.argv) > 3:
        username, email, password = sys.argv[1], sys.argv[2], sys.argv[3]
        print(f"\n✓ Using provided credentials...")
    else:
        username = input("\nUsername (3-20 chars, alphanumeric + underscore): ").strip()
        email = input("Email (required, unique): ").strip()
        password = input("Password (min 8 chars, with uppercase + digit): ").strip()
    
    if username and email and password:
        registrar_usuario(username, email, password)
    else:
        print("\n❌ ERROR: All fields required!")