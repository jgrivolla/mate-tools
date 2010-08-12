package se.lth.cs.srl.http;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import se.lth.cs.srl.CompletePipeline;

import com.sun.net.httpserver.HttpExchange;

public class ParserStatusHandler extends Handler {

	private CompletePipeline pipeline;
	private static final DateFormat dateformat=new SimpleDateFormat();
	
	public ParserStatusHandler(CompletePipeline sp){
		this.pipeline=sp;
	}
	
	public void handle(HttpExchange exchange) throws IOException {
		getContent(exchange); //We dont care about this, but need to read it to be on the safe side
		
		StringBuilder ret=new StringBuilder("Semantic role labeler status, ");
		ret.append("server started ").append(dateformat.format(HttpPipeline.serverStart));
		ret.append("\n\n");

		ret.append(pipeline.getStatusString());
		sendContent(exchange,ret.toString().trim().getBytes(),"text/plain");
	}
}
