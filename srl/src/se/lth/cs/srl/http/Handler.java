package se.lth.cs.srl.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class Handler implements HttpHandler {
	private static final String STYLESHEET=
		"<style type=\"text/css\">\n" +
		"  table { background-color:#000000 }\n" +
		"  td { background-color: #EEEEEE}\n" +
		"  th { background-color: #EEEEEE}\n" +
		"  .topRowCell {border-bottom: 1px solid black}\n" +
		"  .A0, .C-A0 {background-color:#CCCC00}\n" +
		"  .A1, .C-A1 {background-color:#CC0000}\n" +
		"  .A2, .C-A2 {background-color:#00CC00}\n" +
		"  .A3, .C-A3 {background-color:#0000CC}\n" +
		"  .AM-NEG {background-color:#CC00CC}\n" +
		"  .AM-MNR {background-color:#00CCCC}\n" +
		"  .ARG_DEFAULT {background-color:#CCCCCC}\n" +
		"</style>\n";
	protected static final String sentenceDataVarName="sentence";
	protected static final String HTMLHEAD="<html><head>\n<title>Semantic Parser</title>\n"+STYLESHEET+"</head>\n<body>\n";
	protected static final String HTMLTAIL="</body>\n</html>";
	protected static Map<String,String> pages=new HashMap<String,String>();
	
	static {
		pages.put("default",
				  "  <h3>Try the semantic parser</h3>\n" +
				  "  <form action=\"/parse\" method=\"POST\">\n" +
				  "    <table cellpadding=\"2\" cellspacing=\"2\">\n" +
				  "      <tr><td valign=\"center\"><b>Input</b><td><textarea name=\""+sentenceDataVarName+"\" rows=\"3\" cols=\"40\"></textarea></td></tr>\n" +
				  "      <tr><td valign=\"center\"><b>Return type</b><td><input type=\"radio\" name=\"returnType\" value=\"html\" checked=\"checked\" />&nbsp;&nbsp;HTML<br /><input type=\"radio\" name=\"returnType\" value=\"text\"/>&nbsp;&nbsp;Raw text</td></tr>\n" +
				  "      <tr><td colspan=\"2\"><input type=\"checkbox\" name=\"doPerformDictionaryLookup\" /> Attempt to lookup and reference predicates in dictionary<sup>&dagger;</sup></td></tr>." +
				  "      <tr><td colspan=\"2\" align=\"center\"><input type=\"submit\" value=\"Parse\" /><br /></td></tr>\n" +
				  "  </table></form><br/>\n" +
				  "  <font size=\"-1\">\n" +
				  "    <b>Note:</b> For optimal performance, please" +
				  "    <ul><li>Spell properly</li><li>Make sure to end the sentence with a period (or other punctuation)</li><li>Start the sentence with an uppercase letter</li><li>Only feed the parser one sentence a time</li></ul>\n" +
				  "  </font>\n" +
				  "  <font size=\"-1\">\n" +
				  "    <b>System composition</b>" +
				  "    <ul><li>Tokenization - <a href=\"http://opennlp.sourceforge.net/\">OpenNLP tools</a> tokenizer</li>" +
				  "    <li>POS-tagging, Lemmatization and Dependency Parser - by Bernd Bohnet</li>" +
				  "    <li>Morphological tagging - by Bernd Bohnet (not applicable for all languages)</li>"+
				  "    <li>Semantic Role Labeling - based on LTH's contribution to the CoNLL 2009 ST</li></ul>" +
				  "  </font>\n" +
				  "  <font size=\"-1\">For downloads and more information see <a href=\"http://code.google.com/p/mate-tools/\">http://code.google.com/p/mate-tools/</a>.</font><br/>" +
				  "  <font size=\"-1\"><sup>&dagger;</sup> This is only applicable for HTML response, and with English. Note that this takes longer, and if the online dictionary is down, it may time out and take a significant amount of time.</font>");
		
		pages.put("notReady",
				  "Parser is not loaded yet, please be patient. (Shouldn't be longer than 1-2 minutes, roughly)\n");
	}

	
	private static final Pattern ampPattern=Pattern.compile("&");
	private static final Pattern eqPattern =Pattern.compile("=");
	
	protected String getContent(HttpExchange exchange) throws IOException {
		BufferedReader httpInput=new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
		StringBuilder in=new StringBuilder();
		String input;
		while((input=httpInput.readLine())!=null){
			in.append(input).append(" ");
		}
		httpInput.close();
		return in.toString().trim();
	}
	
	protected static Map<String,String> contentToVariableMap(String content) throws IOException {
		Map<String,String> ret=new HashMap<String,String>();
		String[] pairs=ampPattern.split(content);
		for(String pair:pairs){
			String[] a=eqPattern.split(pair,2);
			ret.put(URLDecoder.decode(a[0],"UTF-8"),URLDecoder.decode(a[1],"UTF-8"));
		}
		return ret;
	}
	
	protected void sendContent(HttpExchange exchange,byte[] content,String content_type) throws IOException{
		exchange.getResponseHeaders().add("Content-type",content_type);
		exchange.sendResponseHeaders(200,content.length);
		OutputStream os=exchange.getResponseBody();
		os.write(content);
		os.close();
	}
	
}
