package org.social.graph;

//定义有向图
public class Graph {

	private int n ;	//顶点数
	private Vertex vertex[] ;	//顶点数组
	private double adj[][] ;	//邻接矩阵存储图
	
//	//定义内部类表示顶点类型
//	class Vertex{
//		
////		public int number ;	//顶点编号
//		public double cost ;	//顶点花费
//		public double benefit ;	//顶点收益
//		
//		
//	}
	
	public Graph(Vertex vertex[], double adj[][]){
		this.n = vertex.length ;
		this.vertex = vertex ;
		this.adj = adj ;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public Vertex[] getVertex() {
		return vertex;
	}

	public void setVertex(Vertex[] vertex) {
		this.vertex = vertex;
	}

	public double[][] getAdj() {
		return adj;
	}

	public void setAdj(double[][] adj) {
		this.adj = adj;
	}
	
}
