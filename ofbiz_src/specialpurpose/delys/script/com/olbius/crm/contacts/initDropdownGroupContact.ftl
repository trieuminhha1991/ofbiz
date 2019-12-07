<script>
	var initPartyGridContact = function(){
		var width = 570;
		var dataFieldGroup = [{ name: 'partyId', type: 'string' },
       			 { name: 'firstName', type: 'string'},
    			 { name: 'middleName', type: 'string'},
    			 { name: 'lastName', type: 'string'},
    			 { name: 'groupName', type: 'string'},
    			 { name: 'birthDate', type: 'string'},
    			 { name: 'email', type: 'string'},
    			 { name: 'phoneMobile', type: 'string'},
    			 { name: 'permanentResidence', type: 'string'},
    			 { name: 'assign', type: 'string'}];
		var columnsgroup = [{ text: '${uiLabelMap.CustomerID}', filtertype: 'input', datafield: 'partyId', editable: false, hidden: true },
				   	 { text: '${uiLabelMap.customerName}', filtertype: 'input', datafield: 'firstName', width: '200',
	   	 	cellsrenderer: function (row, column, value, a, b, data) {
				var first = data.firstName ? data.firstName : '';
				var middle = data.middleName ?  data.middleName : '';
				var last = data.lastName ? data.lastName : '';
	    		return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"+ first + ' ' + middle + ' ' + last +"</div>";
          	}
	   	 },
	   	 { text: '${uiLabelMap.storeName}', filtertype: 'input', datafield: 'groupName', width: '200'},
         { text: '${uiLabelMap.birthday}', filtertype: 'input', datafield: 'birthDate', hidden: true, filterable: false},
		 { text: '${uiLabelMap.emailAddr}', filtertype: 'input', datafield: 'email', columntype:'custom', width: '200', filterable: false},
         { text: '${uiLabelMap.phoneNumber}', filtertype: 'input', datafield: 'phoneMobile', columntype:'custom', width: '150', filterable: false, 
         	cellsrenderer : function(row, column, value){
         		var str = "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>";
         		str += value.contactNumber ? value.contactNumber : "";
         		str += "</div>";
         		return str;
         	}
         },
         { text: '${uiLabelMap.address}', filtertype: 'input', datafield: 'permanentResidence', columntype:'custom', filterable: false,
         	cellsrenderer : function(row, column, value){
         		var str = "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>"
         				+ value.address1 ? value.address1 : ""; 
         				+ "</div>";
         		return str;
         	}
         }];
         GridUtils.initDropDownButton({
			url : "getListGroupContacts",
			autorowheight : true,
			filterable : true,
			width : width,
			dropdown: {
				width: width,
				height: 30
			},
			source : {
				cache : true,
				pagesize : 5
			}
		}, dataFieldGroup, columnsgroup, null, $("#emailChosenJqx"), $("#emailList"), ProcessEmailSelected);
	};
</script>
