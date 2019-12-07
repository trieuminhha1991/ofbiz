import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import com.olbius.obb.facebook.Facebook;

import javolution.util.*;

//System.out.println("fbsetting in ");
Facebook facebook = new Facebook();

Map<String, Object> facebookSettings = facebook.getSettings(request);

context.facebookSettings = facebookSettings;
