/**
 * 
 */
package is2.data;

/**
 * @author Dr. Bernd Bohnet, 30.10.2010
 *  
 * This class computes the mapping of features to the weight vector.
 */
final public class D4 {
	private long shift;
	private long h;
	public int a0,a1,a2,a3,a4,a5,a6,a7,a8,a9;
	public int v0,v1,v2,v3,v4,v5,v6,v7,v8,v9; 

	private final Long2IntInterface _li; 
	public D4(Long2IntInterface li) {
		_li=li;
	}
	

	final public void clean() {
		v0=0;v1=0;v2=0;v3=0;v4=0;v5=0;v6=0;v7=0;v8=0;
		shift=0;h=0;
	}
	
	final public  void cz3(){
		if (v0<0||v1<0||v2<0) {  h=-1;return;}
		
		h= v0+v1*(shift =a0)+(long)v2*(shift *=a1);
		shift *=a2;
	}

	final public  long c3(){
		if (v0<0||v1<0||v2<0) {  h=-1;return h;}
		
		h= v0+v1*(shift =a0)+(long)v2*(shift *=a1);
		shift *=a2;
		return h;
	}
	
	final public void cz4(){		
		if (v0<0||v1<0||v2<0||v3<0) {h=-1;return;}
		
		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		shift *=a3;		
	}

	final public long c4(){		
		if (v0<0||v1<0||v2<0||v3<0) {h=-1;return h;}
		
		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		shift *=a3;		
		return h;
	}
	

	final public void cz5(){
		
		if (v0<0||v1<0||v2<0||v3<0||v4<0) {h=-1;return;}
		
		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2)+(long)v4*(shift*=a3);
		shift*=a4;

	}

	final public long c5(){
		
		if (v0<0||v1<0||v2<0||v3<0||v4<0) {h=-1;return h;}
		
		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2)+(long)v4*(shift*=a3);
		shift*=a4;
		return h;
	}
	

	final public void cz6(){

		if (v0<0||v1<0||v2<0||v3<0||v4<0||v5<0) {h=-1; return;}

		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		h +=(long)v4*(shift*=a3)+(long)v5*(shift*=a4);
		shift*=a5;
	}

	final public long c6(){

		if (v0<0||v1<0||v2<0||v3<0||v4<0||v5<0) {h=-1; return h;}

		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		h +=(long)v4*(shift*=a3)+(long)v5*(shift*=a4);
		shift*=a5;
		return h;
	}


	final public void cs(int b, int v) {
		if (h<0) {h=-1;	 return;}
		
		h += (long)v*shift;
		shift *=b;

	}

	final public void csa(int b, int v, IFV f) {
		if (h<0) {h=-1;	return;}

		h += (long)v*shift;
		shift *=b;
		f.add(_li.l2i(h));
	}

	final public long csa(int b, int v) {
		if (h<0) {h=-1;	return-1; }

		h += (long)v*shift;
		shift *=b;
		return h;
	}

	public final long getVal(){
		return h;
	}
	
	public final void map(IFV f, long l){
		if (l>0) f.add(this._li.l2i(l));
	}
	
	/**
	 * @param f
	 */
	final public void add(IFV f) {
		f.add(_li.l2i(h));
	}

	final public void cz7() {
		if (v0<0||v1<0||v2<0||v3<0||v4<0||v5<0||v6<0) {h=-1; return;}

		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		h +=(long)v4*(shift*=a3)+(long)v5*(shift*=a4)+(long)v6*(shift*=a5);
		shift*=a7;
		
	}

	final public long c7() {
		if (v0<0||v1<0||v2<0||v3<0||v4<0||v5<0||v6<0) {h=-1; return h;}

		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		h +=(long)v4*(shift*=a3)+(long)v5*(shift*=a4)+(long)v6*(shift*=a5);
		shift*=a7;
		return h;
	}

	/**
	 * 
	 */
	final public void cz8() {
		if (v0<0||v1<0||v2<0||v3<0||v4<0||v5<0||v6<0||v7<0) {h=-1; return;}

		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		h +=(long)v4*(shift*=a3)+(long)v5*(shift*=a4)+(long)v6*(shift*=a5)+(long)v7*(shift*=a6);
		shift*=a7;
	}

	final public void cz9() {
		if (v0<0||v1<0||v2<0||v3<0||v4<0||v5<0||v6<0||v7<0||v8<0) {h=-1; return;}

		h =v0+v1*(shift =a0)+(long)v2*(shift *=a1)+(long)v3*(shift *=a2);
		h +=(long)v4*(shift*=a3)+(long)v5*(shift*=a4)+(long)v6*(shift*=a5)+(long)v7*(shift*=a6)+(long)v8*(shift*=a7);
		shift*=a7;
	}


}