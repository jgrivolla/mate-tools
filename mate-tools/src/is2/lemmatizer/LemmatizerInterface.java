/**
 * 
 */
package is2.lemmatizer;

/**
 * @author Dr. Bernd Bohnet, 16.09.2010
 * 
 * 
 */
public interface LemmatizerInterface {

	/**
	 * The method computes an array of word lemmata from the input forms. 
	 * The first element in the array should contain the root token: 
	 * forms[0] = CONLLReader09.ROOT;  
	 * 
	 * @param forms array of word forms
	 * @return lemmata
	 */
	public abstract String[] getLemmas(String[] forms);

}