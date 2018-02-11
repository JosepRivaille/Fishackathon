import ZoneModel from './model';
import classifyPoint from 'robust-point-in-polygon';

module.exports =
    {
        deleteZones: function () {
            deleteZones()
        },
        insertZones: function () {
            insertZones()
        },
        nearZones: function (res, lat, lng) {
            nearZones(res, lat, lng)
        },
        insideZones: function (res, lat, lng) {
            insideZones(res, lat, lng)
        },
        getDistanceFromLatLonInKm: function (lat1, lon1, lat2, lon2) {
            getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2)
        }
    };

function deleteZones() {
    ZoneModel.deleteMany({}, function (err, r) {
        if (err) {
            console.log(err)
        } else {
            console.log("OK")
        }
    });
}

function insertZones() {
    var fs = require('fs');
    var json = JSON.parse(fs.readFileSync('src/zones/FAO_AREAS/FAO_AREAS.json', 'utf8'));
    var features = json["features"];

    for (var i = 0; i < features.length; i++) {
        var properties = features[i]["properties"];
        var code = properties["F_CODE"];
        var level = properties["F_LEVEL"];
        var ocean = properties["OCEAN"].toUpperCase();
        if (!["ATLANTIC", "INDIAN", "PACIFIC", "ARTIC"].includes(ocean)) ocean = "ATLANTIC";
        var parent = "";
        if (level === "SUBAREA") parent = properties["F_AREA"];
        else if (level === "DIVISION") parent = properties["F_SUBAREA"];
        else if (level === "SUBDIVISION") parent = properties["F_DIVISION"];
        else if (level === "SUBUNIT") parent = properties["F_SUBDIVIS"];

        var geometry = features[i]["geometry"];
        var coordinates = [];
        var centroids = [];

        //console.log(geometry.coordinates)
        for (var p = 0; p < geometry.coordinates.length; ++p) {
            var polygon = geometry.coordinates[p];
            var firstring = polygon[0];
            var polygoncoords = []
            firstring.forEach(function (point) {
                var latlong = {
                    lat: point[1],
                    lng: point[0]
                }
                polygoncoords.push(latlong);
            });
            var centerpolygon = computeCenterOfPolygon(polygoncoords);
            centroids.push(centerpolygon);

            coordinates.push(polygoncoords);
        }

        var centroidLat = centroids.reduce((a, o, i, p) => a + o.lat / p.length, 0);
        var centroidLng = centroids.reduce((a, o, i, p) => a + o.lng / p.length, 0);
        var centroid = {lat: centroidLat, lng: centroidLng};

        if (coordinates.length > 0) createZone(code, level, ocean, parent, coordinates, centroid);
    }
}

function createZone(code, level, ocean, parent, polygon, centroid) {

    const zone = new ZoneModel({
        id: undefined,
        code,
        level,
        ocean: ocean.toUpperCase(),
        parent,
        polygon: polygon,
        centroid,
        laws: []
    });
    zone.save();
}

function computeCenterOfPolygon(polygon) {
    var maxLat = Math.max.apply(Math, polygon.map(function (o) {
        return o.lat;
    }));
    var minLat = Math.min.apply(Math, polygon.map(function (o) {
        return o.lat;
    }));
    var maxLng = Math.max.apply(Math, polygon.map(function (o) {
        return o.lng;
    }));
    var minLng = Math.min.apply(Math, polygon.map(function (o) {
        return o.lng;
    }));

    var center = {
        lat: minLat + (maxLat - minLat) / 2,
        lng: minLng + (maxLng - minLng) / 2
    }

    return center;
}

function insideZones(res, lat, lng) {
    var coordinates = [lat, lng];

    ZoneModel.find({}, function (err, zones) {
        if (err) {
            console.log(err);
            res.send("ERROR");
        } else {
            var zonesInside = [];
            zones.forEach(function (zone) {
                zone.polygon.forEach(function (polygon) {
                    var polygonArray = polygon.map(Object.values);

                    //var isInside = inside(coordinates, polygonArray);
                    var isInside = classifyPoint(polygonArray, coordinates);
                    if (isInside) zonesInside.push(zone);
                })
            })

            var numCharsSmallestZoneCode = 0;
            var smallestZoneInside = {};
            zonesInside.forEach(function (zone) {
                if (numCharsSmallestZoneCode < zone.code.length) {
                    numCharsSmallestZoneCode = zone.code.length;
                    smallestZoneInside = zone;
                }
            })

            res.send(smallestZoneInside);
        }
    });
}

function nearZones(res, lat, lng) {
    var maxDistance = 1500;

    ZoneModel.find({
        $or: [
            {code: {$eq: "34.1.11"}},
            {code: {$eq: "27.8.e.1"}},
            {code: {$eq: "31.1.12"}},
            {code: {$eq: "27.9.b.2"}},
            {code: {$eq: "27.4.a"}},
        ]
    }, function (err, zones) {
        if (err) {
            console.log(err);
            res.send("ERROR");
        } else {
            var result = [];

            result = zones.filter(zone => {
                var zoneIsNear = false;
                var i = 0;
                while (!zoneIsNear && i < zone.polygon.length) {
                    var j = 0;
                    while (!zoneIsNear && j < zone.polygon[i].length) {
                        var point = zone.polygon[i][j];
                        var distFromZone = getDistanceFromLatLonInKm(lat, lng, point.lat, point.lng);
                        if (distFromZone < maxDistance) {
                            zoneIsNear = true;
                            return true;
                        }
                        ++j;
                    }
                    ++i;
                }
                return false;
            });

            console.log(result.length);

            result = result.map(zone => {
                zone.lawss = [
                    {
                        "id": "67fc9420-0f1b-11e8-a6be-53fb65ec0967",
                        "title": "RD 347/11",
                        "abstract": "Regula pesca marítima de recreo en aguas exteriores. Deroga: O 26.2.99 mod. por O 24.7.00 EXCEPTO CUPOS.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/rd-347-11.pdf",
                        "affects": [
                            "RECREATIONAL"
                        ]
                    },
                    {
                        "id": "a9b7f8a0-0f1b-11e8-a6be-53fb65ec0967",
                        "title": "RD 429/04 C",
                        "abstract": "Medidas ordenación flota pesquera de cerco. Mod. por RD 2176/04.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/rd-429-04.pdf",
                        "affects": [
                            "FENCE"
                        ]
                    },
                    {
                        "id": "cabc93d0-0f1b-11e8-a6be-53fb65ec0967",
                        "title": "O 910/06 C",
                        "abstract": "Regula tipos arrastre fondo en el Caladero Nacional (prohíbe arrastre de bolos y tangones). DEROGADA en lo que la contradiga por O 1307/13. Deroga art. 4 de la O 16/02.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-910-06.pdf",
                        "affects": [
                            "DRAGGER"
                        ]
                    },
                    {
                        "id": "f9018480-0f1b-11e8-a6be-53fb65ec0967",
                        "title": "O 609/07",
                        "abstract": "Puertos desembarque con volantas de fondo VIa,b y VIIbcjk y subárea XII.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-609-07.pdf",
                        "affects": [
                            "NET"
                        ]
                    },
                    {
                        "id": "2b130d90-0f1c-11e8-a6be-53fb65ec0967",
                        "title": "O 6.10.99",
                        "abstract": "Prohibición de pesca con mosca y claro.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-6-10-99.pdf",
                        "affects": [
                            "FLY"
                        ]
                    },
                    {
                        "id": "51b1fc40-0f1c-11e8-a6be-53fb65ec0967",
                        "title": "RD 1379/02",
                        "abstract": "Deroga antiguo RD almadrabas (Real Decreto de 4 de julio de 1924).",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/rd-1379-02.pdf",
                        "affects": [
                            "TRAP"
                        ]
                    },
                    {
                        "id": "626a1360-0f1c-11e8-a6be-53fb65ec0967",
                        "title": "O 62/03 C",
                        "abstract": "Regula pesca almadraba y la concesión de licencias. Mod. por O 2795/05.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-62-03.pdf",
                        "affects": [
                            "TRAP"
                        ]
                    },
                    {
                        "id": "c239f800-0f1c-11e8-a6be-53fb65ec0967",
                        "title": "O 1101/17",
                        "abstract": "Convoca el procedimiento de autorización para la extracción y venta de coral rojo.",
                        "resource": "https://www.boe.es/boe/dias/2017/11/16/pdfs/BOE-A-2017-13204.pdf",
                        "affects": [
                            "CORAL"
                        ]
                    },
                    {
                        "id": "f207ab90-0f1c-11e8-a6be-53fb65ec0967",
                        "title": "O 658/14 NC",
                        "abstract": "Regula la pesca de palangre de superficie. Modificada por O 2210/15 y por O 1057/17 (que deroga la O 1647/09 reguladora de la pesca de especies altamente migratorias). Prohíbe la captura, tenencia a bordo, desembarque o comercialización de pez espada (Xiphias gladius) tiburón azul (Prionacea glauca), Marrajo dientuso (Ixurus oxyrhinchus) y cualquier otro tiburón pelágico, incluida la captura accesoria o fortuita, por parte de cualquier buque que no se encuentre incluido en el censo unificado de palangre de superficie (CUPS). Deroga la O 2521/06 que a su vez derogaba las: O 08.01.93 Pal. sup. cal. int.; O 06.11.95 flota pez espada; O 08.03.99 Palangre en Med.; O 1127/02 para reducir mort. aves marinas; y O 18.01.84 salvo pesca PALOMETA NEGRA.",
                        "resource": "https://normativapesquera.files.wordpress.com/2014/06/o-658-14.pdf",
                        "affects": [
                            "HOOK"
                        ]
                    },
                    {
                        "id": "24328900-0f1d-11e8-a6be-53fb65ec0967",
                        "title": "O 18.01.84 NC",
                        "abstract": "Palangre de superficie para palometa negra. Resto derogada por O 2521/06.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-18-01-84.pdf",
                        "affects": [
                            "HOOK"
                        ]
                    },
                    {
                        "id": "32271350-0f1d-11e8-a6be-53fb65ec0967",
                        "title": "Res 11.2.16",
                        "abstract": "Censo unificado 2016 flota palangre superficie. Corr. Err.",
                        "resource": "http://www.boe.es/boe/dias/2016/02/23/pdfs/BOE-A-2016-1871.pdf",
                        "affects": [
                            "HOOK"
                        ]
                    },
                    {
                        "id": "3eb38220-0f1d-11e8-a6be-53fb65ec0967",
                        "title": "O 1267/11 NC",
                        "abstract": "Plan gestión palangre de superficie para 2011, 2012 y 2013 en Atlántico, Índico y Pacífico. Prohíbe captura, transbordo y desembarco de TIBURONES ZORRO y TIBURONES MARTILLO. Art. 4 mod. por O 1360/12.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-1267-11.pdf",
                        "affects": [
                            "HOOK"
                        ]
                    },
                    {
                        "id": "ce85bee0-0f1d-11e8-96c9-e118e0b016f6",
                        "title": "O 1406/16 NC",
                        "abstract": "Establece un Plan de gestión para los buques de los censos del caladero nacional del Golfo de Cádiz.",
                        "resource": "http://www.boe.es/boe/dias/2016/08/26/pdfs/BOE-A-2016-8052.pdf",
                        "affects": []
                    },
                    {
                        "id": "2a515950-0f1e-11e8-96c9-e118e0b016f6",
                        "title": "O 1805/13 NC",
                        "abstract": "Ayudas compensatorias a determinada flota pesquera que faena en aguas adyacentes al Peñón de Gibraltar.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-1805-13.pdf",
                        "affects": [
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "5c1a40a0-0f1e-11e8-96c9-e118e0b016f6",
                        "title": "RD 632/93 NC",
                        "abstract": "Regula la pesca de arrastre de fondo en el CN del Golfo de Cádiz. Mod. por RD 60/94.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/rd-632-93.pdf",
                        "affects": [
                            "DRAGGER",
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "9f4e1450-0f1e-11e8-96c9-e118e0b016f6",
                        "title": "O 7.5.87",
                        "abstract": "Crea el permiso temporal de arrastre de fondo en la región Suratlántica para buques de 5 a 35 TRB.",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-7-5-87.pdf",
                        "affects": [
                            "DRAGGER",
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "aca09cd0-0f1f-11e8-96c9-e118e0b016f6",
                        "title": "O 22.11.96 NC",
                        "abstract": "Prohíbe pesca con arrastre pelágico y semipelágico en Regiones 2 y 3. Derogada en lo que le afecte por el plan de gestión del CNO (O 1307/13).",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-22-11-96.pdf",
                        "affects": [
                            "DRAGGER",
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "d0576a40-0f20-11e8-96c9-e118e0b016f6",
                        "title": "O 22.11.96 NC",
                        "abstract": "Prohíbe pesca con arrastre pelágico y semipelágico en Regiones 2 y 3. Derogada en lo que le afecte por el plan de gestión del CNO (O 1307/13).",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/o-22-11-96.pdf",
                        "affects": [
                            "DRAGGER",
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "da65eb50-0f21-11e8-96c9-e118e0b016f6",
                        "title": "O 142/15",
                        "abstract": "Establece para 2015 los periodos de pesca autorizados para la flota de arrastre de fondo del caladero Golfo de Cádiz que capture cigala.",
                        "resource": "https://normativapesquera.files.wordpress.com/2015/02/o-142-15.pdf",
                        "affects": [
                            "DRAGGER",
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "4614c0b0-0f22-11e8-96c9-e118e0b016f6",
                        "title": "Res 31.3.17",
                        "abstract": "Resolución de 31 de marzo de 2017, de la Secretaría General de Pesca, por la que se publican las cuotas de cigala para los buques del censo de arrastre de fondo del Golfo de Cádiz durante el año 2017.",
                        "resource": "http://www.boe.es/boe/dias/2017/04/12/pdfs/BOE-A-2017-4103.pdf",
                        "affects": [
                            "DRAGGER",
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "031db270-0f23-11e8-9dac-abd930d2406b",
                        "title": "Res 27.10.17",
                        "abstract": "Establece disposiciones de ordenación de la pesquería del jurel IXa para los buques del Golfo de Cádiz: 2.500 Kg/d/b.",
                        "resource": "http://www.boe.es/boe/dias/2017/11/08/pdfs/BOE-A-2017-12888.pdf",
                        "affects": [
                            "PROFESSIONAL",
                            "FENCE"
                        ]
                    },
                    {
                        "id": "46f56740-0f23-11e8-9dac-abd930d2406b",
                        "title": "Res 28.2.17",
                        "abstract": "Publica el censo de los buques de cerco autorizados a pescar boquerón en el caladero del Golfo de Cádiz durante el año 2017, así como la cuota individual de boquerón asignada a cada uno.",
                        "resource": "http://www.boe.es/boe/dias/2017/03/13/pdfs/BOE-A-2017-2733.pdf",
                        "affects": [
                            "PROFESSIONAL",
                            "FENCE"
                        ]
                    },
                    {
                        "id": "88fc7ac0-0f23-11e8-9dac-abd930d2406b",
                        "title": "RD 1428/97 NC",
                        "abstract": "Regula la pesca con artes menores en el caladero del Golfo de Cádiz. Mod. por RD 284/06",
                        "resource": "https://normativapesquera.files.wordpress.com/2013/10/rd-1428-97.pdf",
                        "affects": [
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "8d613f00-0f24-11e8-9dac-abd930d2406b",
                        "title": "Res 12.8.16",
                        "abstract": "Lista de buques de artes menores censados en el Golfo de Cádiz que pueden pescar pulpo al sur del paralelo 36º 22,9´N (Isla de Sancti Petri) durante el año 2016, bajo las condiciones establecidas por la O 1537/13. Corr. Errores",
                        "resource": "http://www.boe.es/boe/dias/2016/08/13/pdfs/BOE-A-2016-7867.pdf",
                        "affects": [
                            "PROFESSIONAL"
                        ]
                    },
                    {
                        "id": "dbdf79b0-0f26-11e8-9dac-abd930d2406b",
                        "title": "Atún rojo Veda",
                        "abstract": "Veda para el Atún Rojo",
                        "resource": "http://www.juntadeandalucia.es/export/drupaljda/vedas_pesquerias_especies_2017_09012017.pdf",
                        "affects": [
                            "PROFESSIONAL",
                            "FENCE"
                        ]
                    },
                    {
                        "id": "1abac8b0-0f27-11e8-9dac-abd930d2406b",
                        "title": "Atún rojo recreativa",
                        "abstract": "Veda para el Atún recreativa",
                        "resource": "http://www.juntadeandalucia.es/export/drupaljda/vedas_pesquerias_especies_2017_09012017.pdf",
                        "affects": [
                            "RECREATIONAL"
                        ]
                    },
                    {
                        "id": "1747f3a0-0f28-11e8-9dac-abd930d2406b",
                        "title": "Atún rojo cevo vivo",
                        "abstract": "Veda para el Atún con cevo vivo",
                        "resource": "http://www.juntadeandalucia.es/export/drupaljda/vedas_pesquerias_especies_2017_09012017.pdf",
                        "affects": [
                            "HOOK"
                        ]
                    }
                ];
                return zone;
            });
            res.send(result);
        }
    });
}

function getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
    var R = 6371; // Radius of the earth in km
    var dLat = deg2rad(lat2 - lat1);  // deg2rad below
    var dLon = deg2rad(lon2 - lon1);
    var a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2)
    ;
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    var d = R * c; // Distance in km
    return d;
}

function deg2rad(deg) {
    return deg * (Math.PI / 180)
}

