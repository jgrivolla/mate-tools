package se.lth.cs.srl.options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import se.lth.cs.srl.http.HttpPipeline;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.languages.Language.L;

public class HttpOptions {

	public int port=8081;
	
	public L l;
	public File tokenizer;
	public File lemmatizer;
	public File tagger;
	public File morph;
	public File parser;
	public File srl;
	
	public boolean reranker=false;
	public int aiBeam=4;
	public int acBeam=4;
	public double alfa=1.0;

	public HttpOptions(String[] args){
		int ai=0;
		if(args.length<1){
			System.err.println("Not enough arguments. Aborting.");
			usage();
			System.exit(1);
		}
		try {
			l=L.valueOf(args[ai]);
		} catch (Exception e){
			System.err.println("Unknown language: "+args[ai]+", aborting.");
			System.err.println();
			usage();
			System.exit(1);
		}
		Language.setLanguage(l);
		ai++;
		while(ai<args.length){
			if(args[ai].equals("-h") || args[ai].equals("-help") || args[ai].equals("--help")){
				usage();
				System.exit(1);
			} else if(args[ai].equals("-token")){
				ai++;
				tokenizer=new File(args[ai]);
				ai++;
			} else if(args[ai].equals("-lemma")){
				ai++;
				lemmatizer=new File(args[ai]);
				ai++;
			} else if(args[ai].equals("-tagger")){
				ai++;
				tagger=new File(args[ai]);
				ai++;
			} else if(args[ai].equals("-morph")){
				ai++;
				morph=new File(args[ai]);
				ai++;
			} else if(args[ai].equals("-parser")){
				ai++;
				parser=new File(args[ai]);
				ai++;
			} else if(args[ai].equals("-srl")){
				ai++;
				srl=new File(args[ai]);
				ai++;
			} else if(args[ai].equals("-reranker")){
				ai++;
				reranker=true;
			} else if(args[ai].equals("-aiBeam")){
				ai++;
				aiBeam=Integer.valueOf(args[ai]);
				ai++;
			} else if(args[ai].equals("-acBeam")){
				ai++;
				acBeam=Integer.valueOf(args[ai]);
				ai++;
			} else if(args[ai].equals("-alfa")){
				ai++;
				alfa=Double.parseDouble(args[ai]);
				ai++;
			} else if(args[ai].equals("-port")){
				ai++;
				port=Integer.valueOf(args[ai]);
				ai++;
			} else {
				System.err.println("Unknown argument: "+args[ai]);
				System.err.println();
				usage();
				System.exit(1);
			}
		}
		verifyFiles();
	}
	
	private void verifyFiles() {
		List<File> files=new ArrayList<File>();
		if(tokenizer==null){
			System.err.println("You forgot to specify the model file for the tokenizer. Try again.");
			System.exit(1);
		}
		files.add(tokenizer);
		if(lemmatizer==null){
			System.err.println("You forgot to specify the model file for the lemmatizer. Try again.");
			System.exit(1);
		}
		files.add(lemmatizer);
		if(tagger==null){
			System.err.println("You forgot to specify the model file for the pos tagger. Try again.");
			System.exit(1);
		}
		files.add(tagger);
		if(morph!=null) files.add(morph);
		if(parser==null){
			System.err.println("You forgot to specify the model file for the dependency parser. Try again.");
			System.exit(1);
		}
		files.add(parser);
		if(tokenizer==null){
			System.err.println("You forgot to specify the model file for the srl system. Try again.");
			System.exit(1);
		}
		files.add(srl);
		for(File f:files){
			if(!f.exists()){
				System.err.println("File "+f+" does not exist. Aborting.");
				System.exit(1);
			}
			if(!f.canRead()){
				System.err.println("File "+f+" can not be read. Aborting.");
				System.exit(1);
			}
		}
	}

	public static void usage(){
		System.err.println("Usage:");
		System.err.println("java -cp ... "+HttpPipeline.class.getName()+" <lang> <options>");
		System.err.println("");
		System.err.println("Where <lang> is one of: eng,ger");
		System.err.println("");
		System.err.println("And <options> correnspond to one of the following:");
		System.err.println("-token  <file>    path to the tokenizer model file");
		System.err.println("-lemma  <file>    path to the lemmatizer model file");
		System.err.println("-tagger <file>    path to the pos tagger model file");
		System.err.println("-morph  <file>    path to the morphological tagger model file");
		System.err.println("-parser <file>    path to the parser model file");
		System.err.println("-srl    <file>    path to the srl model file");
		System.err.println("-port   <int>     the port to use (default 8081)");
		System.err.println("-reranker         use the reranker for the srl-system (default is not)");
		System.err.println("-aibeam <int>     set the beam width of the ai component (default 4)");
		System.err.println("-acbeam <int>     set the beam width of the ac component (default 4)");
		System.err.println("-alfa   <double>  set the alfa for the reranker (default 1.0)");
		System.err.println("");
		System.err.println("All the files are neccessary to run the system, except the morphological tagger in case you are not using such features in your srl model.");
	}
	
	public ParseOptions getParseOptions(){
		ParseOptions parseOptions=new ParseOptions();
		parseOptions.modelFile=srl;
		parseOptions.useReranker=reranker;
		parseOptions.global_aiBeam=aiBeam;
		parseOptions.global_acBeam=acBeam;
		parseOptions.global_alfa=alfa;
		return parseOptions;
	}
}
