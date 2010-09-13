package is2.parser;


import is2.data.DataF;
import is2.data.F2SF;
import is2.data.FV;
import is2.data.Instances;
import is2.data.PipeGen;

import is2.data.Long2IntInterface;

import is2.data.Parse;
import is2.data.SentenceData09;
import is2.io.CONLLReader09;
import is2.io.CONLLWriter09;
import is2.util.DB;
import is2.util.Long2Int;
import is2.util.OptionsSuper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;



public class Parser {

	public static final double MAX = 0.000000001; // 0.001

	public static int THREADS =4;
	
	Long2IntInterface long2int;
	Decoder decoder;
	ParametersFloat params;
	Pipe pipe;
	OptionsSuper options;
	
	/**
	 * Initialize the parser
	 * @param options
	 */
	public Parser (OptionsSuper options) {
	
		this.options=options;
		pipe = new Pipe(options);

		params = new ParametersFloat(0);  
		
		// load the model
		try {
			decoder = readAll(options, pipe, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}


	public static void main (String[] args) throws Exception
	{
		
		        
		Runtime runtime = Runtime.getRuntime();
		        
		THREADS = runtime.availableProcessors();
		
	
		long start = System.currentTimeMillis();
		OptionsSuper options = new Options(args);

		if (options.cores<THREADS&&options.cores>0) THREADS =options.cores;
		
		System.out.println("Found " + runtime.availableProcessors()+" cores use "+THREADS);
       

		if (options.train) {
			Long2IntInterface long2int = new Long2Int(options.hsize);
			DB.println("li size "+long2int.size());

			Pipe pipe =  new Pipe (options);
			Instances is = new Instances();

			Extractor.initFeatures();
			pipe.extractor = new Extractor[THREADS];
			for (int t=0;t<THREADS;t++)  pipe.extractor[t]=new Extractor(long2int);
			
			pipe.createInstances(options.trainfile,is);

			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(options.modelName)));
			zos.putNextEntry(new ZipEntry("data")); 
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(zos));

			Parameters params = new ParametersFloat(long2int.size());

			train(options, pipe,params,is);
			
			MFO.writeData(dos);
			

			//		AI.analyise(params.parameters, pipe.extractor.s_type, pipe.extractor.mf);
			//	AI.analyise(params.parameters, pipe.extractor.s_type, pipe.extractor.mf);

			MFO.clearData();

			DB.println("Data cleared ");

			params.write(dos);

			Edges.write(dos);


			dos.writeBoolean(options.decodeProjective);

			//	pipe.extractor.write(dos);

			dos.writeUTF(""+Parser.class.toString());
			dos.flush();
			dos.close();
			DB.println("Writting data finished ");

		}

		if (options.test) {

			Pipe pipe = new Pipe(options);
			Parameters params = new ParametersFloat(0);  // total should be zero and the parameters are later read

			// load the model

			Decoder decoder = readAll(options, pipe, params);

			
			outputParses(options,pipe, decoder, params);
//			DB.println("misses "+LongIntHash.misses+" good "+LongIntHash.good);

		}

		System.out.println();

		if (options.eval) {
			System.out.println("\nEVALUATION PERFORMANCE:");
			Evaluator.evaluate(options.goldfile, options.outfile);
		}

		long end = System.currentTimeMillis();
		System.out.println("used time "+((float)((end-start)/100)/10));
	}


	public  static Decoder readAll(OptionsSuper options, Pipe pipe,Parameters params) throws IOException {


		DB.println("Reading data started");

		// prepare zipped reader
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(options.modelName)));
		zis.getNextEntry();
		DataInputStream dis = new DataInputStream(new BufferedInputStream(zis));

		MFO.read(dis);

		params.read(dis);
		Long2IntInterface long2int = new Long2Int(params.size());
		DB.println("parsing -- li size "+long2int.size());


		pipe.extractor = new Extractor[THREADS];

		for (int t=0;t<THREADS;t++) pipe.extractor[t]=new Extractor(long2int);

		Extractor.initFeatures();
		Extractor.initStat();


		for (int t=0;t<THREADS;t++) {
			pipe.extractor[t].init();
		}

		Edges.read(dis);

		options.decodeProjective = dis.readBoolean();

		dis.close();

		DB.println("Reading data finnished");

		Decoder decoder =  new Decoder();
		Decoder.NON_PROJECTIVITY_THRESHOLD =(float)options.decodeTH;

		Extractor.initStat();

		return decoder;
	}



	/**
	 * Do the training
	 * @param instanceLengths
	 * @param options
	 * @param pipe
	 * @param params
	 * @param is 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 */
	static public void train(OptionsSuper options, Pipe pipe, Parameters params, Instances is) 
	throws IOException, InterruptedException, ClassNotFoundException {

		
		System.out.println("\nTraining Information ");
		System.out.println("-------------------- ");


		Decoder decoder =  new Decoder();
		Decoder.NON_PROJECTIVITY_THRESHOLD =(float)options.decodeTH;

		System.out.println("Decoding: "+(options.decodeProjective?"projective":"non-projective"));
		if (!options.decodeProjective) System.out.println(""+Decoder.getInfo());

		int numInstances = is.size();


		int maxLenInstances =0;
		for(int i=0;i<numInstances;i++) if (maxLenInstances<is.length(i)) maxLenInstances=is.length(i);

		DataF data = new DataF(maxLenInstances, MFO.getFeatureCounter().get(PipeGen.REL).shortValue());

		int iter = 0;
		int del=0; 
		float error =0;
		float f1=0;

		FV pred = new FV();
		FV act = new FV();

		double	upd =  (double)(numInstances*options.numIters)+1;
		
		for(; iter < options.numIters; iter++) {

			System.out.print("Iteration "+iter+": ");

			long start = System.currentTimeMillis();

			long last= System.currentTimeMillis();
			error=0;
			f1=0;
			// upd=0;
			for(int n = 0; n < numInstances; n++) {

				
				
				//double upd = (double)(options.numIters*numInstances - (numInstances*((iter+1)-1) +(n+1))+1);
				upd--;
				
				if (is.deprels[n].length>options.maxLen) continue;
				
				String info = " td "+((Decoder.timeDecotder)/1000000F)+" tr "+((Decoder.timeRearrange)/1000000F)
				+" te "+((Pipe.timeExtract)/1000000F);

				if((n+1) %500 == 0) del= PipeGen.outValueErr(n+1,Math.round(error*1000)/1000,f1/n,del, last, upd,info);

				short pos[] = is.pposs[n];
				//	short pos[] = is.gpos[n];

				data = pipe.fillVector((F2SF)params.getFV(), decoder, is, n, data,pos);

				Parse d = Decoder.decode(pos,  data, options.decodeProjective);

				double e= pipe.errors(is, n ,d);

				if (d.f1>0)f1+=d.f1;

				if (e<=0) continue;

				// get predicted feature vector
				pred.clear();
				pipe.extractor[0].encodeCat(pos,is.forms[n],is.lemmas[n],d.heads, d.types, is.feats[n],pred);

				error += e;

				act.clear();
				pipe.extractor[0].encodeCat(pos,is.forms[n],is.lemmas[n],is.heads[n], is.deprels[n], is.feats[n], act);

				params.update(act, pred, is, n, d, upd,e);
			}

			String info = " td "+((Decoder.timeDecotder)/1000000F)+" tr "+((Decoder.timeRearrange)/1000000F)
			+" te "+((Pipe.timeExtract)/1000000F);
			PipeGen.outValueErr(numInstances,Math.round(error*1000)/1000,f1/numInstances,del,last, upd,info);
			del=0;
			//System.out.println();
			//DB.println("Decoder time decode "+(((float)Decoder.timeDecotder)/1000000F)+" rearrange "+(((float)Decoder.timeRearrange)/1000000F)
			//		+" extract "+(((float)Pipe.timeExtract)/1000000F));

			Decoder.timeDecotder=0;Decoder.timeRearrange=0; Pipe.timeExtract=0;
			long end = System.currentTimeMillis();

			System.out.println(" time:"+(end-start));			

		}
		params.average(iter*is.size());
		//	printSim(sim,pipe.extractor.mf);

	}                                   


	/**
	 * Do the parsing
	 * @param options
	 * @param pipe
	 * @param decoder
	 * @param params
	 * @throws IOException
	 */
	static public void outputParses (OptionsSuper options, Pipe pipe, Decoder decoder, Parameters params) 
	throws Exception {

		long start = System.currentTimeMillis();

		CONLLReader09 depReader = new CONLLReader09(options.testfile, options.formatTask);
		CONLLWriter09 depWriter = new CONLLWriter09(options.outfile, options.formatTask);

		Extractor.initFeatures();

		int cnt = 0;
		int del=0;
		long last = System.currentTimeMillis();

		System.out.println("\nParsing Information ");
		System.out.println("------------------- ");

		if (!options.decodeProjective) System.out.println(""+Decoder.getInfo());

		String[] types = new String[MFO.getFeatureCounter().get(PipeGen.REL)];
		for (Entry<String, Integer> e : MFO.getFeatureSet().get(PipeGen.REL).entrySet())  	types[e.getValue()] = e.getKey();

		
		System.out.print("Processing Sentence: ");
		DataF d=null;

		while(true) {

			Instances is = new Instances();
			is.init(1, new MFO(),options.formatTask);

			SentenceData09 instance = pipe.nextInstance(is, depReader);
			if (instance==null) break;
			cnt++;


			String[] forms = instance.forms;

			// use for the training ppos

			d = pipe.fillVector((F2SF)params.getFV(), decoder,is,0,d,is.pposs[0]);//cnt-1

			short[] pos =is.pposs[0];

			Parse prs=	Decoder.decode(pos,d,options.decodeProjective); //cnt-1


			//	System.out.println("double "+params.getScore(d.fv)+" "+params.getScore(d.fv)/(forms.length-1));

			String[] formsNoRoot = new String[forms.length-1];
			String[] posNoRoot = new String[formsNoRoot.length];
			String[] lemmas = new String[formsNoRoot.length];

			String[] org_lemmas = new String[formsNoRoot.length];

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

				labels[j] = types[prs.types[j+1]];
				heads[j] = prs.heads[j+1];
				lemmas[j] = instance.lemmas[j+1];

				if (instance.org_lemmas!=null) org_lemmas[j] = instance.org_lemmas[j+1];
				if (instance.ofeats!=null)  of[j] = instance.ofeats[j+1];
				if (instance.pfeats!=null)	pf[j] = instance.pfeats[j+1];

				if (instance.fillp!=null) fillp[j] = instance.fillp[j+1];
			}

			SentenceData09 i09 = new SentenceData09(formsNoRoot, lemmas, org_lemmas,posNoRoot, pposs, labels, heads,fillp,of, pf);
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

			del=PipeGen.outValue(cnt, del,last);

		}
		//pipe.close();
		depWriter.finishWriting();
		long end = System.currentTimeMillis();
		//		DB.println("errors "+error);
		System.out.println("Used time " + (end-start));
		System.out.println("forms count "+Instances.m_count+" unkown "+Instances.m_unkown);

	}

	static public SentenceData09 outputParses (SentenceData09 instance, OptionsSuper options, Pipe pipe, Decoder decoder, ParametersFloat params) throws InterruptedException  {

		
		String[] types = new String[MFO.getFeatureCounter().get(PipeGen.REL)];
		for (Entry<String, Integer> e : MFO.getFeatureSet().get(PipeGen.REL).entrySet())  	types[e.getValue()] = e.getKey();


		

		Instances is = new Instances();
		is.init(1, new MFO(),options.formatTask);
		try {
			new CONLLReader09().insert(is, instance);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		String[] forms = instance.forms;

		// use for the training ppos

		DataF d2 = pipe.fillVector(params.getFV(), decoder,is,0,null,is.pposs[0]);//cnt-1

		short[] pos = is.pposs[0];

		Parse d=	Decoder.decode(pos,d2,options.decodeProjective); //cnt-1

		String[] formsNoRoot = new String[forms.length-1];
		String[] posNoRoot = new String[formsNoRoot.length];
		String[] lemmas = new String[formsNoRoot.length];

		String[] org_lemmas = new String[formsNoRoot.length];

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

			labels[j] = types[d.types[j+1]];
			//System.out.print(" "+d.types[j+1] );
			heads[j] = d.heads[j+1];
			lemmas[j] = instance.lemmas[j+1];

			if (instance.org_lemmas!=null) org_lemmas[j] = instance.org_lemmas[j+1];
			if (instance.ofeats!=null)  of[j] = instance.ofeats[j+1];
			if (instance.pfeats!=null)	pf[j] = instance.pfeats[j+1];

			if (instance.fillp!=null) fillp[j] = instance.fillp[j+1];
		}

		SentenceData09 i09 = new SentenceData09(formsNoRoot, lemmas, org_lemmas,posNoRoot, pposs, labels, heads,fillp,of, pf);
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

		return i09;

	}

	
	public SentenceData09 parse (SentenceData09 instance)   {

		String[] types = new String[MFO.getFeatureCounter().get(PipeGen.REL)];
		for (Entry<String, Integer> e : MFO.getFeatureSet().get(PipeGen.REL).entrySet())  	types[e.getValue()] = e.getKey();

		Instances is = new Instances();
		is.init(1, new MFO(),options.formatTask);
		try {
			new CONLLReader09().insert(is, instance);
		} catch (IOException e) {
			e.printStackTrace();
		} 

		String[] forms = instance.forms;

		// use for the training ppos
		DataF d2;
		try {
			d2 = pipe.fillVector(params.getFV(), decoder,is,0,null,is.pposs[0]);//cnt-1
		} catch (Exception e ) {
			e.printStackTrace();
			return null;
		}
		short[] pos = is.pposs[0];

		Parse d=	null;
		try {
			d =Decoder.decode(pos,d2,options.decodeProjective); //cnt-1
		}catch (Exception e) {
				
			e.printStackTrace();
			}
		String[] formsNoRoot = new String[forms.length-1];
		String[] posNoRoot = new String[formsNoRoot.length];
		String[] lemmas = new String[formsNoRoot.length];

		String[] org_lemmas = new String[formsNoRoot.length];

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

			labels[j] = types[d.types[j+1]];
			//System.out.print(" "+d.types[j+1] );
			heads[j] = d.heads[j+1];
			lemmas[j] = instance.lemmas[j+1];

			if (instance.org_lemmas!=null) org_lemmas[j] = instance.org_lemmas[j+1];
			if (instance.ofeats!=null)  of[j] = instance.ofeats[j+1];
			if (instance.pfeats!=null)	pf[j] = instance.pfeats[j+1];

			if (instance.fillp!=null) fillp[j] = instance.fillp[j+1];
		}

		SentenceData09 i09 = new SentenceData09(formsNoRoot, org_lemmas, lemmas,posNoRoot, pposs, labels, heads,fillp,of, pf);
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

		return i09;

	}




}
