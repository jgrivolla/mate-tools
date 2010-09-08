package is2.parser;

import is2.data.DataF;
import is2.data.F2SF;
import is2.data.Instances;


import java.util.ArrayList;

/**
 * @author Bernd Bohnet, 30.08.2009
 * 
 * This class implements a parallel feature extractor.
 */
final public class ParallelExtract extends Thread 
{
	// the data space of the weights for a dependency tree  
	final DataF d;

	// the data extractor does the actual work
//	final Extractor extractor;
	final Extractor extractor;


	private Instances is;
	private int i;

	private F2SF para;

	public ParallelExtract(Extractor e, Instances is, int i, DataF d,  F2SF para, short[] pos) {

		this.is =is;
		extractor=e;
		this.d =d;
		this.i=i;
		this.para=para;
	}


	public static class DSet {
		int w1,w2;
	}

	@Override
	public void run() {

		F2SF f= para;

		short[] pos=is.pposs[i];
		int[] forms=is.forms[i];
		int[] lemmas=is.lemmas[i];
		short[][] feats=is.feats[i];
		int length = pos.length;

		while (true){

//			if (sets.size()==0)break;

			DSet set = get();
			if (set ==null) break;

			int w1=set.w1;
			int w2=set.w2;

			f.clear();
			extractor.extractFeatures(pos, w1, w2, f);
			d.pl[w1][w2]=f.getScoreF();

			short[] labels = Edges.get(pos[w1], pos[w2],false);
			float[][] lab = d.lab[w1][w2];

			if (labels!=null) {

				for (int l = labels.length - 1; l >= 0; l--) {

					short label = labels[l];
					f.clear();
					extractor.extractFeatures(pos,forms,lemmas, feats, w1, w2, label, f);
					lab[label][0]=f.getScoreF();
				}
			}

			labels = Edges.get(pos[w1], pos[w2],true);

			if (labels!=null)  {

				for (int l = labels.length - 1; l >= 0; l--) {

					int label = labels[l];
					f.clear();
					extractor.extractFeatures(pos,forms,lemmas, feats,w1, w2, label, f);
					lab[label][1]=f.getScoreF();
				}
			}

			int s = w1<w2 ? w1 : w2;
			int e = w1<w2 ? w2 : w1;

			int sg = w1<w2 ? w1 : 0;
			int eg = w1<w2 ? length : w1+1;

			for(int m=sg;m<eg;m++) {
				for(int dir=0;dir<2;dir++) {				
					labels = Edges.get(pos[w1], pos[w2],dir==1);
					float[] lab2 = new float[labels.length];

					int g = (m==s||e==m) ? -1 : m;

					for (int l = labels.length - 1; l >= 0; l--) {

						int label = labels[l];

						f.clear();
						extractor.gcF(pos,forms,lemmas, feats, w1, w2, g, label, f);
						lab2[l] = f.getScoreF();
					}
					d.gra[w1][w2][m][dir] =lab2;
				}
			}

			for(int m=s;m<e;m++) {
				for(int dir=0;dir<2;dir++) {				
					labels = Edges.get(pos[w1], pos[w2],dir==1);
					float lab2[]= new float[labels.length];

					int g = (m==s||e==m) ? -1 : m;

					for (int l = labels.length - 1; l >= 0; l--) {

						int label = labels[l];
						f.clear();
						extractor.extractSiblingFeatures(pos,forms,lemmas,feats, w1, w2, g, label, f);
						lab2[l] = f.getScoreF();
					}
					d.sib[w1][w2][m][dir]=lab2;
				}
			}

		}

	}

	
	static ArrayList<DSet> sets = new ArrayList<DSet>();
	
	private DSet  get() {
	
		synchronized (sets) {
			if (sets.size()==0) return null;		
			return sets.remove(sets.size()-1);
		}
	}
	/*
	ArrayList<DSet> sets = new ArrayList<DSet>();

	private DSet get() {

		return sets.remove(sets.size()-1);
	}
*/
	static public void add(int w1, int w2){
		DSet ds =new DSet();
		ds.w1=w1;
		ds.w2=w2;
		sets.add(ds);
	}
	
		
		
		
}
