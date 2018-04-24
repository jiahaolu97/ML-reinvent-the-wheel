package Bayes;
import java.util.*;
import java.io.*;
// use Fisher-Yates Shuffling to shuffle the car dataset.
public class Shuffle {
	public static void main(String[] args) throws Exception {
		String total[] = new String[1728];
		File file = new File("E:/programming/JAVAworkbench/Machine Learning/src/Bayes/car evaluation dataset.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		for(int i=0;i<1728;i++){
			total[i] = br.readLine();
		}
		br.close();
		//Fisher-Yates
		Random ran = new Random();
		for(int i=1727;i>-1;i--){
			int k = ran.nextInt(i+1);
			String temp = total[k];
			total[k] = total[i];
			total[i] = temp;
		}
		//write down
		FileWriter fw = new FileWriter("E:/programming/JAVAworkbench/Machine Learning/src/Bayes/Shuffled Car Dataset.txt");
		for(int i=0;i<1728;i++){
			fw.write(total[i]+"\r\n");
		}
		fw.close();
	}
	
}
