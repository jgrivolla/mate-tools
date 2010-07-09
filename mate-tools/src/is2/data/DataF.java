package is2.data;



final public class DataF {

		final public short typesLen;
		final public int len;
		
		// first order features
		final public float[][] p_link;
		
		//final public FV[][][] label;
		final public float[][][][] p_label;

		
		public FV fv;
		
		final public float[][][][][] p_sib;

		final public float[][][][][] p_gra;
	
	
		public DataF(int length, short types) {
			typesLen=types;
			len =length;
			
			p_link = new float[length][length];
			p_label = new float[length][length][types][2];
			
			p_sib = new float[length][length][length][2][];
			p_gra = new float[length][length][length][2][];
		
		}
}
