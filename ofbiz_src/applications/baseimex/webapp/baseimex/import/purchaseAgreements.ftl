<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false)/>
	
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<#assign dataField="[{ name: 'agreementId', type: 'string'},
					{ name: 'agreementCode', type: 'string'},
						   { name: 'attrValue', type: 'string'},
						   { name: 'agreementDate', type: 'date', other: 'Timestamp'},
						   { name: 'fromDate', type: 'date', other: 'Timestamp'},
						   { name: 'thruDate', type: 'date', other: 'Timestamp'},
						   { name: 'partyIdFrom', type: 'string'},
						   { name: 'partyIdTo', type: 'string'},
						   { name: 'partyToName', type: 'string'},
						   { name: 'description', type: 'string'},
						   { name: 'statusId', type: 'string'}]"/>

<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},">
<#if hasOlbPermission("MODULE", "ACC_AGREEMENT", "VIEW")>
	<#assign columnlist= columnlist + "{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementCode', width: 120,
						cellsrenderer: function(row, colum, value){
							var rowData =  $('#jqxGridListAgreements').jqxGrid('getrowdata', row);
					        var link = 'accDetailPurchaseAgreement?agreementId=' + rowData.agreementId;
					        return '<div style=margin:4px;><a href=\"' + link + '\">' + value + '</a></div>';
						}
				},">
<#else>
	<#assign columnlist= columnlist + "{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementCode', width: 120,
						cellsrenderer: function(row, colum, value){
							var rowData =  $('#jqxGridListAgreements').jqxGrid('getrowdata', row);
					        var link = 'detailPurchaseAgreement?agreementId=' + rowData.agreementId;
					        return '<div style=margin:4px;><a href=\"' + link + '\">' + value + '</a></div>';
						}
				},">
</#if>
<#assign columnlist= columnlist + "{ text: '${uiLabelMap.AgreementName}' ,filterable: true, sortable: true, datafield: 'attrValue', width: 200},
					{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, columntype: 'dropdownlist', filtertype: 'checkedlist',
			        	cellsrenderer: function(row, colum, value){
			        		value = value?mapStatus[value]:value;
			        		return '<div style=margin:4px;>' + value + '</div>';
	    		        },createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatus, displayMember: 'statusId', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapStatus[value];
				                }
	    		        	});
	                    }
    		        },
					{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.AvailableFromDate}', datafield: 'fromDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.AvailableThruDate}', datafield: 'thruDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
					{ text: '${uiLabelMap.Supplier}', datafield: 'partyToName', width: 200,
    		        },
					{ text: '${uiLabelMap.description}', datafield: 'description', minWidth: 220 },
					"/>

<@jqGrid filtersimplemode="true" id="jqxGridListAgreements" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
	url="jqxGeneralServicer?sname=JQGetListPurchaseAgreements"/>
		        
<script>
		var listStatus = [<#if listStatus?exists><#list listStatus as item>{
			description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}", statusId: "${item.statusId?if_exists}"
		},</#list></#if>];
		
		var mapStatus = {<#if listStatus?exists><#list listStatus as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get('description', locale)?if_exists)}",
		</#list></#if>};
</script>