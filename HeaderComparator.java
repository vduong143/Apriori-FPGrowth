import java.util.Comparator;

public class HeaderComparator implements Comparator<Header> {

	@Override
	public int compare(Header h1, Header h2) {
		int out;
		if(h1.supportCount > h2.supportCount) {
			out = 1;
		} else if(h1.supportCount < h2.supportCount) {
			out = -1;
		} else {
			out = 0;
		}
		//to sort in decreasing order
		return -out;
	}
}