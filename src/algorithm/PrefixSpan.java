package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import utils.ReadFile;

/**
 * PrefixSpan algorithm
 * @author Raúl Moya Reyes <raulmoya.es>
 * @author Agustín Ruiz Linares <agustruiz.es>
 */
public class PrefixSpan {

    /*
     * Extraoficial Lanzar expeccion si se salen min_sup y max_pat
     */
    ///Minimun support
    private final int minSup;
    ///Max pattern size
    private final int maxPat;
    ///Patterns
    private final List<Integer> pattern;
    // Map of highest pattern counts
    private final HashMap<String, Integer[]> hm;

    /**
     * Class constructor
     * @param minSup Minimun support
     * @param maxPat Max pattern size
     */
    public PrefixSpan(int minSup, int maxPat, HashMap<String, Integer[]> hm) {
        this.minSup = minSup;
        this.maxPat = maxPat;
        this.hm = hm;
        this.pattern = new ArrayList<Integer>();
    }

    /**
     * Read file method
     * @param fileName Path to file
     * @param pairData Pair data
     */
    public void read(String fileName, PairData pairData) {
        String line;
        int id = 0;

        ReadFile readFile = new ReadFile(fileName);

        line = readFile.readLine();
        while (line != null) {
            Transaction transaction = new Transaction(); //transaction.clear();

            for (String item : line.split(" ")) {
                transaction.second.add(Integer.parseInt(item));//itemSets.add(Integer.parseInt(item));
            }

            transaction.first = id++;

            pairData.dataBase.add(transaction);
            pairData.indeces.add(0);

            //Next iteration
            line = readFile.readLine();
        }

    }

    public void store_pattern(PairData projected) {
    	
    	String key = "";
    	int i;
    	Integer[] pageCount = new Integer[2];
    	Integer[] maxCount = new Integer[2];
    	
    	// If the pattern is not a sequence of at least 2 items
    	// don't add it to the hashmap
    	if (pattern.size() > 1) {
	    	
    		for (i=0; i<pattern.size()-1; i++) {
    			key += pattern.get(i).toString() + ",";    			
    		}
    		key = key.substring(0, key.length()-1);
    		
    		pageCount[0] = pattern.get(i);
    		pageCount[1] = projected.dataBase.size();
    		
    		maxCount = hm.get(key);
    		if (maxCount != null) {
    			if (pageCount[1] > maxCount[1]) {
    				hm.put(key, pageCount);
    			}
    		} else {
    			hm.put(key, pageCount);
    		}

    		// TODO: What to do when two patterns have the same count?
    		//       Treat them the same?
    		
    	}
    	
    }
    
    /**
     * Print frequent sequential patterns
     * @param projected Pair data
     */
    public void print_pattern(PairData projected) {

        for (Integer it : pattern) {
            System.out.print(it + " ");
        }

        System.out.print("\n( ");
        for (Transaction it : projected.dataBase) {
            System.out.print(it.first + " ");
        }
        System.out.println(") : " + projected.dataBase.size());
    }

    /**
     * Run prefixspan algorithm
     * @param file Path to file
     */
    public void run(String file) {
        PairData pairData = new PairData(); // Data Base
        this.read(file, pairData);
        project(pairData);
    }

    /**
     * Project database
     * @param projected PairData to project
     */
    public void project(PairData projected) {
        if (projected.dataBase.size() < minSup) {
            return;
        }

        //this.print_pattern(projected);
        this.store_pattern(projected);
        
        if (maxPat != 0 && pattern.size() == maxPat) {
            return;
        }

        Map<Integer, Integer> mapItem = new HashMap<Integer, Integer>();
        List<Transaction> dataBase = projected.dataBase;
        for (int i = 0; i < dataBase.size(); i++) {
            List<Integer> itemSet = dataBase.get(i).second;
            for (int iter = projected.indeces.get(i); iter < itemSet.size(); iter++) {
                // ++mapItem[itemSet[iter]]
                if (mapItem.get(itemSet.get(iter)) != null) {
                    mapItem.put(itemSet.get(iter), mapItem.get(itemSet.get(iter)));
                } else {
                    mapItem.put(itemSet.get(iter), 1);
                }
            }
        }

        PairData pairData = new PairData();
        List<Transaction> newDataBase = pairData.dataBase;
        List<Integer> newIndeces = pairData.indeces;

//  for (map<unsigned int, unsigned int>::iterator it_1 = map_item.begin(); it_1 != map_item.end(); it_1++) {
        for (Map.Entry<Integer, Integer> it_1 : mapItem.entrySet()) {
            for (int i = 0; i < dataBase.size(); i++) {
                Transaction transaction = dataBase.get(i);
                List<Integer> itemSet = transaction.second;
                for (int iter = projected.indeces.get(i); iter < itemSet.size(); iter++) {
                    if (Objects.equals(itemSet.get(iter), it_1.getKey())) { 
                        newDataBase.add(transaction);
                        newIndeces.add(iter + 1);
                        break;
                    }
                }
            }

            pattern.add(it_1.getKey()); // Lo mismo que arriba
            project(pairData);
            pattern.remove(pattern.size() - 1); //popback()
            pairData.clear();

        }

    }

}
