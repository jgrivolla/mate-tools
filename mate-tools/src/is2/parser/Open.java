package is2.parser;

import is2.data.Parse;


final public  class Open {

	public float p;
	short s, e, label;
	byte dir;

	Closed left;
	Closed right;

	public Open(short s, short t, short dir, short label,Closed left, Closed right, float p) {
		this.s = s;
		this.e = t;
		this.label = label;
		this.dir = (byte)dir;
		this.left =left;
		this.right=right;
		this.p=p;
	}


	void createTree(Parse parse) {
		if (dir == 0) {
			parse.heads[s] = e;
			if (label != -1) parse.types[s] = label;
		} else {
			parse.heads[e] = s;
			if (label != -1) parse.types[e] = label;
		}
		if (left != null) left.createTree(parse);
		if (right != null) right.createTree(parse);
	}
	
}
