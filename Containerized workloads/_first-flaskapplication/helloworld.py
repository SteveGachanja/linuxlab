from flask import Flask

app = Flask(__name__)

@app.route('/')   # http//www.google.co.ke/

def home():

    return "Hello Gachanja, welcome to your first flask API program"

app.run(port=5000)