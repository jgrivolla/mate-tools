package is2.lemmatizer;



import is2.data.IEncoder;
import is2.data.Long2IntInterface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;



final public class MF implements Long2IntInterface, IEncoder {


 
	/** The features and its values */
	private final HashMap<String,HashMap<String,Integer>> m_featureSets = new HashMap<String,HashMap<String,Integer>>();

	/** The feature class and the number of values */
	private final HashMap<String,Integer> m_featureCounters = new HashMap<String,Integer>();

	/** The number of bits needed to encode a feature */
	final HashMap<String,Integer> m_featureBits = new HashMap<String,Integer>();
	
	/** Maps sparse long to integer values */
	final HashMap<Number,Integer> m_long2int = new HashMap<Number,Integer>();
//	public final TLongIntHashMap m_long2int = new TLongIntHashMap();
	//public final LongIntHash m_long2int = new LongIntHash();
//	private final BloomFilter m_bloom = new BloomFilter(0, 3);//155359939
	                                                  //2147483647
	                                                  //2147483647
	                                                  //6584983
	                                                  //105359939
	
	/** Integer counter for long2int */
	private int count=0;
	
	/** The number of bit which have to be shift next */
	private static short shift =0;
	
	/** Stop growing */
	public boolean stop=false;
	
	final public static String NONE="<None>";
	
	public static class Data {
		public final String[] a = new String[8];
		public final String[] v = new String[8];
		final short[] s = new short[9];
		public void clear(int i) {
			v[i]=null;
		}
	}
	
	final public static class Data2 {
		public static final int length=10;
		
		final public short[] a = new short[length];
		final public int[] v = new int[length];
	 	private final short[] s = new short[length+1];
		
		final public MF mf;

		public Data2() {mf = null;}

		public Data2(MF m) {mf =m;}

		final public void clear(int i) {stop =i;}
		public int stop =0;
		

		final public long calcs(int b, long v, long l) {
			if (l<0) return l;
			l |= v<<shift;
			shift +=b;
			return l;
		}

		
			
	}
	
	final public long calcs(Data2 w, int b, long v, long l) {
		if (l<0) return l;
		l |= v<<shift;
		shift +=b;
		return l;
	}


	
	final public static class Data3 {
		
		public short a0,a1,a2,a3,a4,a5,a6,a7,a8,a9;
		public int v0,v1,v2,v3,v4,v5,v6,v7,v8,v9; 
	}

	final public static class Data4 {
		public int shift;
		public short a0,a1,a2,a3,a4,a5,a6,a7,a8,a9;
		public int v0,v1,v2,v3,v4,v5,v6,v7,v8,v9; 
		final public long calcs(int b, long v, long l) {
			if (l<0) return l;
			l |= v<<shift;
			shift +=b;
			return l;
		}

	}
	
	public MF () {}
	
	
	public  int size() {return count;}
	
	/**
	 * The number of bits the value is shifted is set to 0.
	 */
	public void restShift() {
		shift=0;	
	}
	
	/**
	 * The number of bits the value is shifted.
	 */
	final public short getShift() {
		return shift;
	}
	
	final public void stop() {
		stop=true;
	//	m_long2int.compact();
	}
	
	final public void start() {
		stop=false;		
	
	}
	
	
	/**
	 * Register an attribute class, if it not exists and add a possible value
	 * @param type
	 * @param type2
	 */
	final public int register(String a, String v) {

		HashMap<String,Integer> fs = getFeatureSet().get(a);
		if (fs==null) {
			fs = new HashMap<String,Integer>();
			getFeatureSet().put(a, fs);
			fs.put(NONE, 0);
			getFeatureCounter().put(a, 1);
		}
		Integer c = getFeatureCounter().get(a);
		
		Integer i = fs.get(v);
		if (i==null) {
			fs.put(v, c);
			c++;
			getFeatureCounter().put(a,c);
			return c-1;
		} else return i;
	}
	
	/**
	 * Calculates the number of bits needed to encode a feature
	 */
	public void calculateBits() {
		
		int total=0;
		for(Entry<String,Integer> e : getFeatureCounter().entrySet() ){
			int bits =(int)Math.ceil((Math.log(e.getValue()+1)/Math.log(2)));
			m_featureBits.put(e.getKey(), bits);
			total+=bits;
		//	System.out.println(" "+e.getKey()+" bits "+bits+" # "+(e.getValue()+1));
		}
		
//		System.out.println("total number of needed bits "+total);
	}
	
	
	
	@Override
	public  String toString() {
		
		StringBuffer content = new StringBuffer();
		for(Entry<String,Integer> e : getFeatureCounter().entrySet() ){
			 content.append(e.getKey()+" "+e.getValue());
			 content.append(':');
			 HashMap<String,Integer> vs = getFeatureSet().get(e.getKey());
			 content.append(this.getFeatureBits(e.getKey()));
			 
			 /*if (vs.size()<120)
			 for(Entry<String,Integer> e2 : vs.entrySet()) {
				 content.append(e2.getKey()+" ("+e2.getValue()+") ");
			 }*/
			 content.append('\n');
				
		}
		return content.toString();
	}
	
	
	
	final public long calc(String a, String v, long l) {
		
		
	//    if (getFeatureSet().get(a)==null) System.out.println("null "+getFeatureSet().get(a));
		Integer vi = getFeatureSet().get(a).get(v);
		
		if (vi==null) return -1;
		int b = m_featureBits.get(a);
		long vl = vi; 
		l = l|vl<<shift;
		shift +=b;
		return l;
	}
	
	final public long calc(String a, int v, long l) {
		if (v<0) return -1;
		int b = m_featureBits.get(a);
		long vl = v;
		l = l|vl<<shift;
		shift +=b;
		return l;
	}
	
	final public long calc(int b, long v, long l) {
		if (v<0||l<0) return -1;
		l |= v<<shift;
		shift +=b;
		if (shift>64) {
			new Exception().printStackTrace();
		}
		//updateLargest(shift);
		return l;
	}

	final public long calcs(int b, long v, long l) {
		//	if (v<0) return -1;
		if (l<0) return l;
		l |= v<<shift;
		shift +=b;
	//	updateLargest(shift);
		return l;
	}

	final public long calcs(Data4 d,int b, long v, long l) {
		if (l<0) return l;
		l |= v<<d.shift;
		d.shift +=b;
		return l;
	}
	
	final public short getFeatureBits(String a) {
		return (short)m_featureBits.get(a).intValue();
	}

	
	final public int getValue(String a, String v) {
		
		if (m_featureSets.get(a)==null) return -1;
		Integer vi = m_featureSets.get(a).get(v);
		if (vi==null) return -1; //stop && 
		return vi.intValue();
	}
	
	 public int hasValue(String a, String v) {
		
		Integer vi = m_featureSets.get(a).get(v);
		if (vi==null) return -1;
		return vi.intValue();
	}
	

	/** 
	 * Calculates an encoding for a feature value set
	 * @param d a data set of attributes and features
	 * @param s start of the calculation in the data set
	 * @param l start values
	 * @return the encoding
	 */ 
	final public long calc(Data d, int s, long l) {
		
		shift=d.s[s];
		
		for(int i=s; i<d.a.length;i++) {
			if (d.v[i]==null)break;
			l=calc(d.a[i],d.v[i], l);
			d.s[i+1]=shift;
			if (l==-1) return -1;
		
		}
		//if (shift>64) System.out.println("Shift error more than 64 bit used");
	
		return l;
	}
	
	
	/*
	final public long calc(Data2 d, int s, long l) {
		
		shift=d.s[s];
		
		for(int i=s; i<d.a.length;i++) {
			if (d.v[i]<0)break;
			l=calc(d.a[i],d.v[i], l);
			d.s[i+1]=shift;
			if (l==-1) return -1;
		
		}
		//if (shift>64) System.out.println("Shift error more than 64 bit used");
	
		return l;
	}
	*/
	
	final public long calc(Data2 d, int s, long l) {
		
		shift=d.s[s];
		
		for(short i=(short)s; i<d.stop;i++) { //Data2.length
			if (d.v[i]<0) return -1;
			
			l |= (long)d.v[i]<<shift;
			shift +=d.a[i];
			d.s[i+1]=shift;
		}
		
	
		return l;
	}
	
	
	final public long calc(Data2 d) {
		
		shift=0; //0
		long l=0;
		for(short i=0; i<d.stop;i++) { 
			if (d.v[i]<0) return -1;
			l |= (long)d.v[i]<<shift;
			shift +=d.a[i];
			d.s[i+1]=shift;
		}
	//	if (shift>64) DB.println("shift error value too large "+shift);
		//if (shift>64)	new Exception().printStackTrace();
		return l;
	}
	

	final public long calc2(Data2 d) {
		
		if (d.v[0]<0||d.v[1]<0) return -1;
		
		long l = d.v[0];//<<shift;
		shift =d.a[0];

		l |= (long)d.v[1]<<shift;
		shift +=d.a[1];

	//	l |= (long)d.v[2]<<shift;
	//	shift +=d.a[2];
		return l;
	}

	
	
	final public long calc3(Data2 d) {
		
		if (d.v[0]<0||d.v[1]<0||d.v[2]<0) return -1;
		
		long l = d.v[0];//<<shift;
		shift =d.a[0];

		l |= (long)d.v[1]<<shift;
		shift +=d.a[1];

		l |= (long)d.v[2]<<shift;
		shift +=d.a[2];
		return l;
	}

	final public long calc3(Data3 d) {
		
		if (d.v0<0||d.v1<0||d.v2<0) return -1;
		
		long l = d.v0;
		shift =d.a0;
		l |= (long)d.v1<<shift;
		shift +=d.a1;
		l |= (long)d.v2<<shift;
		shift +=d.a2;
		return l;
	}

	final public long calc3(Data4 d) {
		
		if (d.v0<0||d.v1<0||d.v2<0) return -1;
		//	if (d.v1<0||d.v2<0) return -1;
		
		long l = d.v0;
		short shift =d.a0;
		l |= (long)d.v1<<shift;
		shift +=d.a1;
		l |= (long)d.v2<<shift;
		d.shift=shift + d.a2;
		
		//d.shift=;
		return l;
	}
	
	
	final public long calc4(Data2 d) {
	//	shift=0;
		if (d.v[0]<0||d.v[1]<0||d.v[2]<0||d.v[3]<0) return -1;
		
		long l = d.v[0];
		shift =d.a[0];
		l |= (long)d.v[1]<<shift;
		shift +=d.a[1];
		l |= (long)d.v[2]<<shift;
		shift +=d.a[2];
		l |= (long)d.v[3]<<shift;
		shift +=d.a[3];
				
		return l;
	}
	
	final public long calc4(Data3 d) {
		//	shift=0;
			if (d.v2<0||d.v3<0) return -1;
			
			long l = d.v0;
			shift =d.a0;
			l |= (long)d.v1<<shift;
			shift +=d.a1;
			l |= (long)d.v2<<shift;
			shift +=d.a2;
			l |= (long)d.v3<<shift;
			shift +=d.a3;
					
			return l;
		}
		
	final public long calc4(Data4 d) {
		//	shift=0;
			if (d.v0<0||d.v1<0||d.v2<0||d.v3<0) return -1;
			
			long l = d.v0;
			int shift =d.a0;
			l |= (long)d.v1<<shift;
			shift +=d.a1;
			l |= (long)d.v2<<shift;
			shift +=d.a2;
			l |= (long)d.v3<<shift;
			d.shift= shift +d.a3;
					
			return l;
		}
		
	
	
	final public long calc5(Data2 d) {
		
		if (d.v[0]<0||d.v[1]<0||d.v[2]<0||d.v[3]<0||d.v[4]<0) return -1;
			
		long l = d.v[0];
		shift =d.a[0];
		l |= (long)d.v[1]<<shift;
		shift +=d.a[1];
		l |= (long)d.v[2]<<shift;
		shift +=d.a[2];
		l |= (long)d.v[3]<<shift;
		shift +=d.a[3];	
		l |= (long)d.v[4]<<shift;
		shift +=d.a[4];
		
		return l;
	}

	final public long calc5(Data3 d) {
		
		if (d.v0<0||d.v1<0||d.v2<0||d.v3<0||d.v4<0) return -1;
			
		long l = d.v0;
	    shift =d.a0;
		l |= (long)d.v1<<shift;
		shift +=d.a1;
		l |= (long)d.v2<<shift;
		shift +=d.a2;
		l |= (long)d.v3<<shift;
		shift +=d.a3;	
		l |= (long)d.v4<<shift;
		shift +=d.a4;
		
		return l;
	}
	final public long calc5(Data4 d) {
		
		if (d.v0<0||d.v2<0||d.v3<0||d.v4<0) return -1;
			
		long l = d.v0;
		int shift =d.a0;
		l |= (long)d.v1<<shift;
		shift +=d.a1;
		l |= (long)d.v2<<shift;
		shift +=d.a2;
		l |= (long)d.v3<<shift;
		shift +=d.a3;	
		l |= (long)d.v4<<shift;
		d.shift =shift+d.a4;
		
		return l;
	}

	
	
	final public long calc6(Data2 d) {
		
		if (d.v[0]<0||d.v[1]<0||d.v[2]<0||d.v[3]<0||d.v[4]<0||d.v[5]<0) return -1;
			
		long l = d.v[0];
		shift =d.a[0];
		l |= (long)d.v[1]<<shift;
		shift +=d.a[1];
		l |= (long)d.v[2]<<shift;
		shift +=d.a[2];
		l |= (long)d.v[3]<<shift;
		shift +=d.a[3];	
		l |= (long)d.v[4]<<shift;
		shift +=d.a[4];
		l |= (long)d.v[5]<<shift;
		shift +=d.a[5];
		
		return l;
	}

	final public long calc6(Data3 d) {
		
		if (d.v0<0||d.v1<0||d.v2<0||d.v3<0||d.v4<0||d.v5<0) return -1;
			
		long l = d.v0;
		shift =d.a0;
		l |= (long)d.v1<<shift;
		shift +=d.a1;
		l |= (long)d.v2<<shift;
		shift +=d.a2;
		l |= (long)d.v3<<shift;
		shift +=d.a3;	
		l |= (long)d.v4<<shift;
		shift +=d.a4;
		l |= (long)d.v5<<shift;
		shift +=d.a5;
		
		return l;
	}

    final public long calc6(Data4 d) {
		
		if (d.v0<0||d.v1<0||d.v2<0||d.v3<0||d.v4<0||d.v5<0) return -1;
			
		long l = d.v0;
		int shift =d.a0;
		l |= (long)d.v1<<shift;
		shift +=d.a1;
		l |= (long)d.v2<<shift;
		shift +=d.a2;
		l |= (long)d.v3<<shift;
		shift +=d.a3;	
		l |= (long)d.v4<<shift;
		shift +=d.a4;
		l |= (long)d.v5<<shift;
		d.shift =shift+d.a5;
		
		return l;
	}

   final public long calc7(Data4 d) {
		
		if (d.v0<0||d.v2<0||d.v3<0||d.v4<0||d.v5<0) return -1;
			
		long l = d.v0;
		int shift =d.a0;
		l |= (long)d.v1<<shift;
		shift +=d.a1;
		l |= (long)d.v2<<shift;
		shift +=d.a2;
		l |= (long)d.v3<<shift;
		shift +=d.a3;	
		l |= (long)d.v4<<shift;
		shift +=d.a4;
		l |= (long)d.v5<<shift;
		shift +=d.a5;
		l |= (long)d.v6<<shift;
		d.shift =shift+d.a6;
		
		return l;
	}

	
	final public long calc7(Data2 d) {
		
		if (d.v[0]<0||d.v[1]<0||d.v[2]<0||d.v[3]<0||d.v[4]<0||d.v[5]<0||d.v[6]<0) return -1;
			
		long l = d.v[0];
		shift =d.a[0];
		l |= (long)d.v[1]<<shift;
		shift +=d.a[1];
		l |= (long)d.v[2]<<shift;
		shift +=d.a[2];
		l |= (long)d.v[3]<<shift;
		shift +=d.a[3];	
		l |= (long)d.v[4]<<shift;
		shift +=d.a[4];
		l |= (long)d.v[5]<<shift;
		shift +=d.a[5];
		l |= (long)d.v[6]<<shift;
		shift +=d.a[6];
		
		return l;
	}

	/** 
	 * Maps a long to a integer value. This is very useful to save memory for sparse data long values 
	 * @param l
	 * @return the integer
	 */
	static public  long misses = 0;
	static public  long good = 0;
	static int ind=0;
	final public int l2i(long l) {
	
	//	if (true) return 0;
		Integer i =m_long2int.get(l);
		if (i==null) i=0;
		
		if (i!=0) return i; 
		if (stop||l==-1||l==0) return -1; 
				
	//	m_bloom.put(l);
		m_long2int.put(l, count);
		count++;
		
		return count-1;
		
	}
		
	final public int checkl2i(long l) {
		
		int i =m_long2int.get(l);
		if (i!=0) return i;
		if (stop) return -1; 
		
		return -1;
	}


	
	/**
	 * The number of bits the value is shifted is set.
	 * 
	 */
	final public void setShift(short s) {
		shift=s;
	}
/*
	public void wirte(DataOutputStream dos) throws IOException {

		dos.writeInt(getFeatureSet().size());
		for(Entry<String, HashMap<String,Integer>> e : getFeatureSet().entrySet()) {
			dos.writeUTF(e.getKey());
			dos.writeInt(e.getValue().size());
		
			for(Entry<String,Integer> e2 : e.getValue().entrySet()) {
				dos.writeUTF(e2.getKey()); 
				dos.writeInt(e2.getValue()); 
			 }			
				
		}
		
		final int size = size();
		long[] ls = new long[size];
		
		TLongIntIterator it = m_long2int.iterator();
		while(it.hasNext()) {
			ls[it.value()]=it.key();
			it.advance();
		}
		
		//		for(Entry<Number, Integer> e : m_long2int.entrySet()) {
///			ls[e.getValue()]=e.getKey().longValue();
//		}
		
		dos.writeInt(size);
		
		for(int i=0; i<size;i++) {
			dos.writeLong(ls[i]);
		}
		
		dos.flush();
		
	}
	*/
	/**
	 * Write the data
	 * @param dos
	 * @throws IOException
	 */
    public void writeData(DataOutputStream dos) throws IOException {
        dos.writeInt(getFeatureSet().size());
        for(Entry<String, HashMap<String,Integer>> e : getFeatureSet().entrySet()) {
            dos.writeUTF(e.getKey());
            dos.writeInt(e.getValue().size());
        
            for(Entry<String,Integer> e2 : e.getValue().entrySet()) {
            
                dos.writeUTF(e2.getKey()); 
                dos.writeInt(e2.getValue());
            
             }          
                
        }
    }
    
    
    /**
     * Write the mapping long to integer.
     * @param dos
     * @param p
     * @param k
     * @throws IOException
     */
	public int wirteMapping(DataOutputStream dos, double[] p, int k, double max) throws IOException {
		
		
		
		final int size = size();
		long[] ls = new long[size];

		
//		long[] ks = m_long2int.keys();
//		for(int i=0;i<ks.length;i++) {
//			ls[m_long2int.get(ks[i])] =ks[i];/
//		}
		 
	
		for(Entry<Number,Integer> e: m_long2int.entrySet()) {
			ls[e.getValue()] =e.getKey().longValue();
		}
	
		dos.writeInt(k);
	
		int count=0;
		boolean first=true;
		for(int i=0; i<size;i++) {
			//if (p[i]!=0.0|| i==0) dos.writeLong(ls[i]);
			if (p[i]>max || p[i]<-max || first) {
				dos.writeLong(ls[i]);
				count++;
			}
			first=false;
			
		}
		return count;
	
			
	}
	public int wirteMapping(DataOutputStream dos, float[] p, int k, double max) throws IOException {
					
		final int size = size();
		long[] ls = new long[size];

		
		
	
		for(Entry<Number,Integer> e: m_long2int.entrySet()) {
			ls[e.getValue()] =e.getKey().longValue();
		}

		dos.writeInt(k);
	
		int count=0;
		boolean first=true;
		for(int i=0; i<size;i++) {
			//if (p[i]!=0.0|| i==0) dos.writeLong(ls[i]);
			if (p[i]>max || p[i]<-max || first) {
				dos.writeLong(ls[i]);
				count++;
			}
			first=false;
			
		}
		return count;
	
			
	}
	
public int wirteMappingEx(DataOutputStream dos, float[] p, int k, double max) throws IOException {
		
		
		
		final int size = size();
		long[] ls = new long[size];

		
		
	
		for(Entry<Number,Integer> e: m_long2int.entrySet()) {
			ls[e.getValue()] =e.getKey().longValue();
		}
	
		dos.writeInt(k);
	
		int count=0;
		boolean first=true;
		for(int i=0; i<size;i++) {
			//if (p[i]!=0.0|| i==0) dos.writeLong(ls[i]);
	//		if (p[i]>max || p[i]<-max || first) {
				dos.writeLong(ls[i]);
				count++;
		//	}
		//	first=false;
			
		}
		
		return count;
	
			
	}
	
	
	
	public void read(DataInputStream din) throws IOException {
		
		int size = din.readInt();
//		System.out.println("read features "+size);
		for(int i=0; i<size;i++) {
			String k = din.readUTF();
			int size2 = din.readInt();
			
			HashMap<String,Integer> h = new HashMap<String,Integer>();
			getFeatureSet().put(k,h);
			for(int j = 0;j<size2;j++) {
				h.put(din.readUTF(), din.readInt());
			}
			getFeatureCounter().put(k, size2);
		}

		size = din.readInt();
		for(int i =0;i<size;i++) {
			
			long l=din.readLong();
			m_long2int.put(l, i);
		//	m_bloom.put(l);

			
		}
		count =size;
		stop();
		calculateBits();
	}
public void read2(DataInputStream din) throws IOException {
		
		int size = din.readInt();
		for(int i=0; i<size;i++) {
			String k = din.readUTF();
			int size2 = din.readInt();
			
			HashMap<String,Integer> h = new HashMap<String,Integer>();
			getFeatureSet().put(k,h);
			for(int j = 0;j<size2;j++) {
				h.put(din.readUTF(), din.readInt());
			}
			getFeatureCounter().put(k, size2);
		}

//		size = din.readInt();
//		for(int i =0;i<size;i++) {
			
//			m_long2int.put(din.readLong(), i);
//		}
//		DB.println("read "+size);
		count =size;
		stop();
		calculateBits();
	}


public void readEx(DataInputStream din) throws IOException {
	
	int size = din.readInt();
	for(int i =0;i<size;i++) {
		
		long l=din.readLong();
		m_long2int.put(l, i);
	//	m_bloom.put(l);

		
	}
	count =size;
	stop();
	calculateBits();
}


	/** 
	 * Clear the data
	 */
    public void clearData() {
      getFeatureSet().clear();
      m_featureBits.clear();
      getFeatureSet().clear();
    }

	public HashMap<String,Integer> getFeatureCounter() {
		return m_featureCounters;
	}

	public HashMap<String,HashMap<String,Integer>> getFeatureSet() {
		return m_featureSets;
	}
	
	public String[] reverse(HashMap<String,Integer> v){
		String[] set = new String[v.size()];
		for(Entry<String,Integer> e : v.entrySet()) {
			set[e.getValue()]=e.getKey();
		}
		return set;
	}
	
}
