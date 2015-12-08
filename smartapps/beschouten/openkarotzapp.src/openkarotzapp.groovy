/**
 *  OpenKarotzApp
 *
 *  Copyright 2015 Brian Schouten
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "OpenKarotzApp",
    namespace: "beschouten",
    author: "Brian Schouten",
    description: "OpenKarotz Controller, requires OpenKarotz Device",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("What Karotz did you define? This Must be created with the Karotz Device type)") {
		input "karotz", "capability.speechSynthesis", required: true, multiple: false
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    subscribe(location, "mode", modeChangeHandler)
}

// Handlers

def modeChangeHandler(mode){
	 log.debug("Event ID : '${mode.id}'")
     log.debug("String value of the event : '${mode.stringValue}'")
     def modeString = mode.stringValue
     
     if(modeString == "Home"){
     	karotz.ledFlashColor("000000", 3000, "00FF00")
        karotz.speak("Welcome Home")
     }
     if(modeString == "Away"){
     	karotz.ledFlashColor("000000", 3000, "FF0000")
        karotz.speak("Everyone is Away")
     }
     if(modeString == "Night"){
     	karotz.ledFlashColor("000000", 3000, "0000FF")
        karotz.speak("Good Night")
     }
}

def parse(description) {
	log.debug "parse description: '$description'"
    def data = parseLanMessage(description)
    def actions = []
    
    def headerMap = description.headers      // => headers as a Map
    def status = description.status          // => http status code of the response
    def json = description.json              // => any JSON included in response body, as a data structure of lists and maps
    
}