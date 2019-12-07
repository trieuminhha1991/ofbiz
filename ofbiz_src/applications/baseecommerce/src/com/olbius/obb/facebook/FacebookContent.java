package com.olbius.obb.facebook;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.json.JSONArray;
import com.olbius.json.JSONObject;

//import com.olbius.json.*;

public class FacebookContent {
	/**/

	private final static Map<String, Object> mainContent = new HashMap<String, Object>();
	private final static Map<String, Object> listPosts = new HashMap<String, Object>();
	private static long lastUpdatedMain;
	private static long lastUpdatedPost;

	private static final int expired_time = 600000;

	public static Map<String, Object> getListPosts() {
		return listPosts;
	}

	public static Map<String, Object> getFacebookContent(HttpServletRequest request) {
		Facebook fb = new Facebook();
		Map<String, Object> facebookSettings = fb.getSettings(request);
		Boolean needUpdated = needUpdated(true);
		if (needUpdated) {
			if (facebookSettings != null) {
				String pageID = (String) facebookSettings.get("FB_PAGE_ID");
				String at = (String) facebookSettings.get("FB_ACCESS_TOKEN");
				if(UtilValidate.isNotEmpty(at)){
					String access_token = getAccessToken(at);
					mainContent.clear();
					mainContent.putAll(getPagePost(pageID, 10, "feed", access_token));
				}
			} else {
				mainContent.put("fbcontent", new ArrayList<Post>());
				mainContent.put("pageInfo", new Page());
			}
		}
		if(mainContent.isEmpty()) {
			mainContent.put("fbcontent", new ArrayList<Post>());
			mainContent.put("pageInfo", new Page());
		}
		return mainContent;
	}

	// get list post in supplier page
	public static Map<String, Object> getListSupplierPost(HttpServletRequest request) {
		Facebook fb = new Facebook();
		Map<String, Object> facebookSettings = fb.getSettings(request);
		ArrayList<Map<String, Object>> posts = new ArrayList<Map<String, Object>>();
		Boolean needUpdated = needUpdated(false);
		if (needUpdated) {
			if (facebookSettings != null) {
				String page = (String) facebookSettings.get("FB_SUPPLIER_PAGE");
				if(UtilValidate.isNotEmpty(page)){
					String[] page_arr = page.split(", ");
					String tokens = (String) facebookSettings.get("FB_ACCESS_TOKEN");
					String token;
					for (int i = 0; i < page_arr.length; i++) {
						token = getAccessToken(tokens);
						Map<String, Object> tmp = getPagePost(page_arr[i], 25,
								"posts", token);
						@SuppressWarnings("unchecked")
						ArrayList<Post> tmp2 = (ArrayList<Post>) tmp
								.get("fbcontent");
						Post res = getFirstPost(tmp2);
						tmp.put("fbcontent", res);
						fb.sortPostByDate(posts, tmp);
					}
				}
			}
			listPosts.put("listPost", posts);
		}
		if(listPosts.isEmpty()) {
			listPosts.put("listPost", new ArrayList<Map<String, Object>>());
		}
		return listPosts;
	}

	private static Post getFirstPost(ArrayList<Post> posts) {
		Post res = new Post();
		String link;
		Post tmp;
		for (int i = 0; i < posts.size(); i++) {
			tmp = posts.get(i);
			link = tmp.getLink();
			if (link != null && link != "") {
				res = posts.get(i);
				return res;
			}
		}
		return res;
	}

	// get list post in page
	public static Map<String, Object> getPagePost(String pageID, int limit,
			String path, String access_token) {
		String url = "https://graph.facebook.com/" + pageID + "/" + path
				+ "?access_token=" + access_token + "&limit=" + limit;
		Map<String, Object> content = FastMap.newInstance();
		HTTPSRequest request = new HTTPSRequest(url);
		JSONObject res = request.getHTTPSContent();
		ArrayList<Post> listData = new ArrayList<Post>();
		Facebook fb = new Facebook();
		if (res.has("data")) {
			JSONArray data = res.getJSONArray("data");
			String status, tmp; // list temp value
			for (int i = 0; i < data.length(); i++) {
				JSONObject o = (JSONObject) data.getJSONObject(i);
				Post p = new Post();
				if (o.has("link")) {
					p.setId(o.getString("id"));
					tmp = fb.convertTime(o.getString("created_time"));
					p.setCreated(tmp);
					tmp = fb.convertTime(o.getString("updated_time"));
					p.setUpdated(tmp);
					// calculate time and change to status (just now....)
					status = fb.timeToStatus(p.getCreated());
					p.setStatus(status);
					p.setLink(o.getString("link"));
					p.setType(o.getString("type"));
					if (o.has("message")) {
						setAbstract(fb, p, o, "message");
					} else if (o.has("caption")) {
						setAbstract(fb, p, o, "caption");
					} else if (o.has("description")) {
						setAbstract(fb, p, o, "description");
					}
					if (o.has("picture")) {
						String ava = fb
								.getOriginPicture(o.getString("picture"));
						p.setPicture(ava);
					}
					if (o.has("object_id")) {
						p.setObjectId(o.getString("object_id"));
					}
					if (o.has("from")) {
						JSONObject fromJs = o.getJSONObject("from");
						if (fromJs.has("id") && fromJs.has("name")) {
							Page from = new Page(fromJs.getString("id"),
									fromJs.getString("name"));
							p.setFrom(from);
						}
					}
					if (o.has("to")) {
						JSONObject tojs = o.getJSONObject("to");
						if (tojs.has("id") && tojs.has("name")) {
							Page to = new Page(tojs.getString("id"),
									tojs.getString("name"));
							p.setTo(to);
						}
					}
					listData.add(p);
				}
			}
			Page p = new Page(pageID);
			p.getPageInfo();
			content.put("fbcontent", listData);
			content.put("pageInfo", p);
			return content;
		}
		content.put("fbcontent", listData);
		content.put("pageInfo", new Page());
		return content;
	}

	/* get random one access token in list access_token */
	private static String getAccessToken(String tokens) {
		String[] token_list = tokens.split(", ");
		String access_token = (token_list[new Random()
				.nextInt(token_list.length)]);
		access_token = access_token.replaceAll("\\s", "");
		return access_token;
	}

	/* set abstract for message, story, description facebook */
	private static void setAbstract(Facebook fb, Post p, JSONObject o,
			String key) {
		int length = 20;
		String msg = o.getString(key);
		String abs = "";
		if (msg.length() > length) {
			abs = fb.getAbstract(msg, length);
			// abs = abs.replaceAll("\\n", "<br\\>");
			p.setAbs(abs);
		}
//		msg = msg.replaceAll("\\n", "<br\\>");
		p.setMessage(msg);
	}

	private static Boolean needUpdated(Boolean flag) {
		Date d = new Date();
		long current = d.getTime();
		if (flag) {
			if (lastUpdatedMain != 0) {
				long sub = current - lastUpdatedMain;
				if (sub >= expired_time) {
					lastUpdatedMain = current;
					return true;
				}
				return false;
			}
			lastUpdatedMain = current;
			return true;
		} else {
			if (lastUpdatedPost != 0) {
				long sub = current - lastUpdatedPost;
				if (sub >= expired_time) {
					lastUpdatedPost = current;
					return true;
				}
				return false;
			}
			lastUpdatedPost = current;
			return true;
		}

	}
}
