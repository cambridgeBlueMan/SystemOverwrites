
+ Exception {
	reportError {
		this.errorString.postln;
		if (~verbose == true, {
			if(protectedBacktrace.notNil, { this.postProtectedBacktrace });
			this.dumpBackTrace;
			this.adviceLink.postln;
		});
	}
}
+ MethodError {
	reportError {
		this.errorString.postln;
		if (~verbose == true, {
			if(protectedBacktrace.notNil, { this.postProtectedBacktrace });
			this.dumpBackTrace;
			this.adviceLink.postln;
		});
	}
}
+ DoesNotUnderstandError {
	reportError {
		this.errorString.postln;
		if (~verbose == true, {
			if(protectedBacktrace.notNil, { this.postProtectedBacktrace });
			this.dumpBackTrace;
			this.adviceLink.postln;
		});
	}
}
+ DeprecatedError {
	reportError {
		this.errorString.postln;
		if (~verbose == true, {
			if(protectedBacktrace.notNil, { this.postProtectedBacktrace });
			this.dumpBackTrace;
			this.adviceLink.postln;
		});
	}
}
