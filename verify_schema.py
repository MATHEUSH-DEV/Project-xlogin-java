#!/usr/bin/env python
import sqlite3
import os

db_path = 'kronus_local.db'
if os.path.exists(db_path):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    print("\n=== DATABASE SCHEMA VERIFICATION ===\n")
    
    # Get all tables
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
    tables = cursor.fetchall()
    
    if tables:
        print(f"✓ Found {len(tables)} tables:\n")
        for table in tables:
            table_name = table[0]
            print(f"  TABLE: {table_name}")
            
            cursor.execute(f"PRAGMA table_info({table_name})")
            columns = cursor.fetchall()
            
            for col in columns:
                col_name, col_type, not_null, _, _, _ = col
                nullable = "NULL" if not_null == 0 else "NOT NULL"
                print(f"    - {col_name}: {col_type} ({nullable})")
            
            # Get indexes
            cursor.execute(f"SELECT name FROM sqlite_master WHERE type='index' AND tbl_name=?", (table_name,))
            indexes = cursor.fetchall()
            if indexes:
                print(f"    Indexes:")
                for idx in indexes:
                    print(f"      - {idx[0]}")
            print()
    else:
        print("❌ No tables found!")
    
    # Get user count
    try:
        cursor.execute("SELECT COUNT(*) FROM users")
        user_count = cursor.fetchone()[0]
        print(f"✓ Users in database: {user_count}")
    except:
        pass
    
    conn.close()
else:
    print(f"❌ Database file not found: {db_path}")
