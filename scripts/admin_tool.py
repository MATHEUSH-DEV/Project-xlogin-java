#!/usr/bin/env python3
"""
Admin tool for Kronus local SQLite database.

Features:
- list users
- add user (hashes password with bcrypt)
- delete user
- export users to CSV

Dependencies:
    pip install bcrypt

Usage examples:
    python scripts/admin_tool.py list
    python scripts/admin_tool.py add --username alice --email a@b.com --password secret
    python scripts/admin_tool.py delete --username alice
    python scripts/admin_tool.py export --out users.csv

This script assumes the SQLite DB file `kronus_local.db` exists at project root
and the users table is named `usuarios` with columns: id, username, password, email, status
"""

import argparse
import csv
import sqlite3
import sys
from pathlib import Path

try:
    import bcrypt
except Exception:
    print("Missing dependency 'bcrypt'. Install with: pip install bcrypt")
    sys.exit(1)

DB_DEFAULT = Path(__file__).resolve().parents[1] / "kronus_local.db"
STATUS_MAP = {0: "OFFLINE", 1: "ONLINE", 2: "AWAY", 3: "BANNED"}
STATUS_REVERSE = {v: k for k, v in STATUS_MAP.items()}


def get_conn(db_path):
    return sqlite3.connect(str(db_path))


def list_users(db_path):
    with get_conn(db_path) as conn:
        cur = conn.cursor()
        cur.execute("SELECT id, username, email, status FROM usuarios")
        rows = cur.fetchall()
        print(f"{len(rows)} user(s) found:\n")
        for r in rows:
            sid, username, email, status = r
            print(f"id={sid}\tusername={username}\temail={email}\tstatus={STATUS_MAP.get(status, status)}")


def add_user(db_path, username, email, password, status_name="OFFLINE"):
    if not username or not password or not email:
        raise ValueError("username, email and password are required")

    hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
    status = STATUS_REVERSE.get(status_name.upper(), 0)

    with get_conn(db_path) as conn:
        cur = conn.cursor()
        try:
            cur.execute("INSERT INTO usuarios (username, password, email, status) VALUES (?, ?, ?, ?)",
                        (username, hashed.decode('utf-8'), email, status))
            conn.commit()
            print(f"User '{username}' created.")
        except sqlite3.IntegrityError as e:
            print(f"Failed to create user: {e}")


def delete_user(db_path, username=None, user_id=None):
    if username is None and user_id is None:
        raise ValueError("Provide --username or --id to delete")
    with get_conn(db_path) as conn:
        cur = conn.cursor()
        if user_id is not None:
            cur.execute("DELETE FROM usuarios WHERE id = ?", (user_id,))
        else:
            cur.execute("DELETE FROM usuarios WHERE username = ?", (username,))
        conn.commit()
        print(f"Deleted {cur.rowcount} row(s)")


def export_csv(db_path, out_path):
    with get_conn(db_path) as conn:
        cur = conn.cursor()
        cur.execute("SELECT id, username, email, status FROM usuarios")
        rows = cur.fetchall()
        with open(out_path, 'w', newline='', encoding='utf-8') as f:
            w = csv.writer(f)
            w.writerow(["id", "username", "email", "status"])
            for r in rows:
                sid, username, email, status = r
                w.writerow([sid, username, email, STATUS_MAP.get(status, status)])
    print(f"Exported {len(rows)} users to {out_path}")


def parse_args():
    p = argparse.ArgumentParser(description="Kronus DB admin tool")
    p.add_argument("--db", default=str(DB_DEFAULT), help="Path to kronus_local.db")
    sub = p.add_subparsers(dest='cmd')

    sub.add_parser('list', help='List users')

    add = sub.add_parser('add', help='Add user')
    add.add_argument('--username', required=True)
    add.add_argument('--email', required=True)
    add.add_argument('--password', required=True)
    add.add_argument('--status', default='OFFLINE')

    delete = sub.add_parser('delete', help='Delete user')
    delete.add_argument('--username')
    delete.add_argument('--id', type=int)

    exp = sub.add_parser('export', help='Export users to CSV')
    exp.add_argument('--out', required=True)

    return p.parse_args()


def main():
    args = parse_args()
    cmd = args.cmd
    db_path = args.db

    if cmd == 'list':
        list_users(db_path)
    elif cmd == 'add':
        add_user(db_path, args.username, args.email, args.password, args.status)
    elif cmd == 'delete':
        delete_user(db_path, username=args.username, user_id=args.id)
    elif cmd == 'export':
        export_csv(db_path, args.out)
    else:
        print('No command provided. Use --help for usage.')


if __name__ == '__main__':
    main()
