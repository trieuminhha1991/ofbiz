package com.olbius.basehr.sqlQuery;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;

import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;


public class ResultEmployeeList extends TypeOlap{
	private SQLProcessor processor;
	private OlbiusQuery query;
	private ModelEntity modelEntity;
	public ResultEmployeeList(SQLProcessor processor, ModelEntity modelEntity, Timestamp fromDate, Timestamp thruDate, List<Map<String, Object>> listCond, 
			List<String> partyIdFromList, List<String> sortedList) {
		super();
		this.processor = processor;
		this.modelEntity = modelEntity;
		if(fromDate == null){
			fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		}
		if(thruDate == null){
			thruDate = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		}
		initQuery(fromDate, thruDate, listCond, partyIdFromList, sortedList);
	}
	private void initQuery(Timestamp fromDate, Timestamp thruDate, List<Map<String, Object>> listCond, List<String> partyIdFromList, List<String> sortedList) {
		query = new OlbiusQuery(processor);
		Condition dateCond = Condition.make("from_date", Condition.LESS_EQ, thruDate);
		dateCond.and(Condition.make("thru_date", Condition.GREATER_EQ, fromDate).or(Condition.make("thru_date is null")));
		OlbiusQuery partyRelQuery = new OlbiusQuery(processor);
		Condition partyRelCond = Condition.make("party_relationship_type_id", Condition.EQ, "EMPLOYMENT");
		partyRelCond.and(Condition.make("role_type_id_to", Condition.EQ, "EMPLOYEE")).and(dateCond);
		if(UtilValidate.isNotEmpty(partyIdFromList)){
			Condition partyFromCond = new Condition();
			for(String partyIdFrom: partyIdFromList){
				partyFromCond.or(Condition.make("party_id_from", Condition.EQ, partyIdFrom));
			}
			partyRelCond.and(partyFromCond);
		}
		partyRelQuery.select("GROUP_CONCAT(pg.group_name, ', ')", "department")
			.select("CONCAT (coalesce(per.last_name,''), ' ', coalesce(per.middle_name,''), ' ', coalesce(per.first_name,''))", "fullname")
			.select("ptyrel.party_id_to", "partyid")
			.select("per.working_status_id", "workingstatusid")
			.select("per.first_name", "firstname")
			.select("per.probationary_deadline", "probationarydeadline")
			.select("per.birth_date", "birthdate")
			.select("per.gender", "gender")
			.select("pty.party_code", "partycode")
			/*.from("party_relationship", "ptyrel")
			.join(Join.INNER_JOIN, "party_group", "pg", "ptyrel.party_id_from = pg.party_id")
			.join(Join.INNER_JOIN, "person", "per", "per.party_id = ptyrel.party_id_to")
			.join(Join.INNER_JOIN, "party", "pty", "pty.party_id = ptyrel.party_id_to")*/
			.from("PARTY_RELATIONSHIP", "ptyrel")
            .join(Join.INNER_JOIN, "PARTY_GROUP", "pg", "ptyrel.party_id_from = pg.party_id")
            .join(Join.INNER_JOIN, "PERSON", "per", "per.party_id = ptyrel.party_id_to")
            .join(Join.INNER_JOIN, "PARTY", "pty", "pty.party_id = ptyrel.party_id_to")

			.where(partyRelCond)
			.groupBy("ptyrel.party_id_to")
			.groupBy("per.working_status_id")
			.groupBy("per.first_name")
			.groupBy("per.probationary_deadline")
			.groupBy("per.birth_date")
			.groupBy("per.gender")
			.groupBy("pty.party_code")
			.groupBy("per.first_name, per.middle_name, per.last_name");
		
		
		OlbiusQuery emplPositionQuery = new OlbiusQuery(processor);
		emplPositionQuery.select("GROUP_CONCAT(postype.description, ', ')", "emplpositiontype")
		.select("posful.party_id", "partyid")
		/*.from("empl_position", "pos")
		.join(Join.INNER_JOIN, "empl_position_fulfillment", "posful", "pos.empl_position_id = posful.empl_position_id")
		.join(Join.INNER_JOIN, "empl_position_type", "postype", "pos.empl_position_type_id = postype.empl_position_type_id")*/
		.from("EMPL_POSITION", "pos")
		.join(Join.INNER_JOIN, "EMPL_POSITION_FULFILLMENT", "posful", "pos.empl_position_id = posful.empl_position_id")
        .join(Join.INNER_JOIN, "EMPL_POSITION_TYPE", "postype", "pos.empl_position_type_id = postype.empl_position_type_id")
        
		.where(dateCond)
		.groupBy("posful.party_id");
		
		OlbiusQuery dateJoinCompanyQuery = new OlbiusQuery(processor);
		dateJoinCompanyQuery.select("min(emplment.from_date)", "datejoincompany")
		.select("emplment.party_id_to", "partyidto")
		//.from("employment", "emplment")
		.from("EMPLOYMENT", "emplment")
		.groupBy("emplment.party_id_to");
		
		OlbiusQuery dateResignQuery = new OlbiusQuery(processor);
		dateResignQuery.select("max(emplment_thru.thru_date)", "dateresign")
		.select("emplment_thru.party_id_to", "partyidto")
		//.from("employment", "emplment_thru")
		.from("EMPLOYMENT", "emplment_thru")
		.groupBy("emplment_thru.party_id_to");
		
		query.select("*")
			.from(partyRelQuery, "ptyrel")
			.join(Join.LEFT_OUTER_JOIN, emplPositionQuery, "ptypos", "ptyrel.partyid = ptypos.partyid")
			.join(Join.LEFT_OUTER_JOIN, dateJoinCompanyQuery, "emplment", "ptyrel.partyid = emplment.partyidto")
			.join(Join.LEFT_OUTER_JOIN, dateResignQuery, "EMTD", "ptyrel.partyid = EMTD.partyidto");
		if(UtilValidate.isNotEmpty(listCond)){
			Condition mainCond = createCondition(listCond);
			if(mainCond != null){
				query.where(mainCond);
			}
		}
		if(UtilValidate.isNotEmpty(sortedList)){
			String orderBy = sortedList.get(0);
			if(orderBy.contains("-")){
				orderBy = orderBy.replaceAll("-", "");
                if(orderBy.indexOf("NULLS LAST") > 0){
                    orderBy = orderBy.substring(0, orderBy.indexOf("NULLS LAST")) + " DESC";
                }else{
                    orderBy = orderBy + " DESC";
                }
            } else {
                if(orderBy.indexOf("NULLS LAST") > 0){
                    orderBy = orderBy.substring(0, orderBy.indexOf("NULLS LAST")) + " ASC";
                }else{
                    orderBy = orderBy + " ASC";
                }
			}
			query.orderBy(orderBy);
		}else{
			query.orderBy("first_name");
		}
	}
	public List<Map<String, Object>> getEmployeeList(){
		List<Map<String, Object>> list = FastList.newInstance();
		try {
			ResultSet rs = query.getResultSet();
			while(rs.next()) {
				Map<String, Object> tmp = FastMap.newInstance();
				tmp.put("partyCode", rs.getObject("partycode"));
				tmp.put("partyId", rs.getObject("partyid"));
				tmp.put("firstName", rs.getObject("firstname"));
				tmp.put("fullName", rs.getObject("fullname"));
				tmp.put("department", rs.getObject("department"));
				tmp.put("workingStatusId", rs.getObject("workingstatusid"));
				tmp.put("probationaryDeadline", rs.getObject("probationarydeadline"));
				tmp.put("birthDate", rs.getObject("birthdate"));
				tmp.put("gender", rs.getObject("gender"));
				tmp.put("emplPositionType", rs.getObject("emplpositiontype"));
				tmp.put("dateJoinCompany", rs.getObject("datejoincompany"));
				tmp.put("dateResign", rs.getObject("dateresign"));
				list.add(tmp);
			}
		} catch (GenericDataSourceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(processor != null) {
				 try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	private Condition createCondition(List<Map<String, Object>> listCond) {
		Condition cond = new Condition();
		for(Map<String, Object> m: listCond){
			String fieldName = (String) m.get("fieldName");
			String operator = (String) m.get("operator");
			String value = (String) m.get("value");
			ModelField modelField = modelEntity.getField(fieldName);
            boolean hasUpper = (boolean) m.get("hasUpper");
			String type = modelField.getType();
			String colVal = fieldName;
            if(operator.equalsIgnoreCase("LIKE")){
                if (hasUpper) cond.and(Condition.makeFuncField(" UPPER(" + colVal + ")", " LIKE ", "UPPER", "%" + value +"%"));
                else cond.and(Condition.make(colVal, " LIKE ", "%" + value +"%"));
			}else if(operator.equalsIgnoreCase("NOT_LIKE")){
				cond.and(Condition.make(colVal, " NOT LIKE ", "%" + value +"%"));
			}else if(operator.equalsIgnoreCase("EQUAL")){
				cond.and(Condition.make(colVal, Condition.EQ, value));
			}else if(operator.equalsIgnoreCase("NOT_EQUAL")){
				cond.and(Condition.make(colVal, Condition.NOT_EQ, value));
			}else if(operator.equalsIgnoreCase("RANGE")){
				String valueFrom = (String) m.get("valueFrom");
				String valueTo = (String) m.get("valueTo");
				if (UtilValidate.isEmpty(valueFrom.trim()) || UtilValidate.isEmpty(valueTo.trim())) {
					break;
				}
				if ("date-time".equals(type)) {
					Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
					Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
					cond.and(Condition.make(colVal, Condition.GREATER_EQ, valueFromTs));
					cond.and(Condition.make(colVal, Condition.LESS_EQ, valueToTs));
				} if ("date".equals(type)){
					Date valueFromDate = Date.valueOf(valueFrom);
					Date valueToDate = Date.valueOf(valueTo);
					cond.and(Condition.make(colVal, Condition.GREATER_EQ, valueFromDate));
					cond.and(Condition.make(colVal, Condition.LESS_EQ, valueToDate));
				}
			}else if(operator.equalsIgnoreCase("=")){
				if ("currency-amount".equals(type) || "currency-precise".equals(type) 
						|| "fixed-point".equals(type) || "fixed-point-extend".equals(type)) {
					BigDecimal valueBd = new BigDecimal(value);
					cond.and(Condition.make(colVal, Condition.EQ, valueBd));
				} else  if ("numeric".equals(type)) {
					Long valueLong = Long.valueOf(value);
					cond.and(Condition.make(colVal, Condition.EQ, valueLong));
				} else {
					cond.and(Condition.make(colVal, Condition.EQ, value));
				}
			}else if(operator.equalsIgnoreCase(">=")){
				if ("currency-amount".equals(type) || "currency-precise".equals(type) 
						|| "fixed-point".equals(type) || "fixed-point-extend".equals(type)) {
					BigDecimal valueBd = new BigDecimal(value);
					cond.and(Condition.make(colVal, Condition.GREATER_EQ, valueBd));
				}else if ("date-time".equals(type)) {
					Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
					cond.and(Condition.make(colVal, Condition.GREATER_EQ, valueTs));
				}else if ("date".equals(type)) {
					Date valueDate = Date.valueOf(value);
					cond.and(Condition.make(colVal, Condition.GREATER_EQ, valueDate));
				}
			}else if(operator.equalsIgnoreCase("<=")){
				if ("currency-amount".equals(type) || "currency-precise".equals(type) 
						|| "fixed-point".equals(type) || "fixed-point-extend".equals(type)) {
					BigDecimal valueBd = new BigDecimal(value);
					cond.and(Condition.make(colVal, Condition.LESS_EQ, valueBd));
				}else if ("date-time".equals(type)) {
					Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
					cond.and(Condition.make(colVal, Condition.LESS_EQ, valueTs));
				}else if ("date".equals(type)) {
					Date valueDate = Date.valueOf(value);
					cond.and(Condition.make(colVal, Condition.LESS_EQ, valueDate));
				}
			}else if(operator.equalsIgnoreCase(">")){
				if ("currency-amount".equals(type) || "currency-precise".equals(type) 
						|| "fixed-point".equals(type) || "fixed-point-extend".equals(type)) {
					BigDecimal valueBd = new BigDecimal(value);
					cond.and(Condition.make(colVal, Condition.GREATER, valueBd));
				}
			}else if(operator.equalsIgnoreCase("<")){
				if ("currency-amount".equals(type) || "currency-precise".equals(type) 
						|| "fixed-point".equals(type) || "fixed-point-extend".equals(type)) {
					BigDecimal valueBd = new BigDecimal(value);
					cond.and(Condition.make(colVal, Condition.LESS, valueBd));
				}
			}else if(operator.equalsIgnoreCase("<>")){
				if ("currency-amount".equals(type) || "currency-precise".equals(type) 
						|| "fixed-point".equals(type) || "fixed-point-extend".equals(type)) {
					BigDecimal valueBd = new BigDecimal(value);
					cond.and(Condition.make(colVal, Condition.NOT_EQ, valueBd));
				}
			}
		
		}
		return cond;
	}
}
