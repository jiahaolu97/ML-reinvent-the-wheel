package Bayes;
import java.io.*;
import java.util.Arrays;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
public class Main {
	public static void main(String[] args) throws Exception {
		File file = new File("E:/programming/JAVAworkbench/Machine Learning/src/Bayes/car evaluation dataset.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		int totalData = 1728;
		int trainingData = 1350;
		int testData = totalData - trainingData;
//		int testData = 8;
		int correct = 0;
		int incorrect = 0;
//		String[] choose = {"unacc","acc","good","vgood"};
		
		int buying[][] = new int[4][4];		//v-high, high, med, low
		int maint[][] = new int[4][4];		//v-high, high, med, low
		int doors[][] = new int[4][4];		//2, 3, 4, 5-more
		int persons[][] = new int[4][3];	//2, 4, more
		int lugboot[][] = new int[4][3];	//small, med, big
		int safety[][] = new int[4][3];		//low, med, high
		int unacc = 0,acc=0,good=0,vgood = 0;
		
		for(int i=0;i<trainingData;i++){
			String line[] = br.readLine().split(",");
			int c=0,f=0;
			switch(line[6]){
			case "unacc":c = 0;unacc++;break;
			case "acc":c = 1;acc++;break;
			case "good": c = 2;good++;break;
			case "vgood": c = 3;vgood++;break;
			}
			switch(line[0]){
			case "vhigh": f=0;break;
			case "high": f=1;break;
			case "med": f=2;break;
			case "low": f=3;break;
			}
			buying[c][f] ++;
			switch(line[1]){
			case "vhigh": f=0;break;
			case "high": f=1;break;
			case "med": f=2;break;
			case "low": f=3;break;
			}
			maint[c][f] ++;
			switch(line[2]){
			case "2": f=0;break;
			case "3": f=1;break;
			case "4": f=2;break;
			case "5more": f=3;break;
			}
			doors[c][f] ++;
			switch(line[3]){
			case "2": f=0;break;
			case "4": f=1;break;
			case "more": f=2;break;
			}
			persons[c][f] ++;
			switch(line[4]){
			case "small": f=0;break;
			case "med": f=1;break;
			case "big": f=2;break;
			}
			lugboot[c][f]++;
			switch(line[5]){
			case "low": f=0;break;
			case "med": f=1;break;
			case "high": f=2;break;
			}
			safety[c][f] ++;
		}

		
//		printMat("buying",buying );
//		printMat("maint",maint );
//		printMat("doors",doors );
//		printMat("persons",persons );
//		printMat("lugboot",lugboot );
//		printMat("safety",safety );
		
		
		double p_buying[][] = new double[5][4];		//v-high, high, med, low
		double p_maint[][] = new double[5][4];		//v-high, high, med, low
		double p_doors[][] = new double[5][4];		//2, 3, 4, 5-more
		double p_persons[][] = new double[5][3];	//2, 4, more
		double p_lugboot[][] = new double[5][3];	//small, med, big
		double p_safety[][] = new double[5][3];		//low, med, high
		
		int[] classes = {unacc,acc,good,vgood};
		double[] p_classes = {(double)unacc/trainingData,(double)acc/trainingData,(double)good/trainingData,(double)vgood/trainingData};
//		System.out.println("p_classes:"+printArr(p_classes));

		int orientation = 0,bigone = -1;
		for(int i=0;i<classes.length;i++){
			if(classes[i]>bigone){
				bigone = classes[i];
				orientation = i;
			}
		}
		double maxProb = p_classes[orientation];
		
		
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				if(classes[j]==0){
					p_buying[j][i] = 0;
					p_maint[j][i] = 0;
					p_doors[j][i] = 0;
					continue;
				}
				p_buying[j][i] = (double)buying[j][i]/classes[j];
				p_maint[j][i] = (double)maint[j][i]/classes[j];
				p_doors[j][i] = (double)doors[j][i]/classes[j];
			}
			p_buying[4][i] = (double)(buying[0][i]+buying[1][i]+buying[2][i]+buying[3][i])/trainingData;
			p_maint[4][i] = (double)(maint[0][i]+maint[1][i]+maint[2][i]+maint[3][i])/trainingData;
			p_doors[4][i] = (double)(doors[0][i]+doors[1][i]+doors[2][i]+doors[3][i])/trainingData;
		}
		for(int i=0;i<3;i++){
			
			for(int j=0;j<4;j++){
				if(classes[j]==0){
					p_persons[j][i] = 0;
					p_lugboot[j][i] = 0;
					p_safety[j][i] = 0;
					continue;
				}
				p_persons[j][i] = (double)persons[j][i]/classes[j];
				p_lugboot[j][i] = (double)lugboot[j][i]/classes[j];
				p_safety[j][i] = (double)safety[j][i]/classes[j];
			}
			p_persons[4][i] = (double)(persons[0][i]+persons[1][i]+persons[2][i]+persons[3][i])/trainingData;
			p_lugboot[4][i] = (double)(lugboot[0][i]+lugboot[1][i]+lugboot[2][i]+lugboot[3][i])/trainingData;
			p_safety[4][i] = (double)(safety[0][i]+safety[1][i]+safety[2][i]+safety[3][i])/trainingData;
		}
		
//		printMat("p_buying",p_buying );
//		printMat("p_maint",p_maint );
//		printMat("p_doors",p_doors );
//		printMat("p_persons",p_persons );
//		printMat("p_lugboot",p_lugboot );
//		printMat("p_safety",p_safety );

		for(int i=0;i<testData;i++){
			String tryLine = br.readLine();
			String[] line = tryLine.split(",");
			int groundtruth = -1;
			int bayes = -1;
			double curProb = -1.0;
			switch(line[6]){
			case "unacc":groundtruth = 0;break;
			case "acc":groundtruth = 1;break;
			case "good":groundtruth = 2;break;
			case "vgood":groundtruth = 3;break;
			}
			double p[] = new double[4];
			Arrays.fill(p, 1.0);
			for(int c=0;c<4;c++){
				int f = 0;
				p[c] *= p_classes[c];
				switch(line[0]){
				case "vhigh": f=0;break;
				case "high": f=1;break;
				case "med": f=2;break;
				case "low": f=3;break;
				}
				p[c] *= (p_buying[c][f]/p_buying[4][f]);
				switch(line[1]){
				case "vhigh": f=0;break;
				case "high": f=1;break;
				case "med": f=2;break;
				case "low": f=3;break;
				}
				p[c] *= (p_maint[c][f]/p_maint[4][f]);
				switch(line[2]){
				case "2": f=0;break;
				case "3": f=1;break;
				case "4": f=2;break;
				case "5more": f=3;break;
				}
				p[c] *= (p_doors[c][f]/p_doors[4][f]);
				switch(line[3]){
				case "2": f=0;break;
				case "4": f=1;break;
				case "more": f=2;break;
				}
				p[c] *= (p_persons[c][f]/p_persons[4][f]);
				switch(line[4]){
				case "small": f=0;break;
				case "med": f=1;break;
				case "big": f=2;break;
				}
				p[c] *= (p_lugboot[c][f]/p_lugboot[4][f]);
				switch(line[5]){
				case "low": f=0;break;
				case "med": f=1;break;
				case "high": f=2;break;
				}
				p[c] *= (p_safety[c][f]/p_safety[4][f]);
				if(p[c]>curProb){
					curProb = p[c];
					bayes = c;
				}
				if(Double.isNaN(p[c])){
					bayes = orientation;
					curProb = maxProb;
					break;
				}
			}
			
//			System.out.println("样本"+i+"选择类别"+(bayes+1)+",概率："+ printArr(p));
			if(bayes == groundtruth){
				correct++;
//				System.out.println(tryLine);
			}
			else{
				incorrect ++;
//				System.out.println(tryLine + "   " + bayes);
			}	
		}
		
		double corrate = (double)correct*100/testData;
		System.out.println("以前"+trainingData+"个数据作为训练集，在"+testData+"个测试数据中：");
		System.out.println("正确分类"+correct+"个，错误分类"+incorrect+"个，正确率为"+String.format("%.2f", corrate)+"%。");
	}
	
	public static void printMat(String name,double[][] mat){
		System.out.println(name+":");
		for(int i=0;i<mat.length;i++){
			for(int j=0;j<mat[i].length;j++){
				System.out.print(String.format("%.2f", mat[i][j])+"\t");
			}
			System.out.println();
		}
	}
	public static void printMat(String name,int[][] mat){
		System.out.println(name+":");
		for(int i=0;i<mat.length;i++){
			for(int j=0;j<mat[i].length;j++){
				System.out.print(mat[i][j]+"\t");
			}
			System.out.println();
		}
	}
	public static String printArr(double[] arr){
		String res = "";
		for(int i=0;i<arr.length;i++){
			res += String.format("%.2f",arr[i]);
			res += "  \t";
			if(arr[i]==0.0) res+=" \t";
		}
		return res;
	}
}
