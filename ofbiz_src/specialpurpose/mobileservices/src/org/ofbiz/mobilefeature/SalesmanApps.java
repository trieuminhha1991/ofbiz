package org.ofbiz.mobilefeature;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.customer.Customer;
import org.ofbiz.service.DispatchContext;

public abstract class SalesmanApps{
	public static String currencyUom = null;
	public static int pagesize;
	public static int pagenum;
	
	public SalesmanApps(){
		
	}
	
	 public abstract void createOrders(HttpServletRequest request,HttpServletResponse response,String module);
	 
	 public abstract Map<String,Object> getListRouteAndSalesMan(DispatchContext dpct,Map<String,?extends Object> context) throws ParseException;
	 
	 public abstract void getListPromotions();
	 
	 public abstract void getListProducts();
	 
	 public abstract void statistic();
	 
	 public abstract void leave();
	 
	 public abstract void synchronize();
	 
	 public abstract List<Map<String,Customer>> getListCustomer();
	 
	
}