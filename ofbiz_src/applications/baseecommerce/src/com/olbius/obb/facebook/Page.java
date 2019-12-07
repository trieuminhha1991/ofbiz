package com.olbius.obb.facebook;

import com.olbius.json.JSONObject;

public class Page {
	private String id;
	private String cover;
	private String avatar;
	private String name;
	private String link;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	public Page(){}
	public Page(String id) {
		this.id = id;
	}

	public Page(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getAvatarOverGraph() {
		String avatar = "";
		HTTPSRequest req = new HTTPSRequest("https://graph.facebook.com/"
				+ this.getId() + "/picture?type=normal&redirect=false");
		JSONObject res = req.getHTTPSContent();
		if (res.has("data")) {
			JSONObject data = res.getJSONObject("data");
			avatar = data.getString("url");
		}
		return avatar;
	}

	public void getPageInfo() {
		HTTPSRequest req = new HTTPSRequest("https://graph.facebook.com/"
				+ this.getId());
		JSONObject obj = req.getHTTPSContent();
		if (obj.has("name")) {
			this.setName(obj.getString("name"));
		}
		if (obj.has("link")) {
			this.setLink(obj.getString("link"));
		}
		if (obj.has("cover")) {
			JSONObject cover = obj.getJSONObject("cover");
			Facebook fb = new Facebook();
			this.setCover(fb.getOriginPicture(cover.getString("source")));
		}
		this.setAvatar(this.getAvatarOverGraph());
	}
}
