/**
 * 
 */
package is2.io;

import is2.data.PSTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Dr. Bernd Bohnet, 17.01.2011
 * 
 * Reads a sentences in Penn Tree Bank bracket style and return sentences.
 */
public class PennTreeReader implements PSReader {

	BufferedReader inputReader;
	ArrayList<File> psFiles = new ArrayList<File>();
	ArrayList<PSTree> psCache = new ArrayList<PSTree>();

	public PennTreeReader() {}

	public PennTreeReader(String file ) {

		try {
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"),32768);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param ps
	 */
	public void startReading(String ps, String[] filter) {

		File dir =new File(ps);
		File[] secontions = dir.listFiles();
		for(File f : secontions) {
			File[] files = f.listFiles();
			if (files==null) continue;
		
			for(File f2 : files) {
				boolean doFilter =false;
				for(String filt : filter) {
					if (f2.getPath().contains(filt)) {
						doFilter=true;
						break;
					}
				}
				
				if (doFilter) continue; 
				psFiles.add(f2);
			}

		}


	}

	
	/**
	 * @return
	 */
	public PSTree getNext() {

		if (!psCache.isEmpty()) return psCache.remove(0);

		// analyse next file

		if (psFiles.isEmpty()) return null;

		File f = psFiles.remove(0);

		try {
			if (inputReader!=null)inputReader.close();
//			System.out.println("read new file "+f);
			inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)),32768);

			String line =null;
			PSTree ps = new PSTree();
			ArrayList<Object> current =  new ArrayList<Object>();
			Stack<ArrayList<Object>> stack = new Stack<ArrayList<Object>>();
			StringBuffer token =new StringBuffer();
			int state = 0;
			final int TOKEN =1;
			while((line = inputReader.readLine())!=null) {

				for(int i =0;i<line.length();i++) {
					if (line.charAt(i)=='(') {
						ArrayList<Object> last = current;
						stack.push(current);
						current= new ArrayList<Object>();
						last.add(current);
					} else if (line.charAt(i)==')') {
						if (state==TOKEN) {
							state =0;
							current.add(token.toString());
							token.setLength(0);
						}
						current =stack.pop();
					} else if ((Character.isWhitespace(line.charAt(i))||line.charAt(i)==' ') && state==TOKEN) {
						current.add(token.toString());
						token.setLength(0);
						state=0;
					} else if ( state==0 && !(line.charAt(i)==' ')) {
						state=TOKEN;
						token.append(line.charAt(i));
					} else if (state==TOKEN) {
						token.append(line.charAt(i));						
					}
				}
			}

			
			// one file might contain more than one ps-tree
			for(int k=0;k<current.size();k++) {

				ArrayList<Object> tree = (ArrayList<Object>)current.get(k);

				// remove traces etc. and empty elements -NONE-
				removeTraces(tree);

				ps = new PSTree();

				int terminals = countTerminals(tree);
				int nonTerminals = countNonTerminals(tree);

				ps.create(terminals+1,nonTerminals);
				
//				System.out.println("terminals "+terminals+" non terminals "+ nonTerminals);

				//	System.out.println("size "+current.size()+" "+current.get(0));

				// 0 is root
				this.insert(ps, tree, 1, nonTerminals+1, 0);


		//		System.out.println(""+ps.toString());
	
				if (ps.containsNull()){
					System.out.println("contain error "+f);
					System.exit(0);
				}
				this.psCache.add(ps);
			//	if ( count++ >4) System.exit(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		if (!psCache.isEmpty()) return psCache.remove(0);
		else return null;
	}

	/**
	 * @param tree
	 */
	private void removeTraces(ArrayList<Object> tree) {

		Stack<ArrayList<Object>> s = new Stack<ArrayList<Object>>();

		s.push(tree);
		ArrayList<Object> list =null;
		while (!s.isEmpty()) {
			
			ArrayList<Object> last =list;
			list = s.pop();
			for(int k=0;k<list.size();k++) {
				Object o = list.get(k);
				if(o instanceof String) {
					String t = (String)o;
					if ((t.endsWith("-1")||t.endsWith("-2")||t.endsWith("-3")||t.endsWith("-4")) && list.size()>(k+1)) {
						t = t.substring(0, t.length()-2);
						list.set(k, t);
					}			
					
					if (t.startsWith("-NONE-")) {
						
						// remove the bigger surrounding phrase, e.g. (NP (-NONE- *))
						if (last.size()==2 && last.get(0) instanceof String && last.contains(list)) {
							ArrayList<Object> rest = remove(tree, last);
							if (rest!=null && rest.size()==1){
								rest = remove(tree, rest);
							}
						}
						// remove the phrase only, e.g. (NP (AP nice small) (-NONE- *))
						else {
							// there might a phrase with two empty elements (VP (-NONE- *) (-NONE- ...))
//							System.out.println("last "+last+" list "+list );
							ArrayList<Object> rest = remove(tree, list);
							removeTraces(rest);
							if (rest.size()==1) {
								rest = remove(tree, rest);
								if (rest!=null && rest.size()==1){
									System.out.println("rest "+rest);
									System.exit(0);
								}
 							}
						}
						continue;
					}
				}
				if (o instanceof ArrayList) {
					s.push((ArrayList<Object>)o);				
				}
			}
		}
	}
	
	
	

	/**
	 * Remove from tree p
	 * @param tree phrase structure tree
	 * @param p elment to remove
	 */
	private ArrayList<Object> remove(ArrayList<Object> tree, Object p) {
		Stack<ArrayList<Object>> s = new Stack<ArrayList<Object>>();

		s.push(tree);

		while (!s.isEmpty()) {
			
			ArrayList<Object> list = s.pop();
			for(int k=0;k<list.size();k++) {
				Object o = list.get(k);
				if (o == p) {
					list.remove(p);
					return list ;
				}
				if (o instanceof ArrayList) {
					s.push((ArrayList<Object>)o);				
				}
			}
		}
		return null;
	}

	/**
	 * Count the terminals
	 * @param current
	 * @return
	 */
	private int countTerminals(ArrayList<Object> current) {

		int count =0;
		boolean found =false, all =true ;
		for(Object o : current) {
			if (o instanceof String) found =true;
			else { 
				all =false;
				if (o instanceof ArrayList) count +=countTerminals((ArrayList<Object>)o); 
			}
		}

		if (found && all) {
			//		System.out.println(""+current);
			count++;
		}

		return count;
	}

	/**
	 * Count the terminals
	 * @param current
	 * @return
	 */
	private int insert(PSTree ps, ArrayList<Object> current, Integer terminal, Integer xxx, int head) {

		boolean found =false, all =true;
		String term =null;
		String pos =null;
		for(Object o : current) {
			if (o instanceof String) {
				if (found) term =(String)o;
				if (!found) pos =(String)o;
				found =true;	
			} else { 
				all =false;
				//				if (o instanceof ArrayList) count +=countTerminals((ArrayList<Object>)o); 
			}
		}

		if (found && all) {
			
			if(term.equals("-LRB-")) term="(";
			if(term.equals("-RRB-")) term=")";
			if(term.equals("-LCB-"))  term="{";
			if(term.equals("-RCB-"))  term="}";
			if(term.contains("1\\/2-year"))  term=term.replace("\\/", "/");
			if(term.contains("1\\/2-foot-tall"))  term=term.replace("\\/", "/");

			
			ps.entries[ps.terminal] =term;
			ps.pos[ps.terminal]=pos;
			ps.head[ps.terminal]=head;
			//	System.out.println("terminal "+term+" "+ps.terminal+" head "+head);
			ps.terminal  ++;
		} else if (found && ! all) {
			if(pos.startsWith("NP-SBJ")) pos="NP-SBJ";
			if(pos.startsWith("WHNP")) pos="WHNP";

			ps.entries[ps.non] =pos;
			ps.head[ps.non]=head;
			//	System.out.println("non terminal "+pos+" "+ps.non+" head "+	head);
			int non =ps.non ++;

			for (Object o : current) {
				if (o instanceof ArrayList) {
					insert(ps,(ArrayList<Object>)o,terminal,ps.non, non);		
				}
			}
		}	
		if(!all  && !found)for (Object o : current) {
			if (o instanceof ArrayList) {
				insert(ps,(ArrayList<Object>)o,terminal,0, ps.non-1);		
			}
		}
		
		return terminal;
	}


	/**
	 * Count the terminals
	 * @param current
	 * @return
	 */
	private int countNonTerminals(ArrayList<Object> current) {

		int count =0;
		boolean found =false, all =true ;
		for(Object o : current) {
			if (o instanceof String) found =true;
			else { 
				all =false;
				if (o instanceof ArrayList) count +=countNonTerminals((ArrayList<Object>)o); 
			}
		}

		if (found && !all) count++;

		return count;
	}


}
