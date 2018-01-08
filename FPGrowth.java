import java.util.*;
import java.io.*;

public class FPGrowth {
	
	public double minSup;
	public ArrayList<Header> headerTable;
	public FPTreeNode FPTreeNode;
	public List<ArrayList<String>> transactions;
	public String fileName;
	public int nTrans;
	public HashSet<ArrayList<String>> freqPatterns;
	public Hashtable<String, Integer> itemWithCounts;
	
	public FPGrowth(String fileName, double minSup){
		this.fileName = fileName;
		this.minSup = minSup;
		nTrans = 0;
		headerTable = new ArrayList<Header>();
		transactions = new ArrayList<ArrayList<String>>();
		freqPatterns = new HashSet<ArrayList<String>>();
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
				}
				transactions.add(transaction);
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("unable to read file: " + fileName);
		}
	}
	
	//create the header table similarly to the first scan of the data in Apriori
	public ArrayList<Header> createHeaderTable() {
		ArrayList<Header> headerTable = new ArrayList<Header>();
		for(String i: itemWithCounts.keySet()) {
			if(itemWithCounts.get(i) >= nTrans*minSup){
				headerTable.add(new Header(i, itemWithCounts.get(i)));
			}
		}
		Collections.sort(headerTable, new HeaderComparator());
		return headerTable;
	}
	
	public void growTree() {
		//create root node
		FPTreeNode = new FPTreeNode("null");
		FPTreeNode.root = true;
		FPTreeNode.item = null;
		
		//scan the transactions
		for(ArrayList<String> t: transactions){
			ArrayList<String> orderedItems = new ArrayList<String>();
			for(Header h: headerTable) {
				if(t.contains(h.item)) {
					orderedItems.add(h.item);
				}
			}
			//insert items into the tree
			insert(orderedItems, FPTreeNode, 0);
		}
	}
	
	//Nobahar, Kamran. An implementation of FP-Growth algorithm in Java, GitHub repository.
	//https://github.com/goodinges/FP-Growth-Java
	public void insert(ArrayList<String> orderedItems, FPTreeNode FPTreeNode, int n){
		if(n < orderedItems.size()) {
			String item = orderedItems.get(n);
			FPTreeNode newNode = null;
			boolean found = false;
			
			for(FPTreeNode child : FPTreeNode.children){
				if(child.item.equals(item)){
					newNode = child;
					child.count ++;
					found = true;
					break;
				}
			}
			
			if(!found){
				newNode = new FPTreeNode(item);
				newNode.count = 1;
				newNode.parent = FPTreeNode;
				FPTreeNode.children.add(newNode);
				for(Header h : headerTable){
					if(h.item.equals(item)) {
						FPTreeNode temp = h.nodeLink;
						if(temp == null){
							h.nodeLink = newNode;
						}else{
							while(temp.next != null){
								temp = temp.next;
							}
							temp.next = newNode;
						}
					}
				}
			}
			insert(orderedItems, newNode, n+1);
		}
	}
	
	
	public ArrayList<ArrayList<String>> generateCombinations(ArrayList<String> items, ArrayList<Header> newHeaderTable){
		ArrayList<ArrayList<String>> combinations = new ArrayList<ArrayList<String>>();
		if(items.size() != 0) {
			String item = items.get(0);
			if(items.size() > 1) {
				items.remove(0);
				ArrayList<ArrayList<String>> subCombinations = generateCombinations(items, newHeaderTable);
				combinations.addAll(subCombinations);
				for(ArrayList<String> c: subCombinations) {
					for(int i = 0; i < c.size(); i++) {
						ArrayList<String> comb = new ArrayList<String>();
						int count = Integer.MAX_VALUE;
						for(int j = 0; j <= i; j++) {
							for(Header h: newHeaderTable) {
								if(h.item.equals(c.get(j)) && count < h.supportCount) {
									count = h.supportCount;
								}
							}
							comb.add(c.get(j));
						}
						for(Header h: newHeaderTable) {
							if(h.item.equals(item) && count < h.supportCount) {
								count = h.supportCount;
							}
						}
						if(count >= minSup*nTrans) {
							comb.add(item);
							combinations.add(comb);
						}
					}
				}
			}
			ArrayList<String> combination = new ArrayList<String>();
			combination.add(item);
			combinations.add(combination);
		}
		return combinations;
	}
	
	//check if a tree is single path
	public boolean isSinglePath(FPTreeNode FPTreeNode) {
		boolean isSinglePath = true;
		if(FPTreeNode.children.size() > 1){
			isSinglePath = false;
			return isSinglePath;
		}else{
			for(FPTreeNode child : FPTreeNode.children) {
				if(isSinglePath) {
					isSinglePath = isSinglePath(child);
				} else {
					break;
				}
			}
		}
		return isSinglePath;
	}
	
	//create a conditional Header Table for each current node
	public ArrayList<Header> createConditionalHeaderTable(ArrayList<ArrayList<String>> conditionalPatternBase, HashSet<String> newOneItems) {
		ArrayList<Header> headerTable = new ArrayList<Header>();
		for(String s: newOneItems) {
			int count = 0;
			for(ArrayList<String> t: conditionalPatternBase){
				if(t.contains(s)){
					count ++;
				}
			}
			if(count >= nTrans*minSup){
				headerTable.add(new Header(s, count));
			}
		}
		Collections.sort(headerTable, new HeaderComparator());
		return headerTable;
	}
	
	//grow subtree at the current node based on the new Header Table
	public FPTreeNode growConditionalTree(ArrayList<ArrayList<String>> conditionalPatternBase, ArrayList<Header> newHeaderTable) {
		FPTreeNode subtree = new FPTreeNode("null");
		subtree.root = true;
		subtree.item = null;
		for(ArrayList<String> item: conditionalPatternBase){
			ArrayList<String> orderedItems = new ArrayList<String>();
			for(Header h: newHeaderTable) {
				if(item.contains(h.item)) {
					orderedItems.add(h.item);
				}
			}
			insertConditionalTree(orderedItems, subtree, 0, newHeaderTable);
		}
		return subtree;
	}
	
	//Nobahar, Kamran. An implementation of FP-Growth algorithm in Java, GitHub repository.
	//https://github.com/goodinges/FP-Growth-Java
	public void insertConditionalTree(ArrayList<String> orderedItems, FPTreeNode FPTreeNode, int n, ArrayList<Header> newHeaderTable) {
		if(n < orderedItems.size()) {
			String item = orderedItems.get(n);
			FPTreeNode newTree = null;
			boolean found = false;
			for(FPTreeNode child: FPTreeNode.children) {
				if(child.item.equals(item)) {
					newTree = child;
					child.count ++;
					found = true;
					break;
				}
			}
			if(!found) {
				newTree = new FPTreeNode(item);
				newTree.count = 1;
				newTree.parent = FPTreeNode;
				FPTreeNode.children.add(newTree);
				for(Header h : newHeaderTable) {
					if(h.item.equals(item)) {
						FPTreeNode temp = h.nodeLink;
						if(temp == null) {
							h.nodeLink = newTree;
						} else {
							while(temp.next != null) {
								temp = temp.next;
							}
							temp.next = newTree;
						}
					}
				}
			}
			insertConditionalTree(orderedItems,newTree,n+1,newHeaderTable);
		}
	}
	
	//recursive FPGowth algorithm
	public void mineTree(FPTreeNode FPTreeNode, ArrayList<String> combination, ArrayList<Header> headerTable){
		if(isSinglePath(FPTreeNode)) {
			ArrayList<String> items = new ArrayList<String>();
			while(FPTreeNode != null) {
				if(FPTreeNode.item != null) {
					items.add(FPTreeNode.item);
				}
				if(FPTreeNode.children.size() > 0) {
					FPTreeNode = FPTreeNode.children.get(0);
				} else {
					FPTreeNode = null;
				}
			}
			ArrayList<ArrayList<String>> combinations = generateCombinations(items, headerTable);
			for(ArrayList<String> comb: combinations) {
				comb.addAll(combination);
			}
			freqPatterns.addAll(combinations);
			
		} else {
			for(int i = headerTable.size() - 1; i >=0; i--) {
				ArrayList<String> newComb = new ArrayList<String>();
				newComb.addAll(combination);
				newComb.remove(null);
				newComb.add(headerTable.get(i).item);
				freqPatterns.add(newComb);
				ArrayList<ArrayList<String>> conditionalPatternBase = new ArrayList<ArrayList<String>>();
				FPTreeNode temp = headerTable.get(i).nodeLink;
				HashSet<String> newOneItems = new HashSet<String>();
				while(temp != null) {
					FPTreeNode temp2 = temp;				
					ArrayList<String> newItemset = new ArrayList<String>();
					while(temp.item != null) {
						if(temp != temp2) {
							newItemset.add(temp.item);
							newOneItems.add(temp.item);
						}
						temp = temp.parent;
					}
					for(int j = 0; j < temp2.count; j++) {
						conditionalPatternBase.add(newItemset);
					}
					temp = temp2.next;
				}
				ArrayList<Header> newHeaderTable = createConditionalHeaderTable(conditionalPatternBase, newOneItems);
				FPTreeNode newTree = growConditionalTree(conditionalPatternBase, newHeaderTable);
				if(newTree.children.size() > 0){
					mineTree(newTree, newComb, newHeaderTable);
				}
			}
		}
	}
	
	public void run() {
		readFile();
		headerTable = createHeaderTable();
		growTree();
		mineTree(FPTreeNode, new ArrayList<String>(), headerTable);
	}
	
	//print the output of FPGrowth
	public void printFreqPatterns(){
		for(ArrayList<String> itemset: freqPatterns) {
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
		FPGrowth fpg = new FPGrowth(args[0], Double.parseDouble(args[1]));
		fpg.run();
		fpg.printFreqPatterns();
		long end = System.currentTimeMillis();
		System.out.println("Runtime: " + (end - start)/1000.0 + "sec");
	}
}
