# dummy_server.py

from flask import Flask
from flask import jsonify
from flask import request



app = Flask(__name__)


# Registration Request


@app.route('/app/users', methods=['GET'])
def all_users():
	output = {'error_code':'000','message' : 'New user created successfully', 'user_id' : str(12345)}
	return jsonify({'Response' : output})



if __name__ == '__main__':
	app.run(debug=True, port = 8080)
