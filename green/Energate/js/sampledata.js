/*
 * Copyright 2010 Daniel Lauzon <daniel.lauzon@gmail.com>
 */

/* so we want
 * data = sampleResultWithWeather
 * 
 * data[0].result.hvac[0].tstat_status
 * data[0].result.weather (.currentTemp|.forecastToday(.min|.max))
 * data[0].result.hvac[0].program (.fan|.mode|.hold(.type|.end))
 */
/*
 * from:
 * curl -i -X POST -b "firstenergystaging_sessionid=VALIDSESSIONID" -d '[{"method":"query","url":"scottdesk/helpers/snapshot_helper?days=7;message=messageHelper;hvac=tstat_helper;weather=1","id":1}]' "http://firstenergy-staging.getgreenbox.com/db/query/"; echo
 */
var sampleResultWithWeatherStripped = [{
    "id": 1,
    "result": {
        "hvac": [{
            "node": "hvac1",
            "program": {
                "drlc_opt_out_today": false,
                "schedule": {
                    "type": "52",
                    "days": "IREMOVED THE [[]]"
                },
                "price": {
                    "comfort": 2,
                    "opt_out": false
                },
                "label": "desk",
                "drlc_opt_out_today_time": 1276721174.61,
                "fan": "auto",
                "mode": "cool",
                "drlc_opt_out": false,
                "hold": {
                    "end": 1276798923.13,
                    "setpoint": -1,
                    "ui_units": "hours",
                    "heat": 64,
                    "type": "none",
                    "cool": 78
                },
                "setpoints": "I REMOVED THE SETPOINS",
                "model": "energate"
            },
            "tstat_status": {
                "high_setpoint": 74.9839984894,
                "inside_temp": 67.8920009613,
                "valid": true,
                "connected": true,
                "low_setpoint": 53.9960004807
            }
        }],
        "weather": {
            "currentTemp": 72.29122,
            "forecastToday": {
                "cdd": 0.0,
                "max": 72.29122,
                "hdd": 0.41436859223,
                "min": 56.16296
            }
        }
    }
}];

var sampleResultWithWeather = [{
    "id": 1,
    "result": {
        "elapsedTime": "0.2s",
        "gasTime": "0.0s",
        "electric": {
            "costSnapshot": 0
        },
        "hvac": [{
            "node": "hvac1",
            "program": {
                "equipment_type": "FurnaceAndAirConditioner",
                "drlc_opt_out_today": false,
                "schedule": {
                    "type": "52",
                    "days": [[{
                        "setpoint": 0,
                        "time": "6:15"
                    }, {
                        "setpoint": 1,
                        "time": "8:15"
                    }, {
                        "setpoint": 2,
                        "time": "16:15"
                    }, {
                        "setpoint": 3,
                        "time": "21:00"
                    }], [{
                        "setpoint": 0,
                        "time": "6:00"
                    }, {
                        "setpoint": 1,
                        "time": "8:00"
                    }, {
                        "setpoint": 2,
                        "time": "18:00"
                    }, {
                        "setpoint": 3,
                        "time": "22:00"
                    }], [{
                        "setpoint": 0,
                        "time": "6:00"
                    }, {
                        "setpoint": 1,
                        "time": "8:00"
                    }, {
                        "setpoint": 2,
                        "time": "18:00"
                    }, {
                        "setpoint": 3,
                        "time": "22:00"
                    }], [{
                        "setpoint": 0,
                        "time": "6:00"
                    }, {
                        "setpoint": 1,
                        "time": "8:00"
                    }, {
                        "setpoint": 2,
                        "time": "18:00"
                    }, {
                        "setpoint": 3,
                        "time": "22:00"
                    }], [{
                        "setpoint": 0,
                        "time": "6:00"
                    }, {
                        "setpoint": 1,
                        "time": "8:00"
                    }, {
                        "setpoint": 2,
                        "time": "18:00"
                    }, {
                        "setpoint": 3,
                        "time": "22:00"
                    }], [{
                        "setpoint": 5,
                        "time": "8:00"
                    }, {
                        "setpoint": 3,
                        "time": "21:30"
                    }], [{
                        "setpoint": 5,
                        "time": "8:00"
                    }, {
                        "setpoint": 3,
                        "time": "21:00"
                    }]]
                },
                "price": {
                    "comfort": 2,
                    "opt_out": false
                },
                "label": "desk",
                "drlc_opt_out_today_time": 1276721174.61,
                "fan": "auto",
                "mode": "cool",
                "drlc_opt_out": false,
                "hold": {
                    "end": 1276798923.13,
                    "setpoint": -1,
                    "ui_units": "hours",
                    "heat": 64,
                    "type": "none",
                    "cool": 78
                },
                "setpoints": [{
                    "heat": 79.9879997253,
                    "name": "WAKE       ",
                    "cool": 84.9920009613
                }, {
                    "heat": 53.9960004807,
                    "name": "LEAVE      ",
                    "cool": 74.9839984894
                }, {
                    "heat": 90.986000824,
                    "name": "RETURN     ",
                    "cool": 92.9840019226
                }, {
                    "heat": 54.986000824,
                    "name": "SLEEP      ",
                    "cool": 60.9980010986
                }, {
                    "heat": 59,
                    "name": "UNOCCUPIED ",
                    "cool": 89.9960021973
                }, {
                    "heat": 63.986000824,
                    "name": "OCCUPIED   ",
                    "cool": 83.9839984894
                }, {
                    "heat": 62.995998764,
                    "name": "SETPOINT 7 ",
                    "cool": 86
                }, {
                    "heat": 63.986000824,
                    "name": "REMOTE     ",
                    "cool": 78.9980010986
                }],
                "model": "energate"
            },
            "tstat_status": {
                "high_setpoint": 74.9839984894,
                "inside_temp": 67.8920009613,
                "valid": true,
                "connected": true,
                "low_setpoint": 53.9960004807
            }
        }],
        "waterTime": "0.0s",
        "gas": {
            "costSnapshot": 0
        },
        "community": {},
        "water": {
            "costSnapshot": 0
        },
        "weather": {
            "coolingCostLastMonth": null,
            "coolingSnapshot": 0,
            "forecastFollowing": {
                "cdd": 0.0,
                "max": null,
                "hdd": 0.0,
                "min": null
            },
            "heatingCostLastMonth": null,
            "coolingToday": 0,
            "heatingSnapshot": 0,
            "coolingLastMonth": 0,
            "coolingCostToday": null,
            "heatingThisMonth": 0,
            "coolingCostThisMonth": null,
            "forecastTomorrow": {
                "cdd": 0.0,
                "max": null,
                "hdd": 0.0,
                "min": null
            },
            "coolingThisMonth": 0,
            "currentCondition": "",
            "windDirection": -1,
            "heatingCostSnapshot": null,
            "currentTemp": 72.29122,
            "forecastToday": {
                "cdd": 0.0,
                "max": 72.29122,
                "hdd": 0.41436859223,
                "min": 56.16296
            },
            "heatingCostToday": null,
            "heatingToday": 0,
            "coolingCostSnapshot": null,
            "heatingCostThisMonth": null,
            "windSpeed": 0,
            "heatingLastMonth": 0
        },
        "weatherTime": "0.1s",
        "solar": {
            "savingsSnapshot": 0
        },
        "solarTime": "0.0s",
        "carbon": {},
        "total": {
            "costSnapshot": 0
        },
        "plugSensor": {},
        "hvacTime": "0.1s",
        "electricTime": "0.0s"
    }
}];
