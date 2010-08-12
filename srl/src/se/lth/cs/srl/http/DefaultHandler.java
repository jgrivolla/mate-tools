package se.lth.cs.srl.http;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class DefaultHandler extends Handler {
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		getContent(exchange); //We dont care about this, but need to read it to be on the safe side
		String ret=HTMLHEAD+(HttpPipeline.isReady()?pages.get("default"):pages.get("notReady"))+HTMLTAIL;
		sendContent(exchange,ret.getBytes(),"text/html; charset=utf-8");
	}
}
