var map;
var geocoder;
var myLatLng;
var markers = new Array();
var selectedMarkerInfoWindow = null;
var latlngs = new Array();

function initialize() {
    geocoder = new google.maps.Geocoder();
    var MY_MAPTYPE_ID = 'custom_style';
    var featureOpts = [
        {
            "stylers": [
                {"hue": "#00ccff"}
            ]
        }, {
            "featureType": "road",
            "elementType": "geometry",
            "stylers": [
                {"lightness": 67}
            ]
        }, {
            "featureType": "landscape",
            "stylers": [
                {"saturation": -72}
            ]
        }, {
            "elementType": "labels.text.fill",
            "stylers": [
                {"invert_lightness": true},
                {"lightness": -16}
            ]
        }, {
            "featureType": "landscape",
            "elementType": "geometry.fill",
            "stylers": [
                {"lightness": 100}
            ]
        }
    ]
            ;

    var mapOptions = {
        zoom: 14,
        disableDefaultUI: true,
        zoomControlOptions: {
            position: google.maps.ControlPosition.LEFT_CENTER
        },
        mapTypeControlOptions: {
            style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
            position: google.maps.ControlPosition.RIGHT_BOTTOM,
            mapTypeIds: [google.maps.MapTypeId.ROADMAP, google.maps.MapTypeId.SATELLITE, MY_MAPTYPE_ID]
        },
        mapTypeId: MY_MAPTYPE_ID
    };
    map = new google.maps.Map(document.getElementById('map'),
            mapOptions);
    var styledMapOptions = {
        name: 'Audiocracy'
    };
    handleNoGeolocation(true);
    var customMapType = new google.maps.StyledMapType(featureOpts, styledMapOptions);
    map.mapTypes.set(MY_MAPTYPE_ID, customMapType);
    var url = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);
    if (url == "locations.xhtml")
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                var pos = new google.maps.LatLng(position.coords.latitude,
                        position.coords.longitude);
                var infowindow = new google.maps.InfoWindow({
                    map: map,
                    position: pos,
                    content: 'You are here!'
                });
                if (window.location.search.substring(1, 3) !== "id") {
                    map.setCenter(pos);
                }
            }, function() {
                handleNoGeolocation(true);
            }, {enableHighAccuracy: true});
        } else {
            handleNoGeolocation(false);
        }
    setMarkers(map, hosts);
    if (window.location.search.substring(1, 3) == "id") {
        map.setCenter(markers[window.location.search.substring(4, 5)].position);
        markers[window.location.search.substring(4, 5)].click();
        ;
        //alert(window.location.search.substring(4));
    }
}

function handleNoGeolocation(errorFlag) {
    if (!errorFlag) {
        var content = 'Error: Your browser doesn\'t support geolocation.';
    }
    var options = {
        map: map,
        position: new google.maps.LatLng(47.070309, 15.439344),
        content: content
    };
    map.setCenter(options.position);
}

function setMarkers(map, locations) {

    var image = {
        url: 'img/marker2.png',
        size: new google.maps.Size(30, 35),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(15, 35)
    };
    for (var i = 0; i < locations.length; i++) {
        var host = locations[i];
        var infowindow = new google.maps.InfoWindow();

        var marker = new google.maps.Marker({
            position: new google.maps.LatLng(host[1], host[2]),
            map: map,
            icon: image,
            title: host[0],
            zIndex: host[3],
            animation: google.maps.Animation.DROP,
            myId: host[3]
        });
        markers[marker.myId] = marker;

        google.maps.event.addListener(marker, 'click', (function(marker, content, infowindow) {
            return function() {
                if (selectedMarkerInfoWindow !== null)
                    selectedMarkerInfoWindow.close();
                selectedMarkerInfoWindow = infowindow;
                marker.setAnimation(google.maps.Animation.BOUNCE);
                infowindow.setContent(content);
                infowindow.open(map, marker);
                document.getElementById('form:hidden').value = marker.myId;
                document.getElementById('form:submitID').click();
                document.getElementById('form:submitLoc').click();
                setTimeout(function() {
                    marker.setAnimation(null);
                }, 700);
            };
        })(marker, marker.title, infowindow));

    }
}

var marker = null;
function codeAddress() {
    var address = jQuery('#LocationForm\\:street').val() + " " +
            jQuery('#LocationForm\\:zipCode').val() + " " +
            jQuery('#LocationForm\\:city').val();

    geocoder.geocode({'address': address}, function(results, status) {
        if (marker != null)
            marker.setMap(null);
        if (status == google.maps.GeocoderStatus.OK) {
            map.setCenter(results[0].geometry.location);
            marker = new google.maps.Marker({
                map: map,
                position: results[0].geometry.location
            });
            var latandlng = results[0].geometry.location.toString().replace('(', '').replace(')', '').split(',');
            document.getElementById('LocationForm:lat').value = parseFloat(latandlng[0].trim());
            document.getElementById('LocationForm:lng').value = parseFloat(latandlng[1].trim());
        }
    });
}