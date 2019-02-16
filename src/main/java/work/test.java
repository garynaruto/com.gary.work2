package work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {
	public static void main(String[] args) {
		String[] p1 = {"1"};
		String[] p2 = {"2"};
		String[] p3 = {"3"};
		List<String> l1 = Arrays.asList(p1);
		List<String> l2 = Arrays.asList(p2);
		List<String> l3 = Arrays.asList(p3);

		List<List<String>> list = new ArrayList<List<String>>();
		list.add(l1);
		list.add(l2);
		list.add(l3);
		mPermutation(list, l1, "");
		System.out.println(list);
		//test1(list, p1, "");
	}
	public static void mPermutation(List<List<String>> list, List<String> arr, String str) {
		for (int i = 0; i < list.size(); i++) {
			if (i == list.indexOf(arr)) {
				for (String st : arr) {
					st = str + st;
					if (i < list.size() - 1) {
						mPermutation(list, list.get(i + 1), st);
					} else if (i == list.size() - 1) {
						System.out.println(st);
					}
				}
			}
		}
	}
	public static void test1(List<String[]> list, String[] arr, String str) {
		for (int i = 0; i < list.size(); i++) {
			if (i == list.indexOf(arr)) {
				for (String st : arr) {
					st = str + st;
					if (i < list.size() - 1) {
						test1(list, list.get(i + 1), st);
					} else if (i == list.size() - 1) {
						System.out.println(st);
					}
				}
			}
		}
	}
}