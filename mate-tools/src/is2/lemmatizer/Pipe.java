package is2.lemmatizer;

//import gnu.trove.TIntArrayList;
import is2.data.FV;
import is2.data.Instances;
import is2.data.PipeGen;
import is2.data.SentenceData09;
import is2.io.CONLLReader09;
import is2.io.CONLLWriter09;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;




final public class Pipe extends PipeGen {

	private static final String _F0 = "F0";
	private static final String _F1 = "F1",_F2 = "F2",_F3 = "F3",_F4 = "F4",_F5 = "F5",_F6= "F6",_F7= "F7",_F8= "F8",_F9="F9",_F10 = "F10";
	private static final String _F11="F11",_F12="F12",_F13= "F13",_F14="F14",_F15="F15",_F16="F16",_F17="F17",_F18="F18",_F19="F19",_F20="F20";
	private static final String _F21="F21",_F22="F22",_F23= "F23",_F24="F24",_F25="F25",_F26="F26",_F27="F27",_F28="F28",_F29="F29",_F30="F30";
	private static final String _F31="F31",_F32="F32",_F33= "F33",_F34="F34",_F35="F35",_F36="F36",_F37="F37",_F38="F38",_F39="F39",_F40="F40";
	private static final String _F41="F41";
	
	private static final String _F1l = "F1l",_F2l = "F2l",_F3l = "F3l",_F4l = "F4l",_F5l = "F5l",_F6l= "F6l",_F7l= "F7l",_F8l= "F8l",_F9l="F9l",_F10l = "F10l";
	private static final String _F11l="F11l",_F12l="F12l",_F13l= "F13l", _F24l="F24l",_F25l="F25l",_F26l="F26l",_F27l="F27l",_F28l="F28l",_F29l="F29l",
	_F33l= "F33l",_F34l="F34l",_F35l="F35l",_F36l="F36l",_F37l="F37l",_F38l="F38l";
	
	
	private static int _f0,_f1,_f2,_f3,_f4,_f5,_f6,_f7,_f8,_f9,_f10,_f11,_f12,_f13,_f14,_f15,_f16,_f17,_f18,_f19,_f20;
	private static int _f21,_f22,_f23,_f24,_f25,_f26,_f27,_f28,_f29,_f30,_f31,_f32,_f33,_f34,_f35,_f36,_f37,_f38,_f39,_f41;
	private static int  _CEND;


	private static final String FIN = "FIN";
	private static final String E2 = "E2";
	private static final String E3 = "E3";
	private static final String C0 = "C0";
	private static final String E0 = "E0";
	private static final String C1 = "C1";
	private static final String E1 = "E1";
	private static final String C2 = "C2";
	private static final String C3 = "C3";
	private static final String C4 = "C4";
	private static final String E4 = "E4";
	private static final String C5 = "C5";
	private static final String E5 = "E5";
	private static final String APOS_TRIP = "APOS_TRIP";
	private static final String POS_TRIP = "POS_TRIP";
	private static final String STWRD = "STWRD";
	private static final String STPOS = "STPOS";
	private static final String _0 = "0";
	private static final String L0 = "L0";
	private static final String R0 = "R0";
	private static final String L4 = "L4";
	private static final String L1 = "L1";
	private static final String L2 = "L2";
	private static final String L3 = "L3";
	private static final String L5 = "L5";
	private static final String L10 = "L10";
	private static final String R1 = "R1";
	private static final String R2 = "R2";
	private static final String R3 = "R3";
	private static final String R4 = "R4";
	private static final String R5 = "R5";
	private static final String R7 = "R7";
	private static final String L7 = "R7";
	private static final String R10 = "R10";
	private static final String _4 = "4";
	private static final String _3 = "3";
	private static final String _2 = "2";
	private static final String _1 = "1";
	private static final String PT = "PT";
	private static final String PC = "PC";
	private static final String CPOS = "CPOS";
	protected static final String POS = "POS";
	protected static final String GPOS = "GPOS";
	protected static final String DIST = "DIST";
	private static final String MID = "MID";
	protected static final String WORD = "WORD";

	public static final String REL = "REL";
	private static final String END = "END";
	private static final String STR = "STR";
	private static final String LA = "LA";
	private static final String RA = "RA";
	protected static final String TYPE = "TYPE";
	private static final String CHAR = "C";
	private static final String _5 = "5";
	private static final String _10 = "10";
	private static final String DIR = "D";
	public static final String OPERATION = "OP";

	private CONLLReader09 depReader;
	private CONLLWriter09 depWriter;

	public String[] types;

	public boolean labeled = false;

	public MF mf =new MF();
	final MF.Data2 dl1 = new MF.Data2();

	private Options options;

	public Pipe (Options options) throws IOException {
		this.options = options;

		depReader = new CONLLReader09();
	}

	
	
	public void createInstances(String file,File featFileName, Instances is) throws Exception {

		depReader.startReading(file);
		mf.register(REL,"<root-type>");
		mf.register(POS,"<root-POS>");


		System.out.println("Registering feature parts ");
		HashMap<String,Integer> ops = new HashMap<String, Integer> ();
		int ic=0;
		while(true) {
			SentenceData09 instance1 = depReader.getNext();
			if (instance1== null) break;
			ic++;

			String[] labs1 = instance1.labels;
			for(int i1 = 0; i1 < labs1.length; i1++) {
				//typeAlphabet.lookupIndex(labs1[i1]);
				mf.register(REL, labs1[i1]);
			}

			String[] w = instance1.forms;
			for(int i1 = 0; i1 < w.length; i1++) mf.register(WORD,  w[i1]);
			for(int i1 = 0; i1 < w.length; i1++) mf.register(WORD,  w[i1].toLowerCase());

//			w = instance1.split_lemma;
//			for(int i1 = 0; i1 < w.length; i1++) mf.register(WORD,  w[i1]);
			
			for(int i1 = 0; i1 < w.length; i1++) registerChars(CHAR,  w[i1]);


			w = instance1.ppos;
			for(int i1 = 0; i1 < w.length; i1++) mf.register(POS,  w[i1]);

			w = instance1.gpos;
			for(int i1 = 0; i1 < w.length; i1++) mf.register(POS,  w[i1]);
			
			
			for(int i1 = 1; i1 < w.length; i1++)  {
				String op = getOperation(instance1, i1);
				if (ops.get(op)==null) ops.put(op, 1);
				else ops.put(op, (ops.get(op)+1));
			//	if (!op.equals("0")) System.out.println("result "+" "+op+" "+instance1.forms[i1]+" "+instance1.lemmas[i1]+" count "+ops.get(op));
						
			}
			
			
		//	createFeatureVector(instance1,true);

		}
		for(Entry<String, Integer> e : ops.entrySet()) {
			mf.register(OPERATION, e.getKey());
		}
		
		System.out.println("found operions count "+ops.size());
		
		initFeatures();

		mf.calculateBits();
		initValues();

		depReader.startReading(file);

		System.gc();
		long mem1 = Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory();
		System.out.println("-- Memory used so far "+(mem1/1024)+" kb");
		int num1 = 0;
		long start1 = System.currentTimeMillis();

		System.out.print("Creating Features: ");
		is.init(ic, mf) ;
		int del=0;
		while(true) {
			if (num1 % 100 ==0) {del = outValue(num1, del);}
			SentenceData09 instance1 = depReader.getNext(is);
			if (instance1== null) break;

			final int instanceLength = instance1.length();
			
			for(int i = 0; i < instanceLength; i++) {
				
				String operation = getOperation(instance1,i);
				
				//	if (operation.equals("0")) continue;
				
					FV fv = new FV();
					addCoreFeatures(instance1.forms,i,instance1.gpos[i],operation,fv);
			//		instance1.setFeatureVector(fv,i);
				
			
			}

			if (num1>options.count) break;

			num1++;
		}
		num1=0;
		mf.stop();
		long end1 = System.currentTimeMillis();
		System.gc();
		long mem2 = Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory();
		System.out.print("  time "+(end1-start1)+" mem "+(mem2/1024)+" kb");

		types = new String[mf.getFeatureCounter().get(OPERATION)];

		for(Entry<String,Integer> e : mf.getFeatureSet().get(OPERATION).entrySet()) {
			types[e.getValue()] = e.getKey();
			//	System.out.println("set pos "+e.getKey());
		}

	
		System.out.println("Num Features: " + mf.size());


		long start11;
		long end11;
		
	    depReader.startReading(file);
		
		//TIntArrayList lengths = new TIntArrayList();
		
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(featFileName),32768);
		
		DataOutputStream dos = new DataOutputStream(bos);
		
		int num11=0;
		
		start11 = System.currentTimeMillis();
		System.out.println("Creating Feature Vectors: ");
		long last = System.currentTimeMillis();
		while(true) {
		
			SentenceData09 instance = depReader.getNext();
			if (instance==null) break;
			if (num11 % 100 ==0) del = outValue(num11, del,last);
		
		
			createFeatureVector(instance);
		
		
		//	instance.actParseTree = "";
		
	//		lengths.add(instance.length());
		
			writeInstance(instance,dos);
		
		
			if (num11>options.count) break;
		
			num11++;
		}
		
		end11 = System.currentTimeMillis();
		System.out.println("\nUsed time "+(end11-start11));
	//	mf.stop();
	
		dos.close();
		

	}



	public static String getOperation(SentenceData09 instance1, int i1) {
		String s = new StringBuffer(instance1.forms[i1].toLowerCase()).reverse().toString();
		String t = new StringBuffer(instance1.org_lemmas[i1].toLowerCase()).reverse().toString();

		StringBuffer po = new StringBuffer();
		String op;
		if (!s.equals(t)) {
		
			int[][] d =StringEdit.LD(s, t);
			StringEdit.searchPath(s,t,d, po, false);
			op = po.toString();
		} else op ="0"; // do nothing
		return op;
	}

	private void registerChars(String type, String word) {

		for(int i=0;i<word.length();i++)
			mf.register(type, Character.toString(word.charAt(i)));      
	}

	

	public void initValues() {
		s_pos = mf.getFeatureBits(POS);
		s_word = mf.getFeatureBits(WORD);
		s_type = mf.getFeatureBits(TYPE);
		s_dir = mf.getFeatureBits(DIR);
		s_dist = mf.getFeatureBits(DIST);
		s_char = mf.getFeatureBits(CHAR);
		s_oper = mf.getFeatureBits(OPERATION);


		
		dl1.a[0] = s_type;
		dl1.a[1] = s_oper;
		for (int k = 2; k < 7; k++)
			dl1.a[k] = s_pos;


	}

	public static short s_pos;
	public static short s_word;
	public static short s_type;
	public static short s_dir;
	public static short s_dist;
	public static short s_char;
	public static short s_oper;



	/**
	 * Initialize the features.
	 * @param maxFeatures
	 */
	public void initFeatures() {
		
		

		_f0 = mf.register(TYPE, _F0);
		_f1 = mf.register(TYPE, _F1);
		_f2 = mf.register(TYPE, _F2);
		_f3 = mf.register(TYPE, _F3);
		_f4 = mf.register(TYPE, _F4);
		_f5 = mf.register(TYPE, _F5);
		_f6 = mf.register(TYPE, _F6);
		_f7 = mf.register(TYPE, _F7);
		_f8 = mf.register(TYPE, _F8);
		_f9 = mf.register(TYPE, _F9);
		_f10 = mf.register(TYPE, _F10);
		_f11 = mf.register(TYPE, _F11);
		_f12 = mf.register(TYPE, _F12);
		_f13 = mf.register(TYPE, _F13);
		_f14 = mf.register(TYPE, _F14);
		_f15 = mf.register(TYPE, _F15);
		_f16 = mf.register(TYPE, _F16);
		_f17 = mf.register(TYPE, _F17);
		_f18 = mf.register(TYPE, _F18);
		_f19 = mf.register(TYPE, _F19);
		_f20 = mf.register(TYPE, _F20);
		_f21 = mf.register(TYPE, _F21);
		_f22 = mf.register(TYPE, _F22);
		_f23 = mf.register(TYPE, _F23);
		_f24 = mf.register(TYPE, _F24);
		_f25 = mf.register(TYPE, _F25);
		_f26 = mf.register(TYPE, _F26);
		_f27 = mf.register(TYPE, _F27);
		_f28 = mf.register(TYPE, _F28);
		_f29 = mf.register(TYPE, _F29);
		_f30 = mf.register(TYPE, _F30);

		_f31 = mf.register(TYPE, _F31);
		_f32 = mf.register(TYPE, _F32);
		_f33 = mf.register(TYPE, _F33);
		_f34 = mf.register(TYPE, _F34);

		_f35 = mf.register(TYPE, _F35);
		_f36 = mf.register(TYPE, _F36);
		_f37 = mf.register(TYPE, _F37);
		_f38 = mf.register(TYPE, _F38);
		
	
		mf.register(POS, MID);
		mf.register(POS, STR);
		mf.register(POS, END);
		mf.register(TYPE, CHAR);

		mf.register(WORD, STR);
		mf.register(WORD, END);


		_CEND = mf.register(CHAR, END);
		
		


		mf.register(DIR, LA);
		mf.register(DIR, RA);



		mf.register(DIST, L0);
		mf.register(DIST, L5);
		mf.register(DIST, L7);
		mf.register(DIST, L10);
		mf.register(DIST, L1);
		mf.register(DIST, L2);
		mf.register(DIST, L3);
		mf.register(DIST, L4);
		mf.register(DIST, L5);

		mf.register(DIST, R0);
		mf.register(DIST, R5);
		mf.register(DIST, R7);
		mf.register(DIST, R10);
		mf.register(DIST, R1);
		mf.register(DIST, R2);
		mf.register(DIST, R3);
		mf.register(DIST, R4);
		mf.register(DIST, R5);


		mf.register(TYPE, C0);
		mf.register(TYPE, C1);
		mf.register(TYPE, C2);
		mf.register(TYPE, C3);
		mf.register(TYPE, C4);
		mf.register(TYPE, C5);
		mf.register(TYPE, "C6");

		mf.register(TYPE, E0);
		mf.register(TYPE, E1);
		mf.register(TYPE, E2);
		mf.register(TYPE, E3);
		mf.register(TYPE, E4);
		mf.register(TYPE, E5);
		mf.register(TYPE, "E6");



		// optional features
		mf.register(TYPE, POS_TRIP);
		mf.register(TYPE, APOS_TRIP);
		mf.register(DIST, "RA");
		mf.register(DIST, "LA");
		mf.register(WORD,STWRD);
		mf.register(POS,STPOS);
		mf.register(DIST, _0);
		mf.register(DIST, _1);
		mf.register(DIST, _2);
		mf.register(DIST, _3);
		mf.register(DIST, _4);
		mf.register(DIST, _5);
		mf.register(DIST, _10);


	}

	public void initInputFile (String file, Options op) throws IOException {
		depReader.startReading(file);
	}

	public void initOutputFile (String file) throws IOException {
		depWriter = new CONLLWriter09(file);
	//	depWriter.startWriting(file);
	}

	public void outputInstance (SentenceData09 instance) throws IOException {
		depWriter.write(instance);
	}

	public void close () throws IOException {
		if (null != depWriter) {
			depWriter.finishWriting();
		}
	}

	public String getType (int typeIndex) {
		return types[typeIndex];
	}

	protected final SentenceData09 nextInstance(Instances is, CONLLReader09 depReader) throws Exception {

		SentenceData09 instance = depReader.getNext(is);
		if (instance == null || instance.forms == null)	return null;


		return instance;
	}




	public final void add(int num, FV fv) {
		if(num >= 0)  fv.createFeature(num);
		
	}

	public void createFeatureVector(SentenceData09 instance) {

		final int instanceLength = instance.length();

		for(int i = 0; i < instanceLength; i++) {

			
				FV fv = new FV();
				String operation = getOperation(instance,i);
				addCoreFeatures(instance.forms,i,instance.gpos[i],operation,fv);
				instance.setFeatureVector(fv,i);
			

		}
		
	}

	

	public void addCoreFeatures(String[] forms,int i, String pos2x, String operation, FV fv) {

		
		int len = forms.length;
		String lower = forms[i].toLowerCase();
		int form =mf.getValue(WORD, forms[i]);
		
		int oper  =mf.getValue(OPERATION, operation);
		
		int c0= mf.getValue(CHAR, String.valueOf(forms[i].charAt(0)));
		int c1= forms[i].length()>1?mf.getValue(CHAR, String.valueOf(forms[i].charAt(1))):mf.getValue(CHAR, END);
		int c2= forms[i].length()>2?mf.getValue(CHAR, String.valueOf(forms[i].charAt(2))):_CEND;
		int c3= forms[i].length()>3?mf.getValue(CHAR, String.valueOf(forms[i].charAt(3))):_CEND;
		int c4= forms[i].length()>4?mf.getValue(CHAR, String.valueOf(forms[i].charAt(4))):_CEND;
		
		
		int e0 = mf.getValue(CHAR,String.valueOf(forms[i].charAt(forms[i].length()-1)));
		int e1 = forms[i].length()>1?mf.getValue(CHAR,String.valueOf(forms[i].charAt(forms[i].length()-2))):mf.getValue(CHAR, END);
		int e2 = forms[i].length()>2?mf.getValue(CHAR,String.valueOf(forms[i].charAt(forms[i].length()-3))):_CEND;
		int e3 = forms[i].length()>3?mf.getValue(CHAR,String.valueOf(forms[i].charAt(forms[i].length()-4))):_CEND;
		int e4 = forms[i].length()>4?mf.getValue(CHAR,String.valueOf(forms[i].charAt(forms[i].length()-5))):_CEND;
	
		b.a[0]=s_type; b.v[0] = _f0;
		b.a[1]=s_word; b.v[1] = form;
		b.a[2]=s_oper; b.v[2]=oper;
		long l = mf.calc3(b);
		add(mf.l2i(l), fv);
			
		b.v[0] = _f1; b.v[1] = mf.getValue(WORD, lower); b.v[2]=oper;
		 l = mf.calc3(b);
		add(mf.l2i(l), fv);
			
		
	    b.v[0] = _f4;  b.a[1]=s_oper; b.v[1] = oper;
		b.a[2]=s_char; b.v[2]=c0;
		add(mf.l2i(mf.calc3(b)), fv);

		
		b.v[0] = _f5;  //b.v[1] = p;
		b.a[2]=s_char; b.v[2] = e0;
		add(mf.l2i(mf.calc3(b)), fv);

		b.a[3]=s_char; b.a[4]=s_char; b.a[5]=s_char; b.a[6]=s_char;
	    b.v[2]=c0; b.v[3]=c1; b.v[4]=c2; b.v[5]=c3; b.v[6]=c4;
	    
		if (forms[i].length()>1) { b.v[0] =_f6; fv.add(mf.l2i(mf.calc4(b)));}
		if (forms[i].length()>2) { b.v[0] =_f7; fv.add(mf.l2i(mf.calc5(b)));}
		if (forms[i].length()>3) { b.v[0] =_f8; fv.add(mf.l2i(mf.calc6(b)));}
		if (forms[i].length()>4) { b.v[0] =_f9; fv.add(mf.l2i(mf.calc7(b)));}

		 b.v[2]=e0; b.v[3]=e1; b.v[4]=e2; b.v[5]=e3; b.v[6]=e4;
		if (forms[i].length()>1) { b.v[0] =_f10; fv.add(mf.l2i(mf.calc4(b)));}
		if (forms[i].length()>2) { b.v[0] =_f11; fv.add(mf.l2i(mf.calc5(b)));}
		if (forms[i].length()>3) { b.v[0] =_f12; fv.add(mf.l2i(mf.calc6(b)));}
		if (forms[i].length()>4) { b.v[0] =_f13; fv.add(mf.l2i(mf.calc7(b)));}

		if (len>i+1) {

			b.v[0] = _f14;  b.v[2] = mf.getValue(CHAR, String.valueOf(forms[i+1].charAt(0)));
			fv.add(mf.l2i(mf.calc3(b)));

			b.v[0] = _f15;  b.v[2] = mf.getValue(CHAR, String.valueOf(forms[i+1].charAt(forms[i+1].length()-1)));
			fv.add(mf.l2i(mf.calc3(b)));
			
			if (forms[i+1].length()>1 ) {
				b.v[0] = _f16;  b.v[2] = mf.getValue(CHAR, String.valueOf(forms[i+1].charAt(0)));
				b.v[3] = mf.getValue(CHAR, String.valueOf(forms[i+1].charAt(1)));
				fv.add(mf.l2i(mf.calc4(b)));

				b.v[0] = _f17;  b.v[2] = mf.getValue(CHAR, String.valueOf(forms[i+1].charAt(forms[i+1].length()-1)));
				b.v[3] = mf.getValue(CHAR, String.valueOf(forms[i+1].charAt(forms[i+1].length()-2)));
				fv.add(mf.l2i(mf.calc4(b)));
			}
			
			
			b.v[0] = _f18;  
			b.a[2]=s_word; b.v[2] =  mf.getValue(WORD, forms[i+1]);
			fv.add(mf.l2i(mf.calc3(b)));

			if (len>i+2) {
				b.v[0] = _f32;  
				b.a[2]=s_word; b.v[2] =  mf.getValue(WORD, forms[i+2]);
				b.a[3]=s_word; b.v[3] =  mf.getValue(WORD, forms[i+1]);
				fv.add(mf.l2i(mf.calc4(b)));
		
					
				fv.add(mf.l2i(mf.calc3(b)));
						
			}
			
			if (len>i+3) {
				b.v[0] = _f33;  
				b.a[2]=s_word; b.v[2] =  mf.getValue(WORD, forms[i+3]);
				b.a[3]=s_word; b.v[3] =  mf.getValue(WORD, forms[i+2]);
				fv.add(mf.l2i(mf.calc4(b)));
		
				fv.add(mf.l2i(mf.calc3(b)));
				
				
			}
			
		}
		
		// length

		dl1.v[0]= _f19;	dl1.v[1]=oper; dl1.v[2]=forms[i].length();
		fv.add(mf.l2i(mf.calc3(dl1)));
		
		// contains upper case

		short upper =0;
		short number = 1;
		for(int k1=0;k1<forms[i].length();k1++){
			char c =forms[i].charAt(k1);
			if (Character.isUpperCase(c)) {
				if (k1==0) upper=1;
				else {
					// first char + another
					if (upper==1)upper=3;
					// another uppercase in the word
					else if (upper==0) upper=2;
				}
			}
			
			if (Character.isDigit(c) && k1==0) number =2 ;
			else if (Character.isDigit(c) && number==1) number = 3 ;

		}
		
//		dl1.v[0]= _f20;	dl1.v[1]=oper; dl1.v[2]=upper; //dl1.v[3]=i==0?1:2;
//		fv.add(mf.l2i(mf.calc3(dl1)));
		
		// contains a number
		dl1.v[0]= _f21;	dl1.v[1]=oper; dl1.v[2]=number;
		fv.add(mf.l2i(mf.calc3(dl1)));


//		if (i<1) return ;

//		dl1.v[0]= _f22;	dl1.v[2]=i>=1?mf.getValue(POS, pposs[i-1]):mf.getValue(POS, STR); 
//		fv.add(mf.l2i(mf.calc3(dl1)));

		if (i<1) return ;
		
		b.a[0]=s_type; b.v[0] = _f27;
		b.a[1]=s_word; b.v[1] = mf.getValue(WORD,forms[i-1]);
		b.a[2]=s_oper; b.v[2]=oper;
		add(mf.l2i(mf.calc3(b)), fv);

		
		if (i<2) return ;
	
//		dl1.v[0]= _f23;	dl1.v[2]=i<2?mf.getValue(POS, STR):mf.getValue(POS, pposs[i-2]); 
//		fv.add(mf.l2i(mf.calc3(dl1)));

	
//		dl1.v[0]= _f24;	dl1.v[2]=mf.getValue(POS, pposs[i-1]); 
//		dl1.v[3]=mf.getValue(POS, pposs[i-2]); 
//		fv.add(mf.l2i(mf.calc4(dl1)));

		//added this before it was 99.46
		b.a[0]=s_type; b.v[0] = _f28;
		b.a[1]=s_word; b.v[1] = mf.getValue(WORD,forms[i-2]);
		b.a[2]=s_oper; b.v[2]=oper;
		add(mf.l2i(mf.calc3(b)), fv);
		
		// result 99.484
		

	//	b.a[0]=s_type; b.v[0] = _f34;
	//	b.a[1]=s_pos; b.v[1]=p;
	//	b.a[2]=s_word; b.v[2] = mf.getValue(WORD,forms[i-1]);
	//	b.a[3]=s_pos; b.v[3] = mf.getValue(POS,pposs[i-2]);
	//	add(mf.l2i(mf.calc4(b)), fv);

	
	//	b.a[0]=s_type; b.v[0] = _f35;
	//	b.a[1]=s_pos; b.v[1]=p;
	//	b.a[2]=s_word; b.v[2] = mf.getValue(WORD,forms[i-2]);
	//	b.a[3]=s_pos; b.v[3] = mf.getValue(POS,pposs[i-1]);
	//	add(mf.l2i(mf.calc4(b)), fv);
	

		
		if (i<3) return ;
		
//		dl1.v[0]= _f25;	dl1.v[2]=mf.getValue(POS, pposs[i-3]); 
//		fv.add(mf.l2i(mf.calc3(dl1)));

//		dl1.v[0]= _f26;	dl1.v[2]=mf.getValue(POS, pposs[i-2]); dl1.v[3]=mf.getValue(POS, pposs[i-3]); 
//		fv.add(mf.l2i(mf.calc4(dl1)));
		
		b.a[0]=s_type; b.v[0] = _f31;
		b.a[1]=s_oper; b.v[1]=oper;
		b.a[2]=s_word; b.v[2] = mf.getValue(WORD,forms[i-3]);
		b.a[3]=s_word; b.v[3] = mf.getValue(WORD,forms[i-2]);
		add(mf.l2i(mf.calc4(b)), fv);
		
		
		
	}

//	final is2.MF.Data d = new is2.MF.Data();
	final MF.Data2 b = new MF.Data2();



	public SentenceData09 m_instance;

	public SentenceData09 getInstance() {

		return m_instance;
	}



	public void fillFeatureVectorsOne(String[] forms,	Parameters params, int w1, 
			FV[][] fvs, double[][] probs, Object[][] o ) {

		 	for(int t = 0; t < types.length; t++) {

				fvs[w1][t] = new FV();  
				addCoreFeatures(forms,w1,null,this.types[t], fvs[w1][t]);
				probs[w1][t] = params.getScore(fvs[w1][t]);

			}
			
			double best = -1;
			for(int t = 0; t < types.length; t++) {

				if (probs[w1][t] >best) {
					o[w1][0] = fvs[w1][t] ;
					o[w1][1] = types[t];
					best =probs[w1][t];
				}

			}
		
	}



	protected void writeInstance (SentenceData09 instance,  DataOutputStream dos) throws IOException {

		final int instanceLength = instance.length();

		for(int w1 = 0; w1 < instanceLength; w1++) {
			for(int t = 0; t < types.length; t++) {

				//System.out.println(" "+types[t]);
				//			prodFV.clear();
				FV prodFV = new FV();
				addCoreFeatures(instance.forms,w1,null,types[t],prodFV);
				prodFV.writeKeys(dos);

			}
		}

		for(int k=0;k<instanceLength;k++) {
			instance.m_fvs[k].writeKeys(dos);
		}
	
		instance.write(dos);

	
	}


	public Object[][]  readInstance(DataInputStream dis, int length,
			ParametersDouble params, Options options, Object decoder) throws IOException, ClassNotFoundException {



		FV[][] fvs = new FV[length][types.length];
		double[][] probs = new double[length][types.length];


		for(int w1 = 0; w1 < length; w1++) {
			for(int t = 0; t < types.length; t++) {

				FV prodFV = new FV(dis);
				fvs[w1][t] = prodFV;  
				probs[w1][t] = prodFV.getScore(params.parameters,false);


			}
		}

		FV nfv[] = new FV[length];

		for(int k=0;k<length;k++) {
			nfv[k] = new FV(dis);
		}


			m_instance = new SentenceData09();
		m_instance.read(dis);
		m_instance.m_fvs=nfv;

		Object o[][] = new Object[length][2];
		for(int w1 = 0; w1 < length; w1++) {
			double best = -100.0;
			for(int t = 0; t < types.length; t++) {

			//		
				if (probs[w1][t] >=best) {
					o[w1][0] = fvs[w1][t];
					o[w1][1] = types[t];
					best =probs[w1][t];
				}		
			}

		}

		return o;


	}




}
