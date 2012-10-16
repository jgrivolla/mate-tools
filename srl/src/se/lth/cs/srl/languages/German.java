package se.lth.cs.srl.languages;

import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.tokenization.OpenNLPToolsTokenizerWrapper;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;
import se.lth.cs.srl.util.FileExistenceVerifier;

public class German extends Language {

	private static final Pattern BAR_PATTERN=Pattern.compile("\\|");
	
	@Override
	public Pattern getFeatSplitPattern() {
		return BAR_PATTERN;
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
		return null;
	}

	@Override
	public Preprocessor getPreprocessor(FullPipelineOptions options) throws IOException {
		Tokenizer tokenizer=(options.loadPreprocessorWithTokenizer ? new OpenNLPToolsTokenizerWrapper(new opennlp.tools.lang.german.Tokenizer(options.tokenizer.toString())) : null);
		Lemmatizer lemmatizer=BohnetHelper.getLemmatizer(options.lemmatizer);
		Tagger tagger=BohnetHelper.getTagger(options.tagger);
		is2.mtag.Tagger mtagger=BohnetHelper.getMTagger(options.morph);
		Parser parser=BohnetHelper.getParser(options.parser);
		Preprocessor pp=new Preprocessor(tokenizer, lemmatizer, tagger, mtagger, parser);
		return pp;
	}

	@Override
	public String verifyLanguageSpecificModelFiles(FullPipelineOptions options) { //TODO this could be done nicer... I guess the proper way would be to create an enum of all the modules in the complete pipeline, and let each language enumerate which modules it requires. Then the language class can handle the verification, and not every subclass. 
		File[] files;
		if(options.loadPreprocessorWithTokenizer){
			files=new File[3];
			files[2]=options.tokenizer;
		} else {
			files=new File[2];
		}
		files[0]=options.lemmatizer;
		files[1]=options.morph;
		return FileExistenceVerifier.verifyFiles(files);

	}
	
}
