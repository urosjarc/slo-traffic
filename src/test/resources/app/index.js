// Where you want to render the map.
const element = document.getElementById('osm-map');

const polylines = [];
const markers = [];

// Height has to be set. You can do this in CSS too.
element.style = 'height:100vh;';

// Create Leaflet map on map element.
const index = L.map(element);

// Add OSM tile layer to the Leaflet map.
L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
}).addTo(index);

// Target's GPS coordinates.
const target = L.latLng(46.038646, 14.505751);

// Set map's center to target with zoom 14.
index.setView(target, 9);

const greenIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const orangeIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-orange.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const redIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const blueIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-blue.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

function clearMap() {
    polylines.forEach(item => index.removeLayer(item))
    markers.forEach(item => index.removeLayer(item))
}

function openInNewTab(url) {
    window.open(url, '_blank').focus();
}

function cameras() {
    clearMap()
    // Place line on the map
    fetch("/cameras").then(res => {
        res.json().then(data => {
            for (feature of data.features) {
                let coords = feature.geometry.coordinates
                let item = feature.properties.items[0]
                const m = L.marker({
                    lon: coords[0],
                    lat: coords[1],
                }, {title: item.text_slo, alt: item.image}).addTo(index).on('click', (e) => {
                    openInNewTab(e.target.options.alt)
                });
                markers.push(m)
            }
        })
    })

}

function winds() {
    clearMap()
    // Place line on the map
    fetch("/winds").then(res => {
        res.json().then(data => {
            for (feature of data.features) {
                let coords = feature.geometry.coordinates
                let props = feature.properties
                const m = L.marker({
                    lon: coords[0],
                    lat: coords[1],
                }, {title: `Hitrost ${props.burja_veter} m/s z sunki do ${props.burja_sunki} m/s`}).addTo(index)
                markers.push(m)
            }
        })
    })
}

function borderDelays() {
    clearMap()
    // Place line on the map
    fetch("/border-delays").then(res => {
        res.json().then(data => {
            for (feature of data.features) {
                let coords = feature.geometry.coordinates
                let props = feature.properties
                const m = L.marker({
                    lon: coords[0],
                    lat: coords[1],
                }, {title: props.Description_i18n}).addTo(index)
                markers.push(m)
            }
        })
    })

}

let stopPlacesMap = {}

function stopPlaces() {
    clearMap()
    // Place line on the map
    fetch("/stop-places").then(res => {
        res.json().then(datas => {
            stopPlacesMap = datas
            for (const [id, data] of Object.entries(datas)) {
                const m = L.marker({
                    lon: data.lon,
                    lat: data.lat,
                }, {title: `${data.type}: ${data.name}`}).addTo(index)
                markers.push(m)
            }
        })
    })

}


function operators() {
    clearMap()
    // Place line on the map
    fetch("/operators").then(res => {
        res.json().then(datas => {
            console.log("Operators", datas)
            alert(`Number of operators ${datas.length}. For data take a look in console.log!`)
        })
    })

}

function events() {
    clearMap()
    // Place line on the map
    fetch("/events").then(res => {
        res.json().then(data => {
            for (feature of data.features) {
                let coords = feature.geometry.coordinates
                let props = feature.properties
                const m = L.marker({
                    lon: coords[0],
                    lat: coords[1],
                }, {title: props.opis}).addTo(index)
                markers.push(m)
            }
        })
    })
}

function restAreas() {
    clearMap()
    // Place line on the map
    fetch("/rest-areas").then(res => {
        res.json().then(data => {
            for (feature of data.features) {
                let coords = feature.geometry.coordinates
                let props = feature.properties
                const m = L.marker({
                    lon: coords[0],
                    lat: coords[1],
                }, {title: props.Name}).addTo(index)
                markers.push(m)
            }
        })
    })
}

function roadWork() {
    clearMap()
    // Place line on the map
    fetch("/road-work").then(res => {
        res.json().then(data => {
            for (feature of data.features) {
                let coords = feature.geometry.coordinates
                let props = feature.properties
                const m = L.marker({
                    lon: coords[0],
                    lat: coords[1],
                }, {title: props.opis}).addTo(index)
                markers.push(m)
            }
        })
    })
}

// function fares() {
//     clearMap()
//     // Place line on the map
//     fetch("/fares").then(res => {
//         res.json().then(datas => {
//             for (const data of datas) {
//                 const start = stopPlacesMap[data.start.stopPlaceId]
//                 const end = stopPlacesMap[data.end.stopPlaceId]
//
//                 const l = L.polyline([
//                     {lon: start.lon, lat: start.lat},
//                     {lon: end.lon, lat: end.lat},
//                 ], {title: `${data.amount}EU - ${data.name}`}).addTo(index)
//                 polylines.push(l)
//             }
//         })
//     })
//
// }

function timetables() {
    clearMap()
    // Place line on the map
    fetch("/timetables").then(res => {
        res.json().then(datas => {
            for (const data of datas) {

                const line = []
                for (const stopPoint of data.journey.stopPoints) {
                    const stopPlace = stopPlacesMap[stopPoint.stopPlaceId]
                    line.push({lon: stopPlace.lon, lat: stopPlace.lat},)
                }

                const l = L.polyline(line).addTo(index)

                polylines.push(l)
            }
        })
    })

}
