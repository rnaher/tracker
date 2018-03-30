<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <style>
       #map {
        height: 500px;
        width: 100%;
       }
    </style>
  </head>
  <body>
    <h3>Current Location Of Package</h3>
    <div id="map"></div>
    <script>
      function initMap() {
        var uluru = {lat: <%  out.println(request.getAttribute("lat")); %>, lng: <%  out.println(request.getAttribute("lng")); %>};
        var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 100,
          center: uluru
        });
        var marker = new google.maps.Marker({
          position: uluru,
          map: map
        });
        map.setZoom(15);
        map.panTo(marker.position);
      }
    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCbmFePg_8LJ58-tRUVASSi831Z4ZCLYNA&callback=initMap">
    </script>
  </body>
</html>
