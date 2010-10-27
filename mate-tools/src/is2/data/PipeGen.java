package is2.data;

public class PipeGen {

	public static final String SENSE = "SENSE";	
	public static final String POS = "POS";
	public static final String DIST = "DIST";
	public static final String WORD = "WORD";
	public static final String PRED = "PRED";
	public static final String ARG = "ARG";
	public static final String FEAT = "F";
	public static final String REL = "REL";
	protected static final String TYPE = "TYPE";

	
 //   public static String[] types;
//    public static String[] pos;
//    public static String[] senses;

	
	static public int outValue(int num1, int del) {
		String out = ""+num1;
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
	
	static public int outValue(int num1, int del, long last) {
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance)";
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
	
	static public int outValueErr(int num1, float err, float f1, int del, long last) {
	
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance "+(err/num1)+" err/instance f1="+
			f1 +") ";
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
	

	static public int outValueErr(int num1, float err, float f1, int del, long last, double upd) {
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance "+(err/num1)+" err/instance f1="+
			f1 +") upd "+upd;
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}

	static public int outValueErr(int num1, float err, float f1, int del, long last, double upd, String info) {
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance "+(err/num1)+" err/instance f1="+
			f1 +") upd "+upd+" "+info;
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
}
