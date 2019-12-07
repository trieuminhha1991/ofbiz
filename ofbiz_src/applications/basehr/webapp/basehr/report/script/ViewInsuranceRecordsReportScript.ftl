<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-3d.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/highcharts-more.js"></script>
<script type="text/javascript" src="/highcharts/assets/js/exporting.js"></script>
<#--<script type="text/javascript" src="/images/bi-x/olbius.js"></script>
<script type="text/javascript" src="/images/bi-x/olbiusWindow.js"></script>
<script type="text/javascript" src="/images/bi-x/olbiusOLap.js"></script>-->
<script type="text/javascript" src="/images/bi-x/olbius.config.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.popup.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.new.js"></script>
<script type="text/javascript" src="/images/bi-x/olbius.grid.js"></script>
<script type="text/javascript">
var date_type_source = [{text: '${StringUtil.wrapString(uiLabelMap.olap_day)}', value: 'DAY'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_week)}', value: 'WEEK'},
        {text:'${StringUtil.wrapString(uiLabelMap.olap_month)}', value: 'MONTH'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_quarter)}', value: 'QUARTER'},
        {text: '${StringUtil.wrapString(uiLabelMap.olap_year)}', value: 'YEAR'}];
var cur_date = new Date();
var past_date = new Date(cur_date);
past_date.setDate(1);
<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>
var globalVar = {
	rootPartyArr: [
			<#if rootOrgList?has_content>
				<#list rootOrgList as rootOrgId>
				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
				{
					partyId: "${rootOrgId}",
					partyName: "${rootOrg.groupName}"
				},
				</#list>
			</#if>
		],
};
var status_data = [
       {text : '${StringUtil.wrapString(uiLabelMap.CommonBefore)}', value : 'b'},
       {text : '${StringUtil.wrapString(uiLabelMap.CommonAfter)}', value : 'a'}
];

var uiLabelMap = {};
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";

</script>