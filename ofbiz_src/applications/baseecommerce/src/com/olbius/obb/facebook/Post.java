package com.olbius.obb.facebook;

public class Post {
	private String id;
	private String objectId;
	private String abs;
	private String type;
	private String created;
	private String updated;
	private String status;
	private String message;
	private String picture;
	private String link;
	// private String story;
	// private String description;
	private Page from;
	private Page to;

	public Post(String id, String story, String type, String created,
			String updated, String status, String message, String abs,
			String picture, String link, Page from, Page to) {
		this.id = id;
		// this.story = story;
		this.type = type;
		this.created = created;
		this.updated = updated;
		this.message = message;
		this.abs = abs;
		this.picture = picture;
		this.link = link;
		this.status = status;
		this.from = from;
		this.to = to;
	}

	public Post() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String id) {
		this.objectId = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreated() {
		return this.created;
	}
	public String getTimeFormat(String time){
		String[] t = time.split(" ");
		if(t.length == 2){
			String[] t1 = t[0].split("-");
			if(t1.length == 3){
				String d = t[1] + " " + t1[2] + "-" + t1[1] + "-" +t1[0];
				return d;
			}
			return time;
		}
		return time;
	}
	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return this.updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPicture() {
		return this.picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAbs() {
		return abs;
	}
	public String getHtmlAbs(){
		return abs.replaceAll("\\n", "<br\\>");
	}
	public void setAbs(String abs) {
		this.abs = abs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Page getFrom() {
		return from;
	}

	public void setFrom(Page from) {
		this.from = from;
	}

	public Page getTo() {
		return to;
	}

	public void setTo(Page to) {
		this.to = to;
	}
	// public String getStory() {
	// return this.story;
	// }
	//
	// public void setStory(String story) {
	// this.story = story;
	// }
	// public String getDescription() {
	// return description;
	// }
	//
	// public void setDescription(String description) {
	// this.description = description;
	// }
}
