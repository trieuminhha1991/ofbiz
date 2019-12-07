package com.olbius.jobanalysis.entity;

public class SkillType {
	private String skillTypeId;
	private String description;
	private boolean selected;
	
	public String getSkillTypeId() {
		return skillTypeId;
	}
	public void setSkillTypeId(String skillTypeId) {
		this.skillTypeId = skillTypeId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
