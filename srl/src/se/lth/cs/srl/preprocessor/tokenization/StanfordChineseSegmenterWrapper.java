package se.lth.cs.srl.preprocessor.tokenization;

import java.io.File;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;

/**
 * This is a wrapper for the Stanford Chinese Segmenter, version 2008-05-21.
 * Note that that very distribution is somewhat weird in the sense that the
 * included sources don't match the binaries. The source doesn't compile,
 * and the binaries obviously have a method CRFClassifier.segmentString(String arg0),
 * which does not appear in the corresponding source. It seems to work though. 
 * 
 * @author anders bjorkelund
 *
 */

public class StanfordChineseSegmenterWrapper implements Tokenizer {

	CRFClassifier classifier;
	/**
	 * Initialize the segmenter
	 * 
	 * @param dataDir this is the 'datadir' from the 2008-05-21 distribution.
	 */
	public StanfordChineseSegmenterWrapper(File dataDir){
		/*
		 * This is pretty much a copy&paste of the SegDemo.java, with minor edits on the files.
		 * No idea if this is the fastest or best way to do this.
		 */
	    Properties props = new Properties();
	    //props.setProperty("sighanCorporaDict", "data");
	    props.setProperty("sighanCorporaDict", dataDir.toString());
	    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
	    // props.setProperty("normTableEncoding", "UTF-8");
	    // below is needed because CTBSegDocumentIteratorFactory accesses it
	    //props.setProperty("serDictionary","data/dict-chris6.ser.gz");
	    props.setProperty("serDictionary",new File(dataDir,"dict-chris6.ser.gz").toString());
	    //props.setProperty("testFile", args[0]);
	    props.setProperty("inputEncoding", "UTF-8");
	    props.setProperty("sighanPostProcessing", "true");

	    classifier = new CRFClassifier(props);
	    //classifier.loadClassifierNoExceptions("data/ctb.gz", props);
	    classifier.loadClassifierNoExceptions(new File(dataDir,"ctb.gz").toString(), props);
	    // flags must be re-set after data is loaded
	    classifier.flags.setProperties(props);
	    //classifier.writeAnswers(classifier.test(args[0]));
	    //classifier.testAndWriteAnswers(args[0]);
	}
	
	@Override
	public String[] tokenize(String sentence) {
		return (String[]) classifier.segmentString(sentence).toArray();
	}

/*
 * Used this to figure out how to invoke the segmenter. I'll leave ith ere for future reference.
 * It's based on the SegDemo.java class provided with the segmenter dist.
 * 

	public static void main(String[] args) throws Exception{
		args=new String[]{"chi-sen.deseg"};		
	    Properties props = new Properties();
	    //props.setProperty("sighanCorporaDict", "data");
	    props.setProperty("sighanCorporaDict", "/home/anders/Download/stanford-chinese-segmenter-2008-05-21/data");
	    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
	    // props.setProperty("normTableEncoding", "UTF-8");
	    // below is needed because CTBSegDocumentIteratorFactory accesses it
	    //props.setProperty("serDictionary","data/dict-chris6.ser.gz");
	    props.setProperty("serDictionary","/home/anders/Download/stanford-chinese-segmenter-2008-05-21/data/dict-chris6.ser.gz");
	    //props.setProperty("testFile", args[0]);
	    props.setProperty("inputEncoding", "UTF-8");
	    props.setProperty("sighanPostProcessing", "true");

	    CRFClassifier classifier = new CRFClassifier(props);
	    //classifier.loadClassifierNoExceptions("data/ctb.gz", props);
	    classifier.loadClassifierNoExceptions("/home/anders/Download/stanford-chinese-segmenter-2008-05-21/data/ctb.gz", props);
	    // flags must be re-set after data is loaded
	    classifier.flags.setProperties(props);
	    //classifier.writeAnswers(classifier.test(args[0]));
	    //classifier.testAndWriteAnswers(args[0]);
	    
	    //ObjectBank<List<CoreLabel>> documents = classifier.makeObjectBank(args[0]);
	    List<String> forms=classifier.segmentString("上海浦东近年来颁布实行了涉及经济、贸易、建设、规划、科技、文教等领域的七十一件法规性文件，确保了浦东开发的有序进行。");
	    for(String form:forms)
	    	System.out.println(form);
	}*/
	
}
