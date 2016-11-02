+Array2D {
putArray {|row, myArray|
	//row.postn("this is the row");
	//myArray.postn("and this is the array");
	array.put(row*2, myArray[0]); //.postn("first col");
	array.put((row*2) + 1, myArray[1]); // postn("second col");
} // end method putArray
} // end add methods