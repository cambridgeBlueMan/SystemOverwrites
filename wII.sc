/// NOTE: this code is still in an experimental state and only works on Linux, if compiled with Wii support.
/// Therefor, it also has no helpfile yet
/// This code may change without notice; do not use this code, unless you really want to and don't mind
/// having to change your code in the future.
/// Expect this code to be fully functional by version 3.2
/// - october 2007 - nescivi

+WiiMote {
	/*
	var dataPtr, <spec, <actionSpec; // <slots
	var <>id;
	var <battery;
	var <ext_type;
	var <>closeAction, <>connectAction, <>disconnectAction;
	var <calibration;
	var <remote_led, <>remote_buttons, <>remote_motion, <>remote_ir;
	var <>nunchuk_buttons, <>nunchuk_motion, <>nunchuk_stick;
	var <>classic_buttons, <>classic_stick1, <>classic_stick2, <>classic_analog;
	var <>dumpEvents = false;
	classvar all;
	classvar < eventLoopIsRunning = false;
	*/
	//	classvar < updateDataTask, <updateTask;
	*initDict {
		/* the actionSpec identity dictionary contains a key for every button and controller of the wiimote. assocuated with each key is a dictionary
		*/
		var spec = this.devicesMap;
		var actionSpec = IdentityDictionary.new;
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//spec.postln;
		// spec['wii_mote'].postln;
		spec['wii_mote'].do({|item, i|
			//item.postln;
			actionSpec[item]= Dictionary.new(0);
		}) ; // end spec.do
		spec['wii_nunchuk'].do({|item, i|
			//item.postln;
			actionSpec[item]= Dictionary.new(0);
		}) ; // end spec.do

		// now return the dictionary
		^actionSpec.deepCopy;
	}
		*start{ |updtime=1|
		ShutDown.add {
			this.closeAll;
			this.prStop;
		};
		this.prStart( updtime );
		eventLoopIsRunning = true;
	}
	*discover{|updtime = 0.1|
		var newid, newwii, newall;
		if ( eventLoopIsRunning.not, { this.start(updtime); } );
		newid = all.size;
		newwii = WiiMote.new;
		"To discover the WiiMote, please press buttons 1 and 2 on the device and wait till the LEDs stop blinking".postln;
		newall = all.copy.add(newwii);
		if(this.prDiscover( newid, newall )) {
			// prDiscover returns true if the device was added, so then update the 'all' array.
			all = newall;
			^newwii;
		};
		^nil;
	}
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	setAction{ |key,keyAction|
		var p = Pipe.new("uuidgen", "r");
		var stamp = p.getLine;
		p.close;
		// key.postn("this is a key"); keyAction.postcs ; //("this is an action");
		actionSpec[key].add(stamp.asSymbol ->  keyAction);
		^stamp.asSymbol;
	}
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	removeAction{ |key, stamp|
		actionSpec[key].removeAt(stamp );
	}
	initDict {
		spec = this.deviceSpec;
		actionSpec = IdentityDictionary.new;
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		spec.do({|item, i|
			//item.postln;
			actionSpec[spec.findKeyForValue(item)]= Dictionary.new(0);
		}) ; // end spec.do
	}
	setDict {|dict|
		actionSpec = dict;
	}

	// PRIVATE
	prInit {
		remote_led = Array.fill( 4, 0 );
		remote_buttons = Array.fill( 11, 0 );
		remote_motion = Array.fill( 4, 0 );
		remote_ir = Array.fill( 4, { WiiMoteIRObject.new } );
		nunchuk_buttons = Array.fill( 2, 0 );
		nunchuk_motion = Array.fill( 4, 0 );
		nunchuk_stick = Array.fill( 2, 0 );
		classic_buttons = Array.fill( 15, 0 );
		classic_stick1 = Array.fill( 2, 0 );
		classic_stick2 = Array.fill( 2, 0 );
		classic_analog = Array.fill( 2, 0 );
		battery = 0;

		this.prOpen;

		closeAction = {};
		connectAction = {};
		disconnectAction = {};

		//		//		this.prWiiGetLED( remote_led );
		//		calibration = this.prCalibration(WiiCalibrationInfo.new);

		spec = this.deviceSpec;
		actionSpec = IdentityDictionary.new;
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		spec.do({|item, i|
			//item.postln;
			actionSpec[spec.findKeyForValue(item)]= Dictionary.new(0);
		}) ; // end spec.do
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		~wiiLatch = Array.fill(2,{"off"});

	}
	prHandleButtonEvent{ |buttonData|
		var symbolArray = [ \bA, \bB, \bOne, \bTwo, \bMinus, \bHome, \bPlus, \bUp, \bDown, \bLeft, \bRight ];
		//		buttonData are bits that decode to separate buttons
		//  (do in Primitive internally, and pass on Array?
		//("handle button Event"+buttonData).postln;
		remote_buttons = buttonData;
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		remote_buttons.do({|item, i|
			//	if (item == 1, {
			actionSpec[symbolArray[i]].do({|jitem, j|
				// now iterate the list of actions
				jitem.value(item);
			}); // end actionSpecs.do
			//	}); // end if
		});//  end remote buttons do
		/*
		if ( dumpEvents, { (key + spec.at(key).value.round(0.00001)).postln; });
		}
		*/
	}

	prHandleNunchukEvent{ |nunchukButtons, nunJoyX, nunJoyY, nunAccX, nunAccY, nunAccZ|
		var symbolArray = 	[ \nax, \nay, \naz, \nsx, \nsy, \nbZ, \nbC ];
		//~wiiLatch[\nbZ] = "off";
		//~wiiLatch[\nbC] = "off";
		// buttonData are bits that decode to separate buttons (do in Primitive)
		nunchuk_buttons = nunchukButtons;
		nunchuk_motion = [ nunAccX, nunAccY, nunAccZ, 0 ];
		nunchuk_stick = [ nunJoyX, nunJoyY ];
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		nunchuk_buttons.do({|item, i|
			if (item == 1 && ~wiiLatch[i] == "off", {
				actionSpec[symbolArray[i+5]].do({|jitem, j|
					// now iterate the list of actions
					jitem.value(item)
				}); // end actionSpecs.do
				//turn on ~wiiLatch
				~wiiLatch[i] = "on";
			});
			if (item ==0 && ~wiiLatch[i] == "on", {
				actionSpec[symbolArray[i+5]].do({|jitem, j|
					// now iterate the list of actions
					jitem.value(item)
				}); // end actionSpecs.do
				//turn on ~wiiLatch
				~wiiLatch[i] = "off"
			});
		});//  end nunchuk buttons do
		//*********
		// JOYSTICK
		nunchuk_stick.do({|item, i|
			actionSpec[symbolArray[i+3]].do({|jitem, j|
				// now iterate the list of actions
				jitem.value(nunchuk_stick[i])
			}); // end actionSpecs.do
		});//  end nunchuk stick do
		~joy = case
		{nunchuk_stick[0] > 0.8} {if (~joy != "right", {~joy = "right"});}
		{nunchuk_stick[0] < 0.2} {if (~joy != "left", {~joy = "left"});}
		{nunchuk_stick[1] > 0.8} {if (~joy != "up", {~joy = "up"});}
		{nunchuk_stick[1] < 0.2} {if (~joy != "down", {~joy = "down"});}
		{nunchuk_stick[0] > 0.2 && nunchuk_stick[0] < 0.8} {if (~joy != "blank", {~joy = "blank"});}
		{nunchuk_stick[1] > 0.2 && nunchuk_stick[1] < 0.8} {if (~joy != "blank", {~joy = "blank"});};
		// ~joy.postln;

		//*************
		// ACCELERATORS
		// note size because of weird extra 0 in the motion array, see above
		(nunchuk_motion.size-1).do({|item, i|
			actionSpec[symbolArray[i]].do({|jitem, j|
				// now iterate the list of actions
				jitem.value(nunchuk_motion[i]);
				actionSpec.at(symbolArray[i]).changed(symbolArray[i], nunchuk_motion[i]);
			}); // end actionSpecs.do
		});//  end nunchuk accel  do
		/*
		[ \nax, \nay, \naz, \nsx, \nsy, \nbZ, \nbC ].do{ |key|
		actionSpec.at( key ).value( spec.at(key).value );
		if ( dumpEvents, { (key + spec.at(key).value.round(0.00001)).postln; });
		}
		*/
	}

	prHandleAccEvent{ |accX,accY,accZ|
		remote_motion = [ accX, accY, accZ, 0 ];
		// actions:
		[ \ax, \ay, \az ].do({ |item, i|
			//item.postln;
			actionSpec.at( item ).do ({|jitem, j|
				//j.postln
				//remote_motion[j].postln;
				jitem.value(remote_motion[i]);
				// alternative approach
				actionSpec.at(item).changed(item, remote_motion[i]);
			});
		});
	}

	prHandleEvent {
		| buttonData, posX, posY, angle, tracking, accX, accY, accZ, orientation, extType, eButtonData, eData1, eData2, eData3, eData4, eData5, eData6, batteryLevel |
		battery = batteryLevel;
		remote_buttons = buttonData;
		remote_motion = [ accX, accY, accZ, orientation/3 ];
		remote_ir = [ posX, posY, angle, tracking ];
		ext_type = extType;
		if ( extType == 1, {
			nunchuk_buttons = eButtonData;
			nunchuk_motion = [ eData3, eData4, eData5, eData6/3 ];
			nunchuk_stick = [ eData1, eData2 ];
		});
		if ( extType == 2, {
			//			classic_buttons.do{ |it,i| classic_buttons[i] = eButtonData.bitTest( i ).asInteger };
			classic_buttons = eButtonData;
			classic_stick1 = [eData1, eData2];
			classic_stick2 = [eData3, eData4];
			classic_analog = [eData5, eData6];
		});

		// event callback
		spec.keysValuesDo{ |key,val,i|
			actionSpec.at( key ).value( val.value );
			if ( dumpEvents, { (key + val.value.round(0.00001)).postln; });
		}
	}
}

// EOF
