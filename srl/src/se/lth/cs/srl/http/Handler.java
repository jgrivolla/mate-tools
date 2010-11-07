package se.lth.cs.srl.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.languages.Language.L;

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
	
	//TODO improve the color codes for different labels, and across languages. I.e. refactor this and put it in the Language classes, rather than here.
	
	protected static final String sentenceDataVarName="sentence";
	protected static final String HTMLHEAD="<html><head>\n<title>Semantic Role Labeler</title>\n"+STYLESHEET+"</head>\n<body>\n";
	protected static final String HTMLTAIL="</body>\n</html>";
	protected static Map<String,String> pages=new HashMap<String,String>();
	
	protected void setupPages(L currentL) {
		pages.put("default",
				  "  <h3>Try the semantic role labeler</h3>\n" +
				  "  Enter a sentence in <b>"+Language.LtoString(currentL)+"</b> and press Parse.<br/>\n" +
				  "  <form action=\"/parse\" method=\"POST\">\n" +
				  "    <table cellpadding=\"2\" cellspacing=\"2\">\n" +
				  "      <tr><td valign=\"center\"><b>Input</b><td><textarea name=\""+sentenceDataVarName+"\" rows=\"3\" cols=\"40\"></textarea></td></tr>\n" +
				  "      <tr><td valign=\"center\"><b>Return type</b><td><input type=\"radio\" name=\"returnType\" value=\"html\" checked=\"checked\" />&nbsp;&nbsp;HTML<br /><input type=\"radio\" name=\"returnType\" value=\"text\"/>&nbsp;&nbsp;Raw text</td></tr>\n" +
				  "      <tr><td colspan=\"2\"><input type=\"checkbox\" name=\"doRenderDependencyGraph\" checked=\"CHECKED\"/> <font size=\"-1\">Include graphical dependency tree output</font></td></tr>" +
				  "      <tr><td colspan=\"2\"><input type=\"checkbox\" name=\"doPerformDictionaryLookup\" /> <font size=\"-1\">Attempt to lookup and reference predicates in dictionary<sup>&dagger;</sup>.</font></td></tr>\n" +
				  "      <tr><td colspan=\"2\" align=\"center\"><input type=\"submit\" value=\"Parse\" /><br /></td></tr>\n" +
				  "  </table></form><br/>\n" +
				  "  <font size=\"-1\">\n" +
				  "    <b>Note:</b> For optimal performance, please\n" +
				  "    <ul>\n" +
				  "      <li>Spell properly.</li>\n" +
				  "      <li>Make sure to end the sentence with a period (or other punctuation) ((In languages where punctuation is typically used, that is.)).</li>\n" +
				  "      <li>Start the sentence with an uppercase letter</li>\n" +
				  "      <li>Only feed the parser one sentence a time</li>\n" +
				  "    </ul>\n" +
				  "  </font>\n" +
				  "  <font size=\"-1\">\n" +
				  "    <b>System composition</b>\n" +
				  "    <ul>\n" +
				  "      <li>Tokenization - <a href=\"http://opennlp.sourceforge.net/\">OpenNLP tools</a> tokenizer (English and German), <a href=\"http://nlp.stanford.edu/software/segmenter.shtml\">Stanford Chinese Segmenter (Chinese)</a></li>\n"+
				  "      <li>POS-tagging, Lemmatization and Dependency Parser - by Bernd Bohnet</li>\n" +
				  "      <li>Morphological tagging - by Bernd Bohnet (not applicable for all languages)</li>\n"+
				  "      <li>Semantic Role Labeling - based on LTH's contribution to the CoNLL 2009 ST</li>\n" +
				  "      <li>Graph Visualization - using <a href=\"http://code.google.com/p/whatswrong/\">What's Wrong With My NLP?</a></li>\n" +
				  "    </ul>\n"+
				  "  </font>\n" +
				  "  <font size=\"-1\">For downloads and more information see <a href=\"http://code.google.com/p/mate-tools/\">http://code.google.com/p/mate-tools/</a>.</font><br/>\n" +
				  "  <font size=\"-1\"><sup>&dagger;</sup> This is only applicable for HTML response, and with English. Note that this takes longer, and if the online dictionary is down, it may time out and take a significant amount of time.</font>\n");
		
		pages.put("notReady",
				  "Parser is not loaded yet, please be patient. (Shouldn't be longer than 1-2 minutes, roughly)\n");
	}

	
	private static final Pattern ampPattern=Pattern.compile("&");
	private static final Pattern eqPattern =Pattern.compile("=");
	
	protected String getContent(HttpExchange exchange) throws IOException {
		BufferedReader httpInput=new BufferedReader(new InputStreamReader(exchange.getRequestBody(),"UTF-8"));
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
	
	protected void sendContent(HttpExchange exchange,String content,String content_type) throws IOException{
		exchange.getResponseHeaders().add("Content-type",content_type);
		byte[] bytes=content.getBytes("UTF-8");
		exchange.sendResponseHeaders(200,bytes.length);
		OutputStream os=new BufferedOutputStream(exchange.getResponseBody());
		os.write(bytes);
		os.close();
	}
	
}
