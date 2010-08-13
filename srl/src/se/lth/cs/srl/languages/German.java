package se.lth.cs.srl.languages;

import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.util.Patterns;

public class German extends Language {

	@Override
	public Pattern getFeatSplitPattern() {
		return Patterns.BAR_PATTERN;
	}

	@Override
	public String getDefaultSense(String lemma) {
		return lemma+".1";
	}

	@Override
	public String getCoreArgumentLabelSequence(Predicate pred,Map<Word, String> proposition) {
		Sentence sen=pred.getMySentence();
		StringBuilder ret=new StringBuilder();
		for(int i=1,size=sen.size();i<size;++i){
			Word word=sen.get(i);
			if(pred==word){
				ret.append(" "+pred.getSense()+"/");
				ret.append(isPassiveVoice(pred) ? "P" : "A");
			}
			if(proposition.containsKey(word)){
				ret.append(" "+proposition.get(word));
			}
		}
		
		
		return ret.toString();
	}

	private boolean isPassiveVoice(Predicate pred){
		Word head=pred.getHead();
		return !head.isBOS() && pred.getPOS().equals("VVPP") && head.getLemma().equals("werden");
	}

	@Override
	public L getL() {
		return L.ger;
	}

	@Override
	public String getLexiconURL(Predicate pred) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
