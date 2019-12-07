package com.olbius.salesmtl.route;


import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove8Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;

import java.util.*;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.googlemaps.GoogleMapsQuery;
public class RoutingAlgorithm {
	
	public static String module = RoutingAlgorithm.class.getName();
	
	public static Map<String, Object> computeShortestTour(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		
		List<GenericValue> customers = (List<GenericValue>)context.get("customers");
		//double depot_lat = (Double)context.get("depot_latitude");
		//double depot_lng = (Double)context.get("depot_longitude");
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		
		ArrayList<Point> clientPoints = new ArrayList<Point>();
		ArrayList<Point> startPoints = new ArrayList<Point>();
		ArrayList<Point> endPoints = new ArrayList<Point>();
		ArrayList<Point> allPoints = new ArrayList<Point>();
		
		HashMap<Point, GenericValue> mPoint2Customer = new HashMap<Point, GenericValue>();
		
		//HashMap<String, Integer> mID2Index = new HashMap<String, Integer>();
		for(int i = 0; i < customers.size(); i++){
			GenericValue gv = customers.get(i);
			String partyId = (String)gv.get("partyId");
			//Debug.log(module + "::computeShortestTour, input customer " + partyId);
			
			int id = i+1;
			Point c = new Point(id);
			clientPoints.add(c);
			allPoints.add(c);
			//mID2Index.put(partyId,i);
			mPoint2Customer.put(c,gv);
		}
		startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
		Point s = new Point(0);
		Point e = new Point(0);
		startPoints.add(s);
		endPoints.add(e);
		allPoints.add(s);
		allPoints.add(e);
		
		ArcWeightsManager awm = new ArcWeightsManager(allPoints);
		for(int i = 0; i < customers.size(); i++){
			GenericValue ci = customers.get(i);
			//Debug.log(module + "::computeShortestTour, customer i = " + ci.get("partyId"));
			double lat_i = (Double)ci.get("latitude");
			double lng_i = (Double)ci.get("longitude");
			//Debug.log(module + "::computeShortestTour, customer i = " + ci.get("partyId") + 
			//		", lat.lng = " + lat_i + "," + lng_i);
			
			Point pi = clientPoints.get(i);
			for(int j = 0; j < customers.size(); j++){
				GenericValue cj = customers.get(j);

				//Debug.log(module + "::computeShortestTour, customer j = " + ci.get("partyId"));
				double lat_j = (Double)cj.get("latitude");
				double lng_j = (Double)cj.get("longitude");
				//Debug.log(module + "::computeShortestTour, customer j = " + ci.get("partyId") + 
				//		", lat.lng = " + lat_i + "," + lng_i);

				Point pj = clientPoints.get(j);
				
				double d = G.computeDistanceHaversine(lat_i, lng_i,  lat_j,  lng_j);
				awm.setWeight(pi, pj, d);
			}
			
			double dis = 0;//G.computeDistanceHaversine(depot_lat, depot_lng, lat_i, lng_i);
			awm.setWeight(s, pi, dis);
			awm.setWeight(e, pi, dis);
			
			dis = 0;//G.computeDistanceHaversine(lat_i, lng_i, depot_lat,  depot_lng);
			awm.setWeight(pi, s, dis); 
			awm.setWeight(pi, e, dis);
			
		}
		
		VRManager mgr= new VRManager();
		VarRoutesVR XR = new VarRoutesVR(mgr);
		XR.addRoute(s, e);
		
		for(Point p: clientPoints) XR.addClientPoint(p);
		
		IFunctionVR obj = new TotalCostVR(XR, awm);
		LexMultiFunctions F = new LexMultiFunctions();
		F.add(obj);
		
		mgr.close();
		
		GenericLocalSearch se = new GenericLocalSearch(mgr);
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		//NE.add(new GreedyTwoOptMove1Explorer(XR, F));
		//NE.add(new GreedyTwoOptMove2Explorer(XR, F));
		//NE.add(new GreedyTwoOptMove3Explorer(XR, F));
		//NE.add(new GreedyTwoOptMove4Explorer(XR, F));
		//NE.add(new GreedyTwoOptMove5Explorer(XR, F));
		//NE.add(new GreedyTwoOptMove6Explorer(XR, F));
		//NE.add(new GreedyTwoOptMove7Explorer(XR, F));
		NE.add(new GreedyTwoOptMoveExplorer(XR, F));
		NE.add(new GreedyOnePointMoveExplorer(XR, F));
		
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);
		
		se.search(100, 5000);
		
		ArrayList<GenericValue> route = new ArrayList<GenericValue>();
		for(Point p = XR.next(XR.startPoint(1)); p != XR.endPoint(1); p = XR.next(p)){
			GenericValue c = mPoint2Customer.get(p);
			route.add(c);
			Debug.log(module + "::computeShortestTour, Solution point " + p.getID() + ", customer " + 
			c.get("partyId"));
		}
		
		retSucc.put("route", route);
		
		return retSucc;
	}
	public static Map<String, Object> computeOptimalSequencePoints(DispatchContext ctx, Map<String, ? extends Object> context){
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		ArrayList<Integer> route = new ArrayList<Integer>();
		retSucc.put("route", route);
		
		List<Map> customers = (List<Map>)context.get("customers");
		String s_time_limit = (String)context.get("timeLimit");
		long timeLimit = 60000;
		try{
		if(s_time_limit != null){
			timeLimit = Long.valueOf(s_time_limit);
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		//double depot_lat = (Double)context.get("depot_latitude");
		//double depot_lng = (Double)context.get("depot_longitude");
		
		GoogleMapsQuery G = new GoogleMapsQuery();
		
		ArrayList<Point> clientPoints = new ArrayList<Point>();
		ArrayList<Point> startPoints = new ArrayList<Point>();
		ArrayList<Point> endPoints = new ArrayList<Point>();
		ArrayList<Point> allPoints = new ArrayList<Point>();
		
		HashMap<Point, Map> mPoint2Customer = new HashMap<Point, Map>();
		HashMap<Point, Integer> mPoint2Index = new HashMap<Point, Integer>();
		//HashMap<String, Integer> mID2Index = new HashMap<String, Integer>();
		for(int i = 0; i < customers.size(); i++){
			Map<String, Object> gv = customers.get(i);
			double lat = 0;
			double lng = 0;
			if(gv.get("latitude") != null) lat = Double.parseDouble((String) gv.get("latitude"));
			else{
				retSucc.put("msg", "KO"); return retSucc;
			}
			if(gv.get("longitude") != null) lng = Double.parseDouble((String)gv.get("longitude"));
			else{
				retSucc.put("msg", "KO"); return retSucc;
			}
			//String partyId = (String)gv.get("partyId");
			//Debug.log(module + "::computeShortestTour, input customer " + partyId);
			
			int id = i+1;
			Point c = new Point(id,lat,lng);
			clientPoints.add(c);
			mPoint2Index.put(c, clientPoints.size()-1);
			allPoints.add(c);
			//mID2Index.put(partyId,i);
			mPoint2Customer.put(c,gv);
		}
		startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
		Point s = new Point(0);
		Point e = new Point(0);
		startPoints.add(s);
		endPoints.add(e);
		allPoints.add(s);
		allPoints.add(e);
		
		ArcWeightsManager awm = new ArcWeightsManager(allPoints);
		
		for(Point pi: clientPoints){
			for(Point pj: clientPoints){
				double d = G.computeDistanceHaversine(pi.getX(), pi.getY(),  pj.getX(),  pj.getY());
				awm.setWeight(pi, pj, d);
			}
			if(context.get("start") == null){
				awm.setWeight(s, pi, 0);
				awm.setWeight(pi, s, 0);
			}else{
				Map<String, Object> ms = (Map)context.get("start");
				double lat = 0;
				double lng = 0;
				if(ms.get("latitude") != null) lat = Double.parseDouble((String)ms.get("latitude"));
				else{
					retSucc.put("msg", "KO"); return retSucc;
				}
				if(ms.get("longitude") != null) lng = Double.parseDouble((String)ms.get("longitude")) ;
				else{
					retSucc.put("msg", "KO"); return retSucc;
				}
				s.setX(lat);s.setY(lng);
				double dis = G.computeDistanceHaversine(pi.getX(), pi.getY(), s.getX(), s.getY());
				awm.setWeight(pi, s, dis);
				dis = G.computeDistanceHaversine(s.getX(), s.getY(), pi.getX(), pi.getY());
				awm.setWeight(s, pi, dis);
			}
			
			if(context.get("end") == null){
				awm.setWeight(e, pi, 0);
				awm.setWeight(pi, e, 0);
			}else{
				Map<String, Object> me = (Map)context.get("end");
				double lat = 0;
				double lng = 0;
				if(me.get("latitude") != null) lat = Double.parseDouble((String)me.get("latitude"));

				else{
					retSucc.put("msg", "KO"); return retSucc;
				}
				if(me.get("longitude") != null) lng = Double.parseDouble((String)me.get("longitude"));
				else{
					retSucc.put("msg", "KO"); return retSucc;
				}
				e.setX(lat);e.setY(lng);
				double dis = G.computeDistanceHaversine(pi.getX(), pi.getY(), e.getX(), e.getY());
				awm.setWeight(pi, e, dis);
				dis = G.computeDistanceHaversine(e.getX(), e.getY(), pi.getX(), pi.getY());
				awm.setWeight(e, pi, dis);					
			}
		}
		
		
		VRManager mgr= new VRManager();
		VarRoutesVR XR = new VarRoutesVR(mgr);
		XR.addRoute(s, e);
		
		for(Point p: clientPoints) XR.addClientPoint(p);
		
		IFunctionVR obj = new TotalCostVR(XR, awm);
		LexMultiFunctions F = new LexMultiFunctions();
		F.add(obj);
		
		mgr.close();
		
		GenericLocalSearch se = new GenericLocalSearch(mgr);
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyTwoOptMoveExplorer(XR, F));
		NE.add(new GreedyOnePointMoveExplorer(XR, F));
		
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);
		
		se.search(100000, (int)timeLimit);
		
		
		for(Point p = XR.next(XR.startPoint(1)); p != XR.endPoint(1); p = XR.next(p)){
			int idx = mPoint2Index.get(p);
			route.add(idx);
			//Debug.log(module + "::computeOptimalSequencePoints, Solution point " + p.getID() + ", customer " + 
			//c.get("partyId"));
		}
		
		retSucc.put("route", route);
		retSucc.put("msg", "OK");
		return retSucc;
	}

}
