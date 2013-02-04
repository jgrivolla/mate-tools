package se.lth.cs.srl.languages;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.preprocessor.Preprocessor;


public abstract class Language {

	public enum L { cat, chi, cze, eng, ger, jap, spa, swe }
	
	private static Language language;
	static final Pattern BAR_PATTERN=Pattern.compile("\\|");
	
	public static Language getLanguage(){
		return language;
	}
	
	public static String getLsString(){
		return "chi, eng, ger";
	}
	
	public static String LtoString(L l){
		switch(l){
		case chi: return "Chinese";
		case eng: return "English";
		case ger: return "German";
		case swe: return "Swedish";
		case spa: return "Spanish";
		default: throw new IllegalArgumentException("Unknown language: '"+l+"'");
		}
	}
	
	public static Language setLanguage(L l){
		switch(l){
		case chi: language=new Chinese(); break;
		case eng: language=new English(); break;
		case ger: language=new German(); break;
		case swe: language=new Swedish(); break;
		case spa: language=new Spanish(); break;
		default: throw new IllegalArgumentException("Unknown language: '"+l+"'");
		}
		return language;
	}

	public Pattern getFeatSplitPattern() { //TODO change this into a function that does the parsing and returns a String array. Right now, the German * catchall/uncertain is used as a feature, i think it shouldnt.
		return BAR_PATTERN;
	} 
	public abstract String getDefaultSense(Predicate pred);
	public abstract String getCoreArgumentLabelSequence(Predicate pred,Map<Word, String> proposition);
	public abstract L getL();
	public abstract String getLexiconURL(Predicate pred);
	
	
	
	public abstract Preprocessor getPreprocessor(FullPipelineOptions options) throws IOException;

	public abstract String verifyLanguageSpecificModelFiles(FullPipelineOptions options);

}
