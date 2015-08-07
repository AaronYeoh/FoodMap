var map;
var ajaxRequest;
var plotlist;
var plotlayers=[];

function initmap() {
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
    

    iconSize:     [38, 95], // size of the icon
    shadowSize:   [50, 64], // size of the shadow
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    shadowAnchor: [4, 62],  // the same for the shadow
    popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
});
	
	
	var marker = L.marker([-36.840560,174.74], {icon: lemonIcon}).addTo(map);
	
	//Add a popup
	marker.bindPopup("<important>I'm not a lemon</important><br>I'm a popup.");
	
	
}
