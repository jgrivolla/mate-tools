package is2.data;


final public class F2SF extends IFV {

	final private float[] parameters;
	
	public float score =0;
	
	public F2SF(float[] p) {
		parameters =p;
	}

	
	@Override
	public void add(int i) {
		if (i>0) score += parameters[i];
	}
	
	@Override
	public void clear() {
		score =0;
	}


	/* (non-Javadoc)
	 * @see is2.IFV#getScore()
	 */
	@Override
	public double getScore() {
		return score;
	}

	public float getScoreF() {
		return score;
	}

	/* (non-Javadoc)
	 * @see is2.IFV#clone()
	 */
	@Override
	public IFV clone() {
		return new F2SF(this.parameters);
	}
	
}
