

package is2.io;

import is2.data.Instances;
import is2.data.SentenceData09;
import is2.util.DB;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;



/**
 * This class reads files in the CONLL-09 format.
 *
 * @author Bernd Bohnet
 */
public class CONLLReader09  {

	private static final String US = "_";
	private static final String REGEX = "\t";
	public static final String STRING = "*";
	public static final String PIPE = "\\|";
	public static final String NO_TYPE = "<no-type>";
	public static final String ROOT_POS = "<root-POS>";
	public static final String ROOT_LEMMA = "<root-LEMMA>";
	public static final String ROOT = "<root>";
	public static final String EMPTY_FEAT = "<ef>";

	private static final String NUMBER = "[0-9]+|[0-9]+\\.[0-9]+|[0-9]+[0-9,]+";
	public static final String NUM = "<num>";

	private BufferedReader inputReader;

	public static final int TASK08=8;
	public static final int TASK09=9;

	public static boolean normalizeOn =true;


	private int lineNumber = 0;

	public CONLLReader09(){}

	public CONLLReader09(String file){
		lineNumber=0;
		try {
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"),32768);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CONLLReader09(String file, int task){
		this(file);
	}



	public void startReading(String file ){
		lineNumber=0;
		try {
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"),32768);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**i.forms[heads[l]-1]+" "+rel+" "+
	 * Read a instance
	 * @return a instance
	 * @throws Exception 
	 */
	public SentenceData09 getNext() throws Exception  {

		try {

			ArrayList<String[]> lineList = new ArrayList<String[]>();

			String line = inputReader.readLine();
			
			while(line !=null && line.length()==0) {
				line = inputReader.readLine();
				lineNumber++;
				System.out.println("skip empty line at line "+lineNumber);
			}
			
			while (line != null && line.length()!=0 &&  !line.startsWith(STRING) &&!line.startsWith(REGEX)) {
				lineList.add(line.split(REGEX));
				line = inputReader.readLine();
				lineNumber++;
			}

			
			
			int length = lineList.size();

			if(length == 0) {
				inputReader.close();
				return null;
			}

			SentenceData09 it = new SentenceData09();

			it.forms = new String[length+1];

			it.lemmas = new String[length+1];
			//	it.ppos = new String[length+1];
			it.gpos = new String[length+1];
			it.labels = new String[length+1];
			it.heads = new int[length+1];
			it.phead = new int[length+1];
			it.pedge = new String[length+1];

			it.ppos = new String[length+1];
				it.org_lemmas = new String[length+1];
				it.fillp = new String[length+1];
				it.feats = new String[length+1][];
				it.ofeats = new String[length+1];
				it.pfeats = new String[length+1];


			it.forms[0] = ROOT;
			it.lemmas[0] = ROOT_LEMMA;
			it.fillp[0] = "N";
			it.org_lemmas[0] = ROOT_LEMMA;

			it.gpos[0] = ROOT_POS;
			it.ppos[0] = ROOT_POS;
			it.labels[0] = NO_TYPE;
			it.heads[0] = -1;
			it.pedge[0] = NO_TYPE;
			it.phead[0] = -1;
			it.ofeats[0] = NO_TYPE;

			// root is 0 therefore start with 1

			for(int i = 1; i <= length; i++) {
				String[] info = lineList.get(i-1);

					it.forms[i] = info[1]; //normalize(
					if (info.length<3) continue;

					it.org_lemmas[i] = info[2];
					it.lemmas[i] =info[3]; 
					it.gpos[i] = info[4];  

					if (info.length<5) continue;
					it.ppos[i] = info[5].split("\\|")[0];
					// feat 6
					// pfeat 7
					
					// this causes trouble in the perl eval09 scirpt
					//it.ofeats[i]=info[6].equals(CONLLWriter09.DASH)? "" : info[6];
					
					// now we try underscore
					it.ofeats[i]=info[6].equals(CONLLWriter09.DASH)? "_" : info[6];


					if (info[7].equals(CONLLWriter09.DASH)) it.feats[i]=null;
					else {
						it.feats[i] =info[7].split(PIPE);
						it.pfeats[i] = info[7];
					}

					if (info[8].equals(US))it.heads[i]=-1;
					else it.heads[i] = Integer.parseInt(info[8]);// head
	
					
					it.phead[i]=info[9].equals(US) ? it.phead[i]=-1:  Integer.parseInt(info[9]);// head
					
					it.labels[i] = info[10];					
					it.pedge[i] = info[11];
					it.fillp[i]=info[12];

					if (info.length>13) {
						if (!info[13].equals(US)) it.addPredicate(i,info[13]);
						for(int k=14;k<info.length;k++)  it.addArgument(i,k-14,info[k]);
						
					}


				

			}
			return it;

		} catch(Exception e) {
			System.out.println("\n!!! Error in input file at line : "+lineNumber+" "+e.toString());
			e.printStackTrace();
			throw new Exception();
			//	return null;
		}

	}

	/**
	 * Read a instance an store it in a compressed format
	 * @param is
	 * @return
	 * @throws IOException
	 */
	final public SentenceData09 getNext(Instances is) throws Exception {

		SentenceData09 it = getNext();

		if (is !=null) insert(is,it);

		return it;

	}




	final public boolean insert(Instances is, SentenceData09 it) throws IOException {

		try {

			if(it == null) {
				inputReader.close();
				return false;
			}

			int i= is.createInstance09(it.length());

			for(int p = 0; p < it.length(); p++) {

				is.setForm(i, p, normalize(it.forms[p]));
				is.setGPos(i, p, it.gpos[p]);	
				
		//		System.out.println(""+is.gpos[i][p]);
	
				if (it.ppos[p]==null||it.ppos[p].equals(US)) {
					is.setPPoss(i, p, it.gpos[p]);
				} else is.setPPoss(i, p, it.ppos[p]);

		
				if (it.lemmas[p]==null ||it.lemmas[p].equals(US)) {
					is.setLemma(i, p, normalize(it.forms[p]));
				} else is.setLemma(i, p, normalize(it.lemmas[p]));

				if (it.org_lemmas!=null)
				if (it.org_lemmas[p]==null ||it.org_lemmas[p].equals(US)) {
					is.setGLemma(i, p, it.lemmas[p]);
				} else is.setGLemma(i, p, it.org_lemmas[p]);

				
				is.setFeats(i,p,it.feats[p]);

				if (it.ofeats!=null) is.setFeature(i,p,it.ofeats[p]);

				
				is.setRel(i,p,it.labels[p]);
				if (it.pedge!=null) is.setPRel(i,p,it.pedge[p]);
				is.setHead(i,p,it.heads[p]);
				if (it.phead!=null) is.setPHead(i,p,it.phead[p]);
				
				if (it.fillp!=null && it.fillp[p]!=null && it.fillp[p].startsWith("Y")) is.pfill[i].set(p);
				else is.pfill[i].clear(p);
			}

			if (is.createSem(i,it)) {
				DB.println("count "+i+" len "+it.length());
				DB.println(it.printSem());
			}
		} catch(Exception e ){
			DB.println("head "+it);
			e.printStackTrace();
		}
		return true;

	}
	public static String normalize (String s) {
		if (!normalizeOn) return s;
		if(s.matches(NUMBER))  return NUM;
		return s;
	}	

}
