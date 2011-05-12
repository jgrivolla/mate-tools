/**
 * 
 */
package is2.data;

import is2.data.IEncoder;
import is2.data.Instances;
import is2.data.SentenceData09;
import is2.parser40.Pipe;

/**
 * @author Dr. Bernd Bohnet, 06.11.2010
 * 
 * 
 */
public class InstancesTagger extends Instances {

	public short[][][] chars; 
	public int[][] formlc;

	public void init(int ic, IEncoder mf) {
		super.init(ic, mf,9);
		chars = new short[capacity][][];
		formlc = new int[capacity][];
		//	System.out.println("create chars "+capacity );
	}

	public void fillChars(SentenceData09 instance, int i, int cend) {
		chars[i] = new short[instance.length()][13];
		formlc[i] = new int[instance.length()];
		
		
		for(int k=0;k<instance.length();k++) {
			chars[i][k][0]= (short) m_encoder.getValue(PipeGen.CHAR, String.valueOf(instance.forms[k].charAt(0)));
			chars[i][k][1]= (short) ( instance.forms[k].length()>1?m_encoder.getValue(PipeGen.CHAR, String.valueOf(instance.forms[k].charAt(1))):cend);//m_encoder.getValue(PipeGen.CHAR, END);
			chars[i][k][2]= (short) ( instance.forms[k].length()>2?m_encoder.getValue(PipeGen.CHAR, String.valueOf(instance.forms[k].charAt(2))):cend);
			chars[i][k][3]= (short) ( instance.forms[k].length()>3?m_encoder.getValue(PipeGen.CHAR, String.valueOf(instance.forms[k].charAt(3))):cend);
			chars[i][k][4]= (short) ( instance.forms[k].length()>4?m_encoder.getValue(PipeGen.CHAR, String.valueOf(instance.forms[k].charAt(4))):cend);
			chars[i][k][5]= (short) ( instance.forms[k].length()>5?m_encoder.getValue(PipeGen.CHAR, String.valueOf(instance.forms[k].charAt(5))):cend);

			chars[i][k][6]= (short) ( m_encoder.getValue(PipeGen.CHAR,String.valueOf(instance.forms[k].charAt(instance.forms[k].length()-1))));
			chars[i][k][7]= (short) ( instance.forms[k].length()>1?m_encoder.getValue(PipeGen.CHAR,String.valueOf(instance.forms[k].charAt(instance.forms[k].length()-2))):cend);//m_encoder.getValue(PipeGen.CHAR, END);
			chars[i][k][8]= (short) ( instance.forms[k].length()>2?m_encoder.getValue(PipeGen.CHAR,String.valueOf(instance.forms[k].charAt(instance.forms[k].length()-3))):cend);
			chars[i][k][9]= (short) ( instance.forms[k].length()>3?m_encoder.getValue(PipeGen.CHAR,String.valueOf(instance.forms[k].charAt(instance.forms[k].length()-4))):cend);
			chars[i][k][10]= (short) ( instance.forms[k].length()>4?m_encoder.getValue(PipeGen.CHAR,String.valueOf(instance.forms[k].charAt(instance.forms[k].length()-5))):cend);
			chars[i][k][11] = (short)instance.forms[k].length();
			formlc[i][k] =m_encoder.getValue(PipeGen.WORD, instance.forms[k].toLowerCase());
		}
	}

}
