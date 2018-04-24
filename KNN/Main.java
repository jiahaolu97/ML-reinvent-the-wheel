package KNN;
import java.util.*;
import java.io.*;
public class Main {
	public static BitSet[] trainingset = new BitSet[1934];
	public static int[] label = new int[1934];
	public static int k = 1;
	public static int testCases[] = {86,96,91,84,113,107,86,95,90,88};
	public static int[] res = new int[10];
	public static void main(String[] args) throws Exception {
		for(int i=0;i<1934;i++){
			trainingset[i] = new BitSet(1024);
		}
		int index = 0;
		index = LoadNumber(0,188,0);
		index = LoadNumber(1,197,index);
		index = LoadNumber(2,194,index);
		index = LoadNumber(3,198,index);
		index = LoadNumber(4,185,index);
		index = LoadNumber(5,186,index);
		index = LoadNumber(6,194,index);
		index = LoadNumber(7,200,index);
		index = LoadNumber(8,179,index);
		index = LoadNumber(9,203,index);
		System.out.println("-----------------");
		System.out.println("在k取"+k+"的情况下：");
		int TOTAL = 0 , SUCC = 0;
		for(int i=0;i<10;i++){
			printResult(i);
			TOTAL += testCases[i]+1;
			SUCC += res[i];
		}
		System.out.println("共"+TOTAL+"个数据，匹配"+SUCC+"个，准确率"+String.format("%.2f", (double)SUCC*100/TOTAL)+"%");
		

//		printResult(9);
//		test(0,3);
	}
	public static void Loadfile(String file,int i) throws Exception{
		File f = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(f));
		for(int j=0;j<32;j++){
			String line = br.readLine();
			for(int k=0;k<32;k++){
				int c = line.charAt(k) - '0';
				if(c==1) trainingset[i].set(j*32+k);
			}
		}
		br.close();
	}
	public static int LoadNumber(int number,int length,int start) throws Exception{
		String front = "E:/programming/JAVAworkbench/Machine Learning/src/KNN/trainingDigits/"+number+"_";
		for(int i=0;i<=length;i++){
			String f = front + i + ".txt";
//			System.out.println(f);
			Loadfile(f,start+i);
			label[start+i] = number;
		}
		return start+length+1;
	}
	public static int Differ(BitSet a,BitSet b){
		int result = 0;
		BitSet differ = (BitSet) a.clone();
		differ.xor(b);
		for(int i=0;i<1024;i++){
			if(differ.get(i)==true) result++;
		}
		return result;
	}
	public static int KNN(BitSet test,int k){
		int dist[] = new int[1934];
		for(int i=0;i<1934;i++){
			dist[i] = Differ(test,trainingset[i]);
		}
		int neighbor[] = new int[k];
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(int i=0;i<k;i++){
			neighbor[i] = dist[i];
			map.put(dist[i], label[i]);
		}
		Arrays.sort(neighbor);
		for(int i=k;i<1934;i++){
			if(dist[i]<neighbor[k-1]){
				map.remove(neighbor[k-1]);
				neighbor[k-1] = dist[i];
				map.put(dist[i],label[i]);
				Arrays.sort(neighbor);
			}
		}
//		for(int i=0;i<neighbor.length;i++){
//			System.out.print(neighbor[i]+" ");
//		}
//		System.out.println();
//		System.out.print("\rmap.size："+map.size());
//		for(int i=0;i<k;i++){
//			System.out.print(map.get(neighbor[i])+" ");
//		}
		int vote[] = new int[10];
		for(Integer value:map.values()){
			vote[value]++;
		}
		int max = -1;
		int result = -1;
		for(int i=0;i<10;i++){
			if(vote[i]>max){
				max = vote[i];
				result = i;
			}
		}
//		System.out.println("Come back:"+result);
		return result;
	}
	public static int KNN_weight(BitSet test,int k){
		int dist[] = new int[1934];
		for(int i=0;i<1934;i++){
			dist[i] = Differ(test,trainingset[i]);
		}
		int neighbor[] = new int[k];
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(int i=0;i<k;i++){
			neighbor[i] = dist[i];
			map.put(dist[i], label[i]);
		}
		Arrays.sort(neighbor);
		for(int i=k;i<1934;i++){
			if(dist[i]<neighbor[k-1]){
				map.remove(neighbor[k-1]);
				neighbor[k-1] = dist[i];
				map.put(dist[i],label[i]);
				Arrays.sort(neighbor);
			}
		}
//		for(int i=0;i<neighbor.length;i++){
//			System.out.print(neighbor[i]+" ");
//		}
//		System.out.println();
//		System.out.print("\rmap.size："+map.size());
//		for(int i=0;i<k;i++){
//			System.out.print(map.get(neighbor[i])+" ");
//		}
		double vote[] = new double[10];
		for(Integer dis: map.keySet()){
			int value = map.get(dis);
			vote[value] += 1.0/(double)dis;
		}
		double max = -1;
		int result = -1;
		for(int i=0;i<10;i++){
			if(vote[i]>max){
				max = vote[i];
				result = i;
			}
		}
//		System.out.println("Come back:"+result);
		return result;
	}
	public static int KNN_weight2(BitSet test,int k){
		int dist[] = new int[1934];
		for(int i=0;i<1934;i++){
			dist[i] = Differ(test,trainingset[i]);
		}
		int neighbor[] = new int[k];
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(int i=0;i<k;i++){
			neighbor[i] = dist[i];
			map.put(dist[i], label[i]);
		}
		Arrays.sort(neighbor);
		for(int i=k;i<1934;i++){
			if(dist[i]<neighbor[k-1]){
				map.remove(neighbor[k-1]);
				neighbor[k-1] = dist[i];
				map.put(dist[i],label[i]);
				Arrays.sort(neighbor);
			}
		}
//		for(int i=0;i<neighbor.length;i++){
//			System.out.print(neighbor[i]+" ");
//		}
//		System.out.println();
//		System.out.print("\rmap.size："+map.size());
//		for(int i=0;i<k;i++){
//			System.out.print(map.get(neighbor[i])+" ");
//		}
		double vote[] = new double[10];
		for(Integer dis: map.keySet()){
			int value = map.get(dis);
			vote[value] += 1.0/(double)(dis*dis);
		}
		double max = -1;
		int result = -1;
		for(int i=0;i<10;i++){
			if(vote[i]>max){
				max = vote[i];
				result = i;
			}
		}
//		System.out.println("Come back:"+result);
		return result;
	}
	
	public static boolean test(int answer,int caseNumber) throws IOException{
		String path = "E:/programming/JAVAworkbench/Machine Learning/src/KNN/testDigits/"+answer+"_"
				+caseNumber+".txt";
		File f = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(f));
		BitSet t = new BitSet(1024);
		for(int j=0;j<32;j++){
			String line = br.readLine();
			for(int k=0;k<32;k++){
				int c = line.charAt(k) - '0';
				if(c==1) t.set(j*32+k);
			}
		}
		br.close();
		int res = KNN_weight2(t,k);
		if(res == answer) return true;
		return false;
	}
	public static void printResult(int num) throws IOException{
		int success = 0;
		for(int i=0;i<=testCases[num];i++){
			if(test(num,i)) success++;
		}
		double accuracy = (double)success*100/(double)(testCases[num]+1);
		String word = "数字"+num+"的测试集，共"+(testCases[num]+1)+"个数据，匹配"+success+"个，准确率"+String.format("%.2f",accuracy)+"%";
		res[num] = success;
		System.out.println(word);
	}
}
