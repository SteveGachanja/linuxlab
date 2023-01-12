# Not required 

import sqlite3

connection = sqlite3.connect('data.db')

cursor = connection.cursor()

create_table01 = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT NOT NULL UNIQUE, password text)"
cursor.execute(create_table01)

create_table02 = "CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, name text, price real)"
cursor.execute(create_table02)

connection.commit()
connection.close()