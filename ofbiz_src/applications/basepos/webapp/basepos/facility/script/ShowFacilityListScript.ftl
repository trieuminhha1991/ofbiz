<#assign facilityList = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityTypeId", "WAREHOUSE"), null, null, null, false) !>
<script type="text/javascript">
    var showFacilityListGlobalObject = (function(){
            var facilityList = [
            <#if facilityList?exists>
                <#list facilityList as facility >
                    {
                        facilityId: '${facility.facilityId?if_exists}',
                        facilityName: '${StringUtil.wrapString(facility.facilityName?if_exists)}'
                    },
                </#list>
            </#if>
        ];
        var getDescriptionFacility = function(facilityId){
        	var facilityName = facilityId;
            for (var index in facilityList) {
                if (facilityList[index].facilityId == facilityId) {
                    facilityName =  facilityList[index].facilityName;
                }
            }
            return facilityName;
        };
        return{
        	facilityList: facilityList,
        	getDescriptionFacility: getDescriptionFacility
        }
    }());
</script>