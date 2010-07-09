/**
 * 
 */
package is2.parser;

import is2.data.MFO;
import is2.data.PipeGen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Dr. Bernd Bohnet, 13.05.2009;
 * 
 * 
 */
public final class Edges {

	
	private static short[][][][] edges;

	private static HashMap<Short,Integer> labelCount = new HashMap<Short,Integer>();

	private static HashMap<String,Integer> slabelCount = new HashMap<String,Integer>();

	
	static short[] def = new short[1];
	
	private Edges () {}
	
	/**
	 * @param length
	 */
	public static void init(int length) {
			edges = new short[length][length][2][];
	}
	
	
	public static void findDefault(){
		int best =0;
		for(Entry<Short,Integer> e :  labelCount.entrySet()) {
			if (best<e.getValue()) {
				best = e.getValue();
				def[0]=e.getKey();
			}
		}
	//	labelCount=null;
		String[] types = new String[MFO.getFeatureCounter().get(PipeGen.REL)];
		for (Entry<String, Integer> e : MFO.getFeatureSet().get(PipeGen.REL).entrySet())  	types[e.getValue()] = e.getKey();

    	is2.util.DB.println("set default label to "+types[def[0]]+" " );
/*
    	for(int i=0; i<edges.length;i++) {
    		
    	   	for(int j=0; j<edges[i].length;j++) {
  
    	   		if(edges[i][j][0]!=null) {
    	   		//	Arrays.sort(edges[i][j][0]);
    	   			ArrayList<Short> l = new ArrayList<Short>();
    	   			for (short s : edges[i][j][0]){
    	   				l.add(s);
    	   			}
    	   			//Collections.reverse(l);
    	   			
    	   			//Collections.sort(l,new C(i+"-"+j+true));
    	   			TIntHashSet it  = new TIntHashSet();
    	   			HashSet<Short> set = new HashSet<Short>();
    	   			for (short s : edges[i][j][0]){
    	   				set.add(s);
    	   				it.add(s);
    	   			}
    	   			l.clear();
    	   			l.addAll(set);
    	   			Collections.reverse(l);
      	   			TIntIterator itt = it.iterator();
      	   		    	
      	   			for (int k =l.size()-1;k>=0;k--){
        	   			//edges[i][j][0][k]=l.get(k);     
        	   			edges[i][j][0][k]=(short)itt.next();
    	   			}
    	   			
    	   		}
       	   		if(edges[i][j][1]!=null) {
    	   			//Arrays.sort(edges[i][j][1]);
    	   			ArrayList<Short> l = new ArrayList<Short>();
    	   			for (short s : edges[i][j][1]){
    	   				l.add(s);
    	   			}
    	   			
    				TIntHashSet it  = new TIntHashSet();

    	   			HashSet<Short> set = new HashSet<Short>();
    	   			for (short s : edges[i][j][1]){
    	   				set.add(s);
    	   				it.add(s);
    	   			}
    	   			l.clear();
    	   			l.addAll(set);
    	   			Collections.reverse(l);
    	   			
    	   		
    	   			//		System.out.print(""+e.getKey()+" ");
    	   			TIntIterator itt = it.iterator();
    	   			//		System.out.print(""+e.getKey()+" ");
    	   					
    	   			for (int k =l.size()-1;k>=0;k--){
        	   			//edges[i][j][1][k]=l.get(k);
    	   				edges[i][j][1][k]=(short)itt.next();
    	   			}
    	   			
    	   		}
    	   		    	   	
    	   	}
    	}
    	
    	*/
		System.out.println("found default "+def[0]);
	//	System.exit(0);
	}
	

	final static public void put(int pos1, int pos2, boolean dir, short label) {
		putD(pos1, pos2,dir, label);
	//	putD(pos2, pos1,!dir, label);
		
	}
	
	
	final static public void putD(int pos1, int pos2, boolean dir, short label) {
		
		Integer lc = labelCount.get(label);
		if (lc==null) labelCount.put(label, 1);
		else labelCount.put(label, lc+1);

		String key = pos1+"-"+pos2+dir+label;
		Integer lcs = slabelCount.get(key);
		if (lcs==null) slabelCount.put(key, 1);
		else slabelCount.put(key, lcs+1);

		
		if (edges[pos1][pos2][dir?0:1]==null) {
			edges[pos1][pos2][dir?0:1]=new short[1];
			edges[pos1][pos2][dir?0:1][0]=label;
		} else {
			short labels[] = edges[pos1][pos2][dir?0:1];
			for(short l : labels) {				
				//contains label already?
				if(l==label) return;
			}
			
			short[] nlabels = new short[labels.length+1];
			System.arraycopy(labels, 0, nlabels, 0, labels.length);
			nlabels[labels.length]=label;
			edges[pos1][pos2][dir?0:1]=nlabels;
		}
	}
	
	final static public short[] get(int pos1, int pos2, boolean dir) {
		
		if (pos1<0 || pos2<0 || edges[pos1][pos2][dir?0:1]==null) return def;
		return edges[pos1][pos2][dir?0:1];
	}

	
	/**
	 * @param dis
	 */
	static public void write(DataOutputStream d) throws IOException {

		int len = edges.length;
		d.writeShort(len);

		for(int p1 =0;p1<len;p1++) {
			for(int p2 =0;p2<len;p2++) {
				if (edges[p1][p2][0]==null) d.writeShort(0);
				else {
					d.writeShort(edges[p1][p2][0].length);
					for(int l =0;l<edges[p1][p2][0].length;l++) {
						d.writeShort(edges[p1][p2][0][l]);
					}
					
				}

				if (edges[p1][p2][1]==null) d.writeShort(0);
				else {
					d.writeShort(edges[p1][p2][1].length);
					for(int l =0;l<edges[p1][p2][1].length;l++) {
						d.writeShort(edges[p1][p2][1][l]);
					}
				}				
			}
		}
		
		d.writeShort(def[0]);

	}

	
	/**
	 * @param dis
	 */
	public static void read(DataInputStream d) throws IOException {
		int len = d.readShort();

		edges = new short[len][len][2][];
		for(int p1 =0;p1<len;p1++) {
			for(int p2 =0;p2<len;p2++) {
				int ll = d.readShort();
				if (ll==0) {
					edges[p1][p2][0]=null;
				} else {
					edges[p1][p2][0] = new short[ll];
					for(int l =0;l<ll;l++) {
						edges[p1][p2][0][l]=d.readShort();
					}					
				}

				ll = d.readShort();
				if (ll==0) {
					edges[p1][p2][1]=null;
				} else {
					edges[p1][p2][1] = new short[ll];
					for(int l =0;l<ll;l++) {
						edges[p1][p2][1][l]=d.readShort();
					}					
				}					
			}
		}
		
		def[0]= d.readShort();

	}

	public static class C implements Comparator<Short> {

		public C() {
			super();
		}

		String _key;
		
		public C(String key) {
			super();
			_key=key;
		}

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Short l1, Short l2) {
			
		//	int c1 = labelCount.get(l1);
		//	int c2 = labelCount.get(l2);
		//	if (true) return c1==c2?0:c1>c2?-1:1;

			int x1 = slabelCount.get(_key+l1.shortValue());
			int x2 = slabelCount.get(_key+l2.shortValue());
		//	System.out.println(x1+" "+x2);
			
			
			return x1==x2?0:x1>x2?-1:1;
			
			
			
		}

	

		
		
	}
	
	
}
