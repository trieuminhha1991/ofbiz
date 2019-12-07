package com.olbius.agreement;

public class AgreementTermData {
	private String textValue;
	private String termTypeId;
	private String termTypeDesc;
	private String agreementTermId;
	private String rootTermTypeId;
	private boolean hasChild;
	//identify level of termTypeId with root  
	private Integer levelInTermTypeTree;
	
	public AgreementTermData() {
		super();
	}
	public AgreementTermData(String termTypeId,
			String termTypeDesc, String rootTermTypeId, Integer levelInTermTypeTree) {
		super();
		this.termTypeId = termTypeId;
		this.termTypeDesc = termTypeDesc;
		this.rootTermTypeId = rootTermTypeId;
		this.levelInTermTypeTree = levelInTermTypeTree;
	}
	public String getTextValue() {
		return textValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public String getTermTypeId() {
		return termTypeId;
	}
	public void setTermTypeId(String termTypeId) {
		this.termTypeId = termTypeId;
	}
	public String getTermTypeDesc() {
		return termTypeDesc;
	}
	public void setTermTypeDesc(String termTypeDesc) {
		this.termTypeDesc = termTypeDesc;
	}
	public String getRootTermTypeId() {
		return rootTermTypeId;
	}
	public void setRootTermTypeId(String rootTermTypeId) {
		this.rootTermTypeId = rootTermTypeId;
	}
	public Integer getLevelInTermTypeTree() {
		return levelInTermTypeTree;
	}
	public void setLevelInTermTypeTree(Integer levelInTermTypeTree) {
		this.levelInTermTypeTree = levelInTermTypeTree;
	}
	public String getAgreementTermId() {
		return agreementTermId;
	}
	public void setAgreementTermId(String agreementTermId) {
		this.agreementTermId = agreementTermId;
	}
	public boolean isHasChild() {
		return hasChild;
	}
	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}
	
}
