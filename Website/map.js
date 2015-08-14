var map;
var ajaxRequest;
var plotlist;
var plotlayers=[];

function initmap() {
	$.getScript("/leaflet/leaflet.js");
	// set up the map
	map = new L.Map('map');

	// create the tile layer with correct attribution
	var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
	var osm = new L.TileLayer(osmUrl, {minZoom: 0, maxZoom: 18, attribution: osmAttrib});		

	// start the map in Auckland, NZ
	map.setView(new L.LatLng(-36.840556,174.74),9);
	map.addLayer(osm);


	//Let's add a marker in Auckland
	var marker = L.marker([-36.840556,174.74]).addTo(map);
	
	//How about another marker?
	//shadowUrl: '',
	var lemonIcon = L.icon({
    iconUrl: "http://www.tuxpaint.org/stamps/stamps-thumbs/stamps/food/fruit/cartoon/lemon.jpg",
    

    iconSize:     [38, 38], // size of the icon
    //shadowSize:   [50, 64], // size of the shadow
    iconAnchor:   [0, 0], // point of the icon which will correspond to marker's location
    //shadowAnchor: [4, 62],  // the same for the shadow
    popupAnchor:  [0,0] // point from which the popup should open relative to the iconAnchor
	});
	
	
	var marker = L.marker([-36.840560,174.74], {icon: lemonIcon}).addTo(map);
	
	//Add a popup
	marker.bindPopup("<important>I'm not a lemon</important><br>I'm a popup.");
	
	
}

function loadFromParse(){
	Parse.initialize("ChUKpoLVli5C5y8oQfv4EmLILIXku8KpXjMmqCzG", "DbIfTbnWOPmqVWcG0epJelvIujUwos5MktAVCwfg");

	//Time to load up the database.
	var FruitObject = Parse.Object.extend("TestObject");
	var query = new Parse.Query(FruitObject);
	query.find({
		success: function(results) {
			alert("Successfully retrieved " + results.length + " scores.");
			// Do something with the returned Parse.Object values
			for (var i = 0; i < results.length; i++) {
				var object = results[i];

				CreateMarkerFromObject(object);
			}
		},
		error: function(error) {
			alert("Error: " + error.code + " " + error.message);
		}
	});
}

function CreateMarkerFromObject(object){
	console.log(object);
	var lat = (object.attributes.LatLng.latitude);
	var lon = (object.attributes.LatLng.longitude);
	var img = object.get("images");
	var title = object.attributes.fruitType;
	//var imageUrl = (object.attributes.images.url());
	var description = object.attributes.description;
	var latlon = L.latLng(lat, lon);
	var marker = L.marker(latlon).addTo(map);
	marker.bindPopup("<h3>"+ title + "</h3><div><img src='" + img.url() + "'/></div> Description :<br>" + description);

}

function CreateMarkerWithPopup(){

}