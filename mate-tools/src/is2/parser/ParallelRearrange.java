package is2.parser;

import is2.data.DataF;

import java.util.ArrayList;

/**
 * @author Dr. Bernd Bohnet, 30.08.2009
 * 
 * This class implements a parallel edge rearrangement for non-projective parsing;
 * The linear method was first suggest by Rayn McDonald et. al. 2005.  
 */
final public class ParallelRearrange extends Thread {

	// new parent child combination to explore
	final static class PA {
		final float p;
		final short ch, pa;
		public PA(float p2, short ch2, short pa2) { p=p2; ch=ch2;pa=pa2;}
	}

	// list of parent child combinations
	static ArrayList<PA> parents = new ArrayList<PA>();

	// best new parent child combination, found so far
	public float max;

	// some data from the dependency tree
	//private EdgesC edges;	
	private short[] pos;
	private DataF x;
	private boolean[][] isChild ;	
	public short[] heads,types;
	
	// child, new parent, new label
	public short wh,nPar,nType;
	
	/**
	 * Initialize the parallel rearrange thread
	 * 
	 * @param isChild2 is a child
	 * @param edgesC the part-of-speech edge mapping
	 * @param pos the part-of-speech 
	 * @param x the data
	 * @param s the heads
	 * @param ts the types
	 */
	public ParallelRearrange(boolean[][] isChild2,short[] pos, DataF x, short[] s, short[] ts) {
		
		heads =new short[s.length];
		System.arraycopy(s, 0,  heads, 0, s.length);

		types =new short[ts.length];
		System.arraycopy(ts, 0,  types, 0, ts.length);

		isChild=isChild2;
	    //edges = edgesC;
		this.pos =pos;
		this.x=x;
	}


	@Override
	public void run() {
		
		// check the list of new possible parents and children for a better combination
		while(true) {
			PA px = getPA();
			if (px==null) break;

			short pa =px.pa, ch =px.ch;
			
			if(ch == pa || pa == heads[ch] || isChild[ch][pa]) continue;

			short oldP = heads[ch], oldT = types[ch]; 

			heads[ch]=pa;

			short[] labels = Edges.get(pos[pa], pos[ch],ch<pa);

			for(int l=0;l<labels.length;l++) {

				types[ch]=labels[l];

				float p_new = Extractor.encode3(pos, heads, types, x);

				if(max < p_new-px.p ) {
					max = p_new-px.p; wh = ch; nPar = pa; nType = labels[l] ;
				}
			}
			heads[ch]= oldP; types[ch]=oldT;
		}
	}

	/**
	 * Add a child-parent combination which are latter explored for rearrangement
	 * 
	 * @param p2
	 * @param ch2
	 * @param pa
	 */
	static public void add(float p2, short ch2, short pa) {
		parents.add(new PA(p2,ch2,pa));
	}

	static private PA getPA() {
		synchronized (parents) {
			if (parents.size()==0) return null;
			return parents.remove(parents.size()-1);
		}
	}

	
}
