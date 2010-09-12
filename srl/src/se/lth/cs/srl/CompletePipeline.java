package se.lth.cs.srl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import is2.data.SentenceData09;
import is2.parser.Parser;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.io.CoNLL09Writer;
import se.lth.cs.srl.io.SentenceWriter;
import se.lth.cs.srl.languages.Language.L;
import se.lth.cs.srl.options.HttpOptions;
import se.lth.cs.srl.options.ParseOptions;
import se.lth.cs.srl.pipeline.Pipeline;
import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.pipeline.Step;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.util.Util;

public class CompletePipeline {

	private static final Pattern WHITESPACE_PATTERN=Pattern.compile("\\s+");
	
	private Preprocessor pp;
	private Parser dp;
	private SemanticRoleLabeler srl;
	
	public long dpTime=0;
	
	public CompletePipeline(File tokenizerModelFile,File lemmatizerModelFile,File POSTaggerModelFile,File morphTaggerModelFile,File dependencyParserModelFile,ParseOptions parseOptions) throws IOException, ClassNotFoundException{
		
		//Language.setLanguage(l); //Sort of redundant since the creation of ParseOptions should set this for us.
		pp=new Preprocessor(tokenizerModelFile,lemmatizerModelFile,POSTaggerModelFile,morphTaggerModelFile);
		String[] argsDP={"-model",dependencyParserModelFile.toString()};
		dp=new Parser(new is2.parser.Options(argsDP));
		Parse.parseOptions=parseOptions;
		if(parseOptions.useReranker){
			srl=new Reranker(parseOptions);
		} else {
			ZipFile zipFile=new ZipFile(parseOptions.modelFile);
			if(parseOptions.skipPI){
				srl=Pipeline.fromZipFile(zipFile, new Step[]{Step.pd,Step.ai,Step.ac});
			} else {
				srl=Pipeline.fromZipFile(zipFile);
			}
			zipFile.close();
		}
	}
	
	public CompletePipeline(HttpOptions options) throws IOException, ClassNotFoundException {
		this(options.tokenizer,options.lemmatizer,options.tagger,options.morph,options.parser,options.getParseOptions());
	}
	
//	public Sentence parse(String sentence) throws Exception{
//		SentenceData09 sen=pp.preprocess(sentence);
//		Sentence ret=dpParse(sen);
//		srl.parseSentence(ret);
//		return ret;
//	}
	
	public Sentence parse(String sentence) throws Exception{
		return parse(Arrays.asList(pp.tokenize(sentence)));
	}
	
	public Sentence parse(List<String> words) throws Exception{
//		SentenceData09 sen=pp.preprocess(words.toArray(new String[0]));
//		Sentence ret=dpParse(sen);
//		srl.parseSentence(ret);
//		return ret;
		
		Sentence s=pp.preprocess(words.toArray(new String[0]));
		
		SentenceData09 sen=new SentenceData09();
		sen.init(s.getFormArray());
		sen.setPPos(s.getPOSArray());
		if(pp.hasMorphTagger())
			sen.setFeats(s.getFeats());
		long start=System.currentTimeMillis();
		SentenceData09 out=dp.parse(sen);
		dpTime+=(System.currentTimeMillis()-start);
		s.setHeadsAndDeprels(out.getParents(),out.getLabels());
		s.buildDependencyTree();
		srl.parseSentence(s);
		return s;
	}
	
	public Sentence parseOraclePI(List<String> words,List<Boolean> isPred) throws Exception{
		Sentence s=pp.preprocess(words.toArray(new String[0]));
		SentenceData09 sen=new SentenceData09();
		sen.init(s.getFormArray());
		sen.setPPos(s.getPOSArray());
		if(pp.hasMorphTagger())
			sen.setFeats(s.getFeats());
		long start=System.currentTimeMillis();
		SentenceData09 out=dp.parse(sen);
		dpTime+=(System.currentTimeMillis()-start);
		s.setHeadsAndDeprels(out.getParents(),out.getLabels());
		s.buildDependencyTree();
		for(int i=0;i<isPred.size();++i){
			if(isPred.get(i)){
				s.makePredicate(i+1);
			}
		}
		srl.parseSentence(s);
		return s;
	}
	
//	private Sentence dpParse(SentenceData09 sen){
//		Sentence s=new Sentence(sen);
////		SentenceData09 sen=new SentenceData09();
////		sen.init(s.getFormArray());
////		sen.setPPos(s.getPOSArray());
////		sen.setFeats(s.getFeats());
//		SentenceData09 out=dp.parse(sen);
//		
//		s.setHeadsAndDeprels(out.getParents(),out.getLabels());
//		s.buildDependencyTree();
//		return s;
//	}

	

	public static void main(String[] args) throws Exception{
		File tokenizer=new File(args[0]);
		File lemmatizer=new File(args[1]);
		File tagger=new File(args[2]);
		File parser=new File(args[3]);
		File mtagger=null;
		File[] files=new File[]{tokenizer,lemmatizer,tagger,parser};
		int argOffset=4;
		try {
			L.valueOf(args[4]);
		} catch(IllegalArgumentException e){
			mtagger=new File(args[4]);
			files=new File[]{tokenizer,lemmatizer,tagger,parser,mtagger};
			argOffset++;
		}
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
		String[] srlArgs=new String[args.length-argOffset];
		for(int i=0;i<srlArgs.length;++i){
			srlArgs[i]=args[argOffset+i];
		}
		ParseOptions parseOptions=new ParseOptions(srlArgs);
		CompletePipeline pipeline=new CompletePipeline(tokenizer,lemmatizer,tagger,mtagger,parser,parseOptions);
		
		BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(parseOptions.inputCorpus),Charset.forName("UTF-8")));
		String str;
		List<String> forms=new ArrayList<String>();
		List<Boolean> isPred=new ArrayList<Boolean>();
		SentenceWriter writer=new CoNLL09Writer(parseOptions.output);
		int senCount=0;
		long start=System.currentTimeMillis();
		while ((str = in.readLine()) != null) {
			if(str.trim().equals("")){
				Sentence s=parseOptions.skipPI ? pipeline.parseOraclePI(forms, isPred) : pipeline.parse(forms);
				forms.clear();
				isPred.clear();
				writer.write(s);
				senCount++;
				if(senCount%100==0){
					System.out.println("Processing sentence "+senCount);
				}
			} else {
				String[] tokens=WHITESPACE_PATTERN.split(str);
				forms.add(tokens[1]);
				if(parseOptions.skipPI)
					isPred.add(tokens[12].equals("Y"));
			}
		}
		in.close();
		if(!forms.isEmpty()){
			writer.write(pipeline.parse(forms));
			senCount++;
		}
		writer.close();
		long time=System.currentTimeMillis()-start;
		System.out.println(pipeline.getStatusString());
		System.out.println();
		System.out.println("Total parsing time (ms):  "+time);
		System.out.println("Overall speed (ms/sen):   "+time/senCount);
	}
	
	public String getStatusString(){
		//StringBuilder ret=new StringBuilder("Semantic role labeling pipeline status\n\n");
		StringBuilder ret=new StringBuilder();
		long allocated=Runtime.getRuntime().totalMemory()/1024;
		long free     =Runtime.getRuntime().freeMemory()/1024;
		ret.append("Memory usage:\n");
		ret.append("Allocated:\t"+Util.insertCommas(allocated)+"kb\n");
		ret.append("Free:\t\t"+Util.insertCommas(free)+"kb\n");
		ret.append("Used:\t\t"+Util.insertCommas((allocated-free))+"kb\n");		
		ret.append("\n");
		ret.append(srl.getStatus()+"\n\n");
		ret.append("Time spent in the preprocessor (ms):\t"+Util.insertCommas(pp.parsingTime)+"\n");
		ret.append("Time spent parsing dependencies (ms):\t"+Util.insertCommas(dpTime)+"\n");
		return ret.toString().trim();
	}
}
