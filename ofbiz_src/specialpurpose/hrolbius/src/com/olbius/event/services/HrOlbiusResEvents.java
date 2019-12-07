/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.olbius.event.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;


public class HrOlbiusResEvents {
    public static final String module = HrOlbiusResEvents.class.getName();
    public static final String resourceError = "ProductErrorUiLabels";
    public static Map<String, Set<String>> allTreeSearchCache = FastMap.newInstance();
    //public static final int sizeCache = 10;
    // Please note : the structure of map in this function is according to the JSON data map of the jsTree
    @SuppressWarnings("unchecked")
    public static void getChildHRCategoryTree(HttpServletRequest request, HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String partyId = request.getParameter("partyId");
        String onclickFunction = request.getParameter("onclickFunction");
        String additionParam = request.getParameter("additionParam");
        String hrefString = request.getParameter("hrefString");
        String hrefString2 = request.getParameter("hrefString2");
        
        List categoryList = FastList.newInstance();
        List<GenericValue> childOfComs;
        try {
        	List<GenericValue> employment= PartyUtil.buildOrg(delegator, partyId).getDirectEmployee(delegator);
        	if(UtilValidate.isNotEmpty(employment)){
            	for(GenericValue employ : employment){
            		 Map josonMap = FastMap.newInstance();
                     Map dataMap = FastMap.newInstance();
                     Map dataAttrMap = FastMap.newInstance();
                     Map attrMap = FastMap.newInstance();
                   
	                    	String partyIdTo= (String)employ.get("partyId");
	                    	if(UtilValidate.isNotEmpty(employ) ){
	                    		String firstname = (String) employ.get("firstName");
	                            String lastname = (String) employ.get("lastName");
	                            String middleName=(String)employ.get("middleName");
	                            if (UtilValidate.isEmpty(lastname)) {
	                                lastname = "";
	                            }
	                            if (UtilValidate.isEmpty(middleName)) {
	                            	middleName = "";
	                            }
	                            if (UtilValidate.isEmpty(firstname)) {
	                                firstname = "";
	                            }
	                            String title = firstname +" "+middleName+" "+ lastname;
	                            
	                            if(title==null){
	                            	title=partyIdTo;
	                            }
	                            dataAttrMap.put("onClick", onclickFunction + "('" + partyIdTo + additionParam + "')");
	                            
	                            String hrefSt = hrefString + partyIdTo;
	                            if (UtilValidate.isNotEmpty(hrefString2)) {
	                                hrefSt = hrefSt + hrefString2;
	                            }
	                            dataAttrMap.put("href", hrefSt);
	                            
	                            dataMap.put("attr", dataAttrMap);
	                            
	                            attrMap.put("rel", "E");
	                            attrMap.put("id", partyIdTo);
	                            josonMap.put("attr",attrMap);
	                            dataMap.put("title", title);
	                            josonMap.put("data", dataMap);
	                    	}
	                    	categoryList.add(josonMap);
	                    	
	                    }
	            		
            	}
            GenericValue partyGroup = delegator.findOne("PartyGroup" ,UtilMisc.toMap("partyId", partyId), false);
            if (UtilValidate.isNotEmpty(partyGroup)) {
            	childOfComs= PartyUtil.buildOrg(delegator, partyId).getDirectChildList(delegator);
                if (UtilValidate.isNotEmpty(childOfComs)) {
                    
                    for (GenericValue childOfCom : childOfComs ) {
                        String catId = null;
                        String catNameField = null;
                        String title = null;
                        
                        Map josonMap = FastMap.newInstance();
                        Map dataMap = FastMap.newInstance();
                        Map dataAttrMap = FastMap.newInstance();
                        Map attrMap = FastMap.newInstance();
                        
                        catId = (String)childOfCom.get("partyId");
	
		                        //Department or Sub department
		                        GenericValue childContext = delegator.findOne("PartyGroup" ,UtilMisc.toMap("partyId", catId), false);
		                        if (UtilValidate.isNotEmpty(childContext)) {
		                            catNameField = (String) childContext.get("groupName");
		                            title = catNameField;
		                            josonMap.put("title",title);
		                            
		                        }
		                        List<GenericValue> childOfSubComs =PartyUtil.buildOrg(delegator, catId).getDirectChildList(delegator);
		                        List<GenericValue> isPosition= PartyUtil.buildOrg(delegator, catId).getDirectEmployee(delegator);
		                        if (UtilValidate.isNotEmpty(isPosition)||UtilValidate.isNotEmpty(childOfSubComs)) {
		                        	josonMap.put("state", "closed");
		                        }
		                        
		                        dataAttrMap.put("onClick", onclickFunction + "('" + catId + additionParam + "')");
		                        
		                        String hrefStr = hrefString + catId;
		                        if (UtilValidate.isNotEmpty(hrefString2)) {
		                            hrefStr = hrefStr + hrefString2;
		                        }
		                        dataAttrMap.put("href", hrefStr);
		                        
		                        dataMap.put("attr", dataAttrMap);
		                        
		                        attrMap.put("rel", "Y");
		                        attrMap.put("id", catId);
		                        josonMap.put("attr",attrMap);
		                        dataMap.put("title", title);
		                        josonMap.put("data", dataMap);
		                        categoryList.add(josonMap);
	                        }
                }
            }
            toJsonObjectList(categoryList,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void toJsonObjectList(List attrList, HttpServletResponse response){
        String jsonStr = "[";
        for (Object attrMap : attrList) {
            JSONObject json = JSONObject.fromObject(attrMap);
            jsonStr = jsonStr + json.toString() + ',';
        }
        jsonStr = jsonStr + "{ } ]";
        if (UtilValidate.isEmpty(jsonStr)) {
            Debug.logError("JSON Object was empty; fatal error!",module);
        }
        // set the X-JSON content type
        response.setContentType("application/json");
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding",module);
        }
        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError("Unable to get response writer",module);
        }
    }
    
    public static Map<String, Object> getSearchDir(DispatchContext dctx, Map<String, Object> context){
    	final Delegator delegator = dctx.getDelegator();
    	List<String> tempPathList = FastList.newInstance();

    	//containing tree path to node of all of search result
    	List<String> allTreeSearchPath = FastList.newInstance();   
    	String rootPathPartyId = null;
    	String search_str = (String) context.get("search_str");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String partyIdFrom = userLogin.getString("partyId");
    	Properties generalProp = UtilProperties.getProperties("general");
    	String defaultOrganizationPartyId = (String)generalProp.get("ORGANIZATION_PARTY");
    	Map<String, Object> retMap = FastMap.newInstance();
    	try{
	    	String ceoId = PartyUtil.getCEO(delegator);
	    	String headHRId = PartyUtil.getHrmAdmin(delegator);
	    	if(userLogin.getString("partyId").endsWith(ceoId) || userLogin.getString("partyId").equals(headHRId)){
	    		rootPathPartyId = defaultOrganizationPartyId;
	    	}else{
	    		rootPathPartyId = PartyUtil.getOrgByManager(partyIdFrom, delegator);
	    	}
	    	if(UtilValidate.isNotEmpty(search_str)){
	    		List<EntityCondition> employmentConditions = FastList.newInstance();
	    		List<EntityCondition> deptConditions = FastList.newInstance();
	    		//get all child of root
	    		//if(!rootPathPartyId.equals(defaultOrganizationPartyId)){
	    		Set<String> allChildPartyIdOfRoot =  allTreeSearchCache.get(rootPathPartyId);
	    		if(UtilValidate.isEmpty(allChildPartyIdOfRoot)){
	    			Organization rootTree = PartyUtil.buildOrg(delegator, rootPathPartyId);	    		
		    		allChildPartyIdOfRoot = getAllChildOfRoot(delegator, rootTree);		    		
		    		allTreeSearchCache.put(rootPathPartyId, allChildPartyIdOfRoot);
	    		}
	    		employmentConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, allChildPartyIdOfRoot));
	    		deptConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, allChildPartyIdOfRoot));
	    		//}
	    		String[] searchArr = search_str.split("\\s");
	    		List<EntityCondition> tempEmplCondition = FastList.newInstance();
	    		for(String str: searchArr){
	    			tempEmplCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + str + "%")));
	    			tempEmplCondition.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + str + "%")));
	    		}
	    		employmentConditions.add(EntityCondition.makeCondition(tempEmplCondition, EntityOperator.OR));
	    		
	    		deptConditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + search_str + "%")));
				
	    		List<GenericValue> tempPartyRel = delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition(deptConditions, EntityOperator.AND), UtilMisc.toSet("partyIdTo") , null, null, false);				
	    		List<GenericValue> tempEmployment = delegator.findList("EmploymentAndPerson", EntityCondition.makeCondition(employmentConditions, EntityOperator.AND), UtilMisc.toSet("partyIdTo") , null, null, false);
				
				//List<String> partyRelStrList = EntityUtil.getFieldListFromEntityList(tempPartyRel, "partyIdTo", true);
				//List<String> employemtStrList = EntityUtil.getFieldListFromEntityList(tempEmployment, "partyIdTo", true);
				//retList.addAll(partyRelStrList);
				//retList.addAll(employemtStrList);				
				
				for(GenericValue empl: tempEmployment){
					GenericValue department = PartyUtil.getDepartmentOfEmployee(delegator, empl.getString("partyIdTo"));
					tempPathList.add(department.getString("partyIdFrom"));
				}
				for(GenericValue dept: tempPartyRel){
					GenericValue parentDept = PartyUtil.getParentOrgOfDepartmentCurr(delegator, dept.getString("partyIdTo"));
					tempPathList.add(parentDept.getString("partyIdFrom"));
				}
				
				for(String deptId: tempPathList){
					String tempRootPathPartyId = deptId;
					if(!allTreeSearchPath.contains("#" + tempRootPathPartyId)){
						List<String> tempList = FastList.newInstance();						
						tempList.add("#" + deptId);
						while(!rootPathPartyId.equals(tempRootPathPartyId)){
							tempRootPathPartyId = PartyUtil.getParentOrgOfDepartmentCurr(delegator, tempRootPathPartyId).getString("partyIdFrom");
							if(allTreeSearchPath.contains("#" + tempRootPathPartyId)){
								break;
							}else{
								tempList.add(0, "#" + tempRootPathPartyId);
							}
						}	
						allTreeSearchPath.addAll(tempList);
					}
				}					
	    	}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			retMap.put(ModelService.ERROR_MESSAGE, e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMap.put(ModelService.ERROR_MESSAGE, e.getMessage());
			e.printStackTrace();
		}
    	
    	retMap.put("search_results", allTreeSearchPath);
    	return retMap;
    }

	private static Set<String> getAllChildOfRoot(Delegator delegator, Organization rootTree) throws GenericEntityException {
		// TODO Auto-generated method stub
		Set<String> retSet = FastSet.newInstance();
		List<GenericValue> allChildDeptOfRoot = rootTree.getChildList();
		List<GenericValue> allEmplOfRoot = rootTree.getEmployeeInOrg(delegator);
		List<String> allDept = EntityUtil.getFieldListFromEntityList(allChildDeptOfRoot, "partyId", true);
		List<String> allEmpl = EntityUtil.getFieldListFromEntityList(allEmplOfRoot, "partyIdTo", true);
		retSet.addAll(allDept);
		retSet.addAll(allEmpl);
		return retSet;
	}
	
	public static Map<String, Object> getChildrenSkillType(DispatchContext dctx, Map<String, Object> context){
		String skillTypeId = (String)context.get("skillTypeId");
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> retMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(skillTypeId)){
			try {
				List<GenericValue> children = delegator.findByAnd("SkillType", UtilMisc.toMap("parentTypeId", skillTypeId), null, false);
				if(UtilValidate.isNotEmpty(children)){
					retMap.put("hasChildren", "Y");
					retMap.put("childrenList", children);
				}else{
					retMap.put("hasChildren", "N");
					List<GenericValue> skillTypeLevel = delegator.findByAnd("SkillTypeAndLevel", UtilMisc.toMap("skillTypeId", skillTypeId), null, false);
					if(UtilValidate.isNotEmpty(skillTypeLevel)){
						retMap.put("hasSkillTypeLevel", "Y");
						retMap.put("skillTypeLevel", skillTypeLevel);
					}else{
						retMap.put("hasSkillTypeLevel", "N");
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retMap;
	}
	
}
