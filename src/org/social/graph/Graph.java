package org.social.graph;

//��������ͼ
public class Graph {

	private int n ;	//������
	private Vertex vertex[] ;	//��������
	private double adj[][] ;	//�ڽӾ���洢ͼ
	
//	//�����ڲ����ʾ��������
//	class Vertex{
//		
////		public int number ;	//������
//		public double cost ;	//���㻨��
//		public double benefit ;	//��������
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
