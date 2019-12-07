import org.ofbiz.base.util.UtilMisc;

import com.olbius.baseecommerce.backend.ConfigWebSiteServices;

def webSiteId = ConfigWebSiteServices.getCurrentWebSite(delegator, userLogin);
def webSite = delegator.findOne("WebSite",
		UtilMisc.toMap("webSiteId", webSiteId), false);
context.webSite = webSite;