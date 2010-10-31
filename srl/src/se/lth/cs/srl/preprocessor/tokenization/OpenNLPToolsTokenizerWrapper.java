package se.lth.cs.srl.preprocessor.tokenization;

public class OpenNLPToolsTokenizerWrapper implements se.lth.cs.srl.preprocessor.tokenization.Tokenizer {
	
	opennlp.tools.tokenize.Tokenizer tokenizer;

	public OpenNLPToolsTokenizerWrapper(opennlp.tools.tokenize.Tokenizer tokenizerImplementation){
		this.tokenizer=tokenizerImplementation;
	}
	

	@Override
	public String[] tokenize(String sentence) {
		String[] tokens=tokenizer.tokenize(sentence);
		String[] withRoot=new String[tokens.length+1];
		//withRoot[0]="<root>";
		withRoot[0]=is2.io.CONLLReader09.ROOT;
		System.arraycopy(tokens, 0, withRoot, 1, tokens.length);
		return withRoot;
	}
	
}
