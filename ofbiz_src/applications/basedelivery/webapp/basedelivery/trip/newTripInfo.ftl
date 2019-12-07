<div style="position:relative">
    <form class="form-horizontal form-window-content-custom" id="initRequirementEntry" name="initRequirementEntry" method="post" action="#">
        <div class="row-fluid">
            <div class="span6">
                <div class='row-fluid' style="display: none">
                    <div class='span5'>
                        <label class="required">${uiLabelMap.BSRequiredByDate}</label>
                    </div>
                    <div class="span7">
                        <div id="requiredByDate"></div>
                    </div>
                </div>
                <div class='row-fluid'>
                    <div class='span5'>
                        <label class="required">${uiLabelMap.BSRequirementStartDate}</label>
                    </div>
                    <div class="span7">
                        <div id="requirementStartDate"></div>
                    </div>
                </div>

                <div class='row-fluid'>
                    <div class='span5'>
                        <label class="required">${uiLabelMap.BDContractorId}</label>
                    </div>
                    <div class="span7">
                        <div id="contractorId">
                            <div id="contractorGrid"></div>
                        </div>
                    </div>
                </div>

                <div class='row-fluid'>
                    <div class='span5'>
                        <label>${uiLabelMap.BDVehicleName}</label>
                    </div>

                    <div class="span7">
                        <div id="vehicleId">
                            <div id="vehicleGrid"></div>
                        </div>
                    </div>
                    <#--<div class="span7">-->
                        <#--<input id="vehicleName" name="vehicleName" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"/>-->
                    <#--</div>-->
                </div>

                <div class='row-fluid'>
                    <div class='span5'>
                        <label>${uiLabelMap.BDProductStoreGroupName}</label>
                    </div>
                    <div class="span7">
                        <div id="distributorGroupId">
                            <#--<div id="distributorGroupGrid"></div>-->
                        </div>
                    </div>
                </div>

                <div class='row-fluid'>
                    <div class='span5'>
                        <label>${uiLabelMap.BDTotalWeight}</label>
                    </div>
                    <div class="span7">
                        <div id="totalWeightId"> </div>
                    </div>
                </div>
            </div>
            <div class="span6">
                <div class='row-fluid'>
                    <div class='span3'>
                        <label>${uiLabelMap.BSDescription}</label>
                    </div>
                    <div class="span9">
                        <textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
                    </div>
                </div>
            </div>
        </div><!--.row-fluid-->
    </form>
</div>
<#assign gridProductItemsId = "jqxgridDelivery">


<script type="text/javascript">
    <#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "DELIVERY_STATUS"}, null, false)/>
    var orderStatusData = [
    <#if orderStatuses?exists>
        <#list orderStatuses as statusItem>
            {
                statusId: '${statusItem.statusId}',
                description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
            },
        </#list>
    </#if>];

    var cellClass = function (row, columnfield, value) {
        var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
        if (typeof(data) != 'undefined') {
            if ("REQ_CANCELLED" == data.statusId) {
                return "background-cancel";
            } else if ("REQ_CREATED" == data.statusId) {
                return "background-important-nd";
            } else if ("REQ_APPROVED" == data.statusId) {
                return "background-prepare";
            }
        }
    }
</script>

<div style="position:relative" class="form-window-content-custom">
<div id="jqxgridDelivery"></div>
<#assign dataField = "[
				{ name: 'deliveryId', type: 'string'},
				{ name: 'partyIdFrom', type: 'string'},
				{ name: 'partyIdTo', type: 'string'},
				{ name: 'deliveryDate', type: 'date', other: 'Timestamp'},
				{ name: 'orderId', type: 'string'},
				{ name: 'createDate', type: 'date', other: 'Timestamp'},
				{ name: 'destContactMechId', type: 'string'},
				{ name: 'originContactMechId', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'totalWeight', type: 'string'}

			]"/>
<#--<#assign columnlist = "-->
				<#--{ text: '${StringUtil.wrapString(uiLabelMap.BDDeliveryId)}', dataField: 'deliveryId', pinned: true, width: '13%'},-->
				<#--{ text: '${StringUtil.wrapString(uiLabelMap.BDPartyIdFrom)}', dataField: 'partyIdFrom', cellClassName: cellClass,  width: '20%',cellsrenderer: function(row, column, value){-->
					  <#--var partyName = value;-->
					  <#--$.ajax({-->
							<#--url: 'getPartyName',-->
							<#--type: 'POST',-->
							<#--data: {partyId: value},-->
							<#--dataType: 'json',-->
							<#--async: false,-->
							<#--success : function(data) {-->
								<#--if(!data._ERROR_MESSAGE_){-->
									<#--partyName = data.partyName;-->
								<#--}-->
					        <#--}-->
						<#--});-->
					  <#--return '<span title' + value + '>' + partyName + '</span>';}-->
	        	  <#--},-->
				<#--{ text: '${StringUtil.wrapString(uiLabelMap.BDPartyIdTo)}', dataField: 'partyIdTo', cellClassName: cellClass,  width: '20%',cellsrenderer: function(row, column, value){-->
					  <#--var partyName = value;-->
					  <#--$.ajax({-->
							<#--url: 'getPartyName',-->
							<#--type: 'POST',-->
							<#--data: {partyId: value},-->
							<#--dataType: 'json',-->
							<#--async: false,-->
							<#--success : function(data) {-->
								<#--if(!data._ERROR_MESSAGE_){-->
									<#--partyName = data.partyName;-->
								<#--}-->
					        <#--}-->
						<#--});-->
					  <#--return '<span title' + value + '>' + partyName + '</span>';}-->
	        	  <#--},-->
				<#--{ text: '${StringUtil.wrapString(uiLabelMap.BDDesContactMechId)}', dataField: 'destContactMechId', cellClassName: cellClass, width: '25%',-->
				    <#--cellsrenderer: function(row, column, value) {-->
				    <#--var address = value;-->
					  <#--$.ajax({-->
							<#--url: 'getContactMechName',-->
							<#--type: 'POST',-->
							<#--data: {contactMechId: value},-->
							<#--dataType: 'json',-->
							<#--async: false,-->
							<#--success : function(data) {-->
								<#--if(!data._ERROR_MESSAGE_){-->
									<#--address = data.fullName;-->
								<#--}-->
					        <#--}-->
						<#--});-->
					  <#--return '<span title' + value + '>' + address + '</span>';}},-->
			    <#--{ text: '${StringUtil.wrapString(uiLabelMap.BDTotalWeight)} (kg)', dataField: 'totalWeight', cellsformat: 'd'},-->
				<#--{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', cellClassName: cellClass, filtertype: 'checkedlist', width: '13%',-->
					<#--cellsrenderer: function(row, column, value){-->
						<#--if (orderStatusData.length > 0) {-->
							<#--for(var i = 0 ; i < orderStatusData.length; i++){-->
    							<#--if (value == orderStatusData[i].statusId){-->
    								<#--return '<span title =\"' + orderStatusData[i].description +'\">' + orderStatusData[i].description + '</span>';-->
    							<#--}-->
    						<#--}-->
						<#--}-->
						<#--return '<span title=' + value +'>' + value + '</span>';-->
				 	<#--},-->
				 	<#--createfilterwidget: function (column, columnElement, widget) {-->
				 		<#--if (orderStatusData.length > 0) {-->
							<#--var filterDataAdapter = new $.jqx.dataAdapter(orderStatusData, {-->
								<#--autoBind: true-->
							<#--});-->
							<#--var records = filterDataAdapter.records;-->
							<#--widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',-->
								<#--renderer: function(index, label, value){-->
									<#--if (orderStatusData.length > 0) {-->
										<#--for(var i = 0; i < orderStatusData.length; i++){-->
											<#--if(orderStatusData[i].statusId == value){-->
												<#--return '<span>' + orderStatusData[i].description + '</span>';-->
											<#--}-->
										<#--}-->
									<#--}-->
									<#--return value;-->
								<#--}-->
							<#--});-->
							<#--widget.jqxDropDownList('checkAll');-->
						<#--}-->
		   			<#--}-->
				<#--},-->
				<#--{ text: '${uiLabelMap.BDDeliveryDate}', dataField: 'deliveryDate', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '13%',-->
					<#--cellsrenderer: function(row, column, value) {-->
						<#--return '<span>' + jOlbUtil.dateTime.formatFullDate(value) + '</span>';-->
					<#--}-->
				<#--},-->
				<#--{ text: '${uiLabelMap.BDCreateDate}', dataField: 'createDate', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '13%',-->
					<#--cellsrenderer: function(row, colum, value) {-->
						<#--return '<span>' + jOlbUtil.dateTime.formatFullDate(value) + '</span>';-->
					<#--}-->
				<#--},-->
			<#--"/>-->
<#--<@jqGrid id=gridProductItemsId idExisted=idExisted clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField-->
<#--viewSize=viewSize defaultSortColumn="partyIdTo" sortdirection="desc" showtoolbar="true" editmode="click" selectionmode="checkbox" width="100%"-->
<#--bindresize="true" groupable="false" url="jqxGeneralServicer?sname=JQGetListDelivery" isShowTitleProperty="true" customTitleProperties="BDListDelivery"-->
<#--/>-->
</div>

<#include "script/newTripInfoScript.ftl"/>
