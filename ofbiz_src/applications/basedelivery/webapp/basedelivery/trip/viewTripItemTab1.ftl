<#assign gridProductItemsId = "jqxgridOrderItem">

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
				{ name: 'totalWeight', type: 'string'},

			]"/>
<#assign columnlist = "
				{ text: '${StringUtil.wrapString(uiLabelMap.BDDeliveryId)}', dataField: 'deliveryId', pinned: true, width: '13%'},
			    { text: '${StringUtil.wrapString(uiLabelMap.BDTotalWeight)} (kg)', dataField: 'totalWeight', cellsformat: 'd', width: '13%'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BDPartyIdFrom)}', dataField: 'partyIdFrom',  width: '20%',cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
	        	  },
				{ text: '${StringUtil.wrapString(uiLabelMap.BDPartyIdTo)}', dataField: 'partyIdTo', width: '20%',cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
	        	  },
				{ text: '${StringUtil.wrapString(uiLabelMap.BDDesContactMechId)}', dataField: 'destContactMechId', width: '20%',
				    cellsrenderer: function(row, column, value) {
				    var address = value;
					  $.ajax({
							url: 'getContactMechName',
							type: 'POST',
							data: {contactMechId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									address = data.fullName;
								}
					        }
						});
					  return '<span title' + value + '>' + address + '</span>';}},
				{ text: '${uiLabelMap.BDDeliveryDate}', dataField: 'deliveryDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '13%',
					cellsrenderer: function(row, column, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.BDCreateDate}', dataField: 'createDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '13%',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
			"/>
<@jqGrid id=gridProductItemsId idExisted=idExisted clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="singleRow" width="100%" bindresize="true" groupable="false"
<#--url="jqxGeneralServicer?sname=JQGetListOrderReqDelivery&requirementId=${requirement.requirementId}"-->
url="jqxGeneralServicer?sname=JQGetListDeliveryByTrip&tripId=${trip.tripId}"
/>