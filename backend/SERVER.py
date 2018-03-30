# SERVER.py


from flask import Flask
from flask import jsonify
from flask import request
from flask_pymongo import PyMongo
from flask_bcrypt import Bcrypt
from bson import ObjectId  

import pprint  

app = Flask(__name__)

app.config['MONGO_DBNAME'] = 'track_package'
app.config['MONGO_URI'] = 'mongodb://localhost:27017/track_package'

mongo = PyMongo(app)
bcrypt = Bcrypt(app)


# Registration Request
@app.route('/app/registration', methods=['POST'])
def addUser():
	print("[DEBUG] Registration Request :\n",request.json)
	try:
		users = mongo.db.users
		# customer_id = customers.insert({'first_name': first_name, 'last_name': last_name})
		request.json['password']=bcrypt.generate_password_hash(request.json['password'])
		user_id = users.insert(request.json)
		# new_user = users.find_one({'_id': user_id })
		output = {'error_code':'000','message' : 'New user created successfully', 'user_id' : str(user_id)}
		print("\n[INFO] Registration successfully...!!!")
	
	except Exception, e:
		print (e.message)
		print("\n[ERROR]] Registration Failed...!!!")
		output = {'error_code':'010','message' : 'New user creation failed'}

	return jsonify({'Response' : output})


# Login Request
@app.route('/app/login', methods=['POST'])
def userLogin():
	print("[DEBUG] Login Request :\n",request.json)
	reqUsername = request.json['username']
	reqPassword = request.json['password']
	try:
		users = mongo.db.users
		user = users.find_one({'username' : reqUsername})

		if (bcrypt.check_password_hash(user["password"], reqPassword)):
			print("\n[INFO] Login successful...!!!")
			output = {'error_code':'000','message' : 'Login successful', 'user_type' : user["user_type"]}


		else:
			print("\n\n[INFO] Login failed ...Wrong password ...!!!")
			output = {'error_code':'001','message' : 'Login failed ... Wrong password ...!!!'}

		# TODO :  handle this case "message": "Your registration request is not accepted...contact your boss or try later"

		print("\n[INFO] Login successfully...!!!")
	
	except Exception, e:
		print (e.message)
		print("\n[ERROR]] Login Failed...!!!")
		output = {'error_code':'100','message' : 'Login Failed... User does not exist or password is incorrect'}

	return jsonify({'Response' : output})

# Add package
@app.route('/app/package/<string:username>', methods=['POST'])
def addPackage(username):
	print("[DEBUG] Check status Request :\n",request.json)
	try:
		packages = mongo.db.packages
		users = mongo.db.users
		user = users.find_one({"username" :username})
		request.json['sellerID']=user['_id']
		request.json['status']="not assigned"
		packages.insert(request.json)
		output = {'error_code':'000','message' : 'package added successfully'}
		print("\n[INFO] Registration successfully...!!!")

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] Add package Failed...!!!")
		output = {'error_code':'100','message' : 'add package Failed... Access denied'}

	return jsonify({'Response' : output})


# Check status
@app.route('/app/status/<int:packageID>/<string:username>', methods=['GET'])
def checkPackageStatus(packageID,username):
	print("[DEBUG] Check status Request ")
	try:
		packages = mongo.db.packages
		package = packages.find_one({'packageID' : packageID})

		output = { "error_code": "000","status":package['status']}

	# TODO handle "message": "not authorized to access this details "
	# TODO handle event value
		
		print("\n[INFO] package: ",packageID, "\t status ",package['status'])

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})


# update status
@app.route('/app/update/<int:packageID>', methods=['PUT'])
def updatePackageStatus(packageID):
	print("[DEBUG] update status Request :\n",request.json)
	# reqUsername = request.json['username']
	
	newStatus = request.json['status']
	try:
		packages = mongo.db.packages
		users = mongo.db.users
		reqUser =  users.find_one({'username' : request.json['username']})

		packages.update_one(
			{'packageID' : packageID},
			{
				"$set": {
					"status":newStatus,
					"deID":reqUser['_id']
					# TODO : add time stamp "HH:MM DD/MM/YYYY" & location 
				}
			}
		)
		# TODO: push package id into DE package list
		package = packages.find_one({'packageID' : packageID})

		print("\n[INFO] status updated ... \n [INFO] package: ",packageID, "\t status ",package['status'])

		output = { "error_code": "000", "message": "status updated successfully"}

	# TODO handle  "message": "not authorized to access this details "
	except Exception, e:
		print (e.message)
		print("\n[ERROR] update status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})

# track package


# contact DE
@app.route('/app/de/<int:packageID>/<string:username>', methods=['GET'])
def contactDE(packageID,username):
	print("[DEBUG] contact DE Request :")
	try:
		packages = mongo.db.packages
		package = packages.find_one({'packageID' : packageID})

		users = mongo.db.users
		DEuser = users.find_one({"_id" :ObjectId(package['deID'])})

		output = { "error_code": "000","name":DEuser['name'],"contact_no":DEuser['contact_no']}

	# TODO handle "message": "not authorized to access this details " & "No DE found for package"
		
		print("\n[INFO] name: ", DEuser['name'],"contact_no: ", DEuser['contact_no'])

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})


# contact Buyer
@app.route('/app/buyer/<int:packageID>/<string:username>', methods=['GET'])
def contactBuyer(packageID,username):
	print("[DEBUG] contact Buyer Request ")
	try:
		packages = mongo.db.packages
		package = packages.find_one({'packageID' : packageID})

		output = { "error_code": "000","name":package["Buyer_details"]["name"],"contact_no":package["Buyer_details"]["contactNo"]}

	# TODO handle "message": "not authorized to access this details " & "No buyer found for package"

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})

# View all packages
@app.route('/app/packages/<string:username>', methods=['GET'])
def all_packages(username):
	print("[DEBUG] View all packages ")
	try:
		packages = mongo.db.packages
		users= mongo.db.users
		pack_list = []
		for package in packages.find({},{'_id':0}):
			pprint.pprint(package)
			if 'sellerID' in package:
				seller=users.find_one({'_id': ObjectId(package['sellerID'])})
				package['seller']=seller['name']
				del package['sellerID']
			if "deID" in package:
				DE=users.find_one({'_id': ObjectId(package['deID'])})
				package['DE']=DE['name']
				del package['deID']
			pprint.pprint(package)
			pack_list.append(package)

		if len(pack_list)!=0:
			output = { "error_code": "000","packages":pack_list}
		else:
			output = {  "error_code": "010","packages":"No packages found"} 


	# TODO handle "message": "not authorized to access this details " & No packages found"

	except Exception, e:
		print (e.message)
		print("\n[ERROR] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})


# View one package details
@app.route('/app/package/<int:packageID>/<string:username>', methods=['GET'])
def get_one_packages(packageID, username):
	print("[DEBUG] View one package details ")
	try:
		packages = mongo.db.packages
		users= mongo.db.users

		package = packages.find_one({'packageID':packageID},{'_id':0})
		pprint.pprint(package)
		if 'sellerID' in package:
			seller=users.find_one({'_id': ObjectId(package['sellerID'])})
			package['seller']=seller['name']
			del package['sellerID']
		if "deID" in package:
			DE=users.find_one({'_id': ObjectId(package['deID'])})
			package['DE']=DE['name']
			del package['deID']
		pprint.pprint(package)


		output = { "error_code": "000","package":package}

	# TODO handle "message": "not authorized to access this details " & No packages found"

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})


# add DE 
@app.route('/app/de/<string:username>', methods=['POST'])
def addDE(username):
	print("[DEBUG] add DE ")
	try:
		users=mongo.db.users
		deUsername=request.json['email_id'].split('@')[0]
		print("deUsername: ", deUsername)
		dePassword="tracker1234"
		request.json['username']=deUsername
		request.json['password']=bcrypt.generate_password_hash(dePassword)
		request.json['user_type']= 'de'
		pprint.pprint(request.json)
		DEuser_id = users.insert(request.json)

		users.update({'username':username},{'$push':{'DE_list':DEuser_id }})
		# update({'ref': ref}, {'$push': {'tags': new_tag}})

		output = { "error_code": "000","message": "DE added successfully","username":deUsername,"password":dePassword}

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})

if __name__ == '__main__':
	app.run(debug=True, port = 8080)
