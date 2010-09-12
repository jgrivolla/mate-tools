package se.lth.cs.srl.preprocessor;

import is2.lemmatizer.Lemmatizer;
import is2.tag3.Tagger;

import java.io.File;
import java.io.IOException;

import opennlp.tools.tokenize.Tokenizer;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.languages.Language;

public class Preprocessor {

	private Tokenizer tokenizer;
	private Lemmatizer lemmatizer;
	private Tagger postagger;
	private is2.mtag.Main mtagger;
	
	public long loadingTime;
	public long parsingTime=0;
	
	public Preprocessor(File tokenizerModelFile,File lemmatizerModelFile,File POSTaggerModelFile,File morphTaggerModelFile) throws IOException{
		long start=System.currentTimeMillis();
		Language l=Language.getLanguage();
		switch(l.getL()){
		case ger:
			tokenizer=new opennlp.tools.lang.german.Tokenizer(tokenizerModelFile.toString());
			String[] argsMT={"-model",morphTaggerModelFile.toString()};
			mtagger=new is2.mtag.Main(new is2.mtag.Options(argsMT));
			break;
		case eng:
			tokenizer=new opennlp.tools.lang.english.Tokenizer(tokenizerModelFile.toString());
			break;
		case chi:
			tokenizer=null; //This is a problem. OpenNLP tools doesn't provide Chinese models. 
			break;
		default: throw new Error("not implemented");
		}
		String[] argsL={"-model",lemmatizerModelFile.toString()};
		lemmatizer=new Lemmatizer(new is2.lemmatizer.Options(argsL));
		String[] argsT={"-model",POSTaggerModelFile.toString()};
		postagger=new Tagger(new is2.tag3.Options(argsT));
		loadingTime=System.currentTimeMillis()-start;
	}
	
	public Sentence preprocess(String[] words) throws Exception{
		long start=System.currentTimeMillis();
		String[] lemmas=lemmatizer.lemma(words, false);
		String[] tags=postagger.tag(words, lemmas);
		String[] morphs;
		if(mtagger!=null){
			morphs=mtagger.out(words, lemmas).pfeats;
		} else {
			morphs=new String[words.length];
		}
		Sentence ret=new Sentence(words,lemmas,tags,morphs);
		parsingTime+=(System.currentTimeMillis()-start);
		return ret;
	}
	
	public String[] tokenize(String sentence) throws Exception {
		long start=System.currentTimeMillis();
		String[] words=tokenizer.tokenize(sentence);
		parsingTime+=(System.currentTimeMillis()-start);
		return words;		
	}

	public boolean hasMorphTagger() {
		return mtagger!=null;
	}
	
//	public SentenceData09 preprocess(String[] words) throws Exception{
//		long start=System.currentTimeMillis();
//		SentenceData09 ret=new SentenceData09();
//		String[] lemmas=getLemmas(words);
//		String[] tags=postagger.tag(words, lemmas);
//		String[] morph_r=null;
//		
//		if(mtagger!=null){
//			String[] m=mtagger.out(words, lemmas).pfeats;
//			morph_r=new String[m.length+1];
//			morph_r[0]=CONLLReader09.NO_TYPE;
//			System.arraycopy(m, 0, morph_r, 1, m.length);
//		}
//		String[] words_r=new String[words.length+1];
//		String[] lemmas_r=new String[words.length+1];
//		String[] tags_r=new String[words.length+1];
//		words_r[0]=CONLLReader09.ROOT;
//		lemmas_r[0]=CONLLReader09.ROOT_LEMMA;
//		tags_r[0]=CONLLReader09.ROOT_POS;
//		System.arraycopy(words, 0, words_r, 1,words.length);
//		System.arraycopy(lemmas,0,lemmas_r,1,words.length);
//		System.arraycopy(tags,0,tags_r,1,words.length);
//		ret.init(words_r);
//		ret.setLemmas(lemmas_r);
//		ret.setPPos(tags_r);
//		if(morph_r!=null)
//			ret.setFeats(morph_r);
//		parsingTime+=(System.currentTimeMillis()-start);
//		return ret;
//	}
//	
//	public SentenceData09 preprocess(String sentence) throws Exception{
//		long start=System.currentTimeMillis();
//		String[] words=tokenizer.tokenize(sentence);
//		parsingTime+=(System.currentTimeMillis()-start);
//		return preprocess(words);
//	}
//	private String[] getLemmas(String[] words){
//		String[] ret=new String[words.length];
//		ret = lemmatizer.lemma(words, false);		
//		return ret;
//	}
}
