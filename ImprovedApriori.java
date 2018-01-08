import java.util.*;
import java.io.*;

public class ImprovedApriori {
	public List<ArrayList<String>> itemsets;
	public List<ArrayList<String>> freqItemsets;
	public List<ArrayList<String>> transactions;
	public String fileName;
	public int nTrans;
	public int nLength;
	public double minSup;
	public List<ArrayList<String>> candidates;
	public Hashtable<String, Integer> itemWithCounts;
	
	public ImprovedApriori(String fileName, double minSup){
		this.fileName = fileName;
		this.minSup = minSup;
		nTrans = 0;
		nLength = 0;
		itemsets = new ArrayList<ArrayList<String>>();
		freqItemsets = new ArrayList<ArrayList<String>>();
		transactions = new ArrayList<ArrayList<String>>();
		candidates = new ArrayList<ArrayList<String>>();
		
		//storing the items with their count
		//so we do not have to recompute the count at each iteration
		itemWithCounts = new Hashtable<String, Integer>();
	}
	
	public void readFile(){
		try {
			Scanner input = new Scanner(new File(fileName));
			while (input.hasNextLine()) {
				String line = input.nextLine();
				ArrayList<String> transaction = new ArrayList<String>();
				nTrans ++;
				StringTokenizer split = new StringTokenizer(line, ", ");
				while(split.hasMoreTokens()){
					String token = split.nextToken();
					transaction.add(token);
					if(itemWithCounts.containsKey(token)){
						itemWithCounts.put(token, itemWithCounts.get(token) + 1);
					}else{
						itemWithCounts.put(token, 1);
					}
					//the initial set of items with their count
					//the set of candidate 1-itemsets C1
				}
				transactions.add(transaction);
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("unable to read file: " + fileName);
		}
	}
	
	public ArrayList<ArrayList<String>> findL1(){
		ArrayList<ArrayList<String>>  L1 = new ArrayList<ArrayList<String>>();
		for(String i: itemWithCounts.keySet()){
			ArrayList<String> itemset = new ArrayList<String>();
			if(itemWithCounts.get(i) >= nTrans*minSup){
				itemset.add(i);
				L1.add(itemset);
			}
		}
		return L1;
	}
	
	//join step
	public void generateCandidates(){
		ArrayList<ArrayList<String>> Ck = new ArrayList<ArrayList<String>>();
		if(nLength == 1){
			Ck = findL1();
		} else {
			String item = null;
			for(int i = 0; i < itemsets.size(); i ++){
				for(int j = i; j < itemsets.size(); j ++){
					int ndiff = 0;
					ArrayList<String> tempCandidate = new ArrayList<String>();
					for(int k = 0; k < itemsets.get(j).size(); k++){
						if(!itemsets.get(i).contains(itemsets.get(j).get(k))){
							ndiff++;
							item = itemsets.get(j).get(k);
						}
					}
					if(ndiff == 1){
						tempCandidate.addAll(itemsets.get(i));
						tempCandidate.add(item);
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
						break;
					}
					c = temp;
				}
				if(frequent) {
					Ck.add(c);
				}
			}
			candidates = Ck;
		}
	}
	
	//reduce the number of transactions of each candidate scan
	//proposed by the textbook
	public void reduceTransactions() {
		for(int i = 0; i < transactions.size(); i++) {
			int notInTransaction = 0;
			for(ArrayList<String> itemset: itemsets) {
				for(String item: itemset){
					if(!transactions.get(i).contains(item)) {
						notInTransaction ++;
					}
				}
			}
			if(notInTransaction == itemsets.size()) {
				transactions.remove(i);
			}
		}
	}
	
	//Apriori algprithm
	public void run(){
		readFile();
		nLength = 1;
		do {
			generateCandidates();
			prune();
			itemsets.clear();
			for(ArrayList<String> c: candidates) {
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
			//reduceTransactions();
			freqItemsets.addAll(itemsets);
			nLength ++;
		} while(itemsets.size() > 0);	
	}
	
	//print the output of Apriori
	public void printFreqItemsets() {
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
		ImprovedApriori apriori = new ImprovedApriori(args[0], Double.parseDouble(args[1]));
		apriori.run();
		apriori.printFreqItemsets();
		long end = System.currentTimeMillis();
		System.out.println("Runtime: " + (end - start)/1000.0 + "sec");
	}
}
