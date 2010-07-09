package is2.data;

public class Parse {
	
	public Parse() {}
		
	public Parse(int i) {
		heads = new short[i];
		types = new short[i];
	}
	public short[] heads;
	public short[] types;
	public double f1;
	
	@Override
	public Parse clone() {
		Parse p = new Parse();
		p.heads = new short[heads.length];
		p.types = new short[types.length];
		
		System.arraycopy(heads, 0, p.heads, 0, heads.length);
		System.arraycopy(types, 0, p.types, 0, types.length);
		
		return p;
	}
}
 