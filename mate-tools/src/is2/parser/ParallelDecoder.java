package is2.parser;

import is2.data.DataF;

import java.util.ArrayList;

/**
 * @author Bernd Bohnet, 30.08.2009
 * 
 * This class implements a parallel feature extractor.
 */
final public class ParallelDecoder extends Thread 
{
	// some constants
	private static final float INIT_BEST = (-1.0F / 0.0F);
	private static final boolean[] DIR ={false,true};

	// the data space of the weights for a dependency tree  
	final private DataF x;



	private short[] pos;

	private Open O[][][][];
	private Closed C[][][][] ;

	private int n;

	boolean done=false;
	public boolean waiting =false;

	/**
	 * Initialize the parallel decoder.
	 * 
	 * @param pos part-of-speech
	 * @param d data
	 * @param edges part-of-speech edge mapping
	 * @param o open spans 
	 * @param c closed spans
	 * @param n number of words
	 */
	public ParallelDecoder(short[] pos, DataF d, Open o[][][][], Closed c[][][][], int n) {

		this.pos =pos;
		this.x =d;

		this.O=o;
		this.C=c;
		this.n=n;
	}


	private static class DSet { short w1,w2;}

	@Override
	public void run() {

		while (true){

			DSet set = get();
			if (done && set==null) break;	

			if (set ==null) {
				if (!done) waiting =true;
		
				while (waiting) yield();
				continue;
			}

			short s=set.w1;
			short t=set.w2;

			for(short dir =1;dir>=0;dir--) {
				
				short[] labs = (dir==1) ? Edges.get(pos[s],pos[t], false):Edges.get(pos[t],pos[s], true);

				O[s][t][dir] = new Open[labs.length];
				double sc= (dir==1) ?  x.pl[s][t]: x.pl[t][s];

				for (int l = labs.length - 1; l >= 0; l--) {					
					short lab = labs[l];

					double labScore = (dir==1) ? x.p_label[s][t][lab][0] : x.p_label[t][s][lab][1];

					double bRS = INIT_BEST; 				
					Closed bLC = null, bRC = null;
					
					for (int r = s; r < t; r++) {
						
						if (s == 0 && r != 0) continue;
						
						double bl = INIT_BEST,br = INIT_BEST;
						Closed bLCld = null, bRCld = null;
						
						if (r == s) bl = dir==1 ? x.p_sib[s][t][s][0][l] : x.p_gra[t][s][s][1 ][l];
						else for (int li = s + 1; li <= r; li++) {		
								Closed  c = C[s][r][1][li];
								if ((dir==1 ? x.p_sib[s][t][li][0][l] : x.p_gra[t][s][li][1][l]) + c.p > bl) {
									bl = (dir==1 ? x.p_sib[s][t][li][0][l] : x.p_gra[t][s][li][1][l]) + c.p;bLCld = c;}
							}
					
						if (r == t-1) br = dir==1 ? x.p_gra[s][t][s][0][l] : x.p_sib[t][s][s][1][l];
						else
							for (int ri = r + 1; ri < t; ri++) {
								
								Closed c = C[r + 1][t][0][ri];
								if ((dir == 1 ? x.p_gra[s][t][ri][0][l] : x.p_sib[t][s][ri][1][l]) + c.p > br) {
									br = (dir == 1 ? x.p_gra[s][t][ri][0][l] : x.p_sib[t][s][ri][1][l]) + c.p; bRCld = c;}
							}
						
						if (bl + br > bRS) {bRS = bl + br; bLC = bLCld;bRC = bRCld;}
					}
					O[s][t][dir][l] = new Open(s, t, dir, lab,bLC, bRC, (float) ( bRS+sc + labScore));					
				}
			}
			C[s][t][1] = new Closed[n];	C[s][t][0] = new Closed[n];

			for (int m = s ; m <= t; m++) {
				for(boolean dir : DIR) {
					if ((dir && m!=s)||(!dir) && (m!=t && s!=0)) {			
						// create closed structure
						
						int from = dir ? s : t, to = dir ? t : s, us = dir ? s : m, 
							 ue = dir ? m : t, ls = dir ? m : s,e = dir ? t : m, st = dir ? 1 : -1;
						
						double bS = INIT_BEST;
						
						Open bUp = null;Closed bDo = null;
						
						int cnt =O[us][ue][dir?1:0].length;
						
						for (int l = 0; l < cnt; l++) {
							
							Open up = O[us][ue][dir?1:0][l];
							for (int cmo = m + st; cmo != to + st; cmo += st) {							
								if (up.p + C[ls][e][dir?1:0][cmo].p +x.p_gra[from][m][cmo][(dir?0:1)][l] > bS) {
									bS = up.p + C[ls][e][dir?1:0][cmo].p +x.p_gra[from][m][cmo][(dir?0:1)][l]; bUp = up; bDo=C[ls][e][dir?1:0][cmo];}
							}
						
							if (m != to) continue;
							
							double sc = up.p + x.p_gra[from][m][from][(dir ? 0 :1)][l]; 
							if (sc > bS) { bS = sc; bUp = up; bDo = null;}
						}
						C[s][t][dir?1:0][m] = new Closed(s, t, m, dir?1:0,bUp,bDo,(float) bS);
					}
				}
			}
		}
	}

	public static ArrayList<DSet> sets = new ArrayList<DSet>();

	static synchronized private DSet get() {
		synchronized (sets) {
			if (sets.size()==0) return null;
			return sets.remove(sets.size()-1);
		}
	}

	public void add(short w1, short w2){
		DSet ds =new DSet();
		ds.w1=w1;
		ds.w2=w2;
		sets.add(ds);
	}
}
