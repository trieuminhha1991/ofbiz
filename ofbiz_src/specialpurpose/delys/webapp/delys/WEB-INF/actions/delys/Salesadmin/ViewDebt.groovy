/*
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
 */
 
 import java.sql.*;
 import java.sql.Timestamp;
 import java.util.Calendar;
 import net.fortuna.ical4j.model.DateTime;
 import org.ofbiz.base.util.*;
 import org.ofbiz.entity.condition.*;
 import sun.util.calendar.LocalGregorianCalendar.Date;
 
 def module = "AddProductBacklogItem.groovy";
 
 // find cust request and items
 def performFindInMap = [:];
// performFindInMap.entityName = "CustRequestAndCustRequestItem";
// def inputFields = [:];
// 
// if(parameters.statusId == null){
//	 parameters.statusId = "";
// }else if("Any".equals(parameters.statusId)){
//	 parameters.statusId = "";
// }
// inputFields.putAll(parameters);
// inputFields.custRequestTypeId = "RF_PROD_BACKLOG";
// performFindInMap.inputFields = inputFields;
// performFindInMap.orderBy = "custSequenceNum";
// def performFindResults = dispatcher.runSync("performFind", performFindInMap);
// context.performFindResults = performFindResults;
// def custRequestAndItems = performFindResults.listIt.getCompleteList();
// performFindResults.listIt.close();
 
 
// <service service-name="performFind" result-map="result" result-map-list="listIt">
// <field-map field-name="inputFields" from-field="parameters"/>
// <field-map field-name="entityName" value="InvoiceAndType"/>
//<!--                 <field-map field-name="orderBy" from-field="parameters.sortField"/> -->
// <field-map field-name="orderBy" from-field="In-Process"/>
// <field-map field-name="viewIndex" from-field="viewIndex"/>
// <field-map field-name="viewSize" from-field="viewSize"/>
// <field-map field-name="noConditionFind" value="Y"/>
//</service>
 
 performFindInMap.inputFields = parameters.inputFields;
 performFindInMap.entityName = "InvoiceAndType";
 performFindInMap.orderBy = "In-Process";
 performFindInMap.viewIndex = "INVOICE_IN_PROCESS";
 
 