package se.lth.cs.srl.preprocessor;

import is2.lemmatizer.LemmatizerInterface;

public class SimpleChineseLemmatizer implements LemmatizerInterface {

	@Override
	public String[] getLemmas(String[] forms) {
		String[] ret=new String[forms.length]; //TODO, make sure to deal with the root token properly.
		//ret[0]="<root-lemma>"; //TODO this isn't working properly either. Have to fix this ASAP.
		//ret[0]=CONLLReader09.ROOT_LEMMA;
		ret[0]="<root>"; //Still not right, I think. I have no clue.
		for(int i=1;i<forms.length;++i)
			ret[i]=forms[i];
		return ret;
	}

}
