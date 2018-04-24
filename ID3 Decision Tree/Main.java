package DecisionTree;
import java.util.*;
import java.io.*;
public class Main {
	public static String[] totalData = new String[150];
	public static String[] trainingSet = new String[120];
	public static String[] testSet = new String[30];
	public enum Label {Iris_setosa,Iris_versicolor,Iris_virginica};
	public static void main(String[] args) throws Exception {
		LoadData();
		Cross_Valid_5();
	}
	
	public static void LoadData() throws Exception{
		File f = new File("E:/programming/JAVAworkbench/Machine Learning/src/DecisionTree/iris.data.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		for(int i=0;i<150;i++){
			totalData[i] = br.readLine(); 
		}
	}
	public static void Cross_Valid_5(){
		System.out.println("本次5折交叉验证的正确率分别为：");
		String[] temp = totalData.clone();
		Random r = new Random();
		double[] accuracy = new double[5];
		for(int i=temp.length-1;i>0;i--){
			int k = r.nextInt(i+1);
			String tempS = temp[k];
			temp[k] = temp[i];
			temp[i] = tempS;
		}
		for(int i=0;i<120;i++){
			trainingSet[i] = temp[i];
		}
		for(int i=0;i<30;i++){
			testSet[i] = temp[120+i];
		}
//		accuracy[0] = BuildTree(trainingSet,testSet);
//		accuracy[0] = BuildPostPrunningTree(trainingSet,testSet);
		accuracy[0] = BuildPrePrunningTree(trainingSet,testSet);
		for(int i=0;i<90 || (i>=120&&i<150);i++){
			if(i>=120){
				trainingSet[i-30] = temp[i];
				continue;
			}
			trainingSet[i] = temp[i];
			if(i==89) i = 120;
		}
		for(int i=0;i<30;i++){
			testSet[i] = temp[90+i];
		}
//		accuracy[1] = BuildTree(trainingSet,testSet);
//		accuracy[1] = BuildPostPrunningTree(trainingSet,testSet);
		accuracy[1] = BuildPrePrunningTree(trainingSet,testSet);
		for(int i=0;i<60 || (i>=90&&i<150);i++){
			if(i>=90){
				trainingSet[i-30] = temp[i];
				continue;
			}
			trainingSet[i] = temp[i];
			if(i==59) i = 90;
		}
		for(int i=0;i<30;i++){
			testSet[i] = temp[60+i];
		}
//		accuracy[2] = BuildTree(trainingSet,testSet);
//		accuracy[2] = BuildPostPrunningTree(trainingSet,testSet);
		accuracy[2] = BuildPrePrunningTree(trainingSet,testSet);
		for(int i=0;i<30 || (i>=60&&i<150);i++){
			if(i>=60){
				trainingSet[i-30] = temp[i];
				continue;
			}
			trainingSet[i] = temp[i];
			if(i==29) i = 60;
		}
		for(int i=0;i<30;i++){
			testSet[i] = temp[30+i];
		}
//		accuracy[3] = BuildTree(trainingSet,testSet);
//		accuracy[3] = BuildPostPrunningTree(trainingSet,testSet);
		accuracy[3] = BuildPrePrunningTree(trainingSet,testSet);
		for(int i=30;i<150;i++){
			trainingSet[i-30] = temp[i];
		}
		for(int i=0;i<30;i++){
			testSet[i] = temp[i];
		}
//		accuracy[4] = BuildTree(trainingSet,testSet);
//		accuracy[4] = BuildPostPrunningTree(trainingSet,testSet);
		accuracy[4] = BuildPrePrunningTree(trainingSet,testSet);
		System.out.print("平均正确率是：");
		double average = (accuracy[0]+accuracy[1]+accuracy[2]+accuracy[3]+accuracy[4])*20;
		System.out.println(String.format("%.2f", average)+"%");
	}
	public static double BuildTree(String[] training,String[] test){
		//build a tree from trainingSet and return the accuracy on testSet
		TreeNode root = new TreeNode();
		for(int i=0;i<training.length;i++){
			root.addTrainingCase(training[i]);
		}
		root.getOriEntAndLabel();
		if(!root.isLeaf){
			root.produceChild();
		}
		int re = 0;
		for(int i=0;i<training.length;i++){
			re += root.test(training[i]);
		}
		double result = (double)re/(double)training.length;
		String RRR = String.format("%.2f", result*100);
		System.out.print("训练集: "+RRR+"%"+" ("+re+"/120) \t ");
		
		re = 0;
		for(int i=0;i<test.length;i++){
			re += root.test(test[i]);
		}
		result = (double)re/(double)test.length;
		RRR = String.format("%.2f", result*100);
		System.out.println("测试集: "+RRR+"%"+" ("+re+"/30)");
		return result;
	}
	
	public static double BuildPostPrunningTree(String[] training,String[] test){
		//build a tree from trainingSet and return the accuracy on testSet
		TreeNode root = new TreeNode();
		for(int i=0;i<training.length;i++){
			root.addTrainingCase(training[i]);
		}
		root.getOriEntAndLabel();
		if(!root.isLeaf){
			root.produceChild();
		}
		
		//post-pruning
		//1.traverse and get all nodes
		ArrayList<TreeNode> t = new ArrayList<TreeNode>();
		traverseTree(root,t);
		System.out.print("此棵树原有"+t.size()+"个节点。");
		//2.compare test accuracy
		int re = 0;
		for(int i=0;i<test.length;i++){
			re += root.test(test[i]);
		}
		double Cur_acc = (double)re/(double)test.length;
		while(!t.isEmpty()){
			TreeNode x = t.get(0);
			if(x.isLeaf){
				t.remove(0);
				continue;
			}
			x.isLeaf = true;
			re = 0;
			for(int i=0;i<test.length;i++){
				re += root.test(test[i]);
			}
			double result = (double)re/(double)test.length;
			if(result> Cur_acc){
				Cur_acc = result;
				t.remove(0);
//				System.out.println("剪枝一次");
				continue;
			}else{
				x.isLeaf = false;
				t.remove(0);
			}
		}
		traverseTree(root,t);
		System.out.println("剪枝之后，剩下"+t.size()+"个节点。");
		
		re = 0;
		for(int i=0;i<training.length;i++){
			re += root.test(training[i]);
		}
		double result = (double)re/(double)training.length;
		String RRR = String.format("%.2f", result*100);
		System.out.print("训练集: "+RRR+"%"+" ("+re+"/120) \t ");
		
		re = 0;
		for(int i=0;i<test.length;i++){
			re += root.test(test[i]);
		}
		result = (double)re/(double)test.length;
		RRR = String.format("%.2f", result*100);
		System.out.println("测试集: "+RRR+"%"+" ("+re+"/30)");
		return result;
	}
	
	public static void traverseTree(TreeNode root,ArrayList<TreeNode> t){
		if(root.isLeaf) {
			t.add(root);
			return;
		};
		traverseTree(root.left,t);
		traverseTree(root.right,t);
		t.add(root);
	}
	
	public static double BuildPrePrunningTree(String[] training,String[] test){
		//build a tree from trainingSet and return the accuracy on testSet
		TreeNode root = new TreeNode();
		root.grand = root;
		for(int i=0;i<training.length;i++){
			root.addTrainingCase(training[i]);
		}
		for(int i=0;i<test.length;i++){
			root.addTestCase(test[i]);
		}
		root.getOriEntAndLabel();
		if(!root.isLeaf){
			root.ThinkBeforeProduceChild();
		}
		
		
		int re = 0;
		for(int i=0;i<training.length;i++){
			re += root.test(training[i]);
		}
		double result = (double)re/(double)training.length;
		String RRR = String.format("%.2f", result*100);
		System.out.print("训练集: "+RRR+"%"+" ("+re+"/120) \t ");
		
		re = 0;
		for(int i=0;i<test.length;i++){
			re += root.test(test[i]);
		}
		result = (double)re/(double)test.length;
		RRR = String.format("%.2f", result*100);
		System.out.println("测试集: "+RRR+"%"+" ("+re+"/30)");
		return result;
	}
}

class TreeNode{
	public int Label = -1; // 0:Iris_setosa   1:Iris_versicolor   2:Iris_virginica 
	public boolean isLeaf = false;
	public int feature = -1; // 0 1 2 3
//	public boolean featureFixed[] = {false,false,false,false};
	public double splitValue = -1.0;
	public TreeNode left;
	public TreeNode right;
	public TreeNode grand;
	public ArrayList<String> TestCase;
	public ArrayList<String> TrainingCase;
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
	}
	public void produceChild(){	//选择使信息增益最大的特征和分割值来进行分裂
		TreeSet<Double> allValue = new TreeSet<Double>();
		double minNewEnt = Double.MAX_VALUE;
		String[] leftset = null ;
		String[] rightset = null;
		for(int i=0;i<4;i++){
//			if(featureFixed[i]) continue;
			//对于每个未分特征i，计算该特征下的最大信息增益
			//加入该特征下所有的连续值
//			System.out.print("feature:"+i);
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
				int len1= 0 ,len2 = 0;
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
		for(int i=0;i<leftset.length;i++){
			left.addTrainingCase(leftset[i]);
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
	public void ThinkBeforeProduceChild(){
		int re = 0;
		this.isLeaf = true;
		for(int i=0;i<this.TestCase.size();i++){
			re += this.test(TestCase.get(i));
		}
		double res_1 = (double)re/(double)this.TestCase.size();
		this.isLeaf = false;
		
		TreeSet<Double> allValue = new TreeSet<Double>();
		double minNewEnt = Double.MAX_VALUE;
		String[] leftset = null;
		String[] rightset = null;
		for(int i=0;i<4;i++){
			Iterator<String> it = TrainingCase.iterator();
			while(it.hasNext()){
				String record = it.next();
				double val = Double.parseDouble(record.split(",")[i]);
				allValue.add(val);
			}
			Double[] allValues = new Double[allValue.size()];
			allValues = allValue.toArray(allValues);
			for(int j=0;j<allValues.length-1;j++){
				double split = (allValues[j]+allValues[j+1])/2;
				String[] set1 = new String[TrainingCase.size()];
				String[] set2 = new String[TrainingCase.size()];
				int len1= 0 ,len2 = 0;
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
		left = new TreeNode();
		right = new TreeNode();
		for(int i=0;i<leftset.length;i++){
			left.addTrainingCase(leftset[i]);
		}
		for(int i=0;i<rightset.length;i++){
			right.addTrainingCase(rightset[i]);
		}
		left.getOriEntAndLabel();
		right.getOriEntAndLabel();
		left.isLeaf = true;
		right.isLeaf = true;
		re = 0;
		for(int i=0;i<this.TestCase.size();i++){
			re += this.test(TestCase.get(i));
		}
		double res_2 = (double)re/(double)this.TestCase.size();
		if(res_2<res_1){
			this.isLeaf = true;
			return;
		}
		left.getOriEntAndLabel();
		right.getOriEntAndLabel();
		if(!left.isLeaf) left.ThinkBeforeProduceChild();
		if(!right.isLeaf) right.ThinkBeforeProduceChild();
		

	}
	
	
	public TreeNode(){
		left = null;
		right = null;
		TestCase = new ArrayList<String>();
		TrainingCase = new ArrayList<String>();
	}
	public TreeNode(boolean[] fatherFeature){
		left = null;
		right = null;
		TestCase = new ArrayList<String>();
		TrainingCase = new ArrayList<String>();
//		this.featureFixed = fatherFeature.clone();		
	}
	public void addTestCase(String t){	//由父节点向子节点调用
		TestCase.add(t);
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
