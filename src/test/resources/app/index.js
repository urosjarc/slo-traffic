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
        res.json().then(cameras => {
            for (camera of cameras) {
                const m = L.marker(camera.location, {title: camera.description.sl, alt: camera.imgUrl}).addTo(index).on('click', (e) => {
                    openInNewTab(e.target.options.alt)
                });
                markers.push(m)
            }
        })
    })

}

function weather(key, icon, title) {
    clearMap()
    // Place line on the map
    fetch("/weather").then(res => {
        res.json().then(weathers => {
            for (w of weathers[key]) {
                const m = L.marker(w.location, {icon: icon(w), title: title(w)}).addTo(index)
                markers.push(m)
            }
        })
    })
}

function wind() {

    weather("wind", (data) => {
        let icon
        if (data.speed < 7.9) icon = greenIcon
        else if (data.speed < 20.7) icon = orangeIcon
        else icon = redIcon
        return icon
    }, (data) => `Smer ${data.direction} deg, hitrost ${data.speed} m/s z sunki do ${data.maxSpeed} m/s`)
}

function temp() {
    weather("temperature", (data) => {
        if (data.value < 0) return redIcon
        else if (data.value < 15) return orangeIcon
        else return greenIcon
    }, (data) => `${data.value} stopinj`)
}

function humidity() {
    weather("humidity", (data) => {
        if (data.percentage < 50) return greenIcon
        else if (data.percentage < 90) return orangeIcon
        else return redIcon
    }, (data) => `${data.percentage} %`)
}

function precipitation() {
    weather("precipitation", (data) => blueIcon, (data) => data.type)
}

function visibility() {
    weather("visibility", (data) => {
        if (data.distance < 500) return redIcon
        else if (data.distance < 1000) return orangeIcon
        else return greenIcon
    }, (data) => `${data.distance} metrov`)
}

function roadSurface() {
    weather("roadSurface", (data) => {
        if (data.condition === "wet") return redIcon
        else if (data.condition === "dry") return greenIcon
        else return orangeIcon
    }, (data) => `Cesta je "${data.condition}" pri temperaturi ${data.temperature} z vodno podlago ${data.waterThickness * 1000} milimetrov`)
}

function counters() {
    clearMap()
    // Place line on the map
    fetch("/counters").then(res => {
        res.json().then(counters => {
            for (counter of counters) {
                const density = counter.trafficConcentration.density
                let icon
                if (density < 7) icon = greenIcon
                else if (density < 14) icon = orangeIcon
                else icon = redIcon
                const m = L.marker(counter.location, {
                    icon: icon,
                    title: `Speed: ${counter.trafficSpeed.average} m/s, Density: ${density}, Flow: ${counter.trafficFlow.rate}`
                }).addTo(index)
                markers.push(m)
            }
        })
    })


}

function events() {
    clearMap()
    // Place line on the map
    fetch("/events").then(res => {
        res.json().then(events => {
            for (evnt of events) {
                let icon = evnt.capacityRemaining === 0 ? redIcon : orangeIcon
                const m = L.marker(evnt.location, {icon: icon, title: evnt.comment.sl}).addTo(index)
                markers.push(m)
            }
        })
    })
}

function restAreas() {
    clearMap()
    // Place line on the map
    fetch("/rest-areas").then(res => {
        res.json().then(areas => {
            for (area of areas) {
                const m = L.marker(area.location, {title: area.description.sl}).addTo(index)
                markers.push(m)
            }
        })
    })


}

function roadWork() {
    clearMap()
    // Place line on the map
    fetch("/road-work").then(res => {
        res.json().then(events => {
            for (evnt of events) {
                let icon = evnt.capacityRemaining === 0 ? redIcon : orangeIcon
                const m = L.marker(evnt.location, {icon: icon, title: evnt.comment.sl}).addTo(index)
                markers.push(m)
            }
        })
    })
}
