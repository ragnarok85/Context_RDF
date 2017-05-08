package tamps.cinvestav.mx.testSomething;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import tamps.cinvestav.mx.Multiprototype.MovMF;
import tamps.cinvestav.mx.utils.jri.VonMisesFisher;

public class TestMovMF {
	Rengine re = new Rengine(new String[]{"--vanilla"}, false, null);
	
	public static void main(String[] args){
		VonMisesFisher vmf = new VonMisesFisher();
		List<MovMF> movMFs = new ArrayList<MovMF>();
		String corpusPath = "C:\\Users\\jnht2\\Desktop\\Corpus_Vector\\";
		int numberClusters = 5;
		movMFs = vmf.applyVonMisesFisherDistribution(corpusPath, numberClusters);
		
		printWords(movMFs);
		
	}
	
	public static void printWords(List<MovMF> movMFs){
		try{
			PrintWriter pw = new PrintWriter(new FileWriter("movMF.txt"));
			PrintWriter pw10 = new PrintWriter(new FileWriter("movMF10.txt"));
			PrintWriter pw20 = new PrintWriter(new FileWriter("movMF20.txt"));
			for(MovMF mfs : movMFs){
				int clusterCounter = 1;
				//System.out.println("main word\t" + mfs.getMainWord());
				pw.write("\nmain word\t" + mfs.getMainWord()+"\n");
				pw10.write("\nmain word\t" + mfs.getMainWord()+"\n");
				pw20.write("\nmain word\t" + mfs.getMainWord()+"\n");
				for(int i = 0 ; i < mfs.getNumCol(); i++){
					pw.write("Cluster number\t"+clusterCounter+"\n");
					pw10.write("Cluster number\t"+clusterCounter+"\n");
					pw20.write("Cluster number\t"+clusterCounter+"\n");
					clusterCounter++;
					//pw.write("word\n");
					for(int j = 0; j < mfs.getNumRow(); j++){
						if(j < 10){
							pw10.write(mfs.getClustersWords()[j][i]+"\n");
						}else if(j < 20){
							pw20.write(mfs.getClustersWords()[j][i]+"\n");
						}
						pw.write(mfs.getClustersWords()[j][i]+"\n");
					}
				}
			}
			pw.close();
			pw10.close();
			pw20.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
//	public static void main(String[] args){
//		TestMovMF test = new TestMovMF();
//		String file = "TestMovMF/look.txt";
//		String index = "TestMovMF/indexxxx.txt";
//		int numCol = 0;
//		int numRow = 0;
//		test.loadData(file);
//		test.applyMovMF();
//		test.loadIndex(index);
//		test.applyIndex();
//		test.applyClusterNames();
//		test.applyClusterValues();
//		
//		REXP cluster_values = test.getClusterValues();
//		String cluster_name[][] = test.getClustersNames();
//		
//		//Processing the matrices
//		double cluster_val[][] = cluster_values.asDoubleMatrix();
//		numCol = test.getNumCol();
//		numRow = test.getNumRow();
//		System.out.println("numRow = " + numRow + " numCol = " + numCol);
//		
//		File path = new File(file);
//		
//		System.out.println("data for word: " + path.getName().replace(".txt",""));
//		
//		for(int i = 0; i < numCol; i++ ){
//			double centroid = 0;
//			for(int j = 0; j < numRow; j++){
//				if(cluster_val[j][i] != new Double(0)){
//					System.out.println(j +" - "+i+" - "+cluster_name[j][i]+" - " + cluster_val[j][i]);
//					centroid = centroid + cluster_val[j][i];
//				}
//				
//			}
//			centroid = centroid/numRow;
//			System.out.println("centroid for cluster "+i+" = " + centroid);
//		}
//		test.re.interrupt();
//		
//	}
	
	public TestMovMF(){
		loadLibraries();
	}
	
	//step 1: Load libraries
	public void loadLibraries(){
		re.eval("library('NLP')");
		re.eval("library('tm')");
		re.eval("library('slam')");
		re.eval("library('movMF)");
	}
	
	//step 2: Load table data
	//file must be content the full path of file.
	public void loadData(String file){
		File path = new File(file);
		System.out.println(path.getAbsolutePath().replace("\\", "\\\\"));
		re.eval("windows <- read.table('"+path.getAbsolutePath().replace("\\", "\\\\")+"')");
		re.eval("windows_matrix <- data.matrix(windows)");
	}
	
	//step 3: apply movMF function
	public void applyMovMF(){
		re.eval("library(\"movMF\")");
		re.eval("windows_movMF <- movMF(windows_matrix, k=nrow(windows_matrix)-1, nruns=20, kappa=list(common=TRUE))");
		System.out.println(re.eval("ls()"));
	}
	
	//step 4: load index
	public void loadIndex(String index){
		File path = new File(index);
		System.out.println(path.getAbsolutePath().replace("\\", "\\\\"));
		re.eval("index <- read.table('"+path.getAbsolutePath().replace("\\", "\\\\")+"')");
		re.eval("index_names <- unlist(index)");
	}
	
	//step 5: apply index to movMF result theta
	public void applyIndex(){
		re.eval("colnames(windows_movMF$theta) <- index_names");
	}
	
	//step 6.1: get words in decreasing order
	public void applyClusterNames(){
		re.eval("clusters_names <- apply(coef(windows_movMF)$theta, 1, function(x)"
				+ "colnames(coef(windows_movMF)$theta)[order(x,decreasing=TRUE)])");
	}
	
	//step 6.2: get values in decreasing order
	public void applyClusterValues(){
		re.eval("clusters_values <- apply(coef(windows_movMF)$theta, 1, function(x)"
				+ " coef(windows_movMF)$theta[order(x,decreasing=TRUE)])");
	}
	
	//step 7: get clusters_values
	public REXP getClusterValues(){
		System.out.println(re.eval("ls()"));
		return re.eval("clusters_values");
	}
	
	//step 8: get clusters_names
	public String[][] getClustersNames() {
		int numCol = re.eval("ncol(clusters_names)").asInt();
		int numRow = re.eval("nrow(clusters_names)").asInt();

		String[][] clusterNames = new String[numRow][numCol];

		for (int i = 1; i <= numCol; i++) {
			System.out.println("clusters_names[," + i + "]");
			String clusterCol[] = re.eval("clusters_names[," + i + "]").asStringArray();
			for (int j = 0; j < numRow; j++) {
				clusterNames[j][i-1] = clusterCol[j];
			}

		}
		return clusterNames;
	}
	
	public int getNumCol(){
		int col = 0;
		REXP ncol = re.eval("ncol(clusters_values)");
		col = ncol.asInt();
		return col;
	}
	
	public int getNumRow(){
		int row = 0;
		REXP nrow = re.eval("nrow(clusters_values)");
		row = nrow.asInt();
		return row;
	}
}
