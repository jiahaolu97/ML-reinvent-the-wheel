//思路：设置不同的选取均值向量的方法： 1.随机选取  2.使用特征空间的等分点作为初始均值向量  3.选择距离尽可能远的点
//思路：用多种方式刻画最后的聚类结果。
//思路：编写一个表示聚类的class  里面有Hashset  可以计算均值向量
package Kmeans;
import java.util.*;
import java.io.*;
public class Main {
	public static int K = 20;
	public static Data u[] = new Data[K];
	public static Cluster c[] = new Cluster[K];
	public static int belong[] = new int[178];
	public static int label[] = new int[178];
	public static boolean updated[] = new boolean[K];
	public static Data points[] = new Data[178];
	public static double min[] = new double[13];
	public static double max[] = new double[13];
	public static double limit[] = new double[13];
	public static double Jaccard = 0,FMI = 0,Rand = 0;
	public static void main(String[] args) throws Exception {
		File file = new File("E:/programming/JAVAworkbench/Machine Learning/src/Kmeans/Wine dataset.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		double record[] = new double[13];
		Arrays.fill(min, Double.MAX_VALUE);
		//read data from file
		for(int i=0;i<178;i++){
			String line = br.readLine();
			String[] fragment = line.split(",");
			label[i] = Integer.parseInt(fragment[0]);
			for(int j=0;j<13;j++){
				record[j] = Double.parseDouble(fragment[j+1]);
				if(record[j]>max[j]) max[j] = record[j];
				if(record[j]<min[j]) min[j] = record[j];
			}
			points[i] = new Data(record);
		}
		for(int i=0;i<13;i++){
			limit[i] = max[i] - min[i];		//记录各个维度的数据范围，用来将normalize各个维度上的距离
		}
		Arrays.fill(updated, true);
		for(int i=0;i<K;i++){
			c[i] = new Cluster();
		}
		init_u_random();
//		init_u_cover();
//		init_u_best();
		k_means();
		for(int i=0;i<K;i++){
			System.out.println("Cluster "+(i+1)+" contains "+c[i].context.size()+" points "+c[i]);
		}
		System.out.println();
		System.out.println("性能度量的结果如下：");
		System.out.println("1.内部指标");
		double E = square_error();
		System.out.println("        均方误差："+E+"      （各维度已标准化）");
		double si = Silhouette();
		System.out.println("        轮廓系数："+si+"      （位于-1到1之间，越大越好）");
		System.out.println("2.外部指标(以数据集本身的标签为参照)");
		ExternalIndex();
		System.out.println("        Jaccard系数："+Jaccard);
		System.out.println("        FM指数："+FMI);
		System.out.println("        Rand指数："+Rand);
		System.out.println("\t\t以上三个指标都在[0,1]之间，越大越好。");
	}
	public static void init_u_random(){
		Random r = new Random();
		HashSet<Integer> temp = new HashSet<Integer>();
		for(int i=0;i<K;i++){
			int next = r.nextInt(178);
			if(temp.contains(next)){
				i--;
			}else{
				temp.add(next);
				u[i] = points[next];
			}
		}
	}
	public static void init_u_cover(){
		for(int i=0;i<K;i++){
			double[] cur = new double[13];
			for(int j=0;j<13;j++){
				cur[j] = min[j]+i*limit[j]/(K-1);
			}
			u[i] = new Data(cur);
		}
	}
	public static void init_u_best(){ //choose the far-most vector according to the updated distance[];
		Random r = new Random();
		u[0] = points[r.nextInt(178)];
		double dist[] = new double[178];
		Arrays.fill(dist, Double.MAX_VALUE);
		for(int i=1;i<K;i++){
			//updata dist[]
			double max = 0.0;
			int maxIndex = -1;
			for(int j=0;j<178;j++){
				double d = u[i-1].dist(points[j]);
				if(d<dist[j]){
					dist[j] = d;
				}
				if(dist[j]>max){
					 max = dist[j];
					 maxIndex = j;
				}
			}
			u[i] = points[maxIndex];
		}
	}
	
	public static void k_means(){
		boolean con = true;
		double dist_each[] = new double[K];
		double min_dist = Double.MAX_VALUE;
		int min_index = -1;
		int circles = 0;
		while(con){
			con = false;
			Arrays.fill(updated, true);
//			if(circles%1 == 0)	System.out.println(circles);
			for(int i=0;i<K;i++){
				c[i].clear();
			}			
			for(int i=0;i<178;i++){
				min_dist = Double.MAX_VALUE;
				min_index = -1;
				for(int j=0;j<K;j++){
					dist_each[j] = points[i].dist(u[j]);
					if(dist_each[j]<min_dist){
						min_dist = dist_each[j];
						min_index = j;
					}
				}
				c[min_index].add(points[i]);
				c[min_index].addRecord(i);
				belong[i] = min_index;
			}
			for(int i=0;i<K;i++){
				Data nData = c[i].calculate_u();
				if(nData.equals(u[i])) updated[i] =false;
				else{
					u[i] = nData;
				}
			}
			for(int i=0;i<K;i++){
				if(updated[i]) con = true;
			}
			circles ++;
//			for(int i=0;i<K;i++){
//				System.out.println(u[i]);
//			}
//			for(int i=0;i<K;i++){
//				System.out.println("Cluster "+(i+1)+" contains points "+c[i]);
//			}
		}
		System.out.println("选取K="+K+",经过"+(circles+1)+"次迭代，最终的分类结果为:");
		System.out.println("--------------------");
	}

	public static double square_error(){
		double res = 0.0;
		for(int i=0;i<K;i++){
			Cluster cl = c[i];
			Iterator<Data> it = cl.context.iterator();
			while(it.hasNext()){
				Data d  = it.next();
				res += Math.pow(d.dist(cl.u), 2);
			}
		}
		return res;
	}
	public static double Silhouette(){
		double res = 0;
		for(int i=0;i<178;i++){
			Cluster cc = c[belong[i]];
			Data dd = points[i];
			double ai = 0.0;
			double bi = Double.MAX_VALUE;
			if(cc.context.size() <= 1){
				ai = 0;
			}else{
				Iterator<Data> it = cc.context.iterator();
				while(it.hasNext()){
					Data tt = it.next();
					ai += tt.dist(dd);
				}
				ai /= (cc.context.size()-1);
			}
			for(int j=0;j<K;j++){
				if(j==belong[i]) continue;
				double bi_temp = 0;
				Cluster cj = c[j];
				Iterator<Data> itt = cj.context.iterator();
				while(itt.hasNext()){
					Data tt = itt.next();
					bi_temp += tt.dist(dd);
				}
				bi_temp /= (cj.context.size()-1);
				if(bi_temp < bi){
					bi = bi_temp;
				}
			}
			double si = (bi-ai)/Math.max(bi, ai);
//			System.out.println(si);
			res += si;
		}
		return res/178;
	}
	public static void ExternalIndex(){
		double a=0,b=0,c=0,d=0;
		for(int i=0;i<178;i++){
			int bi = belong[i];
			int li = label[i];
			for(int j=i+1;j<178;j++){
				int bj = belong[j];
				int lj = label[j];
				if(bi==bj){
					if(li==lj) a++;
					else b++;
				}else{
					if(li == lj) c++;
					else d++;
				}
			}
		}
		Jaccard = a/(a+b+c);
		FMI = Math.sqrt(a*a/(a+b)/(a+c));
		Rand = 2*(a+d)/178/177;
		
	}
}
class Cluster{
	public HashSet<Data> context;
	public TreeSet<Integer> ids;
	public Data u;
	public Cluster(){
		this.context = new HashSet<Data>();
		this.ids = new TreeSet<Integer>();
		this.u = null;
	}
	public void add(Data d){
		this.context.add(d);
	}
	public void addRecord(int k){
		this.ids.add(k);
	}
	public void clear(){
		this.context.clear();
		this.ids.clear();
	}
	public Data calculate_u(){
		double[] pool = new double[13];
		Arrays.fill(pool, 0.0);
		Iterator<Data> it = context.iterator();
		while(it.hasNext()){
			Data d = it.next();
			for(int i=0;i<13;i++){
				
				
				pool[i] += d.data[i];
			}
		}
		int size = context.size();
		for(int i=0;i<13;i++){
			pool[i] /= size;
		}
		this.u = new Data(pool);
		return this.u;
	}
	@Override
	public String toString() {
		StringBuffer res =  new StringBuffer("\n");
		Iterator<Integer> it = this.ids.iterator();
		while(it.hasNext()){
			res.append(it.next()+" ");
		}
		return res.toString();
	}	
}


class Data{
	public double[] data = new double[13];
	public Data(double[] t){
		this.data = t.clone();
	}
	public double dist(Data d){
		double res = 0;
		for(int i=0;i<13;i++){
			double a = this.data[i] - d.data[i];
			double b = Main.limit[i];
			res += (a/b)*(a/b);
		}
		return Math.sqrt(res);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Data){
			Data t = (Data)obj;
			for(int i=0;i<13;i++){
				double comp = Math.abs(this.data[i] - t.data[i]);
				double lim =  Main.limit[i]/10000000.0;
				if(comp > lim) return false;
			}
			return true;
		}else{
			return super.equals(obj);
		}
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<13;i++){
			sb.append(String.format("%.2f", this.data[i])+" ");
		}
		return sb.toString();
	}
}
