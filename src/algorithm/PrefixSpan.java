package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

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
    private final HashMap<String, Integer[]> frequentSequenceMap;

    /**
     * Class constructor
     * @param minSup Minimum support
     * @param maxPat Max pattern size
     */
    public PrefixSpan(int minSup, int maxPat, HashMap<String, Integer[]> frequentSequenceMap) {
        this.minSup = minSup;
        this.maxPat = maxPat;
        this.frequentSequenceMap = frequentSequenceMap;
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
    		
    		maxCount = frequentSequenceMap.get(key);
    		if (maxCount != null) {
    			if (pageCount[1] > maxCount[1]) {
    				frequentSequenceMap.put(key, pageCount);
    			}
    		} else {
    			frequentSequenceMap.put(key, pageCount);
    		}

    		// TODO: What to do when two patterns have the same count?
    		//       Treat them the same?
    		
    	}	
    }
    
    /****************************************
     * Uses frequent sequential patterns found using PrefixSpan to
     * make predictions on the next item that will be seen in a given sequence 
     * in a transaction. The prediction is made on the items that have been seen
     * in the transaction so far until a frequent pattern has been found. If no
     * frequent pattern is found within any subset of the items seen thus far,
     * assume an incorrect prediction.
     * 
     * @param projected		
     * 			contains database of transactions to compare against predictions
     * @param firstIndex
     * 			index of the first transaction in the database to analyze
     */
    public void predict(PairData projected, int firstIndex) {
    	int totalPredictions = 0;
    	int correctPredictions = 0;
    	
    	// Start prediction and analysis at transaction firstIndex
        List<Transaction> dataBase = projected.dataBase;
        for (int i = firstIndex; i < dataBase.size(); i++) {
            List<Integer> itemSet = dataBase.get(i).second;
            
            // Place first element in transaction in itemsSeen
            StringJoiner itemsSeen = new StringJoiner(",");
            itemsSeen.add(String.valueOf(itemSet.get(0)));
            
            // Predict value for each subsequent item in transaction based on sequential patterns
            for (int j=1; j < itemSet.size(); j++) {
            	Integer[] value = frequentSequenceMap.get(itemsSeen.toString());
            	String tempItemsSeen = itemsSeen.toString();
            	
            	// If items seen so far is not a key in the hashmap, then parse off the first element in sequence
            	while (value == null) {
            		int firstSplit = tempItemsSeen.indexOf(",");
            		if (firstSplit == -1) {
            			break;
            		}
            		tempItemsSeen = tempItemsSeen.substring(firstSplit + 1);
            		value = frequentSequenceMap.get(tempItemsSeen);
            	}
            	
            	// Predict based on value stored in hashmap for sequence seen so far
            	totalPredictions++;
            	if (value != null && value[0] == itemSet.get(j)) { // If value in hashmap matches next value in transaction, then successful prediction
            		correctPredictions++;
            	}
            	itemsSeen.add(String.valueOf(itemSet.get(j)));
            }
        }
        
    	System.out.println("\nPredicting Next Element Based on Mined Sequential Patterns");
    	System.out.println("------------------------------------------------------------");
        System.out.println("Total Predictions Made: " + totalPredictions);
        System.out.println("Correct Predictions: " + correctPredictions);
        System.out.println("Incorrect Predictions: " + (totalPredictions - correctPredictions));
    }
    
    
    /**
     * Print frequent sequential patterns
     * @param projected Pair data
     */
    public void print_pattern(PairData projected, int numToProcess) {
    	if (pattern.size() == 0) {
    		System.out.print("Total Number of Transactions Processed");
    	}

    	StringJoiner patternString = new StringJoiner(" -> ");
        for (Integer it : pattern) {
            patternString.add(String.valueOf(it));
        }
        System.out.print(patternString.toString());

//        System.out.print("\n( ");
//        for (int i = 0; i < projected.dataBase.size() && i < numToProcess; i++) {
//            System.out.print(projected.dataBase.get(i).first + " ");
//        }
        int support = projected.dataBase.size() > numToProcess ? numToProcess : projected.dataBase.size();
        System.out.println(" : " + support);
    }

    /**
     * Run prefixspan algorithm
     * @param file Path to file
     */
    public void run(String file, int numTrainingData) {
        PairData pairData = new PairData(); // Data Base
        this.read(file, pairData);
        project(pairData, numTrainingData);
        predict(pairData, numTrainingData);
    }

    /**
     * Project database
     * @param projected PairData to project
     */
    public void project(PairData projected, int numToProcess) {
        if (projected.dataBase.size() < minSup) {
            return;
        }

        this.print_pattern(projected, numToProcess);
        this.store_pattern(projected);
        
        if (maxPat != 0 && pattern.size() == maxPat) {
            return;
        }

        Map<Integer, Integer> mapItem = new HashMap<Integer, Integer>();
        List<Transaction> dataBase = projected.dataBase;
        for (int i = 0; i < dataBase.size() && i < numToProcess; i++) {
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
            for (int i = 0; i < dataBase.size() && i < numToProcess; i++) {
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
            project(pairData, numToProcess);
            pattern.remove(pattern.size() - 1); //popback()
            pairData.clear();

        }

    }

}
