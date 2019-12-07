package com.olbius.recruitment.entity;

import java.util.List;

import org.ofbiz.entity.GenericValue;

public class RecruitmentTestPlan {
	private GenericValue recruitmentTestPlan;
	private List<GenericValue> recruitmentTestList;
	
	
	public GenericValue getRecruitmentTestPlan() {
		return recruitmentTestPlan;
	}
	public void setRecruitmentTestPlan(GenericValue recruitmentTestPlan) {
		this.recruitmentTestPlan = recruitmentTestPlan;
	}
	public List<GenericValue> getRecruitmentTestList() {
		return recruitmentTestList;
	}
	public void setRecruitmentTestList(List<GenericValue> recruitmentTestList) {
		this.recruitmentTestList = recruitmentTestList;
	}
	
	
}
