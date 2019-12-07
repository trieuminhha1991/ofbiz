import org.ofbiz.base.util.UtilMisc;

context.application = delegator.findOne("OlbiusApplication", UtilMisc.toMap("applicationId", parameters.moduleId), false);
context.activeTab = parameters.activeTab;