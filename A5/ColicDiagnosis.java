package cs486.artificial.inteligence;

import cs486.artificial.inteligence.DecisionTreeNode;
import cs486.artificial.inteligence.NodeInfo;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColicDiagnosis {
    public static DecisionTreeNode root = new DecisionTreeNode();

    private double entropy;

    public ColicDiagnosis() {

    }

    public void calculateEntropy(DecisionTreeNode root){
        int size = root.nodeList.size();
        int healthyNum = 0;

        for(int i = 0; i < size ; i++) {
            if(root.nodeList.get(i).healthy == true) healthyNum ++;
        }

        int colicNum = size - healthyNum;

        double first = -((double)healthyNum/(double)size)*(Math.log((double)healthyNum/(double)size)/Math.log(2));
        double second = -((double)colicNum/(double)size)*(Math.log((double)colicNum/(double)size)/Math.log(2));
        entropy =  first + second;
        System.out.println("entropy: " + entropy);

    }

    public void buildTree(DecisionTreeNode root) {
        List<String> unusedAttribute = new ArrayList<>();
        unusedAttribute = root.unusedAttribute;

        if(unusedAttribute.size() == 0) {
            //set result to the remaining record.
            if(root.nodeList.size() == 0) {
                //set record to colid
                root.result = false;
            } else {
                root.result = root.nodeList.get(0).healthy;
            }
            return;
        }

        int size = root.nodeList.size();
        int healthyNum = 0;
        for(int i = 0; i < size ; i++) {
            if(root.nodeList.get(i).healthy == true) healthyNum ++;
        }
        int colidNum = size - healthyNum;

        //Leave node: do not split if all records have the same output value
        if(healthyNum == size || (healthyNum == 1 && colidNum == 0) ){
            //set result to healthy
            root.result = true;
            return;
        }
        if(colidNum == size || (healthyNum == 0 && colidNum == 1)){
            //set result to colic
            root.result = false;
            return;
        }

        if(healthyNum == 0 && colidNum == 1) {
            root.result = false;
            return;
        }

        //if not at leave node, split again
        //calculate threshold for each remaining attribute
        Map<String, Double> thresholdMap = new HashMap<>();
        for(int i = 0; i < unusedAttribute.size(); i++) {
            String attribute = unusedAttribute.get(i);
            //decide threshold using attribute
            double threshold = root.computeThreshold(attribute);
            thresholdMap.put(attribute, threshold);
            //System.out.println(threshold);
        }

        String maxAttribute = "";
        double splitThres = 0.0;
        double maxInfoGain = 0.0;

        //determine the maximum information gain
        for(String attribute: thresholdMap.keySet()) {
            double threshold = thresholdMap.get(attribute);

            //gather information for calculating conditional entropy
            int aboveThres = 0;
            int belowThres = 0;
            int healthyForAboveThres = 0;
            int colicForAboveThres = 0;
            int healthyForBelowThres = 0;
            int colicForBelowThres = 0;

            for(int i = 0; i < root.nodeList.size(); i++) {
                if(Double.parseDouble(root.nodeList.get(i).infoRecord.get(attribute)) > threshold) {
                    aboveThres++;
                    if(root.nodeList.get(i).healthy){
                        healthyForAboveThres++;
                    } else {
                        colicForAboveThres++;
                    }
                } else {
                    belowThres ++;
                    if(root.nodeList.get(i).healthy){
                        healthyForBelowThres++;
                    } else {
                        colicForBelowThres++;
                    }
                }
            }

            //calculate conditional entropy
            double aboveThresProb = - (double) aboveThres / (double) size;
            double belowThresProb = - (double) belowThres / (double) size;

            double healthyForAboveThresProb = (double)healthyForAboveThres/(double)aboveThres;
            double colicForAboveThresProb = (double)colicForAboveThres/(double)aboveThres;
            double healthyForBelowThresProb = (double) healthyForBelowThres/(double) belowThres;
            double colidForBelowThresProb = (double) colicForBelowThres/(double) belowThres;

            double a, b;
            if(healthyForAboveThresProb == 0.0 && colicForAboveThresProb == 0.0) {
                a = 0.0;
            } else if(healthyForAboveThresProb == 0.0) {
                a = aboveThresProb * (colicForAboveThresProb * Math.log(colicForAboveThresProb)/Math.log(2.0));
            } else if (colicForAboveThresProb == 0.0) {
                a = aboveThresProb * (healthyForAboveThresProb * Math.log(healthyForAboveThresProb)/ Math.log(2.0));
            } else {
                a = aboveThresProb * (healthyForAboveThresProb * Math.log(healthyForAboveThresProb)/ Math.log(2.0)
                        + colicForAboveThresProb * Math.log(colicForAboveThresProb)/Math.log(2.0));
            }

            if(healthyForBelowThresProb == 0.0 && colidForBelowThresProb == 0.0) {
                b = 0.0;
            } else if(healthyForBelowThresProb == 0.0) {
                b = belowThresProb * (colidForBelowThresProb * Math.log(colidForBelowThresProb)/Math.log(2.0));
            } else if (colidForBelowThresProb == 0.0) {
                b = belowThresProb * (healthyForBelowThresProb * Math.log(healthyForBelowThresProb)/Math.log(2.0));
            } else {
                b = + belowThresProb * (healthyForBelowThresProb * Math.log(healthyForBelowThresProb)/Math.log(2.0)
                        + colidForBelowThresProb * Math.log(colidForBelowThresProb)/Math.log(2.0));
            }


            //double conditionalEntropy = aboveThresProb * (healthyForAboveThresProb * Math.log(healthyForAboveThresProb)/ Math.log(2.0)
            //                                            + colicForAboveThresProb * Math.log(colicForAboveThresProb)/Math.log(2.0))
            //                            + belowThresProb * (healthyForBelowThresProb * Math.log(healthyForBelowThresProb)/Math.log(2.0)
            //                                            + colidForBelowThresProb * Math.log(colidForBelowThresProb)/Math.log(2.0));

            double conditionalEntropy = a + b;

            if(maxInfoGain < (entropy - conditionalEntropy)) {
                maxInfoGain = entropy - conditionalEntropy;
                splitThres = threshold;
                maxAttribute = attribute;
            }
        }

        //found max info gain, use info to set root
        if(maxInfoGain == 0.0) {
            root.splitAttribute = unusedAttribute.get(0);
            root.threshold = thresholdMap.get(root.splitAttribute);
        } else {
            root.splitAttribute = maxAttribute;
            root.threshold = splitThres;
        }

        //split left and right tree using attribute
        DecisionTreeNode leftNode = new DecisionTreeNode();
        DecisionTreeNode rightNode = new DecisionTreeNode();
        for(int i = 0; i < root.nodeList.size(); i++) {
            if(Double.parseDouble(root.nodeList.get(i).infoRecord.get(root.splitAttribute)) > root.threshold){
                leftNode.nodeList.add(root.nodeList.get(i));
            } else {
                rightNode.nodeList.add(root.nodeList.get(i));
            }
        }

        System.out.println("split attribute: " + root.splitAttribute + ": with thres: " + root.threshold + ": info gain: " + maxInfoGain);

        root.left = leftNode;
        root.right = rightNode;

        unusedAttribute.remove(root.splitAttribute);

        root.left.unusedAttribute = unusedAttribute;
        root.right.unusedAttribute = unusedAttribute;
        //recurse
        buildTree(root.left);
        buildTree(root.right);

        return;
    }

    public static void main(String[] args) {
        // write your code here

        String folderPath = "horseTrain.txt";
        String testPath = "horseTest.txt";
        ColicDiagnosis colicDiagnosis = new ColicDiagnosis();

        try (BufferedReader reader = new BufferedReader(new FileReader(folderPath))) {

            String line = reader.readLine();

            while (line != null) {
                NodeInfo node = new NodeInfo(line);
                root.nodeList.add(node);

                line = reader.readLine();
            }

            colicDiagnosis.calculateEntropy(root);
            colicDiagnosis.buildTree(root);

        } catch (IOException e) {
            System.out.println("Unable to read file.");
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(testPath))) {

            String line = reader.readLine();

            List<NodeInfo> nodeList = new ArrayList<>();
            while (line != null) {
                NodeInfo node = new NodeInfo(line);
                nodeList.add(node);

                boolean result = colicDiagnosis.traverseTree(node.infoRecord, root);
                if(result) {
                    System.out.println("healthy");

                } else {
                    System.out.println("colic");

                }

                line = reader.readLine();
            }



        } catch (IOException e) {
            System.out.println("Unable to read file.");
            e.printStackTrace();
        }

    }

    public boolean traverseTree(Map<String, String> infoRecord, DecisionTreeNode root) {
        if(root.right == null && root.left == null) return root.result;

        String attribute = root.splitAttribute;
        double thres = root.threshold;
        if(Double.parseDouble(infoRecord.get(attribute)) > thres) {
            return traverseTree(infoRecord, root.left);
        } else {
            return traverseTree(infoRecord, root.right);
        }
    }
}
