// sagnihotri
package com.request;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RequestHandler
 * 
 * schema name ----> store
 * table name  ----> package_detail
 * username    ----> root
 * password    ----> root
 * DE url : http://localhost:8080/AppServer/requests?pkgid=1&&lat=17.44798&&lng=78.34830
 * buyer url :http://localhost:8080/AppServer/requests?pkgid=1
 * 
 */
@WebServlet(description = "To handle incoming requests", urlPatterns = { "/requests" })
public class RequestHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RequestHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		Enumeration<String> parameterNames = request.getParameterNames();
		String latitude = "", longitude = "", pkgid = "";
		int is_buyer = 0;
		int items = 0;
		while (parameterNames.hasMoreElements()) {
			items++;
			String paramName = parameterNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			String paramValue = paramValues[0];
			if (paramName.equals("pkgid"))
				pkgid = paramValue;
			;
			if (paramName.equals("lat"))
				latitude = paramValue;
			if (paramName.equals("lng"))
				longitude = paramValue;
		}
		if (items == 1)
			is_buyer = 1;
		if (is_buyer == 0) {
			// app is sending data to update location in database
			update_location(pkgid, latitude, longitude);
		} else {
			// buyer want's to know location;
			String []coordinates=get_package_location(pkgid);
			request.setAttribute("lat", coordinates[0]);
			request.setAttribute("lng", coordinates[1]);
			request.getRequestDispatcher("show_map.jsp").forward(request, response);
		}
	}

	private String[] get_package_location(String pkgid) {
		// TODO Auto-generated method stub
		Connection conn = null;
		String[] coordinate = new String[2];
		coordinate[0] = coordinate[1] = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/store", "root", "root");
			String query = "select * from package_detail where pkgid=" + pkgid + ";";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				coordinate[0] = rs.getString("latitude");
				coordinate[1] = rs.getString("longitude");
			}
		} catch (Exception e) {
			System.out.println("Exception is " + "\n" + e.toString());
		}
		return coordinate;
	}

	void update_location(String pkgid, String latitude, String longitude) {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/store", "root", "root");
			String query = "select count(*) as count from package_detail where pkgid=" + pkgid + ";";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			long present = rs.getLong(1);
			if (present == 1) {
				// pkgid is present in database
				query = "update package_detail set latitude=" + latitude + ",longitude=" + longitude + "where pkgid="
						+ pkgid + ";";
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				// System.out.println("database updated");
			} else {
				// pkgid is not present in database
				query = "insert into package_detail(pkgid,latitude,longitude)values(" + pkgid + "," + latitude + ","
						+ longitude + ")" + ";";
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
			}
		} catch (Exception e) {
			System.out.println("Exception is " + "\n" + e.toString());
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
