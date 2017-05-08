package tamps.cinvestav.mx.utils.jri;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import tamps.cinvestav.mx.Multiprototype.MovMF;

public class VonMisesFisher {
	private Rengine re = new Rengine(new String[]{"--vanilla"}, false, null);
	
	public List<MovMF> applyVonMisesFisherDistribution(String corpusPath, int numberClusters){
		List<File> files = getCorpusFiles(corpusPath+"/");
		String index = new File(corpusPath+"indexxxx.txt").getAbsolutePath();
		//System.out.println("index = " + index);
		List<MovMF> movMFs = new ArrayList<MovMF>();
		
		loadIndex(index);
		for(File absolutePath : files){
			if(absolutePath.getName().equals("allocations.txt")){
				System.out.println("alo");
			}
			System.out.println("Processing File: " + absolutePath.getName());
//			List<Map<String,Double>> clusters = new ArrayList<Map<String,Double>>();
			MovMF movMF = new MovMF();
			int numCol = 0;
			int numRow = 0;
			String mainWord = absolutePath.getName().replace(".txt","");
			if(mainWord.equals("indexxxx") || mainWord.equals("GeneralInformation") || mainWord.equals("aaaaa") || mainWord.equals("aaaaa2")){
				continue;
			}
			loadLibraries();
			//System.out.println("file absolute Path = " + absolutePath.getAbsolutePath());
			loadData(absolutePath.getAbsolutePath());
			applyMovMF(numberClusters);
			List<Integer> movMFClusters = getClusters();
			//applyIndex();
			//applyClusterNames();
			//applyClusterValues();
//			writeNames(mainWord);
//			writeValues(mainWord);
			//Processing the matrices
			//REXP cluster_values = getClusterValues();
			//double cluster_val[][] = cluster_values.asDoubleMatrix();
			//double cluster_val[][] = getClustersValues();
			//String cluster_name[][] = getClustersNames(cluster_val, mainWord);
			numCol = getNumCol();
			numRow = getNumRow();
			
			
			
			//movMF.setClusterVal(cluster_val);
			//movMF.setClustersWords(cluster_name);
			movMF.setMainWord(mainWord);
			movMF.setNumCol(numCol);
			movMF.setNumRow(numRow);
			movMF.setMovMFClusters(movMFClusters);
			
			movMFs.add(movMF);
			
			//System.out.println("numRow = " + numRow + " numCol = " + numCol);
			//System.out.println("data for word: " + mainWord);
		}
		
		//re.interrupt();
		System.out.println("Finish work");
		return movMFs;
	}
	
//	public void writeNames(String mainWord){
//		re.eval("write(clusters_names,file='C:\\Users\\jnht2\\Desktop\\MovMF\\"+mainWord+"Names.txt',ncolumns = ncol(clusters_names), append = FALSE, sep='\\t')");
//	}
//	
//	public void writeValues(String mainWord){
//		re.eval("write(clusters_values,file=\"C:\\Users\\jnht2\\Desktop\\MovMF\\"+mainWord+"Values.txt\""+",ncolumns = ncol(clusters_values), append = FALSE, sep=\"\\t\")");
//	}
	
	
	//step 1: Load libraries
		public void loadLibraries(){
			re.eval("library('NLP')");
			re.eval("library('tm')");
			re.eval("library('slam')");
		}
		
		//step 2: Load table data
		//file must be content the full path of file.
		public void loadData(String file){
			File path = new File(file);
			//System.out.println(path.getAbsolutePath().replace("\\", "\\\\"));
			re.eval("windows <- read.table('"+path.getAbsolutePath().replace("\\", "\\\\")+"')");
			re.eval("windows_matrix <- data.matrix(windows)");
		}
		
		//step 3: apply movMF function
		public void applyMovMF(int numberClusters){
			re.eval("library(\"movMF\")");
			//re.eval("windows_movMF <- movMF(windows_matrix, k=nrow(windows_matrix)-1, nruns=20, kappa=list(common=TRUE))");
			re.eval("windows_movMF <- movMF(windows_matrix, k="+ numberClusters +", nruns=20, kappa=list(common=TRUE, 'Banerjee_et_al_2005'))");
			//System.out.println(re.eval("ls()"));
		}
		
		//step 4: get clusters
		public List<Integer> getClusters(){
			List<Integer> movMFClusters = new ArrayList<Integer>();
			re.eval("clusters <- predict(windows_movMF)");
			REXP clusters = re.eval("clusters");
			if(clusters!= null){
				for(Integer clusterVal : re.eval("clusters").asIntArray()){
					movMFClusters.add(clusterVal);
				}
			}
			
			return movMFClusters;
		}
		
		//step 4: load index
		public void loadIndex(String index){
			File path = new File(index);
			//System.out.println(path.getAbsolutePath().replace("\\", "\\\\"));
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
			//System.out.println(re.eval("ls()"));
			return re.eval("clusters_values");
		}
		
		public double[][] getClustersValues(){
			int numCol = re.eval("ncol(clusters_names)").asInt();
			int numRow = re.eval("nrow(clusters_names)").asInt();
			double[][] cluster_vals = new double[numRow][numCol];
			
			for (int i = 1; i <= numRow; i++) {
				//System.out.println("clusters_names[," + i + "]");
				double clusterCol[] = re.eval("clusters_values["+i+",]").asDoubleArray();
				for (int j = 0; j < clusterCol.length; j++) {
					cluster_vals[i-1][j] = clusterCol[j];
				}

			}
			
			return cluster_vals;
		}
		//step 8: get clusters_names
		public String[][] getClustersNames(double[][] cluster_vals, String mainWord) {
			int numCol = re.eval("ncol(clusters_names)").asInt();
			int numRow = re.eval("nrow(clusters_names)").asInt();
			
			try{
				PrintWriter pw = new PrintWriter(new FileWriter("C:\\Users\\jnht2\\Desktop\\MovMF\\"+mainWord+"Values.txt"));
				for(int i = 0; i < numCol ; i++){
					for(int j = 0; j < numRow ; j++){
						pw.write(cluster_vals[j][i]+"\t");
					}
					pw.write("\n");
				}
				pw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			

			String[][] clusterNames = new String[numRow][numCol];

//			for (int i = 1; i <= numCol; i++) {
//				//System.out.println("clusters_names[," + i + "]");
//				String clusterCol[] = re.eval("clusters_names[," + i + "]").asStringArray();
//				int k = 0;
//				for (int j = 0; j < numRow; j++) {
//					if(cluster_vals[j][i-1] > 0d){
//						System.out.println("cluster_vals["+j+"]"+"["+(i-1)+"]"+" = " + cluster_vals[j][i-1]);
//						clusterNames[j][i-1] = clusterCol[j];
//					}
//				}
//
//			}
			
			for (int i = 1; i <= numRow; i++) {
				//System.out.println("clusters_names[," + i + "]");
				String clusterCol[] = re.eval("clusters_names["+i+",]").asStringArray();
				int k = 0;
				for (int j = 0; j < clusterCol.length; j++) {
					if(cluster_vals[i-1][j] > 0d){
						//System.out.println("cluster_vals["+(i-1)+"]"+"["+j+"]"+" = " + cluster_vals[i-1][j]);
						clusterNames[i-1][j] = clusterCol[j];
					}
				}

			}
			
			try{
				PrintWriter pw = new PrintWriter(new FileWriter("C:\\Users\\jnht2\\Desktop\\MovMF\\"+mainWord+"Words.txt"));
				for(int i = 0; i < numCol ; i++){
					for(int j = 0; j < numRow ; j++){
						pw.write(clusterNames[j][i]+"\t");
					}
					pw.write("\n");
				}
				pw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return clusterNames;
		}
		
		//Utility functions
		public int getNumCol(){
			//return re.eval("ncol(clusters_values)").asInt();
			return re.eval("ncol(windows_matrix)").asInt();
		}
		
		public int getNumRow(){
			//return re.eval("nrow(clusters_values)").asInt();
			return re.eval("nrow(windows_matrix)").asInt();
		}
		
		public List<File> getCorpusFiles(String corpusPath){
			List<File> files = new ArrayList<File>();
			File directory = new File(corpusPath);
			
			for(File file : directory.listFiles()){
				files.add(file);
			}
			return files;
		}
}
