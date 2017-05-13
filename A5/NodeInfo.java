package cs486.artificial.inteligence;

import javax.xml.soap.Node;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 2017-04-02.
 */
public class NodeInfo{

    public Map<String, String> infoRecord;
    public boolean healthy;

    public NodeInfo(String record){
        infoRecord = new HashMap<String, String>();
        record = record.substring(0, record.length()-1);
        String[] recordList = record.split(",");
        infoRecord.put("k", recordList[0]);
        infoRecord.put("Na", recordList[1]);
        infoRecord.put("Cl", recordList[2]);
        infoRecord.put("HCO3", recordList[3]);
        infoRecord.put("endotoxin", recordList[4]);
        infoRecord.put("aniongap", recordList[5]);
        infoRecord.put("PLA2", recordList[6]);
        infoRecord.put("SDH", recordList[7]);
        infoRecord.put("GLDH", recordList[8]);
        infoRecord.put("TPP", recordList[9]);
        infoRecord.put("breathRate", recordList[10]);
        infoRecord.put("PCV", recordList[11]);
        infoRecord.put("pulseRate", recordList[12]);
        infoRecord.put("fibrinogen", recordList[13]);
        infoRecord.put("dimer", recordList[14]);
        infoRecord.put("fibPerDim", recordList[15]);

        if(recordList[16].equals("healthy")){
            this.healthy = true;
        } else {
            this.healthy = false;
        }
    }

}
