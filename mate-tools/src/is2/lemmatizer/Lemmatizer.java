package is2.lemmatizer;


import is2.data.FV;
import is2.data.PipeGen;
import is2.data.SentenceData09;
import is2.data.Instances;

import is2.io.CONLLReader09;
import is2.io.CONLLWriter09;
import is2.util.DB;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;



public class Lemmatizer {

	public static final double MAX = 0.000000000000001; // 0.001

	public static Pipe pipe;
	public static ParametersDouble params;
	
	public Lemmatizer(Options options) throws FileNotFoundException, IOException {
		init(options);
	}
	
	public static void main (String[] args) throws FileNotFoundException, Exception
	{

		long start = System.currentTimeMillis();
		Options options = new Options(args);

	
		
		if (options.train) {

			Pipe pipe =  new Pipe (options);
			
			Instances is = new Instances();
			pipe.createInstances(options.trainfile,options.trainforest,is);

			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(options.modelName)));
			System.out.println("Features: " + pipe.mf.size()+" Relations "+pipe.mf.getFeatureCounter().get(Pipe.REL));   

			pipe.mf.writeData(dos);

			ParametersDouble params = new ParametersDouble(pipe.mf.size());
			train(options, pipe,params,is);

			pipe.mf.clearData();

			System.out.println("Data cleared ");

			int k=0, c=0;
			
			boolean first=true;
			for(double d : params.parameters) {
				if (d>MAX || d<-MAX || first){
					k++;
					
				}  
				first=false;
			}
			
			System.out.println("Writting "+k+" values of "+c+" (rest is 0.0)");

			k =pipe.mf.wirteMapping(dos,params.parameters,k,MAX); 
			DB.println("number of parameters "+params.parameters.length);
			dos.flush();
			params.write(dos,k,MAX);
			
			dos.writeBoolean(options.upper);
						
			dos.flush();
			dos.close();
			
			
			System.out.print("done.");
		}

		if (options.test) {

			init(options);

			outputParses(options,pipe, params);
		}

		System.out.println();

		if (options.eval) {
			System.out.println("\nEVALUATION PERFORMANCE:");
			Evaluator.evaluate(options.goldfile, options.outfile,options.format);
		}
		long end = System.currentTimeMillis();
		System.out.println("used time "+((float)((end-start)/100)/10));
	}


	public static void init(Options options) throws IOException,FileNotFoundException {
		pipe = new Pipe(options);
		//Parameters params = new ParametersFloat(pipe.mf.size());
		params = new ParametersDouble(pipe.mf.size());

	
		// load the model
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(options.modelName)));
		pipe.mf.read(dis);
		pipe.initValues();
		pipe.initFeatures();
		
		params.read(dis);
		options.upper = dis.readBoolean();
		dis.close();

		DB.println("num params "+params.parameters.length);
		
		pipe.types = new String[pipe.mf.getFeatureCounter().get(Pipe.OPERATION)];
		for(Entry<String,Integer> e : pipe.mf.getFeatureSet().get(Pipe.OPERATION).entrySet()) 
			pipe.types[e.getValue()] = e.getKey();
		
		System.out.println("Loading data finnished. "+pipe.types.length);
		
		pipe.mf.stop();
	}

	

	/**
	 * Do the training
	 * @param instanceLengths
	 * @param options
	 * @param pipe
	 * @param params
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 */
	static public void train(Options options, Pipe pipe, ParametersDouble params, Instances is) 
	throws IOException, InterruptedException, ClassNotFoundException {

	
		int i = 0;
		int del=0; 
		for(i = 0; i < options.numIters; i++) {

			System.out.print("Iteration "+i+": ");

			long start = System.currentTimeMillis();
		
			BufferedInputStream bos = new BufferedInputStream(new FileInputStream(options.trainforest));
			DataInputStream dos = new DataInputStream(bos);
			
			int numInstances = is.size();
					
			long last= System.currentTimeMillis();
			for(int n = 0; n < numInstances; n++) {
				if((n+1) % 500 == 0) del= PipeGen.outValue(n+1,del, last);
				
				Object[][] d = pipe.readInstance(dos, is.length(n),params,options,null);
			
				SentenceData09 instance = pipe.getInstance();

				double upd = (options.numIters*numInstances - (numInstances*i+(n+1))+ 1);
			
				params.updateParamsMIRA(instance,d,upd, pipe);

			}


			System.out.print(numInstances);
 
			

			long end = System.currentTimeMillis();
			//System.out.println("Training iter took: " + (end-start));
			System.out.println("|Time:"+(end-start)+"]");			
		}

		params.averageParams(i*is.size());

	}

	


	/**
	 * Do the parsing
	 * @param options
	 * @param pipe
	 * @param params
	 * @throws IOException
	 */
	static public void outputParses (Options options, Pipe pipe, Parameters params) 
	throws Exception {

		
		long start = System.currentTimeMillis();

		CONLLReader09 depReader = new CONLLReader09(options.testfile, options.formatTask);
		CONLLWriter09 depWriter = new CONLLWriter09(options.outfile, options.formatTask);


		System.out.print("Processing Sentence: ");
		pipe.initValues();

		int cnt = 0;
		int del=0;
		while(true) {

		//	Instance09 instance = pipe.nextInstance();
			SentenceData09 instance = pipe.nextInstance(null, depReader);

			if (instance==null) break;
			
			cnt++;

			String[] forms = instance.forms;

 			int length = instance.ppos.length;
			FV[][] fvs = new FV[length][pipe.types.length];
			double[][] probs = new double[length][pipe.types.length];

			Object[][] d = new Object[length][2];
			for(int j = 1; j < length; j++) {

				pipe.fillFeatureVectorsOne(instance.forms,params, j,fvs, probs, d);
				instance.lemmas[j] = StringEdit.change(instance.forms[j], (String)d[j][1]);
				
				if (options.upper && Character.isUpperCase(instance.forms[j].charAt(0))) {
					instance.lemmas[j] = instance.forms[j].charAt(0)+instance.lemmas[j].substring(1);
				} 
				
				
				if (options.upper ) {
					
					boolean allUpperCase =true;
					for(int index =0;index<instance.forms[j].length();index++ ) {
						char c= instance.forms[j].charAt(index);
						if (Character.isLowerCase(c)) {
							allUpperCase = false;
							break;
						}
					}
					if (allUpperCase)
						instance.lemmas[j] = instance.lemmas[j].toUpperCase();
				}
				
				if (!options.upper) {
					instance.lemmas[j] = instance.lemmas[j].toLowerCase(); 
				}
				
			//	if (!operation.equals("0")) System.out.println("changed "+instance.forms[j]+" to "+instance.lemmas[j]+" op "+operation);
			}


		
			
//			for(int j = 0; j < length; j++) {

//				pipe.fillFeatureVectorsOne(instance,params, decoder,j, fvs, probs,d,instance.pposs);
//				instance.pposs[j]= (String)d[j][1];
//			}

			Arrays.toString(forms);
			
			String[] formsNoRoot = new String[forms.length-1];
			String[] posNoRoot = new String[formsNoRoot.length];
			String[] lemmas = new String[formsNoRoot.length];

			String[] org_lemmas = new String[formsNoRoot.length];

			String[] plabels = new String[formsNoRoot.length];

			String[] of = new String[formsNoRoot.length];
			String[] pf = new String[formsNoRoot.length];
			
			String[] pposs = new String[formsNoRoot.length];
			String[] labels = new String[formsNoRoot.length];
			String[] fillp = new String[formsNoRoot.length];
			                             
			int[] heads = new int[formsNoRoot.length];

			for(int j = 0; j < formsNoRoot.length; j++) {
				formsNoRoot[j] = forms[j+1];
				posNoRoot[j] = instance.gpos[j+1];

				pposs[j] = instance.ppos[j+1];

				labels[j] = instance.labels[j+1];// Pipe.types[is.deprels[cnt-1][j+1]];
				plabels[j]= instance.pedge[j+1];
				heads[j] = instance.heads[j+1];
				org_lemmas[j] = instance.org_lemmas[j+1];

				lemmas[j] = instance.lemmas[j+1];

				if (instance.org_lemmas!=null) org_lemmas[j] = instance.org_lemmas[j+1];
				if (instance.ofeats!=null)  of[j] = instance.ofeats[j+1];
				if (instance.pfeats!=null)	pf[j] = instance.pfeats[j+1];//(String)d[j+1][1];

				if (instance.fillp!=null) fillp[j] = instance.fillp[j+1];
			}

			SentenceData09 i09 = new SentenceData09(formsNoRoot, org_lemmas, lemmas,posNoRoot, pposs, labels, heads,fillp,of, pf);
			i09.pedge=plabels;
			
			i09.fillp = fillp;
			i09.sem = instance.sem;
			i09.semposition = instance.semposition;
			
			if (instance.semposition!=null)
			for (int k= 0;k< instance.semposition.length;k++) {
				i09.semposition[k]=instance.semposition[k]-1;
			}
			
			
			i09.arg = instance.arg;

			
			i09.argposition = instance.argposition;

			if (i09.argposition!=null)
			for (int p= 0;p< instance.argposition.length;p++) {
				if (i09.argposition[p]!=null)
				for(int a=0;a<instance.argposition[p].length;a++)
				i09.argposition[p][a]=instance.argposition[p][a]-1;
			}


			depWriter.write(i09);
			
			del=PipeGen.outValue(cnt, del);

		}
		pipe.close();
		depWriter.finishWriting();

		long end = System.currentTimeMillis();
		System.out.println("Used time " + (end-start));

	}
	
	
	public String[] lemma (String[] inForms, boolean lower)  {

		String[] forms = new String[inForms.length+1];
		System.arraycopy(inForms, 0, forms, 1, inForms.length);
		forms[0] = is2.io.CONLLReader09.ROOT;
		                            
		
		
		int length = forms.length;
		FV[][] fvs = new FV[length][pipe.types.length];
		double[][] probs = new double[length][pipe.types.length];

		String lemmas[] = new String[length];
		
		Object[][] d = new Object[length][2];
		for(int j = 1; j < length; j++) {

			pipe.fillFeatureVectorsOne(forms,params, j,fvs, probs, d);
			lemmas[j] = StringEdit.change(forms[j], (String)d[j][1]);
		}


	

		String[] outLemmas = new String[inForms.length];

		for(int j = 0; j < inForms.length; j++) {
			outLemmas[j] = lower?lemmas[j+1].toLowerCase():lemmas[j+1];
		}

		
		
		
			
		return 	outLemmas;
	}

	
	

}
