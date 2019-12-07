package com.olbius.basesales.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelRelation;

public class SCCAnalyzer {
	class Pair{
		int i;
		int j;
		public Pair(int i, int j){
			this.i = i; this.j = j;
		}
	}
	public ArrayList<Pair> P;
	/*
	public static int[][] P = {
		{624,822},// CountryAddressFormat,Geo
		{592,2758}// PartyGroup,Party
	};
	*/
	
	public static String module = "SCCAnalyzer";
	private Delegator delegator;
	public Map<String, Integer> mEntityName2Index;
	public String[] entityNames;
	public Map<String, List<String>> mEntityAB2KeyA;// Relation 2 entities A->B, return list of fields of A corresponding A@B
	public Map<String, List<String>> mEntityAB2KeyB;// Relation 2 entities A->B, return list of fields of B corresponding A@B
	
	
	
	
	public Set<Integer>[] A;// A[u] is the set of adjacent vertices v of u
								// (u,v) is an arc means that entity v has
								// foreign key which is the primary key of u
								// example: u is OrderType, v is OrderHeader
	// private Set<Integer>[] IA;// IA[v] is the set of vertices u such that
	// (u,v) is an arc

	public Set<Integer>[] AT;// represent residual graph
	public int n;// number of vertices of the graph: vertices are numbered
					// 0,1,2,...,n-1

	// data structure for DFS
	char[] color;
	int[] f;// finishing time
	int[] x;// store list of vertices in increasing order of finishing time
	int idx;// index of x
	int t;// global discrete time t
	int nscc;// number of strongly connected components
	int[] scc;// scc[v] is the index of strongly connected component containing
				// v
	public Set<String>[] entitySCC;
	public int[] indexTOPO;// indexTOPO[c] is the topo index of SCC c
	public int[] sequenceTOPO;// sequenceTOPO[i] is the ith element of the topo sequence
	public HashMap<Integer, int[]> mEntityIndex2SortedDependedEntityIndices;
	public HashMap<Integer, int[]> mEntityIndex2SortedDependingEntityIndices;
	
	public void computeSortedDependedDependingEntities(){
		Debug.log(module + "::computeSortedDependedDependingEntities START");
		
		mEntityIndex2SortedDependedEntityIndices = new HashMap<Integer, int[]>();
		mEntityIndex2SortedDependingEntityIndices = new HashMap<Integer, int[]>();
		//Debug.log(module + "::computeSortedDependedDependingEntities, START 1");
		for(int v = 0; v < entityNames.length; v++){
			int[] a = new int[A[v].size()];
			int idx = -1;
			for (int x : A[v]) {
				idx++;
				a[idx] = x;
			}
			for (int i = 0; i < a.length; i++) {
				for (int j = i + 1; j < a.length; j++) {
					if (indexTOPO[scc[a[i]] - 1] > indexTOPO[scc[a[j]] - 1]) {
						int tmp = a[i];
						a[i] = a[j];
						a[j] = tmp;
					}
				}
			}
			mEntityIndex2SortedDependedEntityIndices.put(v, a);
			
			
			//Debug.log(module + "::computeSortedDependedDependingEntities, START 2");
			a = new int[AT[v].size()];
			idx = -1;
			for (int x : AT[v]) {
				idx++;
				a[idx] = x;
			}
			for (int i = 0; i < a.length; i++) {
				for (int j = i + 1; j < a.length; j++) {
					if (indexTOPO[scc[a[i]] - 1] > indexTOPO[scc[a[j]] - 1]) {
						int tmp = a[i];
						a[i] = a[j];
						a[j] = tmp;
					}
				}
			}
			mEntityIndex2SortedDependingEntityIndices.put(v, a);
		}
		Debug.log(module + "::computeSortedDependedDependingEntities FINISHED");
		
	}
	public int[] getSortedDependedEntityIndex(int v){
		return mEntityIndex2SortedDependedEntityIndices.get(v);
	}
	public int[] getSortedDependingEntityIndex(int v){
		return mEntityIndex2SortedDependingEntityIndices.get(v);
	}
	
	private void topoSort() {
		Debug.log(module + "::topoSort START");
		try {
			int m = entitySCC.length;
			Set<Integer>[] AC = new Set[m];// AC[c] is the set of SCC ci such
											// that there is an arc (c,ci)
			for (int c = 0; c < m; c++) {
				AC[c] = new HashSet<Integer>();
			}
			int[] inDegree = new int[m];
			for (int v = 0; v < n; v++) {
				for (int u : A[v]) {
					int cu = scc[u] - 1;
					int cv = scc[v] - 1;
					//Debug.log(module + "::topoSort, cu = " + cu + ", cv = " + cv + ", m = " + m + ", AC.len = " + AC.length);
					if (cu != cv) {
						AC[cv].add(cu);
						//Debug.log(module + "::topoSort, AC[" + cv + "].sz = " + AC[cv].size());
					}
				}
			}
			for (int c = 0; c < m; c++) {
				inDegree[c] = 0;
			}
			for (int c = 0; c < m; c++) {
				for (int ci : AC[c]) {
					inDegree[ci]++;
				}
			}
			Queue<Integer> Q = new LinkedList<>();
			for (int c = 0; c < m; c++) {
				if (inDegree[c] == 0){
					Q.add(c);
					//Debug.log(module + "::topoSort, INIT Q add " + c + ", Q.sz = " + Q.size());
				}
			}
			int idx = -1;
			indexTOPO = new int[m];
			sequenceTOPO = new int[m];
			while (Q.size() > 0) {
				int c = Q.remove();
				//Debug.log(module + "::topoSort, POP " + c + ", Q.sz = " + Q.size());
				idx++;
				indexTOPO[c] = idx;
				sequenceTOPO[idx] = c;
				for (int ci : AC[c]) {
					inDegree[ci]--;
					if (inDegree[ci] == 0){
						Q.add(ci);
						//Debug.log(module + "::topoSort, PUSH " + ci + ", Q.sz = " + Q.size());
					}
				}
			}
			//for(int c = 0; c < m; c++){
			//	Debug.log(module + "::topoSort, indexTOPO[" + c + "] = " + indexTOPO[c]);
			//}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Debug.log(module + "::topoSort FINISHED");
	}

	public String[] findPredecesors(String e) {
		Debug.log(module + "::findPredecesors(" + e + ")");
		if(mEntityName2Index.get(e) == null){
			Debug.log(module + "::findPredecessors, NULL????? for " + e);
			return null;
		}
		int u = mEntityName2Index.get(e);
		// return v[0..k-1] such that there are arcs (v[0],v[1]), (v[1],v[2]),
		// ..., (v[k-1],u)
		Set<Integer> S = new HashSet<Integer>();
		Set<Integer> newS = new HashSet<Integer>();
		S.add(u);
		while (true) {
			newS.clear();
			for (int x : S) {
				for (int y : AT[x])
					if (!S.contains(y)) {
						newS.add(y);
						//Debug.log(module + "::findPredecessors, from " + entityNames[x] + " ADD " + entityNames[y]);
					}
			}
			if (newS.size() == 0){
				//Debug.log(module + "::findPredecesors, newS = 0 -> BREAK");
				break;
			}
			for (int x : newS)
				S.add(x);
			//Debug.log(module + "::findPredecesors, newS = " + newS.size() + ", S = " + S.size());
		}
		int[] r = new int[S.size()];
		int idx = -1;
		for (int x : S)if(x != u) {
			idx++;
			r[idx] = x;
		}
		for (int i = 0; i < r.length; i++) {
			for (int j = i + 1; j < r.length; j++) {
				//Debug.log(module + "::findPredecesors, r[" + i + "] = " + r[i] + ", r[" + j + "] = " + r[j]);
				int ci = scc[r[i]]-1;
				int cj = scc[r[j]]-1;
				if (indexTOPO[ci] > indexTOPO[cj]) {
					int tmp = r[i];
					r[i] = r[j];
					r[j] = tmp;
				}
			}
		}
		String[] rs = new String[r.length];
		for (int i = 0; i < r.length; i++){
			rs[i] = entityNames[r[i]];
		}
		return rs;
	}

	public String[] findSuccessors(String e) {
		int u = mEntityName2Index.get(e);
		// return v[0..k-1] such that there are arcs (u,v[0]), (v[0],v[1]),
		// (v[1],v[2]), ...,
		Set<Integer> S = new HashSet<Integer>();
		Set<Integer> newS = new HashSet<Integer>();
		S.add(u);
		while (true) {
			newS.clear();
			for (int x : S) {
				for (int y : A[x])
					if (!S.contains(y)) {
						newS.add(y);
						//Debug.log(module + "::findSuccessors, from " + entityNames[x] + " ADD " + entityNames[y]);
					}
			}
			if (newS.size() == 0)
				break;
			for (int x : newS)
				S.add(x);
		}
		int[] r = new int[S.size()];
		int idx = -1;
		for (int x : S) if(x != u){
			idx++;
			r[idx] = x;
		}
		for (int i = 0; i < r.length; i++) {
			for (int j = i + 1; j < r.length; j++) {
				int ci = scc[r[i]]-1;
				int cj = scc[r[j]]-1;
				if (indexTOPO[ci] < indexTOPO[cj]) {
					int tmp = r[i];
					r[i] = r[j];
					r[j] = tmp;
				}
			}
		}
		String[] rs = new String[r.length];
		for (int i = 0; i < r.length; i++)
			rs[i] = entityNames[r[i]];
		return rs;
	}

	private void init() {
		color = new char[entityNames.length];
		f = new int[entityNames.length];
		x = new int[entityNames.length];
	}

	private void initColor() {
		for (int v = 0; v < n; v++)
			color[v] = 'W';
	}

	private void DFSVisitA(int v) {
		t++;
		color[v] = 'G';
		//Debug.log(module + "::DFSVisitA(" + v + ")");
		for (int u : A[v])
			if (color[u] == 'W') {
				DFSVisitA(u);
			}
		t++;
		f[v] = t;
		idx++;
		x[idx] = v;
	}

	private void DFSA() {
		idx = -1;
		initColor();
		t = 0;
		Debug.log(module + "::DFSA START");
		for (int v = 0; v < n; v++)
			if (color[v] == 'W') {
				DFSVisitA(v);
			}
	}

	private void DFSVisitAT(int v) {
		//Debug.log(module + "::DFSVisitAT(" + v + "), nscc = " + nscc);
		scc[v] = nscc;
		color[v] = 'G';
		for (int u : AT[v])
			if (color[u] == 'W') {
				DFSVisitAT(u);
			}
	}

	private void DFSAT() {
		Debug.log(module + "::DFSAT START");
		initColor();
		scc = new int[n];
		nscc = 0;
		for (int i = n - 1; i >= 0; i--) {
			//Debug.log(module + "::DFSAT, consider x[" + i + "] = " + x[i]
			//		+ ", color = " + color[x[i]]);
			if (color[x[i]] == 'W') {
				nscc++;
				DFSVisitAT(x[i]);
			}
		}
	}

	public SCCAnalyzer(Delegator delegator) {
		this.delegator = delegator;
		P = new ArrayList<Pair>();
	}
	public String composeName(String nameA, String nameB){
		return nameA + "@" + nameB;
	}
	public static String toStringObject(List<Object> pkValues){
		String s = "";
		for(int i = 0; i < pkValues.size(); i++){
			String e = "";
			if(pkValues.get(i) instanceof java.sql.Timestamp){
				e = pkValues.get(i).toString();
			}else{
				e = pkValues.get(i) + "";
			}
			s = s + "@" + e;
		}
		return s;
	}
	public static String toString(List<String> pkValues){
		String s = "";
		for(int i = 0; i < pkValues.size(); i++){
			String e = "";
			e = pkValues.get(i) + "";
			s = s + "@" + e;
		}
		return s;
	}
	public boolean equalStrings(List<String> L1, List<String> L2){
		for(String s: L1) if(!L2.contains(s)) return false;
		for(String s: L2) if(!L1.contains(s)) return false;
		return true;
	}
	public String getDefineDependList(){
		String s = "";
		for(Pair p: P)
			s = s + "[" + p.i + "," + p.j + "] d-> [" + entityNames[p.i] + "," + entityNames[p.j] + "], ";
		return s;
	}
	private void mapEntityNames() {
		try {

			Set<String> setEntityNames = delegator.getModelReader()
					.getEntityNames();
			entityNames = new String[setEntityNames.size()];
			mEntityName2Index = new HashMap<String, Integer>();// FastMap.newInstance();
			mEntityAB2KeyA = new HashMap<String, List<String>>();
			mEntityAB2KeyB = new HashMap<String, List<String>>();
			
			
			int idx = -1;
			for (String e : setEntityNames) {
				idx++;
				entityNames[idx] = e;
				mEntityName2Index.put(e, idx);
			}

			n = idx + 1;
			A = new Set[n];
			AT = new Set[n];
			for (int e = 0; e < n; e++) {
				A[e] = FastSet.newInstance();
				AT[e] = FastSet.newInstance();
			}
			String info = "";
			for (int i = 0; i < n; i++) {
				//Debug.log(module + "::mapEntityNames, entity " + entityNames[i]);
				ModelEntity table = delegator.getModelEntity(entityNames[i]);
			
				List<ModelRelation> rel = table.getRelationsOneList();
				for (ModelRelation mr : rel) {
					int j = mEntityName2Index.get(mr.getRelEntityName());
					//if(j == 624 && i == 822) continue;
					//if(defineDepends(j, i)) continue;
					ModelEntity rel_table = delegator.getModelEntity(mr.getRelEntityName());
					List<String> fkNames = FastList.newInstance();
					List<String> relfkNames = FastList.newInstance();
					
					for(ModelKeyMap mkm: mr.getKeyMaps()){
						fkNames.add(mkm.getFieldName());
						relfkNames.add(mkm.getRelFieldName());
					}
					boolean eq = equalStrings(fkNames,rel_table.getPkFieldNames()) &&
							equalStrings(table.getPkFieldNames(), relfkNames);
					
					if(eq){
						P.add(new Pair(i,j));
					}
			
					if(entityNames[i].equals("Party") || entityNames[i].equals("PartyGroup")){
						info = info + "(" + entityNames[i] + "<-" + mr.getRelEntityName() + ": fkNames = " + 
					fkNames + ", rel_fieldNames = " + relfkNames + "), ";
						Debug.log(module + "::mapEntityNames " + "(" + entityNames[i] + "<-" + mr.getRelEntityName() + ": fkNames = " + 
					fkNames + ", rel_fieldNames = " + relfkNames + "), ");
					}
						
					A[j].add(i);
					
					AT[i].add(j);
					
					String B = entityNames[i];
					String A = mr.getRelEntityName();
					String AB = composeName(A,B);
					List<String> fA = FastList.newInstance();
					List<String> fB = FastList.newInstance();
					for(ModelKeyMap mkm: mr.getKeyMaps()){
						fA.add(mkm.getRelFieldName());
						fB.add(mkm.getFieldName());
					}
					
					if(i == 822 && j == 624 || i == 624 && j == 822){
						Debug.log(module + "::mapEntityNames DEBUG A[" + j + "," + entityNames[j] + "].add(" + i + "," + 
					entityNames[i] + ") + fA = " + toString(fA) + ", fB = " + toString(fB)
					+ " DEPENDENCY " + B + " DEPENDS ON " + A);
					}
					mEntityAB2KeyA.put(AB, fA);
					mEntityAB2KeyB.put(AB, fB);
					// String keyMaps = "";
					// for(ModelKeyMap mkm: mr.getKeyMaps())
					// keyMaps += "[" + mkm.getFieldName() + "," +
					// mkm.getRelFieldName() + "]";
					// Debug.log(module + ":: cutBackupOrders relation entity "
					// + mr.getRelEntityName() +
					// ", FK = " + mr.getFkName() + ", " + keyMaps);
				}
			}
			//Debug.log(module + "::mapEntityNames, n = " + n);
			for (int i = 0; i < n; i++) {
				String s = "";
				for (int j : A[i])
					s = s + j + ",";
				//Debug.log(module + "::mapEntityNames, A[" + i + "].sz = "
				//		+ A[i].size() + ": " + s);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public List<String> getRelFieldNames(String rel_entity, String entity){
		return mEntityAB2KeyA.get(composeName(rel_entity, entity));
	}
	public List<String> getFieldNames(String rel_entity, String entity){
		return mEntityAB2KeyB.get(composeName(rel_entity, entity));
	}
	public boolean defineDepends(String ti, String tj){
		if(mEntityName2Index.get(ti) == null) return false;
		if(mEntityName2Index.get(tj) == null) return false;
		
		int i = mEntityName2Index.get(ti);
		int j = mEntityName2Index.get(tj);
		return defineDepends(i, j);
	}
	public boolean defineDepends(int i, int j){
		for(int k = 0; k < P.size(); k++){
			if(P.get(k).i == i && P.get(k).j == j) return true;
		}
		return false;
	}
	public boolean bidirectionalDepends(int i, int j){
		//if(A == null) return false;
		//if(i < 0 || i >= A.length) return false;
		//if(j < 0 || j >= A.length) return false;
		//return A[i].contains(j) && A[j].contains(i);
		return depends(i,j) && depends(j,i);
	}
	public boolean depends(int i, int j){
		// return true if entity i depends on j
		if(A == null) return false;
		if(j < 0 || j >= A.length) return false;
		if(j < 0 || j >= A.length) return false;
		//if(A[j].contains(i) && A[i].contains(j)) return defineDepends(i, j);
		
		return A[j].contains(i);
	}
	public boolean depends(String t1, String t2){
		if(mEntityName2Index.get(t1) == null) return false;
		if(mEntityName2Index.get(t2) == null) return false;
		
		int i = mEntityName2Index.get(t1);
		int j = mEntityName2Index.get(t1);
		return depends(i,j);
	}
	public void analyze() {
		mapEntityNames();
		init();
		DFSA();
		DFSAT();
		entitySCC = new Set[nscc];
		for (int i = 0; i < nscc; i++) {
			entitySCC[i] = FastSet.newInstance();
		}
		for (int i = 0; i < n; i++) {
			entitySCC[scc[i] - 1].add(entityNames[i]);
			//Debug.log(module + "::analyze, entitySCC[" + (scc[i] - 1)
			//		+ "].add(" + entityNames[i] + ")");
		}
		for (int i = 0; i < entitySCC.length; i++) {
			String s = "";
			for (String e : entitySCC[i]) {
				s = s + e + ",";
			}
			
			if (entitySCC[i].size() > 1){
				Debug.log(module + "::analyze, SUPRISE!!! ");
				Debug.log(module + "::analyze, entitySCC[" + i + "] = " + s);
			}
		}

	}
	public List<String> getSCC(int idx){
		List<String> L = FastList.newInstance();
		for(String s: entitySCC[sequenceTOPO[idx]]){
			L.add(s);
		}
		return L;
	}
	public void computeTOPO() {
		topoSort();
		computeSortedDependedDependingEntities();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
