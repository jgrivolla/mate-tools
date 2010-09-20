package se.lth.cs.srl.languages;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.preprocessor.Preprocessor;


public abstract class Language {

	public enum L { cat, chi, cze, eng, ger, jap, spa }
	
	private static Language language;
	
	public static Language getLanguage(){
		return language;
	}
	
	public static String getLsString(){
		return "chi, eng, ger";
	}
	
	public static Language setLanguage(L l){
		switch(l){
		case chi: language=new Chinese(); break;
		case eng: language=new English(); break;
		case ger: language=new German(); break;
		default: throw new IllegalArgumentException("Unknown language: '"+l+"'");
		}
		return language;
	}
	public abstract Pattern getFeatSplitPattern(); //TODO change this into a function that does the parsing and returns a String array. Right now, the German * catchall/uncertain is used as a feature, i think it shouldnt.
	public abstract String getDefaultSense(String lemma);
	public abstract String getCoreArgumentLabelSequence(Predicate pred,Map<Word, String> proposition);
	public abstract L getL();
	public abstract String getLexiconURL(Predicate pred);
	
	
	
	public abstract Preprocessor getPreprocessor(FullPipelineOptions options) throws IOException;

}
