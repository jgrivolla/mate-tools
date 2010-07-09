package is2.parser;

import is2.data.Parse;


final public class Closed  {

	public float p;
	short start,end,modifier;
	byte dir;
	
	Closed lower;
	Open upper;

	public Closed(short s, short t, int m, int dir,Open upper, Closed lower, float score) {
		start = s;
		end = t;
		modifier = (short)m;
		this.dir = (byte)dir;
		this.upper=upper;
		this.lower =lower;
		p=score;
	}


	public void createTree(Parse parse) {
		if (upper != null) upper.createTree(parse);
		if (lower != null) lower.createTree(parse);
	}
}


