package se.lth.cs.srl;

import java.util.Date;

import se.lth.cs.srl.corpus.Sentence;

public abstract class SemanticRoleLabeler {
	
	public void parseSentence(Sentence s){
		long startTime=System.currentTimeMillis();
		parse(s);
		parsingTime+=System.currentTimeMillis()-startTime;
		senCount++;
		predCount+=s.getPredicates().size();
	}
	public abstract void parse(Sentence s);
	
	public String getStatus(){
		StringBuilder ret=new StringBuilder("Semantic role labeler started at "+startDate+"\n");
		ret.append("Loading time (ms)\t\t"+insertCommas(loadingTime)+"\n");
		ret.append("Parsing time (ms)\t\t"+insertCommas(parsingTime)+"\n");
		ret.append("\n");
		ret.append("Number of sentences\t"+insertCommas(senCount)+"\n");
		ret.append("Number of predicates\t"+insertCommas(predCount)+"\n");
		ret.append("Parsing speed (ms/sen)\t"+(parsingTime/senCount)+"\n");
		ret.append(getSubStatus());
		return ret.toString();
	}
	protected abstract String getSubStatus();
	
	public long loadingTime=0;
	public long parsingTime=0;
	public int senCount=0;
	public int predCount=0;
	public final Date startDate=new Date();

	public static String insertCommas(long l){
		StringBuilder ret=new StringBuilder(Long.toString(l));
		ret.reverse();
		for(int i=3;i<ret.length();i+=4){
			if(i+1<=ret.length())
				ret.insert(i,",");
		}
		return ret.reverse().toString();
	}

}
