package com.olbius.obb.facebook;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.webapp.website.WebSiteWorker;

import com.olbius.baseecommerce.backend.ConfigWebSiteServices;

public class Facebook {

	private SimpleDateFormat sdfmad;

	public Facebook() {
		this.setSdfmad(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	};

	public String getOriginPicture(String url) {
		String img = "";
		try {
			URL urlobj = new URL(url);
			img += urlobj.getProtocol() + "://" + urlobj.getAuthority();
			String urlTemp = urlobj.getPath();
			String[] temp = urlTemp.split("v/t1.0-9/");
			if (temp.length > 1) {
				img += temp[0];
				String[] temp2 = temp[1].split("/");
				for (int i = 0; i < temp2.length; i++) {
					if (temp2[i].contains(".jpg") || temp2[i].contains(".png")) {
						img += temp2[i];
					}
				}
			}
			return img;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getAbstract(String msg, int max) {
		String readmore = "";
		String[] temp = msg.split(" ");
		if (temp.length > 30) {
			for (int i = 0; i < 30; i++) {
				readmore += temp[i] + " ";
			}
		}
		return readmore;
	}

	// convert time to status trigger
	public String timeToStatus(String orig) {
		// get current datetime
		Date cur = new Date();
		String curDt = this.sdfmad.format(cur);
		String status = this.convertStatus(orig, curDt);
		return status;
	}

	// convert time from fb to standard
	public String convertTime(String orig) {
		String[] temp = orig.split("[T+]");
		if (temp.length != 0) {
			String dt = temp[0] + " " + temp[1];
			dt = this.getTimezone(dt);
			return dt;
		}
		return orig;
	}

	// get convert GMT to current timezone
	public String getTimezone(String datetime) {
		this.sdfmad.setTimeZone(TimeZone.getTimeZone("GMT"));
		// this.sdfmad.setTimeZone(TimeZone.getDefault());
		Date inptdate = null;
		try {
			inptdate = this.sdfmad.parse(datetime);
			this.sdfmad.setTimeZone(TimeZone.getDefault());
			String tmp = this.sdfmad.format(inptdate);
			return tmp;
		} catch (ParseException e) {
			e.printStackTrace();
			return datetime;
		}
	}

	// calculate time to status
	public String convertStatus(String dt1, String dt2) {
		try {
			Date date1 = this.sdfmad.parse(dt1);
			Date date2 = this.sdfmad.parse(dt2);
			long sub = Math.abs(date2.getTime() - date1.getTime());
			String status = this.getStatus(sub);
			return status;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "over";
	}

	/*
	 * mapping time to status if seconds < 60 => just now if min < 60' => about
	 * ? minute if hour < 24 => about ? hour otherwise that over and set date
	 * time
	 */
	public String getStatus(long time) {
		long sec = Math.round(time / 1000);
		long min = Math.round(sec / 60);
		long hour = Math.round(min / 60);
		if (sec < 60) {
			return "Just now";
		} else if (min < 60) {
			return "about " + min + " mins";
		} else if (hour < 24) {
			return "about " + hour + " hours";
		} else {
			return "over";
		}
	}

	public Map<String,Object> getSettings(HttpServletRequest request) {
		Map<String, Object> facebookSettings = FastMap.newInstance();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		try {
			String webSiteId = WebSiteWorker.getWebSiteId(request);
			List<GenericValue> params = delegator.findList("WebSiteContentDetail",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("webSiteId", webSiteId),
								EntityCondition.makeCondition("webSiteContentTypeId", "FACEBOOK_CONFIG"))), null, null, null, true);
			for(GenericValue fb : params){
				facebookSettings.put(fb.getString("contentId"), fb.getString("description"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return facebookSettings;
	}
	public Map<String,Object> getSettings(Delegator delegator, GenericValue userLogin) {
		Map<String, Object> facebookSettings = FastMap.newInstance();
		try {
			String webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
			List<GenericValue> params = delegator.findList("WebSiteContentDetail",
					EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("webSiteId", webSiteId),
								EntityCondition.makeCondition("webSiteContentTypeId", "FACEBOOK_CONFIG"))), null, null, null, true);
			for(GenericValue fb : params){
				facebookSettings.put(fb.getString("contentId"), fb.getString("description"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return facebookSettings;
	}

	public void sortPostByDate(ArrayList<Map<String, Object>> posts, Map<String, Object> current) {
		int size = posts.size();
		if (size != 0) {
			for (int i = 0; i < size; i++) {
				Map<String, Object> t1 = posts.get(i);
				Post p1 = (Post) t1.get("fbcontent");
				Post p2 = (Post) current.get("fbcontent");
				try {
					String d1String = p1.getCreated();
					String d2String = p2.getCreated();
					if (d1String != null && d2String != null) {
						Date d1 = this.sdfmad.parse(d1String);
						Date d2 = this.sdfmad.parse(d2String);
						if (d2.after(d1)) {
							posts.add(i, current);
							return;
						} else if (i == (size - 1)) {
							posts.add(current);
							return;
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else {
			posts.add(current);
		}
	}

	public SimpleDateFormat getSdfmad() {
		return sdfmad;
	}

	public void setSdfmad(SimpleDateFormat sdfmad) {
		this.sdfmad = sdfmad;
	}

}