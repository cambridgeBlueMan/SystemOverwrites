+ Object {
	postn {arg note; this.asString.postn(note); } // end method postln
	postlea {|note|
		if (~debug == true, {
			this.asString.postn(note);
		}); 

	}
}
+ String {
	postn {arg note; note.post; ": ".post; this.postln; }
	postlea {|note|
		if (~debug == true, {
			note.post; ": ".post; this.postln;
		}); 

	}
}