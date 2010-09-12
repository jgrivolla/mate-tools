package se.lth.cs.srl.languages;

import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Word;


public abstract class Language {

	public enum L { cat, chi, cze, eng, ger, jap, spa }
	
	private static Language language;
	
	public static Language getLanguage(){
		return language;
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
	public abstract Pattern getFeatSplitPattern();
	public abstract String getDefaultSense(String lemma);
	public abstract String getCoreArgumentLabelSequence(Predicate pred,Map<Word, String> proposition);
	public abstract L getL();
	public abstract String getLexiconURL(Predicate pred);
}
