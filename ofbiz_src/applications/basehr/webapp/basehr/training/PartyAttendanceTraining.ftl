<#if !addWindowId?has_content>
	<#assign addWindowId = "AddNewPartyAttWindow"/>
</#if>
<script type="text/javascript">
 globalVar.addWindowId = "${addWindowId}";
</script>
<div id="containerpartyAttdanceGrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationpartyAttdanceGrid">
    <div id="notificationContentpartyAttdanceGrid">
    </div>
</div>
<div class="row-fluid" style="margin-top: 15px">
	<div id="partyAttdanceGrid"></div>
</div>
<script type="text/javascript" src="/hrresources/js/training/PartyAttendanceTraining.js"></script>