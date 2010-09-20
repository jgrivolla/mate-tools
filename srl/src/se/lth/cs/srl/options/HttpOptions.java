package se.lth.cs.srl.options;

public class HttpOptions extends FullPipelineOptions {

	public int port=8081;

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
		}
		return ai;
	}

}
