package se.lth.cs.srl.preprocessor.tokenization;

public interface Tokenizer {

	public abstract String[] tokenize(String sentence);
	
}
