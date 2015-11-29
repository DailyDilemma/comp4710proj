import java.util.HashMap;
import java.util.Map.Entry;

import algorithm.PrefixSpan;
import utils.ReadFile;

/**
 * Main class
 *
 * @author Raúl Moya Reyes <raulmoya.es>
 * @author Agustín Ruiz Linares <agustruiz.es>
 */
public class Main {

    ///Params path
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        HashMap<String, Integer[]> hm = new HashMap<>();
    	
        //Read params
        String paramsPath = "./params.txt";
        ReadFile rf = new ReadFile((paramsPath));
        String filePath = rf.readLine();
        int min_sup = Integer.parseInt(rf.readLine());
        int max_pat = Integer.parseInt(rf.readLine());
        
        //Execute algorithm
        PrefixSpan a = new PrefixSpan(min_sup, max_pat, hm);
        a.run(filePath);
        
        printHashMap(hm);
        
    }

    private static void printHashMap(HashMap<String, Integer[]> hm) {    	
    	
    	System.out.println("{initial sequence} -> [was followed by] (number of times)");
    	
    	for (Entry<String, Integer[]> entry : hm.entrySet()) {    	    
    		
    		System.out.print("{" + entry.getKey());
    		System.out.print("} -> [" + entry.getValue()[0] + "]");
    		System.out.print(" (");
    		System.out.println(entry.getValue()[1].toString() + ")");
    		
    	}
    	
    }

}
