#!/usr/bin/env python3

import datetime
from flask import (
    Flask,
    jsonify
)


APP = Flask(__name__)


@APP.route('/')
def main():
    return jsonify(**{'time': str(datetime.datetime.now().isoformat())})


@APP.route('/healthz')
def health():
    return jsonify(**{'status': 'ok', 'date': str(datetime.datetime.now().isoformat())})


if __name__ == "__main__":
    APP.run(host='192.168.0.100', port=5000)