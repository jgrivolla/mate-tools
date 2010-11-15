package is2.lemmatizer;

import is2.data.SentenceData09;
import is2.io.CONLLReader09;


public class Evaluator {

	public static void evaluate (String act_file,  String pred_file, String format) throws Exception {

		CONLLReader09 goldReader = new CONLLReader09(act_file);//DependencyReader.createDependencyReader();
	//	boolean labeled = goldReader.startReading(act_file);

		CONLLReader09 predictedReader = new CONLLReader09();
		 predictedReader.startReading(pred_file);

//		if (labeled != predLabeled)
//			System.out.println("Gold file and predicted file appear to differ on whether or not they are labeled. Expect problems!!!");


		int total = 0, corr = 0, corrL = 0, corrT=0;
		int numsent = 0, corrsent = 0, corrsentL = 0;
		SentenceData09 goldInstance = goldReader.getNext();
		SentenceData09 predInstance = predictedReader.getNext();

		while(goldInstance != null) {

			int instanceLength = goldInstance.length();

			if (instanceLength != predInstance.length())
				System.out.println("Lengths do not match on sentence "+numsent);

		//	int[] goldHeads = goldInstance.heads;
		//	String[] goldLabels = goldInstance.labels;
		//	int[] predHeads = predInstance.heads;
		//	String[] predLabels = predInstance.labels;
		//	String goldPos[] = goldInstance.gpos;
		//	String predPos[] = predInstance.gpos;

			String goldLemma[] = goldInstance.org_lemmas;
			String predLemma[] = predInstance.lemmas;

			
			boolean whole = true;
			boolean wholeL = true;

			// NOTE: the first item is the root info added during nextInstance(), so we skip it.

			for (int i = 1; i < instanceLength; i++) {
				if (goldLemma[i].toLowerCase().equals(predLemma[i].toLowerCase())) corrT++;
				else System.out.println("error gold:"+goldLemma[i]+" pred:"+predLemma[i]+" form:"+goldInstance.forms[i]+" snt "+numsent+" i:"+i);
				
			}
			total += instanceLength - 1; // Subtract one to not score fake root token

			if(whole) corrsent++;
			if(wholeL) corrsentL++;
			numsent++;

			goldInstance = goldReader.getNext();
			predInstance = predictedReader.getNext();
		}

		System.out.println("Tokens: " + total+" Correct: " + corrT+" "+(float)corrT/total);
//		System.out.println("Unlabeled Accuracy: " + ((double)corr/total));
//		System.out.println("Unlabeled Complete Correct: " + ((double)corrsent/numsent));

	}

	public static void main (String[] args) throws Exception {
		String format = "CONLL";
		if (args.length > 2)
			format = args[2];

		evaluate(args[0], args[1], format);
	}

}
