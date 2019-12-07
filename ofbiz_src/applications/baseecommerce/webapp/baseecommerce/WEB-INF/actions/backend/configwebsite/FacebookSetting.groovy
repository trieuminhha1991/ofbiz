import javax.servlet.http.HttpServletRequest;

import com.olbius.obb.facebook.Facebook;

Facebook facebook = new Facebook();

Map<String, Object> settings = facebook.getSettings(delegator, userLogin);

context.settings = settings;