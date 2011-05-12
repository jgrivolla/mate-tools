package is2.data;

import is2.util.DB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;



final public class ParametersFloat  {

	public float[] parameters;
	public float[] total;

	public ParametersFloat(int size) {
		
		parameters = new   float[size];
		total = new float[size];
		for(int i = 0; i < parameters.length; i++) {
			parameters[i] = 0F;
			total[i] = 0F;
		}
	}


	/**
	 * @param parameters2
	 */
	public ParametersFloat(float[] p) {
		parameters =p;
	}

	public void average(double avVal) {
		for(int j = 0; j < total.length; j++) {
			parameters[j] = total[j]/((float)avVal);
		}
		total =null;
	}

	public ParametersFloat average2(double avVal) {
		float[] px = new float[this.parameters.length];
		for(int j = 0; j < total.length; j++) {
			px[j] = total[j]/((float)avVal);
		}
		ParametersFloat pf = new ParametersFloat(px);
		return pf;
	}
	
	public void update(FV pred, FV act,  float upd, float err) {

		
		float lam_dist = act.getScore(parameters,false)- pred.getScore(parameters,false);
		float b =(float)err - lam_dist;

		FV dist = act.getDistVector(pred);	 

		float alpha;
		float A = dist.dotProduct(dist);
		if (A<=0.0000000000000001)  alpha=0.0f;
		else alpha= b/A;
		
		dist.update(parameters, total, alpha, upd,false); 
	
	}

	public double update(FV a, double b) {

		double A = a.dotProduct(a);
		if (A<=0.0000000000000000001) return 0.0;
		return b/A;
	}

	
	public double getScore(FV fv) {
		if (fv ==null) return 0.0F;
		return fv.getScore(parameters,false);

	}

	
	final public void write(DataOutputStream dos) throws IOException{

		dos.writeInt(parameters.length);
		for(float d : parameters) dos.writeFloat(d);

	}

	public void read(DataInputStream dis) throws IOException{

		parameters = new float[dis.readInt()];
		int notZero=0;
		for(int i=0;i<parameters.length;i++) {
			parameters[i]=dis.readFloat();
			if (parameters[i]!=0.0F) notZero++; 
		}
		
		DB.println("read parameters "+parameters.length+" not zero "+notZero);

	}
	
	public int countNZ() {

		int notZero=0;
		for(int i=0;i<parameters.length;i++) {
			if (parameters[i]!=0.0F) notZero++; 
		}
		return notZero;

	}

	public F2SF getFV() {
		return new F2SF(parameters);
	}

	
	public int size() {
		return parameters.length;
	}


	
}
