package se.lth.cs.srl.test;

import org.junit.Test;


import java.io.File;
import java.util.Arrays;
import java.util.Map;

import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.features.Feature;
import se.lth.cs.srl.features.FeatureGenerator;
import se.lth.cs.srl.features.FeatureName;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.languages.Language.L;

public class TestFeatures extends TestCaseOutput {
	

		
    private final static String RESULT_DIR = "test/result/";
    private final static String RESULT_EXTENSION = ".features";


	//Non-q features
    @Test
    public void depSubCat(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("depsubcat","DepSubCat",sentences.get(3),true,new String[]{"V"},true);
    }
   
    @Test
    public void POSPath(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("pospath","POSPath",sentences.get(1),false,new String[]{""},true);
    }
    
    @Test
    public void deprelPath(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("deprelpath","DeprelPath",sentences.get(1),false,new String[]{""},true);
    }
    
    @Test
    public void position(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("position","Position",sentences.get(0),false,new String[]{""},true);
    }
    
    @Test
    public void predWord(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("predword","PredWord",sentences.get(1),true,new String[]{"V"},true);
    }
    
    @Test
    public void predParentPOS(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("predparentpos","PredParentPOS",sentences.get(1),true,new String[]{""},true);
    }
    
    @Test
    public void argDeprel(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("argdeprel","ArgDeprel",sentences.get(0),false,new String[]{""},false);
    }
    
    @Test
    public void leftDepPOS(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("leftdeppos","LeftPOS",sentences.get(1),false,new String[]{""},false);
    }
    
    @Test
    public void childDepSet(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("childdepset","ChildDepSet",sentences.get(1),true,new String[]{"N"},false);
    }
    
    @Test
    public void predFeats(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("predfeats","PredFeats",sentences.get(2),true,new String[]{""},true);
    }
    
    @Test
    public void argFeats(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("argfeats","ArgFeats",sentences.get(2),false,new String[]{""},false);
    }
    
    @Test
    public void predParentFeats(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("predparentfeats","PredParentFeats",sentences.get(3),true,new String[]{"V"},false);
    }
    
    //QDoubleChildSetFeature
    @Test
    public void childDepPOSSet(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("childdepposset","ChildDepSet+ChildPOSSet",sentences.get(1),true,new String[]{""},false);
    }
    @Test
    public void childDepPOSSet2(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("childdepposset2","ChildDepSet+ChildPOSSet",sentences.get(1),true,new String[]{"N"},false);
    }
    @Test
    public void childDepPOSSet3(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("childdepposset3","ChildDepSet+ChildPOSSet",sentences.get(1),true,new String[]{"V"},false);
    }
    @Test
    public void childDepPOSSet4(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("childdepposset4","ChildDepSet+ChildPOSSet",sentences.get(1),true,new String[]{"N","V"},false);
    }
    
    //QLinearLinearFeature
    @Test
    public void argPOSDepSubCat(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("argpos_depsubcat","ArgPOS+DepSubCat",sentences.get(1),false,new String[]{"V"},false);
    }
    @Test
    public void predLemmaPredWord(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("predlemma_predword","PredLemma+PredWord",sentences.get(1),true,new String[]{"N","V"},true);
    }
    @Test
    public void deprelPathPosition(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("deprelpath_position","DeprelPath+Position",sentences.get(1),false,new String[]{"V"},false);
    }
    
    //QLinearSetFeature
    @Test
    public void childDepSetPredWord(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("childdepset_predword","ChildDepSet+PredWord",sentences.get(1),true,new String[]{"N","V"},false);
    }
    @Test
    public void argPOSChildPOSSet(){
    	Language.setLanguage(L.eng);
    	assertCorrectOutput("argpos_childdepset","ArgPOS+ChildDepSet",sentences.get(1),false,new String[]{"N","V"},false);
    }
    @Test
    public void positionPredFeats(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("position_predfeats","Position+PredFeats",sentences.get(2),false,new String[]{"N","V"},false);
    }
    @Test
    public void argFeatsPosition(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("argfeats_position","ArgFeats+Position",sentences.get(2),false,new String[]{"N","V"},false);
    }
    @Test
    public void depSubCatPredParentFeats(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("depsubcat_predparentfeats","DepSubCat+PredParentFeats",sentences.get(3),true,new String[]{""},false);
    }
    
    
    @Test
    public void argFeatsLeftPOS(){
    	Language.setLanguage(L.ger);
    	assertCorrectOutput("argfeats_leftpos","ArgFeats+LeftPOS",sentences.get(3),false,new String[]{"V"},false);
    }
    
    private void assertCorrectOutput(String testName,String featureName,Sentence s,boolean includeAllWords,String[] POSPrefixes,boolean withIndex) {
        dumpFeatures(featureName,s,includeAllWords,POSPrefixes,withIndex);
        assertOutput(new File(RESULT_DIR + testName + RESULT_EXTENSION));
    }

	private static void dumpFeatures(String featureName,Sentence s,boolean includeAllWords,String[] POSPrefixes,boolean withIndex) {
		FeatureGenerator fg=new FeatureGenerator();
		if(POSPrefixes==null || POSPrefixes.length==0)
			throw new Error("No POSPrefixes were specified. Check your implementation.");
		Feature feature=null;
		if(featureName.contains("+")){
			String[] f=featureName.split("\\+");
			FeatureName[] fn=new FeatureName[]{FeatureName.valueOf(f[0]),FeatureName.valueOf(f[1])};
			for(String prefix:POSPrefixes)
				feature=fg.getQFeature(fn[0],fn[1], includeAllWords, prefix);
		} else {
			for(String prefix:POSPrefixes)
				feature=fg.getFeature(FeatureName.valueOf(featureName), includeAllWords, prefix);
		}
		feature.extractFeatures(s, false);
		feature.setDoneWithPredFeatureExtraction();
		if(includeAllWords)
			feature.extractFeatures(s, includeAllWords);
		System.out.println(feature);
		System.out.println("Size small/big: "+feature.size(false)+"/"+feature.size(true));
		Map<String,Integer> map=feature.getMap();
		String[] keys=map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		for(String key:keys){
			System.out.print(key);
			if(withIndex)
				System.out.print(" - "+map.get(key));
			System.out.println();
		}
	}

    
	public static void main(String[] args){
		Language.setLanguage(L.ger); //For feats-features, use german. Shouldn't make a difference otherwise.
		if(args.length<3){
			System.out.println("Not enough arguments, aborting.");
			System.exit(1);
		}
		String featureName=args[0];
		int sentence=Integer.parseInt(args[1]);
		boolean includeAllWords=Boolean.parseBoolean(args[2]);
		String[] POSPrefixes;
		if(args.length>3){
			POSPrefixes=new String[args.length-3];
			for(int i=3;i<args.length;++i){
				POSPrefixes[i-3]=args[i];
			}
		} else {
			POSPrefixes=new String[]{""};
		}
		dumpFeatures(featureName,sentences.get(sentence),includeAllWords,POSPrefixes,false);
	}
	
}
