
package algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raúl Moya Reyes <raulmoya.es>
 * @author Agustín Ruiz Linares <agustruiz.es>
 */
public class PairData {
    ///Database
    public List<Transaction> dataBase;
    ///Indeces
    public List<Integer> indeces;

    /**
     * Class constructor
     */
    public PairData() {
        this.dataBase = new ArrayList<Transaction>();
        this.indeces = new ArrayList<Integer>();
    }
    
    /**
     * Clean pairdata items
     */
    public void clear(){
        this.dataBase.clear();
        this.indeces.clear();
    }
}
