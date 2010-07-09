package is2.tag3;


import is2.data.F2SF;
import is2.data.FV;
import is2.data.IFV;
import is2.data.Instances;
import is2.data.Long2IntInterface;
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
import java.util.ArrayList;
import java.util.Map.Entry;


final public class Pipe extends PipeGen implements Runnable {

	private static final String _F0 = "F0";
	private static final String _F1 = "F1",_F2 = "F2",_F3 = "F3",_F4 = "F4",_F5 = "F5",_F6= "F6",_F7= "F7",_F8= "F8",_F9="F9",_F10 = "F10";
	private static final String _F11="F11",_F12="F12",_F13= "F13",_F14="F14",_F15="F15",_F16="F16",_F17="F17",_F18="F18",_F19="F19",_F20="F20";
	private static final String _F21="F21",_F22="F22",_F23= "F23",_F24="F24",_F25="F25",_F26="F26",_F27="F27",_F28="F28",_F29="F29",_F30="F30";
	private static final String _F31="F31",_F32="F32",_F33= "F33",_F34="F34",_F35="F35",_F36="F36",_F37="F37",_F38="F38",_F39="F39",_F40="F40";
	private static final String _F41="F41",_F42="F42",_F43= "F43",_F44="F44",_F45="F45",_F46="F46",_F47="F47",_F48="F48",_F49="F49",_F50="F50";

	private static int _f0,_f1,_f2,_f3,_f4,_f5,_f6,_f7,_f8,_f9,_f10,_f11,_f12,_f13,_f14,_f15,_f16,_f17,_f18,_f19,_f20;
	private static int _f21,_f22,_f23,_f24,_f25,_f26,_f27,_f28,_f29,_f30,_f31,_f32,_f33,_f34,_f35,_f36,_f37,_f38,_f39,_f40,_f41;
	private static int _f42,_f43,_f44,_f45,_f46,_f47,_f48,_f49,_f50, _CEND;


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
	private static final String STWRD = "STWRD";
	private static final String STPOS = "STPOS";
	private static final String _0 = "0";
//	private static final String L0 = "L0";
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

	private CONLLReader09 depReader;
	public CONLLWriter09 depWriter;

	static public String[] types;


	public MFO mf =new MFO();
	static public Long2IntInterface li = new Long2Int();



//	final is2.MFO.Data2 b = new is2.MFO.Data2();

	final MFO.Data4 d1 = new MFO.Data4();
	final MFO.Data4 d2 = new MFO.Data4();
	final MFO.Data4 d3 = new MFO.Data4();
	final MFO.Data4 dw = new MFO.Data4();
	final MFO.Data4 dwp = new MFO.Data4();



	private Options options;
	static private int _mid, _strp,_endp;

	public Pipe (Options options, Long2Int long2Int) throws IOException {
		this.options = options;

		depReader = new CONLLReader09();
		li =long2Int;
	}



	/**
	 * @param f
	 */
	public Pipe(F2SF f) {
		m_f =f;
		
		d1.a0 = s_type; d1.a1 = s_pos; d1.a2= s_word;
		d2.a0 = s_type; d2.a1 = s_pos; d2.a2= s_pos; d2.a3= s_pos; d2.a4= s_pos; d2.a5= s_pos; d2.a6= s_pos;
		d3.a0 = s_type; d3.a1 = s_pos; d3.a2=  s_char; d3.a3= s_char; d3.a4= s_char; d3.a5=  s_char; d3.a6=  s_char; d3.a7= s_char;
		dw.a0 = s_type; dw.a1 = s_pos;dw.a2=  s_word; dw.a3= s_word; dw.a4= s_word; dw.a5=  s_word; dw.a6=  s_word; dw.a7= s_word;
		dwp.a0 = s_type; dwp.a1 = s_pos;dwp.a2= s_word ; dwp.a3= s_pos; dwp.a4= s_word; 
	}



	public int[] createInstances(String file,File featFileName, Instances is) throws Exception {

		depReader.startReading(file);
		MFO.register(REL,"<root-type>");
		MFO.register(POS,"<root-POS>");


		System.out.println("Registering feature parts ");

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
			for(int i1 = 0; i1 < w.length; i1++) registerChars(CHAR,  w[i1]);
			for(int i1 = 0; i1 < w.length; i1++) mf.register(WORD,  w[i1].toLowerCase());
			for(int i1 = 0; i1 < w.length; i1++) registerChars(CHAR,  w[i1].toLowerCase());

			w = instance1.lemmas;
			for(int i1 = 0; i1 < w.length; i1++) mf.register(WORD,  w[i1]);			
			for(int i1 = 0; i1 < w.length; i1++) registerChars(CHAR,  w[i1]);


			w = instance1.ppos;
			for(int i1 = 0; i1 < w.length; i1++) mf.register(POS,  w[i1]);

			w = instance1.gpos;
			for(int i1 = 0; i1 < w.length; i1++) mf.register(POS,  w[i1]);

		}

		initFeatures();

		mf.calculateBits();
		initValues();

		depReader.startReading(file);

		int num1 = 0;
		long start1 = System.currentTimeMillis();

		System.out.print("Creating Features: ");
		is.init(ic, mf) ;
		int del=0;

		while(true) {
			if (num1 % 100 ==0) {del = outValue(num1, del);}
			SentenceData09 instance1 = depReader.getNext(is);
			if (instance1== null) break;

			//			createFeatureVector(instance1,is,num1);

			if (num1>options.count) break;

			num1++;
		}
		long end1 = System.currentTimeMillis();
		System.gc();
		long mem2 = Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory();
		System.out.print("  time "+(end1-start1)+" mem "+(mem2/1024)+" kb");

		types = new String[mf.getFeatureCounter().get(POS)];

		for(Entry<String,Integer> e : mf.getFeatureSet().get(POS).entrySet()) {
			types[e.getValue()] = e.getKey();
		}



		long start11;
		long end11;

		depReader.startReading(file);

		int[] lengths = new int[num1+1];

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(featFileName),32768);

		DataOutputStream dos = new DataOutputStream(bos);
		num1=0;

		int num11=0;

		start11 = System.currentTimeMillis();
		System.out.println("Creating Feature Vectors: ");
		long last = System.currentTimeMillis();
		while(true) {

			SentenceData09 instance = depReader.getNext();
			if (instance==null) break;
			if (num11 % 100 ==0) del = outValue(num11, del,last);


			createFeatureVector(instance,is,num11);


			lengths[num11]=instance.length();

			writeInstance(instance,dos);


			if (num11>options.count) break;

			num11++;
		}
		end11 = System.currentTimeMillis();
		System.out.println("\nUsed time "+(end11-start11));


		dos.close();

		return lengths;//.toNativeArray();

	}

	private void registerChars(String type, String word) {
		for(int i=0;i<word.length();i++) 	MFO.register(type, Character.toString(word.charAt(i)));      
	}



	public void initValues() {
		s_pos = MFO.getFeatureBits(POS);
		s_word =  MFO.getFeatureBits(WORD);
		s_type =  MFO.getFeatureBits(TYPE);
		s_dir =  MFO.getFeatureBits(DIR);
		s_dist =  MFO.getFeatureBits(DIST);
		s_char =  MFO.getFeatureBits(CHAR);

		d1.a0 = s_type; d1.a1 = s_pos; d1.a2= s_word;
		d2.a0 = s_type; d2.a1 = s_pos; d2.a2= s_pos; d2.a3= s_pos; d2.a4= s_pos; d2.a5= s_pos; d2.a6= s_pos;
		d3.a0 = s_type; d3.a1 = s_pos; d3.a2=  s_char; d3.a3= s_char; d3.a4= s_char; d3.a5=  s_char; d3.a6=  s_char; d3.a7= s_char;
		dw.a0 = s_type; dw.a1 = s_pos;dw.a2=  s_word; dw.a3= s_word; dw.a4= s_word; dw.a5=  s_word; dw.a6=  s_word; dw.a7= s_word;
		dwp.a0 = s_type; dwp.a1 = s_pos;dwp.a2= s_word ; dwp.a3= s_pos; dwp.a4= s_word; 

	}

	public static short s_pos;
	public static short s_word;
	public static short s_type;
	public static short s_dir;
	public static short s_dist;
	public static short s_char;



	/**
	 * Initialize the features types.
	 */
	public void initFeatures() {



		_f0 = MFO.register(TYPE, _F0);
		_f1 = MFO.register(TYPE, _F1);
		_f2 = MFO.register(TYPE, _F2);
		_f3 = MFO.register(TYPE, _F3);
		_f4 = MFO.register(TYPE, _F4);
		_f5 = mf.register(TYPE, _F5);
		_f6 = MFO.register(TYPE, _F6);
		_f7 = MFO.register(TYPE, _F7);
		_f8 = MFO.register(TYPE, _F8);
		_f9 = MFO.register(TYPE, _F9);
		_f10 = MFO.register(TYPE, _F10);
		_f11 = MFO.register(TYPE, _F11);
		_f12 = MFO.register(TYPE, _F12);
		_f13 = MFO.register(TYPE, _F13);
		_f14 = MFO.register(TYPE, _F14);
		_f15 = MFO.register(TYPE, _F15);
		_f16 = MFO.register(TYPE, _F16);
		_f17 = MFO.register(TYPE, _F17);
		_f18 = MFO.register(TYPE, _F18);
		_f19 = MFO.register(TYPE, _F19);
		_f20 = MFO.register(TYPE, _F20);
		_f21 = MFO.register(TYPE, _F21);
		_f22 = MFO.register(TYPE, _F22);
		_f23 = MFO.register(TYPE, _F23);
		_f24 = MFO.register(TYPE, _F24);
		_f25 = MFO.register(TYPE, _F25);
		_f26 = MFO.register(TYPE, _F26);
		_f27 = MFO.register(TYPE, _F27);
		_f28 = MFO.register(TYPE, _F28);
		_f29 = MFO.register(TYPE, _F29);
		_f30 = MFO.register(TYPE, _F30);

		_f31 = MFO.register(TYPE, _F31);
		_f32 = MFO.register(TYPE, _F32);
		_f33 = MFO.register(TYPE, _F33);
		_f34 = MFO.register(TYPE, _F34);

		_f35 = MFO.register(TYPE, _F35);
		_f36 = MFO.register(TYPE, _F36);
		_f37 = MFO.register(TYPE, _F37);
		_f38 = MFO.register(TYPE, _F38);

		_f39 = MFO.register(TYPE, _F39);

		_f40 = MFO.register(TYPE, _F40);
		_f41 = MFO.register(TYPE, _F41);

		_f42 = MFO.register(TYPE, _F42);
		_f43 = MFO.register(TYPE, _F43);
		_f44 = MFO.register(TYPE, _F44);
		_f45 = MFO.register(TYPE, _F45);
		_f46 = MFO.register(TYPE, _F46);
		_f47 = MFO.register(TYPE, _F47);
		_f48 = MFO.register(TYPE, _F48);
		_f49 = MFO.register(TYPE, _F49);
		_f50 = MFO.register(TYPE, _F50);


		_mid = MFO.register(POS, MID);
		_strp = MFO.register(POS, STR);
		_endp= MFO.register(POS, END);
		MFO.register(TYPE, CHAR);

		MFO.register(WORD, STR);
		MFO.register(WORD, END);


		_CEND = MFO.register(CHAR, END);




		MFO.register(DIR, LA);
		MFO.register(DIR, RA);



//		MFO.register(DIST, L0);
		MFO.register(DIST, L5);
		MFO.register(DIST, L7);
		MFO.register(DIST, L10);
		MFO.register(DIST, L1);
		MFO.register(DIST, L2);
		MFO.register(DIST, L3);
		MFO.register(DIST, L4);
		MFO.register(DIST, L5);

		MFO.register(DIST, R0);
		MFO.register(DIST, R5);
		MFO.register(DIST, R7);
		MFO.register(DIST, R10);
		MFO.register(DIST, R1);
		MFO.register(DIST, R2);
		MFO.register(DIST, R3);
		MFO.register(DIST, R4);
		MFO.register(DIST, R5);


		MFO.register(TYPE, C0);
		MFO.register(TYPE, C1);
		MFO.register(TYPE, C2);
		MFO.register(TYPE, C3);
		MFO.register(TYPE, C4);
		MFO.register(TYPE, C5);
		MFO.register(TYPE, "C6");

		MFO.register(TYPE, E0);
		MFO.register(TYPE, E1);
		MFO.register(TYPE, E2);
		MFO.register(TYPE, E3);
		MFO.register(TYPE, E4);
		MFO.register(TYPE, E5);
		MFO.register(TYPE, "E6");



		// optional features
		MFO.register(DIST, "RA");
		MFO.register(DIST, "LA");
		MFO.register(WORD,STWRD);
		MFO.register(POS,STPOS);
		MFO.register(DIST, _0);
		MFO.register(DIST, _1);
		MFO.register(DIST, _2);
		MFO.register(DIST, _3);
		MFO.register(DIST, _4);
		MFO.register(DIST, _5);
		MFO.register(DIST, _10);


	}


	public String getType (int typeIndex) {
		return types[typeIndex];
	}




	public void createFeatureVector(SentenceData09 instance, Instances is, int n) {

		final int instanceLength = instance.length();

		for(int i = 0; i < instanceLength; i++) {

			FV f = new FV();
			addCoreFeatures(instance,i,instance.gpos[i],is.gpos[n],is.forms[n],is.lemmas[n],f);
			instance.setFeatureVector(f,i);
		}

	}


	public void addCoreFeatures(SentenceData09 instance,int i, String pos, short pposs[], int[] forms, int[] lemmas, IFV fv) {

		int len = forms.length;
		String lower = instance.forms[i].toLowerCase();
		int form = forms[i];
		int p =mf.getValue(POS, pos);

		int c0= mf.getValue(CHAR, String.valueOf(instance.forms[i].charAt(0)));
		int c1= instance.forms[i].length()>1?mf.getValue(CHAR, String.valueOf(instance.forms[i].charAt(1))):_CEND;//mf.getValue(CHAR, END);
		int c2= instance.forms[i].length()>2?mf.getValue(CHAR, String.valueOf(instance.forms[i].charAt(2))):_CEND;
		int c3= instance.forms[i].length()>3?mf.getValue(CHAR, String.valueOf(instance.forms[i].charAt(3))):_CEND;
		int c4= instance.forms[i].length()>4?mf.getValue(CHAR, String.valueOf(instance.forms[i].charAt(4))):_CEND;
		int c5= instance.forms[i].length()>5?mf.getValue(CHAR, String.valueOf(instance.forms[i].charAt(5))):_CEND;

		int e0 = mf.getValue(CHAR,String.valueOf(instance.forms[i].charAt(instance.forms[i].length()-1)));
		int e1 = instance.forms[i].length()>1?mf.getValue(CHAR,String.valueOf(instance.forms[i].charAt(instance.forms[i].length()-2))):_CEND;//mf.getValue(CHAR, END);
		int e2 = instance.forms[i].length()>2?mf.getValue(CHAR,String.valueOf(instance.forms[i].charAt(instance.forms[i].length()-3))):_CEND;
		int e3 = instance.forms[i].length()>3?mf.getValue(CHAR,String.valueOf(instance.forms[i].charAt(instance.forms[i].length()-4))):_CEND;
		int e4 = instance.forms[i].length()>4?mf.getValue(CHAR,String.valueOf(instance.forms[i].charAt(instance.forms[i].length()-5))):_CEND;

		d1.v0 = _f0; d1.v1=p; d1.v2=form;
		fv.add(li.l2i(mf.calc3(d1)));

		d1.v0 = _f1; d1.v1 = p; d1.v2=mf.getValue(WORD, lower);
		fv.add(li.l2i(mf.calc3(d1)));



		d2.v0 =_f2; d2.v1=p; d2.v2= (float)instance.length()/(i+1)<0.333?_strp:(float)instance.length()/(i+1)<0.666?_mid:_endp;
		fv.add(li.l2i(mf.calc3(d2)));


		d3.v0 = _f4; d3.v1 =p; d3.v2=c0;
		fv.add(li.l2i(mf.calc3(d3)) );

		d3.v0 = _f5; d3.v1 =p; d3.v2=e0;
		fv.add(li.l2i(mf.calc3(d3)) );


		d3.v2=c0; d3.v3=c1; d3.v4=c2; d3.v5=c3; d3.v6=c4;
		d3.v0=_f6; fv.add(li.l2i(mf.calc4(d3)));
		d3.v0=_f7; fv.add(li.l2i(mf.calc5(d3)));
		d3.v0=_f8; fv.add(li.l2i(mf.calc6(d3)));
		d3.v0=_f9; fv.add(li.l2i(mf.calc7(d3)));

		d3.v2=e0; d3.v3=e1; d3.v4=e2; d3.v5=e3; d3.v6=e4;
		d3.v0 =_f10; fv.add(li.l2i(mf.calc4(d3))); 
		d3.v0 =_f11; fv.add(li.l2i(mf.calc5(d3))); 
		d3.v0 =_f12; fv.add(li.l2i(mf.calc6(d3)));
		d3.v0 =_f13; fv.add(li.l2i(mf.calc7(d3))); 


		// sign bigrams
//		d3.v2=c1; d3.v3=c2; 
//		d3.v0 =_f41; fv.add(li.l2i(mf.calc4(d3))); 

//		d3.v2=c2; d3.v3=c3; 
//		d3.v0 =_f42; fv.add(li.l2i(mf.calc4(d3))); 

//		d3.v2=c3; d3.v3=c4; 
//		d3.v0 =_f43; fv.add(li.l2i(mf.calc4(d3))); 

//		d3.v0 =_f44; fv.add(li.l2i(mf.calc4(d3))); 
//		d3.v2=c4; d3.v3=c5; 


		// sign three-grams
		d3.v2=c1; d3.v3=c2; d3.v4=c3; 
		d3.v0 =_f45; fv.add(li.l2i(mf.calc5(d3))); 

		d3.v2=c2; d3.v3=c3; d3.v4=c4;
		d3.v0 =_f46; fv.add(li.l2i(mf.calc5(d3))); 

		d3.v2=c3; d3.v3=c4; d3.v4=c5;
		d3.v0 =_f47; fv.add(li.l2i(mf.calc5(d3))); 

		// sign quad-grams
		d3.v2=c1; d3.v3=c2; d3.v4=c3; d3.v5=c4; 
		d3.v0 =_f48; fv.add(li.l2i(mf.calc6(d3))); 

		d3.v2=c2; d3.v3=c3; d3.v4=c4; d3.v5=c5;
		d3.v0 =_f49; fv.add(li.l2i(mf.calc4(d3))); 

		
		if (len>i+1) {

			d3.v0 =_f14; d3.v2 =mf.getValue(CHAR, String.valueOf(instance.forms[i+1].charAt(0)));
			fv.add(li.l2i(mf.calc3(d3)));

			d3.v0 =_f15; d3.v2 =mf.getValue(CHAR, String.valueOf(instance.forms[i+1].charAt(instance.forms[i+1].length()-1)));
			fv.add(li.l2i(mf.calc3(d3)));



			if (instance.forms[i+1].length()>1 ) {
				d3.v0=_f16; d3.v2 = mf.getValue(CHAR, String.valueOf(instance.forms[i+1].charAt(0)));
				d3.v3 =mf.getValue(CHAR, String.valueOf(instance.forms[i+1].charAt(1)));
				fv.add(li.l2i(mf.calc4(d3)));

				d3.v0=_f17;d3.v2= mf.getValue(CHAR, String.valueOf(instance.forms[i+1].charAt(instance.forms[i+1].length()-1)));
				d3.v3=mf.getValue(CHAR, String.valueOf(instance.forms[i+1].charAt(instance.forms[i+1].length()-2)));
				fv.add(li.l2i(mf.calc4(d3)));

			}

			dw.v0=_f18; dw.v1=p;dw.v2= forms[i+1];
			fv.add(li.l2i(mf.calc3(dw)));

			if (len>i+2) {
				dw.v0=_f32; dw.v2= forms[i+2]; dw.v3 = forms[i+1];
				fv.add(li.l2i(mf.calc4(dw)));

				fv.add(li.l2i(mf.calc3(dw)));

				d2.v0= _f40; d2.v1=p; d2.v2=pposs[i+1]; d2.v3=  pposs[i+2]; 
				fv.add(li.l2i(mf.calc4(d2)));


			}

			if (len>i+3) {

				dw.v0=_f33; dw.v2= forms[i+3]; dw.v3 = forms[i+2];
				fv.add(li.l2i(mf.calc4(dw)));

				fv.add(li.l2i(mf.calc3(dw)));


			}



		}

		// length
		d2.v0= _f19;	d2.v1=p; d2.v2=instance.forms[i].length();
		fv.add(li.l2i(mf.calc3(d2)));

		// contains upper case

		short upper =0;
		short number = 1;
		for(int k1=0;k1<instance.forms[i].length();k1++){
			char c =instance.forms[i].charAt(k1);
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

		d2.v0= _f20;	d2.v1=p; d2.v2=upper; //dl1.v[3]=i==0?1:2;
		fv.add(li.l2i(mf.calc3(d2)));

		// contains a number
		d2.v0= _f21;	d2.v1=p; d2.v2=number;
		fv.add(li.l2i(mf.calc3(d2)));


		// from 0.9729681 to 0.9731779		
		d1.v0=_f36; d1.v1=p; d1.v2=lemmas[i];
		fv.add(li.l2i(mf.calc3(d1)));


		if (i!=0 &&len>i+1) {

			dw.v0=_f38;dw.v1=p;dw.v2=lemmas[i-1];dw.v3=lemmas[i+1];
			fv.add(li.l2i(mf.calc4(dw)));                      

			d2.v0=_f39;d2.v1=p;d2.v2=pposs[i-1];d2.v3=pposs[i+1];
			fv.add(li.l2i(mf.calc4(d2)));                      


		}

		d2.v0= _f22; d2.v1=p;	d2.v2=i>=1? pposs[i-1]:_strp; 
		fv.add(li.l2i(mf.calc3(d2)));


		if (i<1) return ;

		dw.v0 = _f27; dw.v1=p; dw.v2 =  forms[i-1];
		fv.add(li.l2i(mf.calc3(dw)) );


		// 0.9731779 to 0.9732079
		dw.v0 = _f37; dw.v1=p; dw.v2 =  lemmas[i-1];
		fv.add(li.l2i(mf.calc3(dw)) );


		if (i<2) return ;

		d2.v0= _f23;	d2.v2=i<2?_strp:  pposs[i-2]; 
		fv.add(li.l2i(mf.calc3(d2)));


		d2.v0= _f24;	d2.v2= pposs[i-1]; d2.v3= pposs[i-2]; 
		fv.add(li.l2i(mf.calc4(d2)));

		dw.v0= _f28;	dw.v2= forms[i-2];  
		fv.add(li.l2i(mf.calc3(dw)));

		dwp.v0= _f34;dwp.v1= p; dwp.v2 = forms[i-1]; dwp.v3 =  pposs[i-2];
		fv.add(li.l2i(mf.calc4(dwp)));


		dwp.v0= _f35;dwp.v1= p; dwp.v2 = forms[i-2]; dwp.v3 =  pposs[i-1];
		fv.add(li.l2i(mf.calc4(dwp)));



		if (i<3) return ;

		d2.v0= _f25;	d2.v2=  pposs[i-3]; 
		fv.add(li.l2i(mf.calc3(d2)));

		d2.v0= _f26;	d2.v2= pposs[i-2]; d2.v3= pposs[i-3]; 
		fv.add(li.l2i(mf.calc4(d2)));

		dw.v0 = _f31; dw.v1=p; dw.v2 = forms[i-3]; dw.v3 = forms[i-2];
		fv.add(li.l2i(mf.calc4(dw)));
	}


	public SentenceData09 m_instance;

	public SentenceData09 getInstance() {

		return m_instance;
	}



	public int fillFeatureVectorsOne(SentenceData09 instance,	ParametersFloat params, 
			int w1, Object[][] o, Instances is, int n,  short[] pos) {
		double best = -1;
		int bestType=-1;

		F2SF f = new F2SF(params.parameters);
		for(int t = 0; t < types.length; t++) {

			f.clear();
			addCoreFeatures(instance,w1,types[t], pos, is.forms[n],is.lemmas[n],f);

			if (f.score >best) {
				bestType=t;
				best =f.score;
			}

		}	
		o[w1][1] = types[bestType];
		return bestType;

	}


	
	
	protected void writeInstance (SentenceData09 instance,  DataOutputStream dos) throws IOException {

		final int instanceLength = instance.length();

		for(int k=0;k<instanceLength;k++) {
			instance.m_fvs[k].writeKeys(dos);
		}

		instance.write(dos);


	}


	public void  readInstance(DataInputStream dis, int length,
			ParametersFloat params, Options options, Object decoder) throws IOException, ClassNotFoundException {

		FV nfv[] = new FV[length];

		for(int k=0;k<length;k++) {
			nfv[k] = new FV(dis);
		}



		m_instance = new SentenceData09();
		m_instance.read(dis);
		m_instance.m_fvs=nfv;



	}

	public static class T {
		final int w1;
		final int t;
		float score;
		
		public T (int w, int t){
			//System.out.println("w "+w+" t "+t);
			w1=w;
			this.t=t;
		}
		
	}
	
	static ArrayList<T> todo = new ArrayList<T>();
	static SentenceData09 instance;
	private F2SF m_f;
	static int[] forms, lemmas;
	static short[] posx;
	
	
	static boolean stop =false;
	
	int bestType;
	float best= Float.MIN_NORMAL;
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		while(true) {
			
			T t = getNext();
			if (t==null) {
				if (stop) break;
				Thread.yield();
				continue;
			}
		
			m_f.clear();
			addCoreFeatures(instance,t.w1,types[t.t], posx, forms,lemmas,m_f);
			t.score=m_f.score;
			done.add(t);

		}
		
		
	}
	
	
	public static ArrayList<T> done = new ArrayList<T>();
	
	private void add(T t){
		synchronized(done) {
			done.add(t);
		}
	}
	
	
	/**
	 * @return
	 */
	private T getNext() {
		synchronized(todo) {
			if (todo.size()==0) return null;
			return todo.remove(todo.size()-1);
		}
	}



}
