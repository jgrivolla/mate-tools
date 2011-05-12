/**
 * 
 */
package is2.io;

import is2.data.HFG;
import is2.data.PSTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

import rt.model.Environment;
import rt.model.Graph;
import rt.util.DB;

/**
 * @author Dr. Bernd Bohnet, 17.01.2011
 * 
 * Reads a sentences in Penn Tree Bank bracket style and return sentences.
 */
public class HFGReader {

	BufferedReader inputReader;

	public HFGReader() {}

	public HFGReader(String file ) {

		try {
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"),32768);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HFG getNext() {
 		
		String line =null;
		try {
			
			HFG hfg = new HFG();
			
			// read the sentence id
			
			line=inputReader.readLine();
			if (line ==null) return null;			
			hfg.sentId=Integer.parseInt(line.split("=")[1]);
			
			
			ArrayList<String> nodes = new ArrayList<String>(); 
			// read the graph
			while((line=inputReader.readLine())!=null ) {
				
				if (line.isEmpty()) break;				
				nodes.add(line);
				hfg.semGraph.createNode();
			}

			// helps to build graphs
			Environment env = new Environment(hfg.semGraph);

			// create the nodes
			int n=0;
			
			// create root node
			env.createNode("ROOT", Graph.NODE);

			// create all other nodes
			for(String l : nodes) {
				String content[] = l.split("\t");
				
				//env.content(arg0);
			}
			
			
			
		} catch (IOException e) {
			
			DB.println("Error while reading hfg-input-format !!!");
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;
	}

	

}
