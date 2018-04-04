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

# ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
# Registration Request
@app.route('/app/registration', methods=['POST'])
def addUser():
	print("[DEBUG] Registration Request :\n",request.json)
	form={}
	try:
		users = mongo.db.users
		
		# TODO: unique username
		form['username']=request.json['username']

		prev_user=users.find_one({'username':form['username']})
		

		if prev_user != None :
			print("\n[ERROR] Username aleady exists. Registration Failed...!!!")
			output = {'error_code':'011','message' : 'Username aleady exists. Registration Failed'}
			return jsonify({'Response':output})

		form['name']=request.json['name']
		form['contact_no']=request.json['contact_no']
		form['email_id']=request.json['email_id']
		
		form['password']=bcrypt.generate_password_hash(request.json['password'])
		form['user_type']=request.json['user_type']

		if form['user_type'] =='de':
			form['seller_id']=ObjectId(request.json['seller_id'])
			# check the seller exists or not
			try:
				seller=users.find_one({'_id':form['seller_id']})
			except Exception, e:
				print(e.message)
				print("\n[ERROR] Incorrect sellerID. Registration Failed...!!!")
				output = {'error_code':'011','message' : 'Incorrect sellerID.. Registration Failed'}
				return jsonify({'Response':output})

		user_id = users.insert(form)

		output = {'error_code':'000','message' : 'New user created successfully', 'user_id' : str(user_id)}
		if form['user_type']=='de':
			users.update({'_id':form['seller_id']},{'$push':{'DE_list':user_id }})
			
		print("\n[INFO] Registration successfully...!!!")
	
	except Exception, e:
		print (e.message)
		print("\n[ERROR] Registration Failed...!!!")
		output = {'error_code':'010','message' : 'Registration failed'}

	return jsonify({'Response' : output})

# ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
# Login Request
@app.route('/app/login', methods=['POST'])
def userLogin():
	print("[DEBUG] Login Request :\n",request.json)
	try:
		reqUsername = request.json['username']
		reqPassword = request.json['password']

	except Exception as e:
		print (e.message)
		print("\n[ERROR] Wrong/missing inputs. Login failed")
		output={'error_code':'010','message' : 'Wrong/missing inputs. Login failed.'}
		return jsonify({'Response' : output})

	try:
		users = mongo.db.users
		user = users.find_one({'username' : reqUsername})

		if (bcrypt.check_password_hash(user["password"], reqPassword)):
			print("\n[INFO] Login successful...!!!")
			output = {'error_code':'000','message' : 'Login successful', 'user_type' : user["user_type"]}


		else:
			print("\n[INFO] Login failed. Wrong password.")
			output = {'error_code':'001','message' : 'Login failed. Wrong password.'}

		# TODO :  handle this case "message": "Your registration request is not accepted...contact your boss or try later"
	
	except Exception, e:
		print (e.message)
		print("\n[ERROR] Login Failed...!!!")
		output = {'error_code':'100','message' : 'Login Failed. User does not exist or password is incorrect'}

	return jsonify({'Response' : output})

# ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
# Add package
@app.route('/app/package/<string:username>', methods=['POST'])
def addPackage(username):
	print("[DEBUG] Check status Request :\n",request.json)
	form={}
	try:
		print("check the username exists")
		users = mongo.db.users
		user = users.find_one({"username" :username})
		if user==None :
			print("\n[ERROR] User does not exist. Add package Failed...!!!")
			output = {'error_code':'100','message' : 'User does not exist. Add package Failed. Access denied'}
			return jsonify({'Response' : output})

		print("check the user is seller")
		if user['user_type']!='seller':
			print("\n[ERROR] User should be a seller. Add package Failed...!!!")
			output = {'error_code':'100','message' : 'User should be a seller. Add package Failed. Access denied'}
			return jsonify({'Response' : output})

		print("check unique packageID")
		form['packageID']= request.json['packageID']
		form['sellerID']= user['_id']
		packages = mongo.db.packages

		old_packs=packages.find_one({"$and": [{'packageID':form['packageID']},{'sellerID':form['sellerID']}]})

		if old_packs != None :
			print("\n[ERROR] package aleady exists. add package Failed...!!!")
			output = {'error_code':'011','message' : 'package aleady exists. add package Failed'}
			return jsonify({'Response':output})

		Buyer_details={}
		Buyer_details['name']=request.json['Buyer_details']['name']
		Buyer_details['contactNo']=request.json['Buyer_details']['contactNo']
		form['Buyer_details']=Buyer_details
		form['destination']=request.json['destination']
		form['status']='In warehouse'

		packages = mongo.db.packages
		packages.insert(form)
		output = {'error_code':'000','message' : 'package added successfully'}
		print("\n[INFO] package added ...!!!")

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] Add package Failed...!!!")
		output = {'error_code':'010','message' : 'Add package Failed.'}

	return jsonify({'Response' : output})

# ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
# Check status
@app.route('/app/status/<int:packageID>/<string:username>', methods=['GET'])
def checkPackageStatus(packageID,username):
	print("[DEBUG] Check status Request ")
	try:
		# check user is seller or DE
		users = mongo.db.users
		user = users.find_one({"username" :username})
		user_type =user['user_type']
	except Exception, e:
		print (e.message)
		print("\n[ERROR]] User does not exist. check status Failed...!!!")
		output = { "error_code": "010", "message": "User does not exists in system"}
		return jsonify({'Response' : output})

	try :

		packages = mongo.db.packages
		package = packages.find_one({'packageID' : packageID})

		
		if user_type =='seller' and user['_id']!=package['sellerID']:
			print("\n[ERROR] Access denied...!!!")
			output = {'error_code':'100','message' : 'Access denied'}
			return jsonify({'Response' : output})

		elif user_type=='de' and user['seller_id'] !=package['sellerID']:
			print("\n[ERROR] Access denied...!!!")
			output = {'error_code':'100','message' : 'Access denied'}
			return jsonify({'Response' : output})

		

		output = { "error_code": "000","status":package['status']}

	# TODO handle "message": "not authorized to access this details "
	# TODO handle event value
		
		print("\n[INFO] package: ",packageID, " status ",package['status'])

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})

# ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
# update status
@app.route('/app/update/<int:packageID>/<string:username>', methods=['PUT'])
def updatePackageStatus(packageID,username):
	print("[DEBUG] update status Request :\n",request.json)
	# reqUsername = request.json['username']
	
	try:
		print("check user is seller or DE")
		users = mongo.db.users
		user = users.find_one({"username" :username})
		user_type =user['user_type']
	except Exception, e:
		print (e.message)
		print("\n[ERROR]] User does not exist. check status Failed...!!!")
		output = { "error_code": "010", "message": "User does not exists in system"}
		return jsonify({'Response' : output})

	try :
		print("check package exists or not")
		packages = mongo.db.packages
		package=packages.find_one({'packageID':packageID})

		if package == None:
			print("\n[ERROR]] package details do not exist. check status Failed...!!!")
			output = { "error_code": "010", "message": "package details do not exist in system"}
			return jsonify({'Response' : output})

		if user_type =='seller' and user['_id']!=package['sellerID']:
			print("\n[ERROR] Access denied...!!!")
			output = {'error_code':'100','message' : 'Access denied'}
			return jsonify({'Response' : output})

		elif user_type=='de' and user['seller_id'] !=package['sellerID']:
			print("\n[ERROR] Access denied...!!!")
			output = {'error_code':'100','message' : 'Access denied'}
			return jsonify({'Response' : output})


	# create event
		form={}
		form["packageID"]=packageID
		form["status"]=request.json['status']
		form["userID"]=user['_id']
		form["timeStamp"]=datetime.datetime.utcnow()
		print ("[INFO] ",form["timeStamp"])

		# create events
		events=mongo.db.events

		new_event_id=events.insert(form)
		# new_event= events.find_one({'_id': new_event_id })
		packages.update({"packageID" :packageID},{'$push':{'event_list':new_event_id }})
		newStatus = request.json['status']

		packages.update_one(
			{'packageID' : packageID},
			{
				"$set": {
					"status":newStatus,
					"deID":form["userID"]
				}
			}
		)

		package = packages.find_one({'packageID' : packageID})

		print("\n[INFO] status updated ... \n [INFO] package: ",packageID, "\t status ",package['status'])

		output = { "error_code": "000", "message": "status updated successfully"}


	# TODO handle  "message": "not authorized to access this details "
	except Exception, e:
		print (e.message)
		print("\n[ERROR] update status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	# add notification
	if package['status']=="Failed":
		try:
			print("[DEBUG] create notifications :")
			# if request.json['status']=="Failed":
			data={}
			data["notification_data"]=request.json['status']
			data['packageID']=packageID
			data['seen']=False
			users= mongo.db.users
			data["seller_id"]=package['sellerID']

			notifications=mongo.db.notifications
			notifications.insert(data)

			print("[INFO] Notification added successfully")

		except Exception, e:
			print (e.message)
			print("[ERROR] create notification Failed...!!!")

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
	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	try:
		users = mongo.db.users
		DEuser = users.find_one({"_id" :ObjectId(package['deID'])})

		output = { "error_code": "000","name":DEuser['name'],"contact_no":DEuser['contact_no']}

	# TODO handle "message": "not authorized to access this details " & "No DE found for package"
		
		print("\n[INFO] name: ", DEuser['name'],"contact_no: ", DEuser['contact_no'])

	except Exception, e:
		print (e.message)
		print("\n[ERROR]] check status Failed...!!!")
		output = { "error_code": "010", "message": "DE not found"}

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
		output = { "error_code": "010", "message": "Package details/buyer details do not exist in system"}

	return jsonify({'Response' : output})

# ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
# View all packages
@app.route('/app/packages/<string:username>', methods=['GET'])
def all_packages(username):
	print("[DEBUG] View all packages ")
	try:

		users= mongo.db.users
		packages = mongo.db.packages
		user = users.find_one({'username':username})
		
		if user['user_type']== 'seller':
			package_list=packages.find({'sellerID':user['_id']})

		elif user['user_type']== 'de':
			package_list=packages.find({'deID':user['_id']})

		if (package_list.count())==0:
			output = {  "error_code": "010","packages":"No packages found"} 
			return jsonify({'Response' : output})


		pack_list = []

		for package in package_list:
			pack={}
			pack["packageID"]=package["packageID"]
			pack["status"]=package["status"]
			pack["destination"]=package["destination"]
			pack["Buyer_details"]=package["Buyer_details"]

			if 'sellerID' in package:
				seller=users.find_one({'_id': ObjectId(package['sellerID'])})
				pack['seller']=seller['name']
			if "deID" in package:
				DE=users.find_one({'_id': ObjectId(package['deID'])})
				pack['DE']=DE['name']
			# else :
			# 	pack['DE']="none"

			pprint.pprint(pack)
			pack_list.append(pack)

		print("[INFO] view all successful")
		output = { "error_code": "000","packages":pack_list}
	
	# TODO handle "message": "not authorized to access this details " & No packages found"

	except Exception, e:
		print (e.message)
		print("\n[ERROR] check status Failed...!!!")
		output = { "error_code": "010", "message": "Package details do not exist in system"}

	return jsonify({'Response' : output})

# ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
# View one package details
@app.route('/app/package/<int:packageID>/<string:username>', methods=['GET'])
def get_one_packages(packageID, username):
	print("[DEBUG] View one package details ")
	try:

		users= mongo.db.users
		packages = mongo.db.packages
		user = users.find_one({'username':username})
		
		if user['user_type']== 'seller':
			package=packages.find_one({"$and":[{'packageID':packageID},{'sellerID':user['_id']}]})

		elif user['user_type']== 'de':
			package=packages.find_one({"$and":[{'packageID':packageID},{'deID':user['_id']}]})

		if package==None:
			output = {  "error_code": "010","packages":"No packages found"} 
			return jsonify({'Response' : output})

		# package = packages.find_one({'packageID':packageID},{'_id':0})
		pprint.pprint(package)
		pack["packageID"]=package["packageID"]
		pack["status"]=package["status"]
		pack["destination"]=package["destination"]
		pack["destination"]=package["Buyer_details"]

		if 'sellerID' in package:
			seller=users.find_one({'_id': ObjectId(package['sellerID'])})
			pack['seller']=seller['name']
		if "deID" in package:
			DE=users.find_one({'_id': ObjectId(package['deID'])})
			pack['DE']=DE['name']
		# else :
		# 	pack['DE']="none"
		
		pprint.pprint(pack)


		output = { "error_code": "000","package":pack}

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
		for notification in notifications.find({"$and":[{'seller_id':user_id},{'notification_data':'Failed'},{"seen":{'$ne': True}}]},{'_id':0,'seller_id':0}):
			notifn_list.append(notification)

		
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

# get profile
@app.route('/app/profile/<string:username>',methods=['GET'])
def getProfile(username):
	print("[DEBUG] get profile request ")
	try:
		users = mongo.db.users
		user = users.find_one({'username' : username})
		profile = {}

		profile['_id']=str(user['_id'])
		profile['name']=user['name']
		profile['contact_no']=user['contact_no']
		profile['email_id']=user['email_id']
		profile['username']=user['username']
		profile['user_type']=user['user_type']

		output = { "error_code": "000","profile":profile}

	except Exception, e:

		print (e.message)
		print("\n[ERROR]] get profile Failed...!!!")
		output = { "error_code": "100", "message":  "not found"}

	return jsonify({'Response' : output})

# edit profile
@app.route('/app/profile/<string:username>',methods=['PUT'])
def editProfile(username):
	print("[DEBUG] edit profile request ")
	try:
		pass
	except Exception, e:

		print (e.message)
		print("\n[ERROR]] edit profile Failed...!!!")
		output = { "error_code": "100", "message":  "not found"}

	return jsonify({'Response' : output})

if __name__ == '__main__':
	app.run(host='0.0.0.0',debug=True, port = 8080)
