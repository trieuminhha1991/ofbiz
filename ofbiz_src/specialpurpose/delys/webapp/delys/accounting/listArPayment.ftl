<script type="text/javascript">
	var dataStatusItem = new Array();
	<#list 0..(listStatusItem.size() - 1) as i>
		var row = {};
	    row["statusId"] = "${StringUtil.wrapString(listStatusItem.get(i).statusId?if_exists)}";
	    row["description"] = "${StringUtil.wrapString(listStatusItem.get(i).description?if_exists)}";
	    dataStatusItem[${i}] = row;
	</#list>
	var dataPaymentType = new Array();
	<#list 0..(listPaymentType.size() - 1) as i>
		<#assign description = StringUtil.wrapString(listPaymentType.get(i).get("description", locale)) />
		row = {};
	    row["paymentTypeId"] = "${StringUtil.wrapString(listPaymentType.get(i).paymentTypeId?if_exists)}";
	    row["description"] = "${description}";
	    dataPaymentType[${i}] = row;
	</#list>
</script>
<@jqGridMinimumLib/>
<#assign dataField="[{ name: 'paymentId', type: 'string' },
					 { name: 'paymentTypeId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'comments', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyIdTo', type: 'string'},
					 { name: 'fullNameFrom', type: 'string'},
					 { name: 'fullNameTo', type: 'string'},
					 { name: 'effectiveDate', type: 'date', other:'Timestamp'},
					 { name: 'amount', type: 'number'},
					 { name: 'amountToApply', type: 'number'},
					 { name: 'currencyUomId', type: 'string'}]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_paymentId}', width:100, filtertype:'input', datafield: 'paymentId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	return \"<span><a href='/delys/control/accArpaymentOverview?paymentId=\" + value + \"'>\" + value + \"</a></span>\";
                        }},
					 { text: '${uiLabelMap.OrderPaymentType}', filtertype: 'checkedlist', width:200, datafield: 'paymentTypeId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	for(i=0; i<dataPaymentType.length;i++){
                        		if(dataPaymentType[i].paymentTypeId == value){
                        			return \"<span>\" + dataPaymentType[i].description +\"</span>\";
                    			}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				var sourcePT =
						    {
						        localdata: dataPaymentType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapterPT = new $.jqx.dataAdapter(sourcePT,
			                {
			                    autoBind: true
			                });
			                var uniqueRecordsPT = filterBoxAdapterPT.records;
			   				uniqueRecordsPT.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ dropDownWidth: 300, source: uniqueRecordsPT, displayMember: 'paymentTypeId', valueMember : 'paymentTypeId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataPaymentType.length; i++){
									if(dataPaymentType[i].paymentTypeId == value){
										return dataPaymentType[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},
					 { text: '${uiLabelMap.FormFieldTitle_statusId}', filtertype: 'checkedlist', width:180, datafield: 'statusId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	for(i=0; i<dataStatusItem.length;i++){
                        		if(dataStatusItem[i].statusId==value){
                        			return \"<span>\" + dataStatusItem[i].description +\"</span>\";
                    			}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				var sourceSI =
						    {
						        localdata: dataStatusItem,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapterSI = new $.jqx.dataAdapter(sourceSI,
			                {
			                    autoBind: true
			                });
			                var uniqueRecordsSI = filterBoxAdapterSI.records;
			   				uniqueRecordsSI.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecordsSI, displayMember: 'statusId', valueMember : 'statusId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataStatusItem.length; i++){
									if(dataStatusItem[i].statusId == value){
										return dataStatusItem[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},
					 { text: '${uiLabelMap.FormFieldTitle_comments}', width:150, datafield: 'comments',cellsrenderer : function(row){
					 	
					 }},
					 { text: '${uiLabelMap.accAccountingToParty}', filtertype: 'olbiusdropgrid', datafield: 'partyIdFrom',  width:300, displayfield: 'fullNameFrom', 
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(290);
			   			}},
					 { text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:300, displayfield: 'fullNameTo', datafield: 'partyIdTo',
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(140);
			   			}},
					 { text: '${uiLabelMap.FormFieldTitle_effectiveDate}', filtertype: 'range', width:100, datafield: 'effectiveDate', cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.FormFieldTitle_amount}', filtertype: 'number', width:150, datafield: 'amount', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<div><div class='pull-right'>\" + formatcurrency(data.amount,data.currencyUomId) + \"</div></div>\";
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_amountToApply}', filtertype: 'number', width:150, datafield: 'amountToApply', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<div><div class='pull-right'>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</div></div>\";
					 	}}
					"/>
<@jqGrid url="jqxGeneralServicer?sname=JQListARPayment" dataField=dataField columnlist=columnlist jqGridMinimumLibEnable="false" filterable="true" filtersimplemode="true"
		 otherParams="amountToApply:S-getListApPayment(inputValue{paymentId})<outputValue>" usecurrencyfunction="true" clearfilteringbutton="true"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createPaymentAndFinAccountTrans" addrefresh="true"
		 addColumns="statusId[PMNT_NOT_PAID];currencyUomId[${defaultOrganizationPartyCurrencyUomId}];partyIdFrom;paymentTypeId;partyIdTo;paymentMethodId;paymentRefNum;overrideGlAccountId;amount(java.math.BigDecimal);comments;isDepositWithDrawPayment[Y];finAccountTransTypeId[WITHDRAWAL]" addType="popup" alternativeAddPopup="alterpopupWindow" showtoolbar="true" addrow="true" autorowheight="true" viewSize="5"/>					
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	var dataPAGA = new Array();
    var row;
    <#list listPartyAcctgPrefAndGroup as lpaga>
        row = {};
        row["partyId"] = "${lpaga.partyId}";
        row["groupName"] = "${lpaga.groupName}";
        dataPAGA[${lpaga_index}] = row;
    </#list>
    

	var dataPTE = new Array();
    <#list listPaymentTypeImport as lpte>
    <#assign des = lpte.get("description",locale)/>
        row = {};
        row["paymentTypeId"] = "${lpte.paymentTypeId}";
        row["description"] = "${StringUtil.wrapString(des)}";
        dataPTE[${lpte_index}] = row;
    </#list>
    
    var dataPM = new Array();
    <#list listPaymentMethod as lpm>
        row = {};
        row["paymentMethodId"] = "${lpm.paymentMethodId}";
        row["description"] = "${lpm.description}";
        dataPM[${lpm_index}] = row;
    </#list>
    
    var dataFA = new Array();
    row = {};
    row["finAccountId"] = "";
    row["description"] = "";
    dataFA[0] = row;
    <#list listFinAccount as lfa>
        row = {};
        row["finAccountId"] = "${lfa.finAccountId}";
        row["description"] = "${lfa.finAccountName}[${lfa.finAccountId}]";
        dataFA[${lfa_index}+1] = row;
    </#list>
    
</script>		
<script src="/delys/images/js/generalUtils.js"></script>
<#include 'popup/popupPayment.ftl'/>	
<#include "popup/popupGridPartyFilter.ftl"/>