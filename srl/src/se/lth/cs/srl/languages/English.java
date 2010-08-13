package se.lth.cs.srl.languages;

import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

public class English extends Language {

	@Override
	public Pattern getFeatSplitPattern() {
		return null;
	}

	@Override
	public String getDefaultSense(String lemma) {
		return lemma+".01";
	}

	private static Pattern CALSPattern=Pattern.compile("^A0|A1|A2|A3|A4$");
	
	@Override
	public String getCoreArgumentLabelSequence(Predicate pred, Map<Word, String> proposition) {
		Sentence sen=pred.getMySentence();
		StringBuilder ret=new StringBuilder();
		for(int i=1,size=sen.size();i<size;++i){
			Word word=sen.get(i);
			if(pred==word){
				ret.append(" "+pred.getSense()+"/");
				ret.append(isPassiveVoice(pred) ? "P" : "A");
			} //Don't make this else if, since the predicate can also be its on argument
			if(proposition.containsKey(word)){
				String label=proposition.get(word);
				if(CALSPattern.matcher(label).matches())
					ret.append(" "+label);
			}
		}
		return ret.toString();
	}

	private boolean isPassiveVoice(Predicate pred) {
			if(!pred.getPOS().equals("VBN")) 
				return false;
			
			Word head=pred.getHead();
			return !head.isBOS() && head.getForm().matches("(be|am|are|is|was|were|been)");
	}

	@Override
	public L getL() {
		return L.eng;
	}

	@Override
	public String getLexiconURL(Predicate pred) {
		if(pred.getPOS().startsWith("V")){
			return "http://verbs.colorado.edu/propbank/framesets-english/"+pred.getLemma()+"-v.html";
		} else {
			return "http://nlp.cs.nyu.edu/meyers/nombank/nombank.1.0/frames/"+pred.getLemma()+".xml";
		}
	}

}
