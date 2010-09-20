package se.lth.cs.srl.preprocessor;

public class SimpleChineseLemmatizer implements is2.lemmatizer.LemmatizerInterface {

	@Override
	public String[] getLemmas(String[] forms) {
		String[] ret=new String[forms.length]; //TODO, make sure to deal with the root token properly.
		for(int i=0;i<forms.length;++i)
			ret[i]=forms[i];
		return ret;
	}

}
