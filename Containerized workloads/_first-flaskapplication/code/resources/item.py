from flask_restful import Resource, reqparse
from flask_jwt import jwt_required
from models.item_model import ItemModel

class Item(Resource):
      parser = reqparse.RequestParser()
      
      parser.add_argument('price',
       type=float,
       required=True,
       help="Warning, Price field is Blank or has an Error!"
      )

      parser.add_argument('store_id',
       type=int,
       required=True,
       help="Every item needs a store_id."
      )

      @jwt_required()
      def get(self, name):
            item = ItemModel.find_by_name(name)
            if item:
                  return item.json()
            return {'message': 'Item not found in the database'}, 404

      def post(self, name):
            #if next(filter(lambda x: x['name'] == name, items),None):
            if ItemModel.find_by_name(name):
                return {'Error message': "An item with name '{}' already exists.".format(name)}, 400 #400 is bad request

            data = Item.parser.parse_args()
            item = ItemModel(name, data['price'], data['store_id'])
            #item = ItemModel(name, **data)
            
            try:
                  item.save_to_db()
            except:
                  return {"message":"An error occurred inserting the item"}, 500 # Internal server error
            return item.json(), 201    #means created item 


      def delete(self, name):
            item = ItemModel.find_by_name(name)
            if item:
                  item.delete_from_db()
                  return {'message': 'Item deleted.'}
            return {'message': 'Item not found.'}, 404

      def put(self, name):
            data = Item.parser.parse_args()
            item = ItemModel.find_by_name(name)
                        
            if item is None:
                  try:
                        item = ItemModel(name, data['price'], data['store_id'])
                  except:
                        return {"message": "An error occured inserting the item"}, 500
            else:
                  try:
                        item.price = data['price'] 
                  except:
                        return {"message": "An error occured updating the item"}, 500
            
            item.save_to_db()
            return item.json()


class All_items(Resource):
      @jwt_required()
      def get(self):
            return {'items': list(map(lambda x: x.json(), ItemModel.query.all()))}
            #return {'items': [item.json() for item in ItemModel.query.all()]}
            
            #connection = sqlite3.connect('data.db')
            #cursor = connection.cursor()
            #select_query = "SELECT * FROM items"
            #result = cursor.execute(select_query)
            #items = []

            #for row in result:
            #      items.append({'name': row[0], 'price': row[1]})

            #connection.close() 
            #return {'items': items}