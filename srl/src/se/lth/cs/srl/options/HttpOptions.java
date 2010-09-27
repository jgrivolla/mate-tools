package se.lth.cs.srl.options;

import se.lth.cs.srl.http.HttpPipeline;

public class HttpOptions extends FullPipelineOptions {

	public int port=8081;
	public boolean https=false;

	@Override
	String getSubUsageOptions() {
		return "-port   <int>     the port to use (default 8081)";
	}

	@Override
	int trySubParseArg(String[] args, int ai) {
		if(args[ai].equals("-port")){
			ai++;
			port=Integer.valueOf(args[ai]);
			ai++;
		} else if(args[ai].equals("-https")){ //TODO test that this works, and document this. If people want to use HTTPS for some reason.
			ai++;
			https=true;
		}
		return ai;
	}

	@Override
	Class<?> getIntendedEntryClass() {
		return HttpPipeline.class;
	}

}
