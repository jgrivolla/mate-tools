package examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import is2.parser.Options;
import is2.parser.Parser;
import is2.tag3.Tagger;

/**
 * @author Bernd Bohnet, 13.09.2010
 * 
 * Illustrates the application of some components: lemmatizer, tagger, and parser
 */
public class Lemmatize {

	
	/**
	 * How to lemmatize a sentences?
	 */
	public static void main(String[] args) throws IOException {

		
		// Create a data container for a sentence
		SentenceData09 i = new SentenceData09();

		if (args.length==1) { // input might be a sentence: "This is another test ." 
			StringTokenizer st = new StringTokenizer(args[0]);
			ArrayList<String> forms = new ArrayList<String>();
			
			forms.add("<root>");
			while(st.hasMoreTokens()) forms.add(st.nextToken());
			
			i.init(forms.toArray(new String[0]));
			
		} else {
			// provide a default sentence 
			i.init(new String[] {"<root>","HŠuser","hat","ein","Umlaut","."});
		}

		//print the forms
		for (String l : i.forms) System.out.println("forms : "+l);

		// tell the lemmatizer the location of the model
		is2.lemmatizer.Options optsLemmatizer = new is2.lemmatizer.Options(new String[] {"-model","models/lemma-ger.model"});

		// create a lemmatizer
		Lemmatizer lemmatizer = new Lemmatizer(optsLemmatizer);

		// lemmatize a sentence; the result is stored in the stenenceData09 i 
		lemmatizer.lemmatize(optsLemmatizer,i);

		
		// output the lemmata
		for (String l : i.lemmas) System.out.println("lemma : "+l);
		
		System.out.println("A variant to call the lemmatizer");
		
		String[] lemmata = lemmatizer.getLemmas(i.forms);
		for (String l : lemmata) System.out.println("lemma : "+l);
		
	}

	
}
