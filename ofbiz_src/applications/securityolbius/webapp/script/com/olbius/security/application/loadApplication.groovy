
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.Delegator

Delegator dlg = delegator

olbiusApp = dlg.findOne("OlbiusApplication", UtilMisc.toMap("applicationId", parameters.applicationId), false)

context.olbiusApp = olbiusApp
