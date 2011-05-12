/**
 * 
 */
package is2.data;

import rt.model.Graph;

/**
 * @author Dr. Bernd Bohnet, 28.04.2011
 * 
 * Human-Friendly graph (hfg) format of the shared task on surface realization.
 */
public class HFG {

	// the sematnic graph
	public Graph semGraph;
	
	// the sentence id as provided in the semantic graph
	public int sentId;
	
	// the corresponding sentence
	public String sentence;

	public HFG(){
		semGraph = new Graph();
	}
	
	
}
