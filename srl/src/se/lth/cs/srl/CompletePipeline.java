package se.lth.cs.srl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import is2.data.SentenceData09;
import is2.parser.Parser;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.io.CoNLL09Writer;
import se.lth.cs.srl.io.SentenceWriter;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.pipeline.Pipeline;
import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.pipeline.Step;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.util.BohnetHelper;
import se.lth.cs.srl.util.Util;

public class CompletePipeline {

	private static final Pattern WHITESPACE_PATTERN=Pattern.compile("\\s+");
	
	private Preprocessor pp;
	private Parser dp;
	private SemanticRoleLabeler srl;
	
	public long dpTime=0;
	
	public static CompletePipeline getCompletePipeline(FullPipelineOptions options) throws ZipException, IOException, ClassNotFoundException{
		Preprocessor pp=Language.getLanguage().getPreprocessor(options);
		Parser dp=BohnetHelper.getParser(options.parser);
		Parse.parseOptions=options.getParseOptions();
		SemanticRoleLabeler srl;
		if(options.reranker){
			srl=new Reranker(Parse.parseOptions);
		} else {
			ZipFile zipFile=new ZipFile(Parse.parseOptions.modelFile);
			if(Parse.parseOptions.skipPI){
				srl=Pipeline.fromZipFile(zipFile, new Step[]{Step.pd,Step.ai,Step.ac});
			} else {
				srl=Pipeline.fromZipFile(zipFile);
			}
			zipFile.close();			
		}
		return new CompletePipeline(pp,dp,srl);
	}
	
	public CompletePipeline(Preprocessor preprocessor,Parser parser,SemanticRoleLabeler srl){
		this.pp=preprocessor;
		this.dp=parser;
		this.srl=srl;
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
		CompletePipelineCMDLineOptions options=new CompletePipelineCMDLineOptions();
		options.parseCmdLineArgs(args);
		CompletePipeline pipeline=getCompletePipeline(options);
		BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(options.input),Charset.forName("UTF-8")));
		String str;
		List<String> forms=new ArrayList<String>();
		List<Boolean> isPred=new ArrayList<Boolean>();
		SentenceWriter writer=new CoNLL09Writer(options.output);
		int senCount=0;
		long start=System.currentTimeMillis();
		while ((str = in.readLine()) != null) {
			if(str.trim().equals("")){
				Sentence s=options.skipPI ? pipeline.parseOraclePI(forms, isPred) : pipeline.parse(forms);
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
				if(options.skipPI)
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
		ret.append("Time spent doing tokenization (ms):           "+Util.insertCommas(pp.tokenizeTime)+"\n");
		ret.append("Time spent doing lemmatization (ms):          "+Util.insertCommas(pp.lemmatizeTime)+"\n");
		ret.append("Time spent doing pos-tagging (ms):            "+Util.insertCommas(pp.tagTime)+"\n");
		ret.append("Time spent doing morphological tagging (ms):  "+Util.insertCommas(pp.mtagTime)+"\n");
		ret.append("Time spent doing dependency parsing (ms):     "+Util.insertCommas(dpTime)+"\n");
		ret.append("Time spent doing semantic role labeling (ms): "+Util.insertCommas(srl.parsingTime)+"\n");
		ret.append("\n\n");
		ret.append(srl.getStatus());
		return ret.toString().trim();
	}
}
