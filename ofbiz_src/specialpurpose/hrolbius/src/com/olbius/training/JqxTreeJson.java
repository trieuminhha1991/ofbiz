package com.olbius.training;

public class JqxTreeJson {
	
	private String id;
	private String text;
	private String parentId;
	private String idValueEntity;
	private String iconUrl;
	public JqxTreeJson(String id, String text, String parentId) {
		super();
		this.id = id;
		this.text = text;
		this.parentId = parentId;
	}
	
	public JqxTreeJson(String id, String text, String parentId,
			String idValueEntity) {
		super();
		this.id = id;
		this.text = text;
		this.parentId = parentId;
		this.idValueEntity = idValueEntity;
	}

	public JqxTreeJson(String id, String text, String parentId,
			String idValueEntity, String iconUrl) {
		super();
		this.id = id;
		this.text = text;
		this.parentId = parentId;
		this.idValueEntity = idValueEntity;
		this.iconUrl = iconUrl;
	}

	public JqxTreeJson() {
		super();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getIdValueEntity() {
		return idValueEntity;
	}

	public void setIdValueEntity(String idValueEntity) {
		this.idValueEntity = idValueEntity;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
}
