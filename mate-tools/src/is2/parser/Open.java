package is2.parser;

import is2.data.Parse;


final public  class Open {

	public float p;
	short start, end, type;
	byte dir;

	Closed left;
	Closed right;

	public Open(short s, short t, short dir, short label,Closed left, Closed right, float score) {
		start = s;
		end = t;
		type = label;
		this.dir = (byte)dir;
		this.left =left;
		this.right=right;
		p=score;
	}


	void createTree(Parse parse) {
		if (dir == 0) {
			parse.heads[start] = end;
			if (type != -1) parse.types[start] = type;
		} else {
			parse.heads[end] = start;
			if (type != -1) parse.types[end] = type;
		}
		if (left != null) left.createTree(parse);
		if (right != null) right.createTree(parse);
	}
	
}
