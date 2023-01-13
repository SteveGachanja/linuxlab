#CRUD Application - CREATE READ UPDATE DELETE

#In a User/Browser perspective
#POST - used to send us data
#GET - used to receive data

#But in Server Perspective, because here we are not a browser, we are a server
#POST - used to receive data from the clients side
#GET - used to send data back to client

#JWT - Jason Web Token , used in obfuscation of data

from flask import Flask
from flask_restful import Api
from flask_jwt import JWT

from security import authenticate, identity
from resources.user import UserRegister
from resources.item import Item, All_items
from resources.store import Store, StoreList
from db import db

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
#app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+mysqlconnector://api:api@172.30.26.38/flask_apidb'
#app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+mysqlconnector://api:api@172.30.26.38/flask_apidb'
#app.config['SQLALCHEMY_DATABASE_URI'] = 'oracle+cx_oracle://OMNIANALYTICS:L_d_2iFOZ6k##E6Xrde6TBE5xy@172.16.17.39:1561/OATS'

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.secret_key = 'docker'
api = Api(app)

@app.before_first_request
def create_tables():
    db.create_all()

jwt = JWT(app, authenticate, identity)   # /auth

api.add_resource(Store, '/store/<string:name>')
api.add_resource(StoreList, '/stores')

api.add_resource(Item, '/item/<string:name>')
api.add_resource(All_items, '/items')
api.add_resource(UserRegister, '/register')

if __name__ == '__main__':
    #from db import db
    db.init_app(app)
    app.run(host='0.0.0.0', port=5000, debug=True)