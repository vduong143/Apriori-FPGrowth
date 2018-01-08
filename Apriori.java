import java.util.*;
import java.io.*;

public class Apriori {
	public List<ArrayList<String>> itemsets;
	public List<ArrayList<String>> freqItemsets;
	public List<ArrayList<String>> transactions;
	public Set<String> C1;
	public String fileName;
	public int nTrans;
	public int nLength;
	public double minSup;
	public List<ArrayList<String>> candidates;
	
	public Apriori(String fileName, double minSup){
		this.fileName = fileName;
		this.minSup = minSup;
		nTrans = 0;
		nLength = 0;
		itemsets = new ArrayList<ArrayList<String>>();
		freqItemsets = new ArrayList<ArrayList<String>>();
		transactions = new ArrayList<ArrayList<String>>();
		C1 = new HashSet<String>();
		candidates = new ArrayList<ArrayList<String>>();
	}
	
	public void readFile() {
		try {
			Scanner input = new Scanner(new File(fileName));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				ArrayList<String> transaction = new ArrayList<String>();
				nTrans ++;
				StringTokenizer split = new StringTokenizer(line,", ");
				while(split.hasMoreTokens()){
					String token = split.nextToken();
					transaction.add(token);				
					//each item is a member of the set of candidate 1-itemsets C1
					C1.add(token);
				}
				transactions.add(transaction);
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("unable to read file: " + fileName);
		}
	}
	
	//determine the set of frequent 1-itemsets L1
	public ArrayList<ArrayList<String>> findL1() {
		ArrayList<ArrayList<String>>  L1 = new ArrayList<ArrayList<String>>();
		for(String i: C1){
			ArrayList<String> itemset = new ArrayList<String>();
			int count = 0;
			for(ArrayList<String> t: transactions){
				if(t.contains(i)){
					count++;
				}
			}
			if(count >= nTrans*minSup){
				itemset.add(i);
				L1.add(itemset);
			}
		}
		return L1;
	}
	
	//join step
	public void generateCandidates() {
		ArrayList<ArrayList<String>> Ck = new ArrayList<ArrayList<String>>();
		if(nLength == 1){
			Ck = findL1();
		} else {
			String s = null;
			for(int i = 0; i < itemsets.size(); i ++) {
				for(int j = i; j < itemsets.size(); j ++){
					int ndiff = 0;
					ArrayList<String> tempCandidate = new ArrayList<String>();
					for(int k = 0; k < itemsets.get(j).size(); k++){
						if(!itemsets.get(i).contains(itemsets.get(j).get(k))){
							ndiff++;
							s = itemsets.get(j).get(k);
						}
					}
					if(ndiff == 1){
						tempCandidate.addAll(itemsets.get(i));
						tempCandidate.add(s);
						Ck.add(tempCandidate);
					}
				}
			}
		}
		candidates = Ck;
	}
	
	//prune step
	public void prune(){
		ArrayList<ArrayList<String>> Ck = new ArrayList<ArrayList<String>>();
		if(nLength > 1){
			for(ArrayList<String> c: candidates){
				boolean frequent = true;
				for(int i = 0; i < c.size(); i++){
					ArrayList<String> temp = new ArrayList<String>();
					temp.addAll(c);
					c.remove(i);
					if(!itemsets.contains(c)){
						frequent = false;
					}
					c = temp;
				}
				if(frequent){
					Ck.add(c);
				}
			}
			candidates = Ck;
		}
	}
	
	//Apriori algorithm
	public void run() {
		readFile();
		nLength = 1;
		do {
			generateCandidates();
			prune();
			itemsets.clear();
			for(ArrayList<String> c: candidates){
				int count = 0;
				for(ArrayList<String> transaction: transactions){
					boolean contain = true;
					for(String itemset: c){
						if(!transaction.contains(itemset)){
							contain = false;
							break;
						}
					}
					if(contain){
						count ++;
					}
				}
				if(count >= nTrans*minSup && !itemsets.contains(c)){
					itemsets.add(c);
				}
			}
			freqItemsets.addAll(itemsets);
			nLength ++;
		} while(itemsets.size() > 0);
	}
	
	//print the output of Apriori
	public void printFreqItemsets(){
		for(ArrayList<String> itemset: freqItemsets) {
			String out = "{";
			for(String s: itemset) {
				out += s + ",";
			}
			char[] temp = out.toCharArray();
			char[] temp2 = new char[temp.length-1];
			for(int i = 0; i<temp.length-1; i++) {
				temp2[i] = temp[i];
			}
			out = (new String(temp2)) + "}";
			System.out.println(out);
		}
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Apriori apriori = new Apriori(args[0], Double.parseDouble(args[1]));
		apriori.run();
		apriori.printFreqItemsets();
		long end = System.currentTimeMillis();
		System.out.println("Runtime: " + (end - start)/1000.0 + "sec");
	}
}
