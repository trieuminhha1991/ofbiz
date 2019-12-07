${screens.render("component://basepo/widget/AgreementScreens.xml#AddAgreement")}
<script src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<script>
	const agreementId = "${(parameters.agreementId)?if_exists}";
</script>

<div id="jqxNotification">
<div id="notificationContent">
</div>
</div>