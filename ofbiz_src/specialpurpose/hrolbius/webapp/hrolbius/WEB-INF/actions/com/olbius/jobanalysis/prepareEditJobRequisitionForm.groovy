import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.jobanalysis.entity.SkillType;
import com.olbius.jobanalysis.entity.Qualification;

//set up data for Edit Job Requisition Form
jobRequisitionId = parameters.jobRequisitionId
if(jobRequisitionId != null){
	requisition = delegator.findOne("JobRequisition",[jobRequisitionId:jobRequisitionId],false);
	selectedSkillTypeList = delegator.findList("JobRequisitionSkill", 
									EntityCondition.makeCondition("jobRequisitionId",jobRequisitionId), 
									null, null,null, false);
	selectedQualificationList = delegator.findList("JobRequisitionQualType", 
									EntityCondition.makeCondition("jobRequisitionId",jobRequisitionId), 
									null, null,null, false);
}else{
	requisition = delegator.makeValue("JobRequisition");
	selectedSkillTypeList = delegator.makeValue("JobRequisitionSkill");
	selectedQualificationList = delegator.makeValue("JobRequisitionQualType");
}
orderBy = new ArrayList<String>();
orderBy.add("description");
skillTypeList = delegator.findList("SkillType", null , null, orderBy,null, false);
qualificationList = delegator.findList("PartyQualType",EntityCondition.makeCondition("parentTypeId","DEGREE"), null, orderBy,null, false);
examTypeEnumList = delegator.findList("Enumeration", 
									EntityCondition.makeCondition("enumTypeId","EXAM_TYPE"), 
									null, orderBy,null, false);
customSkillTypeList = new ArrayList<SkillType>();
customQualificationList = new ArrayList<Qualification>();

selectedSkillTypeIdList = new ArrayList<String>();
selectedPartyQualTypeIdList = new ArrayList<String>();
for(GenericValue skillType : selectedSkillTypeList){
	selectedSkillTypeIdList.add(skillType.getString("skillTypeId"));
}
for(GenericValue qualification : selectedQualificationList){
	selectedPartyQualTypeIdList.add(qualification.getString("partyQualTypeId"));
}
for(GenericValue skillType : skillTypeList){
	SkillType st = new SkillType();
	st.setSkillTypeId(skillType.getString("skillTypeId"));
	st.setDescription(skillType.getString("description"));
	st.setSelected(selectedSkillTypeIdList.contains(skillType.getString("skillTypeId")));
	customSkillTypeList.add(st);
}
for(GenericValue qualification : qualificationList){
	Qualification qual = new Qualification();
	qual.setPartyQualTypeId(qualification.getString("partyQualTypeId"));
	qual.setDescription(qualification.getString("description"));
	qual.setSelected(selectedPartyQualTypeIdList.contains(qualification.getString("partyQualTypeId")));
	customQualificationList.add(qual);
}
context.requisition = requisition;
context.customQualificationList = customQualificationList;
context.customSkillTypeList = customSkillTypeList;
context.examTypeEnumList = examTypeEnumList;