package cs486.artificial.inteligence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mac on 2017-04-02.
 */
public class DecisionTreeNode {
    public List<NodeInfo> nodeList;

    public boolean result;
    public String splitAttribute = "";
    public double threshold = 0.0;
    public DecisionTreeNode left = null;
    public DecisionTreeNode right = null;
    public List<String> unusedAttribute;

    public DecisionTreeNode() {
        nodeList = new ArrayList<>();
        unusedAttribute = new ArrayList<>();
        unusedAttribute.add("k");
        unusedAttribute.add("Na");
        unusedAttribute.add("Cl");
        unusedAttribute.add("HCO3");
        unusedAttribute.add("endotoxin");
        unusedAttribute.add("aniongap");
        unusedAttribute.add("PLA2");
        unusedAttribute.add("SDH");
        unusedAttribute.add("GLDH");
        unusedAttribute.add("TPP");
        unusedAttribute.add("breathRate");
        unusedAttribute.add("PCV");
        unusedAttribute.add("pulseRate");
        unusedAttribute.add("fibrinogen");
        unusedAttribute.add("dimer");
        unusedAttribute.add("fibPerDim");
    }

    public double computeThreshold(String attribute){
        List<Double> orderedList = new ArrayList<>();

        for(int i = 0; i < nodeList.size(); i++) {
            orderedList.add(Double.parseDouble(nodeList.get(i).infoRecord.get(attribute)));
        }

        Collections.sort(orderedList);
        return orderedList.get(orderedList.size()/2);

    }

}
