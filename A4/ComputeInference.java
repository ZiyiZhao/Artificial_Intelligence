package cs486.artificial.inteligence;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComputeInference {
    List<String> valueList = new ArrayList<>();

    public HashMap<String, Double> inference(List<HashMap<String, Double>> factorList,
                                             List<String> queryVariables, List<String> orderedListOfHiddenVariables,
                                             List<String> evidenceList) {

        List<HashMap<String, Double>> working_factorList = factorList;
        //restrict value
        if(evidenceList != null) {

            for(int i = 0; i < factorList.size(); i++) {
                HashMap<String, Double> factor = new HashMap<>();
                for(int j = 0; j < evidenceList.size(); j++) {
                    factor = restrict(factorList.get(i), evidenceList.get(j), valueList.get(j));
                }
                if(factor.size() !=0) {
                    working_factorList.add(factor);
                }
            }
        }

        HashMap<String, Double> productFactor = factorList.get(0);
        for(int i = 1; i < working_factorList.size(); i++) {
            if(i == 5) {
                System.out.println("this is the 5th");
            }
            productFactor = multiply(productFactor, working_factorList.get(i));
        }

        System.out.println("PRINTING Product factor after multiply");
        for(String key: productFactor.keySet()) {
            System.out.println("Key: " + key);
        }

        for(int i = 0; i < orderedListOfHiddenVariables.size(); i++) {
            productFactor = sumout(productFactor, orderedListOfHiddenVariables.get(i));
        }

        productFactor = normalize(productFactor);

        return productFactor;

        //return new HashMap<>();
    }

    public HashMap<String, Double> normalize(HashMap<String, Double> productFactor) {

        System.out.println("This is normalization.");
        double sum = 0.0;
        for(double value : productFactor.values()) {
            sum+=value;
        }
        sum = 1/sum;

        HashMap<String, Double> newFactor = new HashMap<>();

        for(String key: productFactor.keySet()) {
            newFactor.put(key, sum * productFactor.get(key));
        }

        return newFactor;
    }

    public HashMap<String, Double> sumout(HashMap<String, Double> factor, String variable) {

        System.out.println("This is sumout.");

        HashMap<String, Double> newFactor = new HashMap<>();
        for(String key1 : factor.keySet()) {
            if(key1.contains(variable)) {
                //know its positive
                String newKey = key1.replace(variable, "");
                if(newKey.startsWith(",")) {
                    newKey = newKey.substring(1, newKey.length());
                }

                if(newKey.endsWith(",")) {
                    newKey = newKey.substring(0, newKey.length()-1);
                }
                newKey = newKey.replace(",,", ",");

                System.out.println("This is variable: " + variable);
                //System.out.println("Get Previous value: ");
                System.out.println("Key1: " + key1);
                //System.out.println(factor.get(key1.replace(variable, variable.toLowerCase())) );


                newFactor.put(newKey, factor.get(key1.replace(variable, variable.toLowerCase())) + factor.get(key1));
            } else {
                //know its negative
                String newKey = key1.replace(variable.toLowerCase(), "");
                if(newKey.startsWith(",")) {
                    newKey = newKey.substring(1, newKey.length());
                }
                if(newKey.endsWith(",")) {
                    newKey = newKey.substring(0, newKey.length()-1);
                }
                newKey = newKey.replace(",,", ",");
                newFactor.put(newKey, factor.get(key1.replace(variable.toLowerCase(), variable)) + factor.get(key1));

            }
        }

        for(String key: newFactor.keySet()) {
            System.out.println(key + ": " + newFactor.get(key));
        }

        return newFactor;
    }

    public HashMap<String, Double> restrict(HashMap<String, Double> factor, String variable, String value){

        System.out.println("This is restrict.");

        HashMap<String, Double> newFactor = new HashMap<String, Double>();

        for (String key: factor.keySet()) {
            String[] splitedKey= key.split(",");
            for(String individual: splitedKey) {
                if(individual.equals(value)) {
                    String newKey = key.replace(value, "");
                    if(newKey.startsWith(",")) {
                        newKey = newKey.substring(1, newKey.length());
                    }
                    if(newKey.endsWith(",")) {
                        newKey = newKey.substring(0, newKey.length()-1);
                    }
                    newKey = newKey.replace(",,", ",");

                    newFactor.put(newKey, factor.get(key));

                    System.out.println(newKey + ": " + newFactor.get(newKey));

                }
            }
        }

        for(String key: newFactor.keySet()) {
            System.out.println(key + ": " + newFactor.get(key));
        }

        return newFactor;
    }

    public HashMap<String, Double> multiply(HashMap<String, Double> factor1, HashMap<String, Double> factor2) {

        System.out.println("This is multiply.");

        HashMap<String, Double> newFactor = new HashMap<>();
        ArrayList<String> commonVariable = new ArrayList<>();
        for(String key1 : factor1.keySet()) {
            for(String key2: factor2.keySet()) {
                commonVariable = findCommon(key1.toUpperCase(), key2.toUpperCase());
                break;
            }
        }

        if(commonVariable == null) {
            for(String key1 : factor1.keySet()) {
                for (String key2 : factor2.keySet()) {
                    //no common variable
                    newFactor.put(key1 + "," + key2, factor1.get(key1) * factor2.get(key2));
                }
            }
        } else {
            for(String key1 : factor1.keySet()) {
                for (String key2 : factor2.keySet()) {
                    //have common variable

                    ArrayList<String> newCommon = findCommon(key1, key2);

                    if(newCommon != null && newCommon.size() == commonVariable.size()) {

                        String newKey2 = key2;
                        for(int i = 0; i < newCommon.size(); i++) {
                            newKey2 = newKey2.replace(newCommon.get(i), "");
                        }
                        String newCombinedKey = key1 + "," + newKey2;
                        newCombinedKey = newCombinedKey.replace(",,", ",");
                        if(newCombinedKey.endsWith(",")) {
                            newCombinedKey = newCombinedKey.substring(0, newCombinedKey.length()-1);
                        }
                        newFactor.put(newCombinedKey, factor1.get(key1) * factor2.get(key2));
                    }
                }
            }


        }

        for(String key: newFactor.keySet()) {
            System.out.println(key + ": " + newFactor.get(key));
        }

        return newFactor;
    }

    public ArrayList<String> findCommon(String key1, String key2) {
        ArrayList<String> common = new ArrayList<>();

        String[] splitKey1 = key1.split(",");
        String[] splitKey2 = key2.split(",");

        for(int i = 0; i < splitKey1.length; i++) {
            for(int j = 0; j < splitKey2.length; j++) {
                if(splitKey1[i].equals(splitKey2[j])) {
                    common.add(splitKey1[i]);
                }
            }
        }

        if(common.size() == 0) {
            return null;
        }
        return common;
    }

    public ComputeInference() {
	// write your code here
        List<HashMap<String, Double>> factorList = new ArrayList<>();

        //construct factorList
        HashMap<String, Double> factorFB = new HashMap<>();
        factorFB.put("FB,FS", 0.6);
        factorFB.put("FB,fs", 0.1);
        factorFB.put("fb,FS", 0.4);
        factorFB.put("fb,fs", 0.9);
        factorList.add(factorFB);

        HashMap<String, Double> factorFH = new HashMap<>();
        factorFH.put("FH,FS,FM,NDG", 0.99);
        factorFH.put("fh,FS,FM,NDG", 0.01);
        factorFH.put("FH,FS,fm,ndg", 0.5);
        factorFH.put("fh,FS,fm,ndg", 0.5);
        factorFH.put("FH,FS,FM,ndg", 0.9);
        factorFH.put("fh,FS,FM,ndg", 0.1);
        factorFH.put("FH,FS,fm,NDG", 0.75);
        factorFH.put("fh,FS,fm,NDG", 0.25);
        factorFH.put("FH,fs,FM,NDG", 0.65);
        factorFH.put("fh,fs,FM,NDG", 0.35);
        factorFH.put("FH,fs,FM,ndg", 0.4);
        factorFH.put("fh,fs,FM,ndg", 0.6);
        factorFH.put("FH,fs,fm,NDG", 0.2);
        factorFH.put("fh,fs,fm,NDG", 0.8);
        factorFH.put("FH,fs,fm,ndg", 0.0);
        factorFH.put("fh,fs,fm,ndg", 1.0);
        factorList.add(factorFH);

        HashMap<String, Double> factorFS = new HashMap<>();
        factorFS.put("FS", 0.05);
        factorFS.put("fs", 0.95);
        factorList.add(factorFS);



        HashMap<String, Double> factorFM = new HashMap<>();
        factorFM.put("FM", (float) 1.0/28.0);
        factorFM.put("fm", (float) 27.0/28.0);
        factorList.add(factorFM);

        HashMap<String, Double> factorNA = new HashMap<>();
        factorNA.put("NA", (float) 3.0/10.0);
        factorNA.put("na", (float) 7.0/10.0);
        factorList.add(factorNA);

        HashMap<String, Double> factorNDG = new HashMap<>();
        factorNDG.put("NDG,FM,NA", 0.8);
        factorNDG.put("ndg,FM,NA", 0.2);
        factorNDG.put("NDG,fm,NA", 0.5);
        factorNDG.put("NDG,FM,na", 0.4);
        factorNDG.put("ndg,fm,NA", 0.5);
        factorNDG.put("ndg,FM,na", 0.6);
        factorNDG.put("NDG,fm,na", 0.0);
        factorNDG.put("ndg,fm,na", 1.0);
        factorList.add(factorNDG);


        List<String> queryVariables = new ArrayList<>();
        queryVariables.add("FH");

        List<String> orderedListOfHiddenVariables = new ArrayList<>();
        orderedListOfHiddenVariables.add("FB");
        orderedListOfHiddenVariables.add("FM");
        orderedListOfHiddenVariables.add("NA");
        orderedListOfHiddenVariables.add("FS");
        orderedListOfHiddenVariables.add("NDG");


        List<String> evidenceList = new ArrayList<>();
        evidenceList.add("");

        valueList.add("");

        //HashMap<String, Double> result = inference(factorList, queryVariables, orderedListOfHiddenVariables, null);

        HashMap<String, Double> result = inference(factorList, queryVariables, orderedListOfHiddenVariables, null);


        for(String key: result.keySet()) {
            System.out.println("this is result");
            System.out.println(key + ": " + result.get(key));
        }


        //restrict(factorFB, "A", "A");
        //HashMap<String, Double> productFactor = factorList.get(0);


        //productFactor = multiply(factorFB, factorFH);
        //System.out.println("//////");
        //productFactor = multiply(factorFH, factorFS);

        //System.out.println("PRINTING product factor");


        //sumout(factorFB, "A");
    }

    public static void main(String[] args) {
        ComputeInference ci = new ComputeInference();

    }


}
