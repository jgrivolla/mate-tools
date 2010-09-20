package se.lth.cs.srl.options;

import java.io.File;

public class CompletePipelineCMDLineOptions extends FullPipelineOptions {

	public File output=new File("out.txt");
	public File input;
	public boolean skipPI=false;
	
	@Override
	String getSubUsageOptions() {
		return "-test   <file>    the input corpus. assumed to be tokenized like CoNLL 09 data\n" +
			   "-out    <file>    the file to write output to (default out.txt)\n" +
			   "-nopi             skips the predicate identification";
	}

	@Override
	int trySubParseArg(String[] args, int ai) {
		if(args[ai].equals("-out")){
			ai++;
			output=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-test")){
			ai++;
			input=new File(args[ai]);
			ai++;
		} else if(args[ai].equals("-nopi")){
			ai++;
			skipPI=true;
		}
		return ai;
	}

}
