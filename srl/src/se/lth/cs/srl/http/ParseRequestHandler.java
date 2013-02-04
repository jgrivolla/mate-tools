package se.lth.cs.srl.http;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

public class ParseRequestHandler extends AbstractHandler {
	
	private DefaultHandler defaultHandler;
	
	public ParseRequestHandler(DefaultHandler defaultHandler,AbstractPipeline pipeline) throws IOException{
		super(pipeline);
		this.defaultHandler=defaultHandler;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//If its not a post request, let the default handler deal with it
		if(!exchange.getRequestMethod().equals("POST")){
			defaultHandler.handle(exchange);
			return;
		}
		
		//Get the parameters from the HTML form
		Map<String,String> vars=AbstractHandler.contentToVariableMap(getContent(exchange));
		String inputSentence=vars.get(sentenceDataVarName);
		//Return if we didnt get a proper sentence to parse
		if(inputSentence==null || inputSentence.length()==0){
			sendContent(exchange,"Error, invalid request.","text/plain");
			return;
		}
		
		//Parse
		String httpResponse;
		String content_type;
		System.out.println("@Parsing ``"+inputSentence+"'' at "+new Date(System.currentTimeMillis()));
		System.out.println("Requested by "+exchange.getRemoteAddress());
		
		try {
			StringPair res=pipeline.parseRequest(inputSentence,vars);
			httpResponse=res.s1;
			content_type=res.s2;
		} catch(Throwable t){
			t.printStackTrace();
			content_type="text/plain";
			httpResponse="Server crashed.";
			sendContent(exchange,httpResponse,content_type);
			System.err.println("Server crashed. Exiting.");
			System.exit(1);
		}
		//Return the response to the client
		sendContent(exchange,httpResponse,content_type);
	}

	static class StringPair {
		final String s1,s2;
		StringPair(String s1,String s2){
			this.s1=s1;
			this.s2=s2;
		}
	}
}
