package com.olbius.jobanalysis.entity;

import java.sql.Timestamp;
import java.util.List;

import org.ofbiz.entity.GenericValue;

public class JobRequisitionEntity {
	private String jobRequisitionId;
	private String jobRequestId;
	private List<GenericValue> SkillTypeIds;
	private String JobPostingType;
	private String ExamTypeEnumId;
	private Long experienceMonths;
	private Long experienceYears;
	private Timestamp fromDate;
	private Timestamp thruDate;
	private String statusId;
	private List<GenericValue> qualifications;
	
	
	public String getJobRequestId() {
		return jobRequestId;
	}
	public void setJobRequestId(String jobRequestId) {
		this.jobRequestId = jobRequestId;
	}
	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getThruDate() {
		return thruDate;
	}
	public void setThruDate(Timestamp thruDate) {
		this.thruDate = thruDate;
	}
	public String getJobRequisitionId() {
		return jobRequisitionId;
	}
	public void setJobRequisitionId(String jobRequisitionId) {
		this.jobRequisitionId = jobRequisitionId;
	}
	public List<GenericValue> getSkillTypeIds() {
		return SkillTypeIds;
	}
	public void setSkillTypeIds(List<GenericValue> skillTypeIds) {
		SkillTypeIds = skillTypeIds;
	}
	public String getJobPostingType() {
		return JobPostingType;
	}
	public void setJobPostingType(String jobPostingType) {
		JobPostingType = jobPostingType;
	}
	public String getExamTypeEnumId() {
		return ExamTypeEnumId;
	}
	public void setExamTypeEnumId(String examTypeEnumId) {
		ExamTypeEnumId = examTypeEnumId;
	}
	public Long getExperienceMonths() {
		return experienceMonths;
	}
	public void setExperienceMonths(Long experienceMonths) {
		this.experienceMonths = experienceMonths;
	}
	public Long getExperienceYears() {
		return experienceYears;
	}
	public void setExperienceYears(Long experienceYears) {
		this.experienceYears = experienceYears;
	}
	public List<GenericValue> getQualifications() {
		return qualifications;
	}
	public void setQualifications(List<GenericValue> qualifications) {
		this.qualifications = qualifications;
	}
}
