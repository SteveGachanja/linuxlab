from db import db

class ItemModel(db.Model):

    __tablename__ = 'items'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(80))
    price = db.Column(db.Float(precision=2))

    store_id = db.Column(db.Integer, db.ForeignKey('stores.id'))
    store = db.relationship('StoreModel')

    def __init__(self, name, price, store_id):
        self.name = name
        self.price = price
        self.store_id = store_id

    def json(self):
        return {'name': self.name, 'price':self.price}
    
    @classmethod
    def find_by_name(cls, name):
        return cls.query.filter_by(name=name).first() #SELECT * FROM items WHERE name=name
        
        #connection = sqlite3.connect('data.db')
        #cursor = connection.cursor()
        #select_query = "SELECT * FROM items WHERE name=?"
        #result = cursor.execute(select_query, (name,))
        #row = result.fetchone()
        #connection.close()

        #if row:
        #    return cls(*row)

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

        #connection = sqlite3.connect('data.db')
        #cursor = connection.cursor()
        #insert_query = "INSERT INTO items VALUES (?, ?)"
        #cursor.execute(insert_query, (self.name, self.price))    
        #connection.commit()
        #connection.close()

    def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()
    
    #def update(self):
        #connection = sqlite3.connect('data.db')
        #cursor = connection.cursor()
        #update_query = "UPDATE items SET price=? WHERE name=?"
        #cursor.execute(update_query, (self.price, self.name))            
        #connection.commit()
        #connection.close()