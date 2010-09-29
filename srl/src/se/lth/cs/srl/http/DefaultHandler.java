package se.lth.cs.srl.http;

import java.io.IOException;

import se.lth.cs.srl.languages.Language.L;

import com.sun.net.httpserver.HttpExchange;

public class DefaultHandler extends Handler {
	
	public DefaultHandler(L currentL){
		super.setupPages(currentL); //Note this is a bit nasty, with the pages being static, this being an object setting the language stuff, but we want the pages to be static. Or rather, this is TODO: make the handler non-static and use the object instead.
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if(HttpPipeline.isReady()){
			servePage("default",exchange);
		} else {
			servePage("notReady",exchange);
		}
	}
	
	private void servePage(String pageName,HttpExchange exchange) throws IOException{
		getContent(exchange); //We dont care about this, but need to read it to be on the safe side
		String content;
		if(pageName.equals("default")){
			content=HTMLHEAD+
					pages.get(pageName)+
					HTMLTAIL;
		} else if(pageName.equals("notReady")){
			content=HTMLHEAD+
					pages.get(pageName)
					+HTMLTAIL;
		} else {
			return; //This is just wrong. We shouldn't enter here.
		}
		sendContent(exchange,content,"text/html; charset=utf-8");
	}
}
