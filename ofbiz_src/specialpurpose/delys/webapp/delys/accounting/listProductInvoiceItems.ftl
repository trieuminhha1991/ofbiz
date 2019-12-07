<script>
	var dataItt = [
		<#if invoiceItemTypes?exists><#list invoiceItemTypes as item>
			{
				invoiceItemTypeId : '${item.invoiceItemTypeId}',
				description : '${StringUtil.wrapString(item.description)}'
			},
		</#list>
		</#if>
	];
	<#assign glAccountOACs = delegator.findList("GlAccountOrganizationAndClass", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	var glAccountOACsData =  [<#list glAccountOACs as item>{<#assign description = StringUtil.wrapString(item.accountCode?if_exists + " - " + item.accountName?if_exists + "[" + item.glAccountId?if_exists +"]")>'overrideGlAccountId' : "${item.glAccountId?if_exists}",'description' : "${description?if_exists}"},</#list>];
	
</script>
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'invoiceItemSeqId', type: 'string' },
					{ name: 'invoiceItemTypeId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'overrideGlAccountId', type: 'string' },
					{ name: 'amount', type: 'number' },
					{ name: 'total', type: 'number' }]
					"/>
<#assign columnlist="
					 { text: '${uiLabelMap.invoiceItemSeqId}', datafield: 'invoiceItemSeqId', width: 150,editable:false },
					 { text: '${uiLabelMap.quantity}', datafield: 'quantity', width: 150, columntype:'numberinput',
				        createeditor: function (row, column, editor) {
					        editor.jqxNumberInput({ width:  150, decimalDigits: 0, max : 999999999999999999, digits: 18,spinButtons: false, min: 0});
					    }
					 },
					 { text: '${uiLabelMap.invoiceItemTypeId}', datafield: 'invoiceItemTypeId', columntype: 'template', width: 180,
	                      cellsrenderer: function(row, colum, value){
						 		for(i = 0; i < dataItt.length; i++){
						 			if(value == dataItt[i].invoiceItemTypeId){
						 				return \"<span>\" + dataItt[i].description + \"</span>\";
						 			}
						 		}
						 		return \"<span>\" + value + \"</span>\";
						 	},
						 	createeditor: function (row, column, editor) {
						        var sourceGla =
						        {
						            localdata: dataItt,
						            datatype: \"array\"
						        };
						        var dataAdapterGla = new $.jqx.dataAdapter(sourceGla);
						        editor.jqxDropDownList({source: dataAdapterGla, width: 180, dropDownWidth: 300, filterable: true, valueMember: \"invoiceItemTypeId\",
						        displayMember: 'description',renderer : function(index,label,value){
					        		return dataItt[index].description;
						        },
						        selectionRenderer : function(){
						        	var data = editor.jqxDropDownList('getSelectedItem');
						        	if(data != null){
						        		return data.label;	
						        	}else return '<span>' + '${uiLabelMap.PleaseChooseAcc?if_exists?default('')}'+ '</span>';
						      	  }
						        });
						    },cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                      		  if (newvalue == '') return oldvalue;
	                   		 }
					    },
					 { text: '${uiLabelMap.accProductName}', datafield: 'productId', width: '30%',editable  :false},
					 { text: '${uiLabelMap.description}', datafield: 'description', width: 250 },
					 { text: '${uiLabelMap.accOverrideGlAccountId}', columntype: 'dropdownlist', datafield: 'overrideGlAccountId', width: 250,
					 	createeditor: function (row, column, editor) {
					        var sourceGla =
					        {
					            localdata: glAccountOACsData,
					            datatype: \"array\"
					        };
					        var dataAdapterGla = new $.jqx.dataAdapter(sourceGla);
					        editor.jqxDropDownList({source: dataAdapterGla, valueMember: \"overrideGlAccountId\",displayMember: 'description', filterable: true,renderer : function(index,label,value){
					        		return glAccountOACsData[index].description;
						        },
						        selectionRenderer : function(){
						        	var data = editor.jqxDropDownList('getSelectedItem');
						        	if(data != null){
						        		return data.label;	
						        	}else return '<span>' + '${uiLabelMap.PleaseChooseAcc?if_exists?default('')}'+ '</span>';
						      	  } });
					    },cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                      		  if (newvalue == '') return oldvalue;
	                   		 }
					 },
					 { text: '${uiLabelMap.unitPrice}', datafield: 'amount', width: 250, cellsformat: 'c2', columntype:'numberinput',
				        createeditor: function (row, column, editor) {
				        	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
					        editor.jqxNumberInput({ width: 250,  max : 999999999999999999, digits: 18, decimalDigits:2, spinButtons: false});
					        editor.jqxNumberInput('val', data.amount ? data.amount  : '');
					    }
					 },
					 { text: '${uiLabelMap.ApTotal}', dataField: 'total', width: 200, cellsformat: 'c2', editable: false,
					 	cellsrenderer: function(row, colum, value){
				        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				        	if(data.quantity != null && data.amount!=null){
				        		var total = parseFloat(data.quantity) * parseFloat(data.amount);
				        		return \"<span>\" + formatcurrency(total, 'đ') + \"</span>\";	
				    		}else{
				    			return null;
				    		}
				        }
				     } 
					 "/>   
<div id="invoice-listInvoicesItem" class="tab-pane">
	 <@jqGrid filtersimplemode="false" addType="popup" isShowTitleProperty="false" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
			url="jqxGeneralServicer?invoiceId=${parameters.invoiceId}&sname=JQGetListInvoiceItemsInPayment"
			updateUrl="jqxGeneralServicer?invoiceId=${parameters.invoiceId}&jqaction=U&sname=updateInvoiceItemJQ"
			createUrl="jqxGeneralServicer?invoiceId=${parameters.invoiceId}&jqaction=C&sname=createInvoiceItem"
			removeUrl="jqxGeneralServicer?invoiceId=${parameters.invoiceId}&jqaction=D&sname=removeInvoiceItem" 
			editColumns="invoiceId[${parameters.invoiceId}];invoiceItemSeqId;invoiceItemTypeId;productId;description;overrideGlAccountId;quantity(java.math.BigDecimal);amount(java.math.BigDecimal)"	 
			addColumns="invoiceId[${parameters.invoiceId}];invoiceItemTypeId;productId;description;overrideGlAccountId;quantity(java.math.BigDecimal);amount(java.math.BigDecimal)"
			deleteColumn="invoiceId[${parameters.invoiceId}];invoiceItemSeqId" addrefresh="true"
	 />
 </div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#include "popup/popupAddInvoiceItem.ftl"/>