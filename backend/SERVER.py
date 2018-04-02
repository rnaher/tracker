# SERVER.py


from flask import Flask
from flask import jsonify
from flask import request
from flask_pymongo import PyMongo
from flask_bcrypt import Bcrypt
from bson import ObjectId  
import datetime
import pprint
import json


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
		if request.json['user_type']=='de':
			request.json['seller_id']=ObjectId(request.json['seller_id'])
		request.json['password']=bcrypt.generate_password_hash(request.json['password'])
		
		user_id = users.insert(request.json)

		# new_user = users.find_one({'_id': user_id })
		output = {'error_code':'000','message' : 'New user created successfully', 'user_id' : str(user_id)}
		if request.json['user_type']=='de':
			users.update({'_id':request.json['seller_id']},{'$push':{'DE_list':DEuser_id }})
			
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
		request.json['status']="In warehouse"
		packages.insert(request.json)
		output = {'error_code':'000','message' : 'package added successfully'}
		print("\n[INFO] package added ...!!!")

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
@app.route('/app/update/<int:packageID>/<string:username>', methods=['PUT'])
def updatePackageStatus(packageID,username):
	print("[DEBUG] update status Request :\n",request.json)
	# reqUsername = request.json['username']
	
	# create event
	try :

		packages=mongo.db.packages
		pack_id=packages.find_one({'packageID':packageID},{'_id':1})
		if pack_id == None:
			output = { "error_code": "010", "message": "package details do not exist in system"}
			return jsonify({'Response' : output})
		request.json["packageID"]=packageID
		users =mongo.db.users
		reqUser = users.find_one({'username' : username})
		request.json["userID"]=reqUser['_id']
		request.json["timeStamp"]=datetime.datetime.utcnow()
		print (request.json["timeStamp"])

		# create events
		events=mongo.db.events

		new_event_id=events.insert(request.json)
		new_event= events.find_one({'_id': new_event_id })
		packages.update({"packageID" :packageID},{'$push':{'event_list':new_event_id }})
		newStatus = request.json['status']

		#  TODO : user does not exist case

		# output = { "error_code": "000","message": "event created successfully","eventID":str(new_event_id),"time_stamp":str(new_event["timeStamp"])}
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

	try:
		print("[DEBUG] create notifications :")
		# if request.json['status']=="Failed":
		data={}
		data["notification_data"]=request.json['status']
		data['packageID']=packageID
		data['seen']=False
		users= mongo.db.users
		de = users.find_one({'username':username})
		data["seller_id"]=de["seller_id"]

		notifications=mongo.db.notifications
		notifications.insert(data)

		print("\n[INFO] Notification added successfully")



	except Exception, e:
		print (e.message)
		print("\n[ERROR] create notification Failed...!!!")

	return jsonify({'Response' : output})

# track package
@app.route('/app/track/<int:packageID>/<string:username>', methods=['GET'])
def trackPackage(packageID,username):
	print("[DEBUG] track package :")
	try:
		packages = mongo.db.packages
		package_events=packages.find_one({'packageID':packageID},{'event_list':1})
		# print package_events
		if package_events == None:
			# print ("Package details do not exist in system")
			output = { "error_code": "010", "message": "Package details do not exist in system"}
			return jsonify({'Response' : output})

		if "event_list" not in package_events:
			print ("no events found for the package")
			output = { "error_code": "010", "message": "no events found for the package"}
			return jsonify({'Response' : output})

		count = len(package_events["event_list"])
		details = []
		events = mongo.db.events

		for event_ID in  package_events["event_list"]:
			detail={}
			# print("event_ID:", event_ID)
			event = events.find_one({'_id':event_ID})
			detail['status']=event['status']
			detail['timeStamp']=event['timeStamp']
			
			users = mongo.db.users
			Updated_by=users.find_one({'_id': event['userID']})
			detail['Updated By']=Updated_by['name']

			details.append(detail)

		# print(details)
		print("\n[INFO] track package successful ... ")

		output = { "error_code": "000","count":count,"details":details}
		# if len(event_list) ==0:
		# 	output = { "error_code": "010", "message": "Package details do not exist in system or no events found for the package"}
			
	except Exception, e:
		print (e.message)
		print("\n[ERROR] update status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}
	
	return jsonify({'Response' : output})




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
		for package in packages.find({},{'_id':0,'event_list':0}):
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
		output = { "error_code": "010", "message": "add DE failed"}

	return jsonify({'Response' : output})

# delete DE:

# create event
@app.route('/app/event/<int:packageID>/<string:username>', methods=['POST'])
def createEvent(packageID,username):
	print("[DEBUG] create event ")
	output =""
	try:
		request.json["packageID"]=packageID

		packages=mongo.db.packages
		pack_id=packages.find_one({'packageID':packageID},{'_id':1})
		
		if pack_id == None:
			output = { "error_code": "010", "message": "package details do not exist in system"}
			raise Exception()
		users =mongo.db.users
		user = users.find_one({'username' : username})
		request.json["userID"]=user['_id']
		request.json["timeStamp"]=datetime.datetime.utcnow()
		print (request.json["timeStamp"])

		events=mongo.db.events

		new_event_id=events.insert(request.json)
		new_event= events.find_one({'_id': new_event_id })
		packages.update({"packageID" :packageID},{'$push':{'event_list':new_event_id }})

		#  TODO : user does not exist case

		output = { "error_code": "000","message": "event created successfully","eventID":str(new_event_id),"time_stamp":str(new_event["timeStamp"])}

	except Exception, e:

		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		if len(output)==0:
			output = { "error_code": "010", "message": "create event failed"}

	return jsonify({'Response' : output})

# get notification
@app.route('/app/notification/<string:username>',methods=['GET'])
def getNotification(username):
	print("[DEBUG] create event ")
	try:
		users = mongo.db.users
		user = users.find_one({'username' : username})
		user_id = user['_id']
		notifications=mongo.db.notifications

		notifn_list = []
		for notification in notifications.find({'seller_id':user_id,"seen":{'$ne': True}},{'_id':0,'seller_id':0}):
			notifn_list.append(notification)

		# notifications.update(
		# 	{'seller_id':user_id,"seen":{'$ne': True}},
		# 	{
		# 		"$set": {
		# 			"seen":True
		# 			# TODO : add time stamp "HH:MM DD/MM/YYYY" & location 
		# 		}
		# 	},
		# 	{"multi":True}
		# )
		# TODO: only failed notifications are shown
		# TODO: delete after find

		if len(notifn_list)!=0:
			output = { "error_code": "000","notifications":notifn_list}
		else:
			output = { "error_code": "010","message":"No notifications found"}

	except Exception, e:

		print (e.message)
		print("\n[ERROR]] get notification Failed...!!!")
		output = { "error_code": "100", "message":  "not authorized to access this details"}

	return jsonify({'Response' : output})



if __name__ == '__main__':
	app.run(host='0.0.0.0',debug=True, port = 8080)
