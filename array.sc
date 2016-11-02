+ Array {
midi {
	^this.collect({|item, i|
	if (item == \, {Rest},
		{item.notemidi});
	});
}
	asWeights {
		var sum = 0;
		// add all the elements
		this.do({|item, i|
			sum = sum + item;
		});
		// now divide the elements by the total
		^this.collect({|item,i|
			item/sum
		});
	}
}