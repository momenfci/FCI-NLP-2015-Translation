package nlp.translation;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;

class Word {
	public boolean F, M, A, plural, defined;
	public String type;
	
	public Word () {
		type = "";
		F = M = A = defined = plural = false;
	}
	
	public void clear() {
		type = "";
		F = M = A = defined = plural = false;
	}
	
	public boolean isPlural() {
		return plural;
	}
	
	public boolean isVerb() {
		return type.equals("verb");
	}
	
	public boolean isNoun() {
		return type.equals("noun");
	}
	
	public boolean isF() {
		return F;
	}
	
	public boolean isM() {
		return M;
	}
	
	public boolean isDefined() {
		return defined;
	}
}


public class Translation {
	private JFrame frame;
	private JTextField textFieldNum1;
	private JTextField textFieldAns;
	private JButton btnNewButton1;
	private JButton btnNewButton2;
	
	public Translation() {
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 499 , 362);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textFieldNum1 = new JTextField();
		textFieldNum1.setBounds(22, 23, 420 , 30);
		frame.getContentPane().add(textFieldNum1);
		textFieldNum1.setColumns(10);
		
		textFieldAns = new JTextField();
		textFieldAns.setBounds(22, 255 , 420 , 30);
		frame.getContentPane().add(textFieldAns);
		textFieldAns.setColumns(10);
		
		btnNewButton1 = new JButton("Arabic");
		btnNewButton1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String text = textFieldNum1.getText();
				textFieldAns.setText(TranslateEnglishToArabic(text));
			}
		});
		btnNewButton1.setBounds(36, 136 , 200 , 60);
		frame.getContentPane().add(btnNewButton1);
		
		btnNewButton2 = new JButton("English");
		btnNewButton2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String text = textFieldNum1.getText();
				textFieldAns.setText(TranslateArabicToEnglish(text));
			}
		});
		btnNewButton2.setBounds(242 , 136 , 200 , 60);
		frame.getContentPane().add(btnNewButton2);
		
		
	}
	
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Translation window = new Translation();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		
		
		
		
		
		////////////////////////////////////////////////////////////
//		Scanner input = new Scanner(System.in);
//		String lang = input.nextLine();
//		String text = input.nextLine();
//		if (lang.equals("a"))
//			System.out.println(TranslateArabicToEnglish(text));
//		else
//			System.out.println(TranslateEnglishToArabic(text));
//		input.close();
	}

	private static HashMap<String, String> getDictionary(String path1, String path2, boolean lang) {
		HashMap<String, String> ret = new HashMap<String, String>();
		try {
			File fileDir1 = new File(path1);
			File fileDir2 = new File(path2);

			BufferedReader in1 = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileDir1), "UTF8"));
			BufferedReader in2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileDir2), "UTF8"));

			String str1, str2;

			while ((str1 = in1.readLine()) != null) {
				str2 = in2.readLine();
				if (lang)
					ret.put(stemEng(normalizeEng(str1)), stemArb(normalizeArb(str2)));
				else
					ret.put(stemArb(normalizeArb(str1)), stemEng(normalizeEng(str2)));
			}

			in1.close();
			in2.close();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return ret;
	}

	private static String TranslateEnglishToArabic(String text) {
		HashMap<String, String> dic = getDictionary("english_words", "arabic_words", true);
		text = ReArrangeEnglish(text);
		String word = "";
		StringBuilder ret = new StringBuilder("");
		Word props = new Word();
		for (int i = 0; i < text.length(); ++i) {
			if (Character.isLetter(text.charAt(i))) {
				word += text.charAt(i);
			}
			if (!Character.isLetter(text.charAt(i))) {
				word = normalizeEng(word);
				if (processEng(word, ret, props, dic))
					ret.append(text.charAt(i));
				word = "";
			}
		}
		if (word.length() != 0) {
			word = normalizeEng(word);
			processEng(word, ret, props, dic);
		}
		
		return ret.toString();
	}
	
	private static String ReArrangeEnglish(String text) {
		// TODO Auto-generated method stub
		List<Pair<String,String>> parsingResult = new ArrayList<Pair<String,String>>();
		StanfordParser parser = new StanfordParser();
		parsingResult = parser.parseEnglishPhrase(text);
		
		String ret = "";
		
		int i;
		for (i = 0; i < parsingResult.size(); ++i) {
			if (i + 1 < parsingResult.size() && parsingResult.get(i).getR().equals("NNP") && parsingResult.get(i + 1).getR().equals("VBP")) {
				if (ret.length() != 0) ret += " ";
				ret += parsingResult.get(i + 1).getL() + " " + parsingResult.get(i).getL();
				++i;
			} else {
				if (ret.length() != 0) ret += " ";
				ret += parsingResult.get(i).getL();
			}
		}
		
//		for (Pair<String,String> p : parsingResult) {
//			System.out.println(p.getL() + " " + p.getR());
//		}
		return text;
	}

	private static String TranslateArabicToEnglish(String text) {
		HashMap<String, String> dic = getDictionary("arabic_words", "english_words", false);
		text = ReArrangeArabic(text);
		String word = "";
		StringBuilder ret = new StringBuilder("");
		Word props = new Word();
		
		for (int i = 0; i < text.length(); ++i) {
			if (Character.isLetter(text.charAt(i))) {
				word += text.charAt(i);
			}
			if (!Character.isLetter(text.charAt(i))) {
				word = normalizeArb(word);
				if (processArb(word, ret, props, dic))
					ret.append(text.charAt(i));
				word = "";
			}
		}

		if (word.length() != 0) {
			word = normalizeArb(word);
			processArb(word, ret, props, dic);
		}
		return ret.toString();
	}
	
	private static String ReArrangeArabic(String text) {
		// TODO Auto-generated method stub
		List<Pair<String,String>> parsingResult = new ArrayList<Pair<String,String>>();
		StanfordParser parser = new StanfordParser();
		parsingResult = parser.parseArabicPhrase(text);
		
		String ret = "";
		
		int i;
		for (i = 0; i < parsingResult.size(); ++i) {
			if (i + 1 < parsingResult.size() && parsingResult.get(i).getR().equals("VBP") && parsingResult.get(i + 1).getR().equals("NNP")) {
				if (ret.length() != 0) ret += " ";
				ret += parsingResult.get(i + 1).getL() + " " + parsingResult.get(i).getL();
				++i;
			} else {
				if (ret.length() != 0) ret += " ";
				ret += parsingResult.get(i).getL();
			}
		}
		
//		for (Pair<String,String> p : parsingResult) {
//			System.out.println(p.getL() + " " + p.getR());
//		}
		
		return ret;
	}

	private static String normalizeEng(String word) {
		StringBuilder ret = new StringBuilder(word);
		for (int i = 0; i < ret.length(); ++i) {
			if (Character.isUpperCase(ret.charAt(i)))
				ret.setCharAt(i, Character.toLowerCase(word.charAt(i)));
		}
		return ret.toString();
	}
	
	private static String normalizeArb(String word) {
		StringBuilder ret = new StringBuilder(word);
		for (int i = 0; i < ret.length(); ++i) {
			if (ret.charAt(i) == 'أ' || ret.charAt(i) == 'إ') {
				ret.setCharAt(i, 'ا');
			}
			if (ret.charAt(i) == 'ي') {
				ret.setCharAt(i, 'ى');
			}
			if (ret.charAt(i) == 'ة') {
				ret.setCharAt(i, 'ه');
			}
		}
		return ret.toString();
	}
	
	private static boolean processEng(String word, StringBuilder ret, Word props, HashMap<String, String> dic) {
		if (word.length() == 0) return true;
		if (word.equals("the")) {
			props.defined = true;
			return false;
		}
		if (word.equals("her")) {
			props.F = true;
			return false;
		}
		if (word.equals("his")) {
			props.M = true;
			return false;
		}
		if (word.equals("thier")) {
			props.A = true;
			return false;
		}
		if (word.equals("a")) {
			return false;
		}
		if (word.equals("an")) {
			return false;
		}
		if (word.equals("am")) {
			props.type = "am";
			return false;
		}
		if (word.equals("is")) {
			props.type = "is";
			return false;
		}
		if (word.equals("are")) {
			props.plural = true;
			return false;
		}
		if (word.endsWith("ing"))
			props.type = "verb";
		
		word = stemEng(word);
		
		if (props.isDefined()) ret.append("ال");
		if (props.isVerb()) ret.append("ي");
		ret.append(match(dic, word));
		if (props.isPlural()) ret.append("ون");
		if (props.A) {
			if (ret.charAt(ret.length() - 1) == 'ه') ret.setCharAt(ret.length() - 1, 'ت');
			ret.append("هم");
		}
		if (props.F) {
			if (ret.charAt(ret.length() - 1) == 'ه') ret.setCharAt(ret.length() - 1, 'ت');
			ret.append("ها"); 
		}
		if (props.M) {
			if (ret.charAt(ret.length() - 1) == 'ه') ret.setCharAt(ret.length() - 1, 'ت');
			ret.append("ه"); 
		}
		props.clear();
		return true;
	}
	
	private static boolean processArb(String word, StringBuilder ret, Word props, HashMap<String, String> dic) {
		if (word.length() == 0) return true;
		if (word.startsWith("ال")) {
			props.defined = true;
		} else if (word.startsWith("ى")) {
			props.type = "verb";
		}
		if (word.endsWith("ون")) {
			props.plural = true;
		}
		if (!props.isDefined()) {
			if (word.endsWith("هم")) {
				props.A = true;
			} else if (word.endsWith("ه")) {
				props.M = true;
			} else if (word.endsWith("ها")) {
				props.F = true;
			}
		}
		word = stemArb(word);
		
		if (props.isDefined()) ret.append("the");
		if (props.isVerb()) {
			if (ret.length() != 0) ret.append(" ");
			if (props.isPlural()) {
				ret.append("are");
			} else if (!props.type.equals("am")){
				ret.append("is");
			} else {
				ret.append("am");
			}
		}
		if (props.A) {
			if (ret.length() != 0) ret.append(" ");
			ret.append("thier");
		}
		if (props.F) {
			if (ret.length() != 0) ret.append(" ");
			ret.append("her");
		}
		if (props.M) {
			if (ret.length() != 0) ret.append(" ");
			ret.append("his");
		}
		
		if (ret.length() != 0) ret.append(" ");
		ret.append(match(dic, word));
		
		if (props.isVerb()) ret.append("ing");

		
		props.clear();
		if (word == "انا") props.type = "am";
		if (word.equals("هذا")) props.type = "verb";
		return true;
	}
	
	private static String stemEng(String word) {
		if (word.endsWith("ing")) word = word.substring(0, word.length() - 3);
		return word;
	}
	
	private static String stemArb(String word) {
		if (!word.startsWith("ال") && !word.startsWith("بال")) {
			if (word.length() > 2 && (word.charAt(word.length() - 3) == 'ى' || word.charAt(word.length() - 3) == 'ت' || word.charAt(word.length() - 3) == 'ب')) {
				if (word.endsWith("ون")) word = word.substring(0, word.length() - 3);
				if (word.endsWith("هم")) word = word.substring(0, word.length() - 3);
				if (word.endsWith("ه")) word = word.substring(0, word.length() - 2);
				if (word.endsWith("ها")) word = word.substring(0, word.length() - 3);
			}
		}
		if (word.startsWith("بال") && word.length() > 4) word = word.substring(3);
		if (word.startsWith("ال") && word.length() > 3) word = word.substring(2);
		if (word.startsWith("ى")) word = word.substring(1);
		return word;
	}
	
	private static String match(HashMap<String, String> dic, String word) {
		int dist = 1000000000;
		String bestword = word;
	    for (String key : dic.keySet()) {
	        String a = key, b = word;
	        int n = a.length(), m = b.length();
	        int [][] dp = new int [n + 1][m + 1];
	        for (int i = 0; i <= n; ++i) {
	        	for (int j = 0; j <= m; ++j) dp[i][j] = 1000000000;
	        }
	        dp[0][0] = 0;
	        for (int i = 0; i <= n; ++i) {
	        	for (int j = 0; j <= m; ++j) {
	        		if (i == n && j == m) continue;
	        		else if (i == n) dp[i][j + 1] = Math.min(dp[i][j + 1], dp[i][j] + 1);
	        		else if (j == m) dp[i + 1][j] = Math.min(dp[i + 1][j], dp[i][j] + 1);
	        		else {
	        			if (a.charAt(i) == b.charAt(j)) dp[i + 1][j + 1] = Math.min(dp[i + 1][j + 1], dp[i][j]);
	        			dp[i][j + 1] = Math.min(dp[i][j + 1], dp[i][j] + 1);
	        		}
	        	}
	        }
	        
	        if (dp[n][m] < dist) {
	        	dist = dp[n][m];
	        	bestword = dic.get(key);
	        }
	    }
	    
		return bestword;
	}
	
}
