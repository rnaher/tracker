# SERVER.py


from flask import Flask
from flask import jsonify
from flask import request
from flask_pymongo import PyMongo


app = Flask(__name__)

app.config['MONGO_DBNAME'] = 'track_package'
app.config['MONGO_URI'] = 'mongodb://localhost:27017/track_package'

mongo = PyMongo(app)


# Registration Request
@app.route('/app/registration', methods=['POST'])
def addUser():
	print("[DEBUG] Registration Request :\n",request.json)
	try:
		users = mongo.db.users
		# customer_id = customers.insert({'first_name': first_name, 'last_name': last_name})
		user_id = users.insert(request.json)
		# new_user = users.find_one({'_id': user_id })
		output = {'error_code':'000','message' : 'New user created successfully', 'user_id' : str(user_id)}
		print("[INFO] Registration successfully...!!!")
	
	except:
		print("[ERROR] Registration Failed...!!!")
		output = {'error_code':'010','message' : 'New user creation failed'}

	return jsonify({'Response' : output})


if __name__ == '__main__':
	app.run(debug=True, port = 8080)
