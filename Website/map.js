var map;
var ajaxRequest;
var plotlist;
var plotlayers=[];

function initmap() {
		// set up the map
	map = new L.Map('map');

	// create the tile layer with correct attribution
	var osm = L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
		attribution: 'Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012'
	});

	//var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
	//var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
	//var osm = new L.TileLayer(osmUrl, {minZoom: 0, maxZoom: 18, attribution: osmAttrib});

	// start the map in Auckland, NZ
	map.setView(new L.LatLng(-36.840556,174.74),9);
	map.addLayer(osm);
}



function loadFromParse(){
	Parse.initialize("ChUKpoLVli5C5y8oQfv4EmLILIXku8KpXjMmqCzG", "DbIfTbnWOPmqVWcG0epJelvIujUwos5MktAVCwfg");

	//Time to load up the database.
	var FruitObject = Parse.Object.extend("TestObject");
	var query = new Parse.Query(FruitObject);
	query.find({
		success: function(results) {
			//alert("Successfully retrieved " + results.length + " scores.");
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

	var ico = AttachIcon(object.attributes.fruitType);
	var marker = L.marker(latlon, {icon: ico}).addTo(map);

	var newDescription = description.split("###");

	var $list = $('<ol/>');
	newDescription.forEach(function (item, index, array){
		$list.append("<li>"+item+"</li>");
		console.log(item);
	})


	marker.bindPopup("<h3>"+ title + "</h3><div><img src='" + img.url() + "'/>" +
		"</div> <div style='width: 300px; height: 150px; overflow-y: scroll;'><h4>Description</h4>"+ $list.html() +"</div>" +
		"<!--<input type='text' class='form-control' placeholder='Add a comment'>" +
		"<button type='submit' class='btn-lg'>Submit</button>-->");



	//marker.setIcon(ico);
}

function AttachIcon(fruitType){
	var baseUrl = "resources/";
	var newstr = fruitType.toLowerCase();
	switch (newstr){
		case "orange":
			var url = baseUrl+"orange.png";
			break;
		case "apple":
			var url = baseUrl+"apple.png";
			break;
		case "lemon":
			var url = baseUrl+"lemon.png";
			break;
		case "avocado":
			var url = baseUrl+"avo.png";
			break;
		default :

	}

	var fruitIcon = L.icon({
		iconUrl: url,
		shadowUrl:"resources/shadow.png",


		iconSize:     [46, 80], // size of the icon
		shadowSize:   [50, 92], // size of the shadow
		iconAnchor:   [23, 80], // point of the icon which will correspond to marker's location
		shadowAnchor: [0, 100],  // the same for the shadow
		popupAnchor:  [0,0] // point from which the popup should open relative to the iconAnchor
	});
	return fruitIcon;
}
$(document).ready(function () {
	$('#grocer-form').on('submit', function (event) {
		console.log('submit', event);

		event.preventDefault();
		if (typeof navigator.geolocation == 'undefined') {
			//Nothing to do
			alert("Please enable geolocation to submit new items.");
		}
		else {
			navigator.geolocation.getCurrentPosition(granted, denied);
		}

	});
});

function granted(position){
	//alert("Your position is: lat" + position.coords.latitude + " | long: " + position.coords.longitude);


	var name = $("#name").val();
	var desc = $("#description").val();
	var point = new Parse.GeoPoint({latitude: position.coords.latitude, longitude: position.coords.longitude});
	var image = $("#image")[0];
	if (image.files.length>0){
		var file = image.files[0];

		var filename = $("#image").val().split('\\').pop();

		//alert(name);
		//alert(file);

		var parseFile = new Parse.File(filename, file);
		console.log(parseFile);

		parseFile.save().then(function() {
			console.log("The file has been saved" + filename);
		}, function(error) {
			alert("The file either could not be read, or could not be saved");
		});
	}




	console.log('tree name', name);
	console.log('description', desc);

	var FruitObject = Parse.Object.extend("TestObject");
	var fruit = new FruitObject();
	fruit.set("fruitType", name);
	fruit.set("description", desc);
	fruit.set("LatLng",point);
	fruit.set("images", parseFile);

	fruit.save(null, {
		success: function(fruit) {
			// Execute any logic that should take place after the object is saved.
			alert('Success! You\'ve submitted a new item!');
		},
		error: function(fruit, error) {
			// Execute any logic that should take place if the save fails.
			// error is a Parse.Error with an error code and message.
			alert('Failed to create new object, with error code: ' + error.message);
		}
	});


	CreateMarkerFromObject(fruit);

	console.log('end');

}
function denied(){}