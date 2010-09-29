package se.lth.cs.srl.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.corpus.Yield;
import se.lth.cs.srl.languages.Language;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ParseRequestHandler extends Handler {

	private CompletePipeline pipeline;
	private HttpHandler defaultHandler;
	
	public ParseRequestHandler(HttpHandler defaultHandler,CompletePipeline pipeline) throws IOException{
		this.defaultHandler=defaultHandler;
		this.pipeline=pipeline;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		//If its not a post request, let the default handler deal with it
		if(!exchange.getRequestMethod().equals("POST")){
			defaultHandler.handle(exchange);
			return;
		}
		
		//Get the parameters from the HTML form
		Map<String,String> vars=Handler.contentToVariableMap(getContent(exchange));
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
		long parsingTime=System.currentTimeMillis();
		Sentence sen=null;
		try {
			sen = pipeline.parse(inputSentence);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parsingTime=System.currentTimeMillis()-parsingTime;
		
		//Prepare the response;
		if(vars.containsKey("returnType") && vars.get("returnType").equals("html")){
			boolean performURLLookup=vars.containsKey("doPerformDictionaryLookup"); //TODO: verify that this works.
			httpResponse=getHTMLResponse(sen,parsingTime,performURLLookup);
			content_type="text/html";
		} else {
			httpResponse=sen.toString();
			content_type="text/plain";
		}
		System.out.println("Content type returned: "+content_type);
		System.out.println("Sentence returned:");
		System.out.println(sen.toString());
		//Return the response to the client
		sendContent(exchange,httpResponse,content_type);
	}
		
	private static final HashSet<String> styleSheetArgs;
	static {
		styleSheetArgs=new HashSet<String>();
		styleSheetArgs.add("A0");
		styleSheetArgs.add("C-A0");
		styleSheetArgs.add("A1");
		styleSheetArgs.add("C-A1");
		styleSheetArgs.add("A2");
		styleSheetArgs.add("C-A2");
		styleSheetArgs.add("A3");
		styleSheetArgs.add("C-A3");
		styleSheetArgs.add("AM-NEG");
		styleSheetArgs.add("AM-MNR");
	}
	
	private String getHTMLResponse(Sentence sen, long parsingTime, boolean performURLLookup){
		StringBuilder ret=new StringBuilder(HTMLHEAD);
		//ret.append("<html><head><title>Semantic Parser</title>\n"+STYLESHEET+"</head><body>\n");
		ret.append("<table cellpadding=10 cellspacing=1>\n<tr><td class=\"topRowCell\">&nbsp;</td>");
		for(int i=1;i<sen.size();++i){
			ret.append("<td align=\"center\" class=\"topRowCell\">").append(sen.get(i).getForm()).append("</td>");
		}
		StringBuilder errors=new StringBuilder();
		for(Predicate pred:sen.getPredicates()){
			int indexCount=1;
			ret.append("\n<tr><td>");
			String URL=Language.getLanguage().getLexiconURL(pred);
			if(performURLLookup && URL!=null && isValidURL(URL)){
				ret.append("<a href=\""+URL+"\">");
				ret.append(pred.getSense());
				ret.append("</a>");
			} else {
				ret.append(pred.getSense());
			}
			
			ret.append("</td>\n");
//			if(pred.getPOS().startsWith("V")){ //Link to propbank
//				ret.append("<a href=\"http://verbs.colorado.edu/propbank/framesets-english/"+pred.getLemma()+"-v.html\">");
//			} else { //Link to Nombank
//				ret.append("<a href=\"http://nlp.cs.nyu.edu/meyers/nombank/nombank.1.0/frames/"+pred.getLemma()+".xml\">");
//			}
//			ret.append(pred.getSense()+"</a></td>");
			
			SortedSet<Yield> yields=new TreeSet<Yield>();
			Map<Word,String> argmap=pred.getArgMap();
			for(Word arg:argmap.keySet()){
				yields.addAll(arg.getYield(pred,argmap.get(arg),argmap.keySet()).explode());
			}
			for(Yield y:yields){
				if(!y.isContinuous()){ //Warn the user if we have discontinuous yields
					errors.append("((Discontinous yield of argument '"+y+"' of predicate '"+pred.getSense()+"'. Yield contains tokens [");
					for(Word w:y)
						errors.append("'"+w.getForm()+"', ");
					errors.delete(errors.length()-2,errors.length());
					errors.append("])).\n");
				}
				int blankColSpan=sen.indexOf(y.first())-indexCount;
				if(blankColSpan>0){
					ret.append("<td colspan=\"").append(blankColSpan).append("\">&nbsp;</td>");
				} else if(blankColSpan<0){
					errors.append("Argument '"+y.getArgLabel()+"' of '"+pred.getSense()+"' at index "+indexCount+" overlaps with previous argument(s), ignored.\n");
					continue;
				}
				int argColWidth=sen.indexOf(y.last())-sen.indexOf(y.first())+1;
				String argLabel=y.getArgLabel();
				ret.append("<td colspan=\"")
				   .append(argColWidth)
				   .append("\" class=\"")
				   .append((styleSheetArgs.contains(argLabel)?argLabel:"ARG_DEFAULT"))
				   .append("\" align=\"center\">")
				   .append(argLabel)
				   .append("</td>");
				indexCount+=argColWidth;
				indexCount+=blankColSpan;
			}
			if(indexCount<sen.size())
				ret.append("<td colspan=\""+(sen.size()-indexCount)+"\">&nbsp;</td>");
			ret.append("</tr>");
		}
		ret.append("\n</table><br/>\n");
		ret.append("Parsing sentence required "+parsingTime+"ms.<br/>\n");
		if(errors.length()>0){
			ret.append("<br/><hr><br/><font color=\"#FF0000\">Errors</font><br/>");
			ret.append(errors.toString().replace("\n","<br/>"));
			System.err.println(errors.toString().trim());
		}
		ret.append("<br/>\n<hr/>\n<br/>\n");
		//The raw CoNLL 2009 output as a table
		ret.append("<table><tr><th>ID</th><th>Form</th><th>Lemma</th><th>PLemma</th><th>POS</th><th>PPOS</th><th>Feats</th><th>PFeats</th><th>Head</th><th>PHead</th><th>Deprel</th><th>PDeprel</th><th>IsPred</th><th>Pred</th>");
		for(int i=0;i<sen.getPredicates().size();++i)
			ret.append("<th>Args: "+sen.getPredicates().get(i).getSense()+"</th>");
		ret.append("</tr>\n");
		for(String line:sen.toString().split("\n")){
			ret.append("<tr>");
			for(String token:line.split("\t")){
				ret.append("<td>").append(token).append("</td>");
			}
			ret.append("</tr>\n");
		}
		ret.append("</table>\n<br/><hr/><br/>");
		ret.append(DefaultHandler.pages.get("default"));
		ret.append("</body></html>");
		return ret.toString();
	}

	/**
	 * This method tries to make an HTTP request to the given URL and if it works and the request is OK (i.e. return code 200), it returns true. Otherwise false.
	 * @param url the url
	 * @return true if the url resolves and returns properly (i.e. return code 200), otherwise false
	 */
	private boolean isValidURL(String url) { 
		try {
			HttpURLConnection conn=(HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("HEAD");
			conn.connect();
			return conn.getResponseCode()==HttpURLConnection.HTTP_OK;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
