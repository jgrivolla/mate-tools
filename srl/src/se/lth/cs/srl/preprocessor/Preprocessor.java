package se.lth.cs.srl.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import is2.lemmatizer.LemmatizerInterface;
import is2.tag3.Tagger;

import se.lth.cs.srl.preprocessor.tokenization.StanfordChineseSegmenterWrapper;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;
import se.lth.cs.srl.corpus.Sentence;

public class Preprocessor {

	private final Tokenizer tokenizer;
	private final LemmatizerInterface lemmatizer;
	private final Tagger tagger;
	private final is2.mtag.Main mtagger;
	
	public Preprocessor(Tokenizer tokenizer,LemmatizerInterface lemmatizer,Tagger tagger,is2.mtag.Main mtagger){
		this.tokenizer=tokenizer;
		this.lemmatizer=lemmatizer;
		this.tagger=tagger;
		this.mtagger=mtagger;
	}
	
	public long tokenizeTime=0;
	public long lemmatizeTime=0;
	public long tagTime=0;
	public long mtagTime=0;

	//TODO add loading times for each module too.
	
	
//	public Preprocessor(File tokenizerModelFile,File lemmatizerModelFile,File POSTaggerModelFile,File morphTaggerModelFile) throws IOException{
//		long start=System.currentTimeMillis();
//		Language l=Language.getLanguage();
//		switch(l.getL()){
//		case ger:
//			tokenizer=new opennlp.tools.lang.german.Tokenizer(tokenizerModelFile.toString());
//			String[] argsMT={"-model",morphTaggerModelFile.toString()};
//			mtagger=new is2.mtag.Main(new is2.mtag.Options(argsMT));
//			break;
//		case eng:
//			tokenizer=new opennlp.tools.lang.english.Tokenizer(tokenizerModelFile.toString());
//			break;
//		case chi:
//			tokenizer=null; //This is a problem. OpenNLP tools doesn't provide Chinese models. 
//			break;
//		default: throw new Error("not implemented");
//		}
//		String[] argsL={"-model",lemmatizerModelFile.toString()};
//		lemmatizer=new Lemmatizer(new is2.lemmatizer.Options(argsL));
//		String[] argsT={"-model",POSTaggerModelFile.toString()};
//		postagger=new Tagger(new is2.tag3.Options(argsT));
//		loadingTime=System.currentTimeMillis()-start;
//	}

	public Sentence preprocess(String[] forms) throws Exception{
		String[] lemmas=null;
		String[] tags=null;
		String[] morphs=null;
		if(lemmatizer!=null){
			long start=System.currentTimeMillis();
			lemmas=lemmatizer.getLemmas(forms);
			lemmatizeTime+=System.currentTimeMillis()-start;
		} else {
			lemmas=new String[forms.length]; //TODO fix all the sentence crap and lose this.
		}
		if(tagger!=null){
			long start=System.currentTimeMillis();
			tags=tagger.tag(forms, lemmas);
			tagTime=System.currentTimeMillis()-start;
		} else {
			tags=new String[forms.length]; //TODO fix all the sentence crap and lose this.
		}
		if(mtagger!=null){
			long start=System.currentTimeMillis();
			morphs=mtagger.out(forms, lemmas).pfeats;
			mtagTime=System.currentTimeMillis()-start;
		} else {
			morphs=new String[forms.length]; //TODO fix all the sentence crap and lose this.
		}
		Sentence ret=new Sentence(forms,lemmas,tags,morphs);
		return ret;
	}
	
//	public Sentence preprocess(String[] words) throws Exception{
//		long start=System.currentTimeMillis();
//		String[] lemmas=lemmatizer.lemma(words, false);
//		String[] tags=postagger.tag(words, lemmas);
//		String[] morphs;
//		if(mtagger!=null){
//			morphs=mtagger.out(words, lemmas).pfeats;
//		} else {
//			morphs=new String[words.length];
//		}
//		Sentence ret=new Sentence(words,lemmas,tags,morphs);
//		parsingTime+=(System.currentTimeMillis()-start);
//		return ret;
//	}
	
	public String[] tokenize(String sentence) {
		long start=System.currentTimeMillis();
		String[] words=tokenizer.tokenize(sentence);
		tokenizeTime+=(System.currentTimeMillis()-start);
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

	public static void main(String[] args) throws Exception{
		File desegmentedInput=new File("chi-desegmented.out");
		Tokenizer tokenizer=new StanfordChineseSegmenterWrapper(new File("/home/anders/Download/stanford-chinese-segmenter-2008-05-21/data"));
		LemmatizerInterface lemmatizer=new SimpleChineseLemmatizer();
		Tagger tagger=BohnetHelper.getTagger(new File("models/chi/tag-chn.model"));
		Preprocessor pp=new Preprocessor(tokenizer,lemmatizer,tagger,null);
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(desegmentedInput),"UTF-8"));
		String line;
		while((line=reader.readLine())!=null){
			String[] tokens=pp.tokenize(line);
			Sentence s=pp.preprocess(tokens);
			System.out.println(s);
		}
		
		
	}
	
	
}
