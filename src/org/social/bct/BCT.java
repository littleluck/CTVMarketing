package org.social.bct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.social.graph.Graph;
import org.social.graph.Vertex;

public class BCT {
	
	private static final double C = 2*(Math.E - 2) ;
	private static final double coefficient = 8*C*(1 - 1/(2*Math.E))*(1 - 1/(2*Math.E)) ;

	/**
	 * BCT算法实现影响最大化
	 * @param args
	 */
	public static void main(String[] args) {
		
		
	}
	
	
	public Set<Integer> mainBCT(Graph graph, double budget, double epi, double q){
		
		if(budget<=0 || (epi<=0 || epi>=1)||(q<=0 || q>=1)) System.exit(1) ;	//程序异常退出
		
		int n = graph.getN() ;
		double cost[] = new double[n] ;
		for(int i=0; i<n; i++){
			cost[i] = graph.getVertex()[i].getCost() ;
		}
		int kMax = getKMAX(cost, budget) ;
		
		double L = coefficient*(-Math.log(q) + logCombination(n, kMax) + 2.0/n)/(epi*epi) ;	//for uniform cost
//		double L = coefficient*(-Math.log(q) + kMax*Math.log(n) + 2.0/n)/(epi*epi) ;	//otherwise
		
		double AL = (1 + Math.E*epi/(2*Math.E - 1))*L ;
		double Nt = AL ;
		List<Set<Integer>> hypergraph = new ArrayList<Set<Integer>>() ;
		Set<Integer> s = null ;
		
		do{
			for(int j=1; j<=Nt-hypergraph.size(); j++){
				Set<Integer> set = benefitSampling(graph) ;
				hypergraph.add(set) ;
			}
			Nt = 2*Nt ;
			s = weightedMaxCoverage(n, graph.getVertex(), hypergraph, budget) ;
		}while(degHS(hypergraph, s) < AL) ;
		
		return s ;
	}
	
	
	/**
	 *  log(C(n,k)) = log(n!) - log((n-k)!) - log(k!)
	 * @return C(n,k)组合数的对数
	 */
	public static double logCombination(int n, int k){
		if(n < k){
			System.err.println("n要不小于k") ;
			System.exit(1) ;
		}
		double sum = 0 ;
		for(int i=1;i<=k; i++){
			sum = sum + Math.log(n-k+i) - Math.log(i) ;
		}
		return sum ;
	}
	
	
	/**
	 * |S|=k, 且c(S)<=budget，返回最大的k
	 * @param cost
	 * @param budget
	 * @return
	 */
	public int getKMAX(double cost[], double budget){
		Arrays.sort(cost) ;
		double sum = 0 ;
		int i ;
		for(i=0; i<cost.length; i++){
			sum += cost[i] ;
			if(sum > budget) break ;
		}
		return i ;
	}
	
	/**
	 * Algorithm 2 BSA - Benefit Sampling Algorithm for LT model
	 * @param graph
	 * @return 采样的节点集合
	 */
	public Set<Integer> benefitSampling(Graph graph){
		Set<Integer> set = new HashSet<Integer>() ;
		int n = graph.getN() ;
		double sumBenefits = 0 ;
		Vertex vertex[] = graph.getVertex() ;
		double adj[][] = graph.getAdj() ;
		for(int i=0; i<n; i++){	//得到benefit总和
			sumBenefits += vertex[i].getBenefit() ;
		}
		
		Random rand1 = new Random() ;
		double p = rand1.nextDouble() ;
		double sum1 = 0 ;
		int u = -1 ;
		for(int i=0; i<n; i++){
			sum1 += vertex[i].getBenefit()/sumBenefits ;
			if(p <= sum1){	//选中了一个节点
				u = i ;
				break ;
			}
		}
		
		//只要节点u不在set中就循环
		Random rand2 = new Random() ;
		while(!set.contains(u)){
			set.add(u) ;
			
			//Attempt to select an edge (v, u) using live-edge model
			double p2 = rand2.nextDouble() ;
			int v = -1 ;
			double sum2 = 0 ;
			for(int i=0; i<n; i++){
				if(adj[i][u] != 0){	//存在边(i,u)
					sum2 += adj[i][u] ;
					if(p2 <= sum2){	//选中了一条边
						v = i ;
						break ;
					}
				}
			}
			if(v != -1){	//选中边(v,u)
				u = v ;
			}
		}
		
		return set ;
	}
	
	/**
	 * Algorithm 3 Weighted-Max-Coverage Algorithm
	 * @param n
	 * @param hypergraph
	 * @param B
	 * @return 临时种子集合
	 */
	public Set<Integer> weightedMaxCoverage(int n, Vertex vertex[], List<Set<Integer>> hypergraph, double B){
		
		Set<Integer> seed = new HashSet<Integer>() ;
		boolean flag = true ;
		double sumCost = 0 ;	//保存seed集合中所有节点的cost总和
		while(flag){
			flag = false ;
			for(int i=0; i<n; i++){	//判断是否还有节点满足条件，若有则将flag设置为true，此处应该可以不必每次都从i=0开始遍历
				if((!seed.contains(i)) && (vertex[i].getCost()<=(B-sumCost))){
					flag = true ;
					break ;
				}
			}
			if(flag){
				double max = -1 ;
				int maxNumber = 0 ;
				int deg = degHS(hypergraph, seed) ;
				for(int i=0; i<n; i++){	//找到具有最大(degH(S∪{v})−degH(S))/c(v)的节点
					if((!seed.contains(i)) && (vertex[i].getCost()<=(B-sumCost))){
						seed.add(i) ;
						int deg2 = degHS(hypergraph, seed) ;
						double temp = (deg2 - deg)/vertex[i].getCost() ;
						if(max < temp){
							max = temp ;
							maxNumber = i ;
						}
						seed.remove(i) ;	//记得将结点i删除
					}
				}//end for
				seed.add(maxNumber) ;	//将具有最大(degH(S∪{v})−degH(S))/c(v)的节点加入到seed中
				sumCost += vertex[maxNumber].getCost() ;	//要更新sumCost
			}
		}//end while
		
		Set<Integer> v = new HashSet<Integer>() ;
		int u = -1 ;	//保存最大degH(v)的结点编号
		int maxDeg = -1 ;
		for(int i=0; i<n; i++){
			if(vertex[i].getCost() <= B){
				v.add(i) ;
				int temp = degHS(hypergraph, v) ;
				if(maxDeg < temp){
					maxDeg = temp ;
					u = i ;
				}
				v.remove(i) ;
			}
		}
		if(degHS(hypergraph, seed) < maxDeg){
			seed.clear() ;
			seed.add(u) ;
		}
		
		return seed ;
	}
	
	/**
	 * 求degH(S)，应该是求集合s与List中的Set有交集的个数
	 * @param hypergraph
	 * @param s
	 * @return	
	 */
	public int degHS(List<Set<Integer>> hypergraph, Set<Integer> s){
		int intersections = 0 ;	//保存交集个数
		for(Set<Integer> set : hypergraph){
			for(Integer ele : s){
				if(set.contains(ele)){
					intersections++ ;
					break ;
				}
			}
		}
		
		return intersections ;
	}

}
