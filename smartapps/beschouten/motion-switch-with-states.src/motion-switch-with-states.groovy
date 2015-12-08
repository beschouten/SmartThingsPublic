/**
 *  Motion Smart Switch
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
    name: "Motion Switch with States",
    namespace: "beschouten",
    author: "Brian Schouten",
    description: "Motion detection to turn on a dimmer light during the night but not if it is not already on.  Can turn the light off with switch. Will set the level of the dimmer to the level provided in the app with motion, or set to full power from the touch of the switch.  Takes a few cycles after installation to get the states set",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Motion Sensor") { 
        input "motion", "capability.motionSensor", title: "Motion Sensor", required: true, multiple: false
    }
    section("Dimmer Switch") {
    	input "lightswitch", "capability.switch", title: "Choose the lightswitch", required: true, multiple: false
        input "dimmer", "capability.switchLevel", title: "Choose the Dimmer", required: true, multiple: false
        input "dimLevel", "number", title: "Set dimmers to this level", required: true, multiple: false, description: "0 to 99"
    }
}

def installed() {
    installThis()
}

def updated() {
    unsubscribe()
	installThis()
}

def installThis(){
    subscribe(motion, "motion", motionHandler)
    subscribe(lightswitch, "switch", switchHandler)
}

def motionHandler(evt) {
	log.debug "Motion event : ${evt.value}"
	if(isItNight()){
    	state.motion = false
        if(evt.value == "active"){
        	state.motion = true
            log.debug "Motion state is : ${state.motion}"
        }
        if(evt.value == "inactive"){
        	//state.motion = false
   			log.debug "Motion state is : ${state.motion}"
        }
        if(state.switch == false){
			lightHandler()
        }
    }
}

def switchHandler(evt) {
	log.debug "Switch event : ${evt.value}"
	state.switch = false
    if(evt.value == "on"){
		state.switch = true
        log.debug "Switch state is : ${state.switch}"
        lightHandler()
	} 
	if(evt.value == "off"){
    	dimmer.setLevel(0)
        log.debug "Switch state is : ${state.switch}"
    }
}

def private lightHandler(){
	log.debug "calling lightHandler with switch : ${state.switch} and motion : ${state.motion}"
    if(state.switch == false && state.motion == false){
        dimmer.setLevel(0)
    }
	else if(state.switch == true && state.motion == true){
        state.motion = false
    }
    else if(state.switch == true && state.motion == false){
        dimmer.setLevel(99)
    }
    else if(state.switch == false && state.motion == true){
        state.motion = false
        dimmer.setLevel(dimLevel)
    }

}

def private isItNight(){
    if(getSunriseAndSunset().sunrise.time < now() || 
       getSunriseAndSunset().sunset.time > now()){
        return true
    }
    else {
        return false
    }
}