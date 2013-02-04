package se.lth.cs.srl.http;

import java.util.Map;

import se.lth.cs.srl.http.ParseRequestHandler.StringPair;
import se.lth.cs.srl.languages.Language.L;

public abstract class AbstractPipeline {
	public abstract String getStatusString();
	public abstract String getHTMLHead();
	public abstract String getHTMLTail();
	public abstract String getParseInterfaceHTML(L l);
	public abstract StringPair parseRequest(String inputSentence, Map<String, String> vars) throws Exception;
	public abstract DefaultHandler getDefaultHandler(); 
}
