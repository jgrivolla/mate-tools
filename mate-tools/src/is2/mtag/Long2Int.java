package is2.mtag;

import is2.data.Long2IntInterface;



final public class Long2Int implements Long2IntInterface {

	
	/** Integer counter for long2int */
	final private int size=67108863;// 3292489; //0x03ffffff //0x07ffffff
	                       
	
	/** Stop growing */
	public boolean stop=false;
	
		

	
	public Long2Int () {
	//	size =0x07ffffff; //67108863=0x03ffffff
		                 //105359939  
	}
	
	
	/* (non-Javadoc)
	 * @see is2.sp09k9992.Long2IntIterface#size()
	 */
	public  int size() {return size;}
	
	
	final public void stop() {stop=true;}
	
	/* (non-Javadoc)
	 * @see is2.sp09k9992.Long2IntIterface#start()
	 */
	final public void start() {
		stop=false;		
	
	}
	
	

	/* (non-Javadoc)
	 * @see is2.sp09k9992.Long2IntIterface#l2i(long)
	 */
	final public int l2i(long l) {
		
		if (l<0) return -1;
		
		// this works well LAS 88.138
	//	int r= (int)(( l ^ (l&0xffffffff00000000L) >>> 29 ));//0x811c9dc5 ^ // 29
	//	return Math.abs(r % size);
		// this works a bit better and good with 0x03ffffff 
	//	/*
/*
		long r= l;//25
		l = (l>>11)&0xfffffffffffff000L;
		r ^= l;//36
		l = (l>>9)&0xffffffffffffc000L;
		r ^= l;//45
		l = (l>>7)& 0xffffffffffff0000L; //53
		r ^= l;//52
		l = (l>>5)&0xfffffffffffc0000L; //62
		r ^=l;//57
		int x = (int)r;
		x = x % size;
		*/
		long r= l;//25
		l = (l>>13);
		r ^= l;//36
		l = (l>>11);
		r ^= l;//45
		l = (l>>9); //53
		r ^= l;//52
		int x = (int)r;
		x = x % size;
		
		/*
		long r= l;//26
		l = (l>>12)&0xfffffffffffff000L;
		r ^= l;//38
		l = (l>>11)&0xffffffffffffc000L;
		r ^= l;//49
		l = (l>>9)& 0xffffffffffff0000L; //53
		r ^= l;//58
		l = (l>>7)&0xfffffffffffc0000L; //62
		r ^=l;//65
		int x = (int)r;
		x = x % size;
		*/
	//	return x >= 0 ? x : -x ;// Math.abs(r % size);

	//	*/
		/*
		// together with 0x07ffffff 27 88.372
		long r= l;// 27
		l = (l>>13)&0xffffffffffffe000L;
		r ^= l;   // 40
		l = (l>>11)&0xffffffffffff0000L;
		r ^= l;   // 51
		l = (l>>9)& 0xfffffffffffc0000L; //53
		r ^= l;  // 60
		l = (l>>7)& 0xfffffffffff00000L; //62
		r ^=l;    //67
		int x = ((int)r) % size;
	*/
		// together with 0x000fffff 78.557/83.047
		/*
		long r= l;// 20
		l = (l>>11)&0xfffffffffffffe00L;
		r ^= l;   // 31
		l = (l>>10)&0xfffffffffffffc00L;
		r ^= l;   // 41
		l = (l>>9)& 0xfffffffffffff800L; //53
		r ^= l;  // 50
		l = (l>>8)& 0xfffffffffffff000L; //62
		r ^=l;    //58
		l = (l>>7)& 0xfffffffffffff000L; //62
		r ^=l;    //63
		int x = ((int)r) % size;
		*/
		return x >= 0 ? x : -x ; 
	}
	


	

	
}
