package se.lth.cs.srl.languages;

import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;

public class Chinese extends Language {

	private static Pattern CALSPattern=Pattern.compile("^A0|A1|A2|A3|A4$");
	@Override
	public String getCoreArgumentLabelSequence(Predicate pred,Map<Word, String> proposition) {
		Sentence sen=pred.getMySentence();
		StringBuilder ret=new StringBuilder();
		for(int i=1,size=sen.size();i<size;++i){
			Word w=sen.get(i);
			if(pred==w){
				ret.append(" "+pred.getSense());
			}
			if(proposition.containsKey(w)){
				String label=proposition.get(w);
				if(CALSPattern.matcher(label).matches())
					ret.append(" "+label);
			}
		}
		return ret.toString();
	}

	@Override
	public String getDefaultSense(String lemma) {
		return lemma+".01";
	}

	@Override
	public Pattern getFeatSplitPattern() {
		throw new Error("You are wrong here.");
	}

	@Override
	public L getL() {
		return L.chi;
	}

	@Override
	public String getLexiconURL(Predicate pred) {
		return null;
	}

}
