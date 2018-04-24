package RandomForest;

import java.io.*;
import java.util.*;


public class Main {
	public static Random R = new Random();
	public static int NumberOfTrees = 9;
	public static TreeNode[] trees;
	public static String[] totalData = new String[150];
	public enum Label {Iris_setosa,Iris_versicolor,Iris_virginica};
	public static void main(String[] args) throws Exception {
		LoadData();
		trees = new TreeNode[NumberOfTrees];
		for(int i=0;i<NumberOfTrees;i++){
//			trees[i] = Grow1TreeWith2Feature();
			trees[i] = Grow1Tree();
//			System.out.println(i+" built");
		}
		int totalCorrect = 0;
		int tC = 0;
		for(int i=0;i<150;i++){
			String record = totalData[i];
			int correct = 0;
			for(int j=0;j<NumberOfTrees;j++){
				correct += trees[j].test(record);
			}
			totalCorrect += correct;
			double accuracy = (double)correct/(double)NumberOfTrees;
			String acu = String.format("%.2f", accuracy*100) + "%";
			String x = null;
			if(accuracy>0.5){
				x = "正确";
				tC ++;
			} 
			else x = "不正确";
			System.out.println("样本"+(i+1)+":正确率为"+acu+" \t("+correct+"/"+NumberOfTrees+")\t"+x);
		}
		double accuracy = (double)totalCorrect/(double)(NumberOfTrees*150);
		String acu = String.format("%.2f", accuracy*100) + "%";
		System.out.println("当森林中的决策树数量为"+NumberOfTrees+"时，");
		System.out.println("所有基学习器正确率为:"+acu+" \t("+totalCorrect+"/"+NumberOfTrees*150+")");
		accuracy = (double)tC/150.0;
		acu = String.format("%.2f", accuracy*100) + "%";
		System.out.println("总正确率为:"+acu+" \t("+tC+"/"+150+")");
	}

	public static void LoadData() throws Exception{
		File f = new File("E:/programming/JAVAworkbench/Machine Learning/src/RandomForest/iris.data.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		for(int i=0;i<150;i++){
			totalData[i] = br.readLine(); 
		}
	}
	
	public static TreeNode Grow1Tree(){
		TreeNode result = new TreeNode();
		for(int i=0;i<150;i++){
			int choose = R.nextInt(150);
			result.addTrainingCase(totalData[choose]);
		}
		result.getOriEntAndLabel();
		if(!result.isLeaf){
			result.produceChild();
		}
		return result;
	}

	public static TreeNode Grow1TreeWith2Feature(){
		TreeNode result = new TreeNode();
		for(int i=0;i<150;i++){
			int choose = R.nextInt(150);
			result.addTrainingCase(totalData[choose]);
		}
		result.getOriEntAndLabel();
		if(!result.isLeaf){
			result.produceChildWithFeature();
		}
		return result;
	}

}

class TreeNode{
	public int Label = -1; // 0:Iris_setosa   1:Iris_versicolor   2:Iris_virginica 
	public boolean isLeaf = false;
	public int feature = -1; // 0 1 2 3
	public double splitValue = -1.0;
	public TreeNode left;
	public TreeNode right;
	public ArrayList<String> TrainingCase;
	public int[] feat;
	public double oriEnt = -1;	//未继续细分时的信息熵
	//每个TreeNode 在接下来可以选择的feature中,选择一个feature,计算信息增益
	//计算信息增益时，要取遍每一个可能值的中值，最后取最大的信息增益。
	public double f(double p){
		if(p==0) return 0;
		return -p*Math.log(p)/Math.log(2);
	}
	public double Ent(String[] set,int len){
		if(len==0) return 0;
		double res = 0;
		int vote[] = new int[3];
		for(int i=0;i<len;i++){
			String record = set[i];
//			System.out.println(record);
			String l = record.split(",")[4];
			switch(l){
			case "Iris-setosa": vote[0]++; break;
			case "Iris-versicolor": vote[1]++;break;
			case "Iris-virginica": vote[2]++; break;
			}
		}
		double p[] = {(double)vote[0]/len,(double)vote[1]/len,(double)vote[2]/len};
		res = f(p[0])+f(p[1])+f(p[2]);
		return res;
	}
	public void getOriEntAndLabel(){
		int vote[] = new int[3];
		int cnt = 0;
		for(String x:TrainingCase){
			cnt++;
//			System.out.println(cnt+"   "+x);
			if(x == null) break;
			String l = x.split(",")[4];
			switch(l){
			case "Iris-setosa": vote[0]++; break;
			case "Iris-versicolor": vote[1]++;break;
			case "Iris-virginica": vote[2]++; break;
			}
		}
		int max = -1;
		if(vote[0]>vote[1]){ Label = 0; max = vote[0];}
		else {Label = 1; max = vote[1];}
		if(vote[2]>max) { Label = 2;max = vote[2];}
		int size = TrainingCase.size();
		double p[] = {(double)vote[0]/size,(double)vote[1]/size,(double)vote[2]/size};
		oriEnt = f(p[0])+f(p[1])+f(p[2]);
		if(oriEnt == 0) isLeaf = true;
//		System.out.println("Original Ent is "+oriEnt + " Original label is "+ Label);
	}
	public void produceChild(){	//选择使信息增益最大的特征和分割值来进行分裂
		TreeSet<Double> allValue = new TreeSet<Double>();
		double minNewEnt = Double.MAX_VALUE;
		String[] leftset = null ;
		String[] rightset = null;
		int choices[][] = {{0,1},{0,2},{0,3},{1,2},{1,3},{2,3}};
		int cc = Main.R.nextInt(6);
		for(int b=0;b<2;b++){
			int i = choices[cc][b];
			//对于每个未分特征i，计算该特征下的最大信息增益
			//加入该特征下所有的连续值
			Iterator<String> it = TrainingCase.iterator();
			while(it.hasNext()){
				String record = it.next();
				double val = Double.parseDouble(record.split(",")[i]);
				allValue.add(val);
//				System.out.println(val);
			}
			Double[] allValues = new Double[allValue.size()];
			allValues = allValue.toArray(allValues);
			for(int j=0;j<allValues.length-1;j++){
//				System.out.println(allValues[j]);
				double split = (allValues[j]+allValues[j+1])/2;
				String[] set1 = new String[TrainingCase.size()];
				String[] set2 = new String[TrainingCase.size()];
				int len1 = 0 ,len2 = 0;
				for(String x:TrainingCase){
					if(Double.parseDouble(x.split(",")[i])<= split){
						set1[len1] = x;
						len1++;
					}else{
						set2[len2] = x;
						len2++;
					}
				}
				double newEnt = (double)len1/(len1+len2)*Ent(set1,len1) + (double)len2/(len1+len2)*Ent(set2,len2);
//				System.out.println(newEnt);
//				System.out.println("len1:"+len1 +" len2:"+len2 + " Ent1:"+Ent(set1,len1)+" Ent2:"+Ent(set2,len2));
//				System.out.println(" and newEnt is "+newEnt);
				if(newEnt < minNewEnt){
					minNewEnt = newEnt;
					feature = i;
					splitValue = split;
					leftset = new String[len1];
					for(int k=0;k<len1;k++){
						leftset[k] = set1[k];
					}
					rightset = new String[len2];
					for(int k=0;k<len2;k++){
						rightset[k] = set2[k];
					}
				}
			}
		}
//		featureFixed[feature] = true;
//		System.out.println("This node choose feature"+feature);
//		left = new TreeNode(this.featureFixed);
		left = new TreeNode();
//		right = new TreeNode(this.featureFixed);
		right = new TreeNode();
		
		if(leftset==null||rightset==null){
			this.isLeaf = true;
			return;
		}
		
		for(int kk=0;kk<leftset.length;kk++){
			left.addTrainingCase(leftset[kk]);
		}
		for(int i=0;i<rightset.length;i++){
			right.addTrainingCase(rightset[i]);
		}
//		System.out.println("left born"+" "+leftset.length+" case");
		left.getOriEntAndLabel();
		if(!left.isLeaf) left.produceChild();
//		else System.out.println("left is leaf!");
//		System.out.println("right born"+" "+rightset.length+ " case");
		right.getOriEntAndLabel();
		if(!right.isLeaf) right.produceChild();
//		else System.out.println("right is leaf!");
	}
	
	public void produceChildWithFeature(){
		TreeSet<Double> allValue = new TreeSet<Double>();
		double minNewEnt = Double.MAX_VALUE;
		String[] leftset = null ;
		String[] rightset = null;
		for(int b=0;b<2;b++){
			int i = this.feat[b];
			//对于每个未分特征i，计算该特征下的最大信息增益
			//加入该特征下所有的连续值
			Iterator<String> it = TrainingCase.iterator();
			while(it.hasNext()){
				String record = it.next();
				double val = Double.parseDouble(record.split(",")[i]);
				allValue.add(val);
//				System.out.println(val);
			}
			Double[] allValues = new Double[allValue.size()];
			allValues = allValue.toArray(allValues);
			for(int j=0;j<allValues.length-1;j++){
//				System.out.println(allValues[j]);
				double split = (allValues[j]+allValues[j+1])/2;
				String[] set1 = new String[TrainingCase.size()];
				String[] set2 = new String[TrainingCase.size()];
				int len1 = 0 ,len2 = 0;
				for(String x:TrainingCase){
					if(Double.parseDouble(x.split(",")[i])<= split){
						set1[len1] = x;
						len1++;
					}else{
						set2[len2] = x;
						len2++;
					}
				}
				double newEnt = (double)len1/(len1+len2)*Ent(set1,len1) + (double)len2/(len1+len2)*Ent(set2,len2);
//				System.out.println(newEnt);
//				System.out.println("len1:"+len1 +" len2:"+len2 + " Ent1:"+Ent(set1,len1)+" Ent2:"+Ent(set2,len2));
//				System.out.println(" and newEnt is "+newEnt);
				if(newEnt < minNewEnt){
					minNewEnt = newEnt;
					feature = i;
					splitValue = split;
					leftset = new String[len1];
					for(int k=0;k<len1;k++){
						leftset[k] = set1[k];
					}
					rightset = new String[len2];
					for(int k=0;k<len2;k++){
						rightset[k] = set2[k];
					}
				}
			}
		}
//		featureFixed[feature] = true;
//		System.out.println("This node choose feature"+feature);
//		left = new TreeNode(this.featureFixed);
		left = new TreeNode(this.feat);
//		right = new TreeNode(this.featureFixed);
		right = new TreeNode(this.feat);
		
		if(leftset==null||rightset==null){
			this.isLeaf = true;
			return;
		}
		
		for(int kk=0;kk<leftset.length;kk++){
			left.addTrainingCase(leftset[kk]);
		}
		for(int i=0;i<rightset.length;i++){
			right.addTrainingCase(rightset[i]);
		}
//		System.out.println("left born"+" "+leftset.length+" case");
		left.getOriEntAndLabel();
		if(!left.isLeaf) left.produceChildWithFeature();
//		else System.out.println("left is leaf!");
//		System.out.println("right born"+" "+rightset.length+ " case");
		right.getOriEntAndLabel();
		if(!right.isLeaf) right.produceChildWithFeature();
//		else System.out.println("right is leaf!");
		
	}
	
	
	public TreeNode(){
		left = null;
		right = null;
		TrainingCase = new ArrayList<String>();
		int choices[][] = {{0,1},{0,2},{0,3},{1,2},{1,3},{2,3}};
		int cc = Main.R.nextInt(6);
		feat = choices[cc];
	}

	public TreeNode(int[] feat){
		left = null;
		right = null;
		TrainingCase = new ArrayList<String>();
		this.feat = feat;
	}
	
	public void addTrainingCase(String t){
		TrainingCase.add(t);
	}
	public int test(String record){	//  1 正确  0 不正确
		if(!isLeaf){
			double t = Double.parseDouble(record.split(",")[feature]);
			if(t<=splitValue) return left.test(record);
			else return right.test(record);
		}
		String[] l = {"Iris-setosa","Iris-versicolor","Iris-virginica"};
		if(l[Label].equals(record.split(",")[4])){
			return 1;
		}
		return 0;
	}
}
