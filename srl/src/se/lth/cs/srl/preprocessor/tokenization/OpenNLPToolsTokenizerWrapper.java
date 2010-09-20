package se.lth.cs.srl.preprocessor.tokenization;

public class OpenNLPToolsTokenizerWrapper implements se.lth.cs.srl.preprocessor.tokenization.Tokenizer {
	
	opennlp.tools.tokenize.Tokenizer tokenizer;
	
	public OpenNLPToolsTokenizerWrapper(opennlp.tools.tokenize.Tokenizer tokenizerImplementation){
		this.tokenizer=tokenizerImplementation;
	}
	
	@Override
	public String[] tokenize(String sentence) {
		return tokenizer.tokenize(sentence);
	}
	
}
