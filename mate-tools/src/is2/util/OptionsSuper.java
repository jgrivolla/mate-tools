package is2.util;

import is2.io.CONLLReader09;

import java.io.File;

public class OptionsSuper {

	public String trainfile = null;
	public String testfile = null;
	public File trainforest = null;

	public String nbframes = null;
	public String pbframes = null;

	public boolean nopred = false;
	public boolean upper = false;
	
	public boolean train = false;
	public boolean eval = false;
	public boolean test = false;
	public boolean keep = false;
	public boolean flt = false;

	public String modelName = "prs.model";
	public String useMapping = "model";
	public String device = "C:";
	public String tmp = null;
	public boolean createForest = true;
	public boolean decodeProjective = false;
	public double decodeTH = 0.3d;
	public String format = "CONLL";
	public int formatTask =CONLLReader09.TASK09;
	public int numIters = 10;
	public String outfile = "dp.conll";
	public String charset = "UTF-8";
	public String goldfile = null;
	public String features = null;
	public int hsize = 115911564;
	public int maxLen = 255;
	
	//public boolean secondOrder = true;
	public boolean useRelationalFeatures = false;
	public int count = Integer.MAX_VALUE;
	public int cores = Integer.MAX_VALUE;

	public boolean allFeatures =false;
	public boolean normalize =true;
	
	public void addOption(String args[], int i) {

			if (args[i].equals("-train")) {
				train = true;
				trainfile = args[i+1];
			} else if (args[i].equals("-eval")) {
				eval = true;
				goldfile =args[i+1]; i++;
			} else if (args[i].equals("-test")) {
				test = true;
				testfile = args[i+1]; i++;
			} else if (args[i].equals("-i")) {
				numIters = Integer.parseInt(args[i+1]); i++;
			}
			else if (args[i].equals("-out")) {
				outfile = args[i+1]; i++;
			}

			else if (args[i].equals("-count")) {
				count = Integer.parseInt(args[i+1]); i++;
			} else if (args[i].equals("-model")) {
				modelName = args[i+1]; i++;
			} else if (args[i].equals("-nonormalize")) {
				normalize=false;
			} else if (args[i].equals("-float")) {
				flt =true;	 
			} else if (args[i].equals("-hsize")) {
				hsize= Integer.parseInt(args[i+1]); i++;			
			} else if (args[i].equals("-charset")) {
				charset= args[++i]; 			
			} else if (args[i].equals("-len")) {
				maxLen= Integer.parseInt(args[i+1]); i++;			
			} else if (args[i].equals("-cores")) {
				cores= Integer.parseInt(args[i+1]); i++;			
			}// else System.out.println("Option unkown "+args[i]);
					
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FLAGS [");
		sb.append("train-file: " + trainfile);
		sb.append(" | ");
		sb.append("test-file: " + testfile);
		sb.append(" | ");
		sb.append("gold-file: " + goldfile);
		sb.append(" | ");
		sb.append("output-file: " + outfile);
		sb.append(" | ");
		sb.append("model-name: " + modelName);
		sb.append(" | ");
		sb.append("train: " + train);
		sb.append(" | ");
		sb.append("test: " + test);
		sb.append(" | ");
		sb.append("eval: " + eval);
		sb.append(" | ");
		sb.append("training-iterations: " + numIters);
		sb.append(" | ");
		sb.append("decode-type: " + decodeProjective);
		sb.append(" | ");
		sb.append("create-forest: " + createForest);
		sb.append(" | ");
		sb.append("format: " + format);
	
		sb.append("]\n");
		return sb.toString();
	}

}