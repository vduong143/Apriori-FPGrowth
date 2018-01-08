import java.util.*;

public class FPTreeNode {
	public boolean root;
	public ArrayList<FPTreeNode> children;
	public FPTreeNode parent;
	public String item;
	public int count;
	public FPTreeNode next;
	
	public FPTreeNode(String item) {
		this.item = item;
		root = false;
		children = new ArrayList<FPTreeNode>();		
	}
}
