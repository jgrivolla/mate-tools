package is2.mtag;

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


		int total = 0, totalP=0,corr = 0, corrL = 0, corrT=0,totalX=0;
		int totalD=0, corrD=0;
		int numsent = 0, corrsent = 0, corrsentL = 0;
		SentenceData09 goldInstance = goldReader.getNext();
		SentenceData09 predInstance = predictedReader.getNext();

		while(goldInstance != null) {

			int instanceLength = goldInstance.length();

			if (instanceLength != predInstance.length())
				System.out.println("Lengths do not match on sentence "+numsent);

			int[] goldHeads = goldInstance.heads;
			String[] goldLabels = goldInstance.labels;
			int[] predHeads = predInstance.heads;
			String[] predLabels = predInstance.labels;
			String goldPos[] = goldInstance.gpos;
			String predPos[] = predInstance.ppos;

			String goldFeats[] = goldInstance.ofeats;
			String predFeats[] = predInstance.pfeats;

			boolean whole = true;
			boolean wholeL = true;

			// NOTE: the first item is the root info added during nextInstance(), so we skip it.

			for (int i = 1; i < instanceLength; i++) {
				if (goldFeats[i].equals(predFeats[i])||(goldFeats[i].equals("_")&&predFeats[i]==null)) corrT++;
				else {
					System.out.println("gold:"+goldFeats[i]+" pred:"+predFeats[i]+" "+goldInstance.forms[i]+" snt "+numsent+" i:"+i);
					//for (int k = 1; k < instanceLength; k++) {
						
				//		System.out.print(goldInstance.forms[k]+":"+goldInstance.gpos[k]);
				//		if (k==i)  	System.out.print(":"+predInstance.gpos[k]);
				//		System.out.print(" ");
						
				//	}
					//System.out.println();
				}
				String[] gf = goldFeats[i].split("|");
				int eq=0;
					
				if (predFeats[i]!=null) {
					String[] pf = predFeats[i].split("|");
					totalP +=pf.length;

					if (pf.length>gf.length) totalX +=pf.length;
					else totalX+=gf.length;
					
					for(String g : gf) {
						for(String p : pf) {
							if (g.equals(p)) {eq++;break;}
					}		
				}
				} else totalX+=gf.length;
				totalD +=gf.length;
				corrD +=eq;
			}
			total += instanceLength - 1; // Subtract one to not score fake root token

			if(whole) corrsent++;
			if(wholeL) corrsentL++;
			numsent++;

			goldInstance = goldReader.getNext();
			predInstance = predictedReader.getNext();
		}

		System.out.println("Tokens: " + total+" Correct: " + corrT+" "+(float)corrT/total+" Details: "+((float)corrD/totalD)+" tP "+totalP+" tG "+totalD+" "+(float)corrD/totalP+" tx "+((float)corrD/totalX));
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
