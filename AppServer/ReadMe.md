<h1> Installation</h1>

* Webserver  [Apache 9.0](https://tomcat.apache.org/download-90.cgi) (use same version)
* Download Eclipse IDE (java EE one) [Eclipse Java EE Developers](https://www.eclipse.org/downloads/packages/release/Oxygen/3) 
* MySql
  * [MySql Community Server Windows](https://dev.mysql.com/downloads/mysql/)
  * [Ubuntu](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-16-04) (check other if it does not work)
  * Use <b>username</b>=`root` and <b>password</b>=`root` during installation of mysql server.
  * Create Database with <b>name</b> `store` and table with <b>name</b>=`DE` in it. [(helpful commands for trivial stuff)](https://www.a2hosting.in/kb/developer-corner/mysql/managing-mysql-databases-and-users-from-the-command-line)
* <h>Request URL <h>
  * Buyer / Seller can track package using this url (http://localhost:8080/AppServer/requests?pkgid=1). It will show the current location    of package on google map.It will find the corresponding DE for package and show the location of DE.
  * Delivery Executive can update location of package using similar url (http://localhost:8080/AppServer/requests?deid=1&&lat=17.44798&&lng=78.34830). Such call will update location of DE with given deid to given latitude and longitude.
