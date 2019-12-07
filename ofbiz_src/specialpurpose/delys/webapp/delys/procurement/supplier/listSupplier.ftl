<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<#assign id="jqxgridsupplier"/>
<#include "initContact.ftl"/>
<#include "initRowDetailSupplier.ftl"/>
<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'groupName', type: 'string' }, 
					 { name: 'statusId', type: 'string'},
					 { name: 'phone', type: 'string'},
					 { name: 'fax', type: 'string'},
					 { name: 'email', type: 'string'},
					 { name: 'address', type: 'string'},
					 { name: 'description', type: 'string'},
					 ]
					 "/>
<#assign columnlist = "{ text: '${uiLabelMap.partyId}', width:200, datafield: 'partyId'},
					  { text: '${uiLabelMap.groupName}', width:200, datafield: 'groupName'},
					  { text: '${uiLabelMap.TelephoneNumber}', width:200, datafield: 'phone', 
						  cellsrenderer: function(row, column, value){
							  var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
							  str += value.contactNumber ? value.contactNumber : '';
							  str += \"</div>\"; 
							  return str;
						  },
						  createeditor: function(row, value, editor){
							  editor.jqxInput();
							  editor.jqxInput('val', value.contactNumber ? value.contactNumber : '');
						  }
					  },
					  { text: '${uiLabelMap.fromContactMechFax}', width:200, datafield: 'fax', 
						  cellsrenderer: function(row, column, value){
							  var str = \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
							  str += value.contactNumber ? value.contactNumber : '';
							  str += \"</div>\"; 
							  return str;
						  },
						  createeditor: function(row, value, editor){
							  editor.jqxInput();
							  editor.jqxInput('val', value.contactNumber ? value.contactNumber : '');
						  }
					  },
					  { text: '${uiLabelMap.EmailAddress}', width:200, datafield: 'email'},
					  { text: '${uiLabelMap.DAAddressDistributor}', width:200, datafield: 'address', editable: false,
						  cellsrenderer: function(row, column, value){
							  var data = $('#${id}').jqxGrid('getrowdata', row);
							  var str = \"<a href='javascript:openUpdateAddressPopup(\"+'\"'+data.partyId+'\"'+\")'><div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\";
							  var addr = value.address1 ? value.address1 : '';
							  var city = value.city ? value.city : '';
							  str += addr + ' ' + city;
							  str += \"</div></a>\"; 
							  return str;
						  }
					  },
					  { text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 150, filtertype: 'checkedlist',columntype: 'dropdownlist',
						  cellsrenderer: function(row, column, value){
							for(var x in statusData){
								if(statusData[x].statusId == value){
									return \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\"+statusData[x].description+\"</div>\";
								}
							}
							return \"<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>\"+value+\"</div>\";
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterBoxAdapter = new $.jqx.dataAdapter(statusData, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'statusId'});
						},
						createeditor: function(row, value, editor){
							editor.jqxDropDownList({ source: statusData, displayMember: 'description', valueMember: 'statusId', dropDownHeight:70});
						}
					  },
					  { text: '${uiLabelMap.description}', datafield: 'description', width: 150},
					 	"/>		

<@jqGrid url="jqxGeneralServicer?sname=JQGetListSupplier" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addrow="true" addType="popup" addrow="true" addType="popup"
	     editable="true" editrefresh="true"
		 initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="400" id=id
		 createUrl="jqxGeneralServicer?sname=createSupplier&jqaction=C" addColumns="groupName;roleTypeId;email;phone;fax;address;country;province;district;ward;description"
		 updateUrl="jqxGeneralServicer?sname=updateSupplier&jqaction=U" editColumns="partyId;statusId;phone;fax;email;description;groupName" autorowheight="true" altrows="true"
	 />
<#include "popupSupplier.ftl"/>
<#include "popupUpdateAddress.ftl"/>

<script>
	$("#emailInput").jqxInput({width: 195, height: 24});
	$("#groupNameInput").jqxInput({width: 195, height: 24});
	$("#phoneNumber").jqxInput({width: 195, height: 24});
	$("#faxNumber").jqxInput({width: 195, height: 24});
	$(".address").jqxInput({width: 195, height: 24});
	$("#roleType").jqxDropDownList({theme: 'olbius', source: roleTypeData,width: 200, height: 28, valueMember: "roleTypeId", displayMember: "description", filterable: true});
	$(".country").jqxDropDownList({theme: 'olbius', source: countryData,width: 200, height: 28, valueMember: "geoId", displayMember: "description", filterable: true});
	$(".province").jqxDropDownList({theme: 'olbius', source: provinceData, width: 200, height: 28, valueMember: "geoId", displayMember: "description", filterable: true});
	$(".district").jqxDropDownList({theme: 'olbius', source: districtData, width: 200, height: 28, valueMember: "geoId", displayMember: "description", filterable: true});
	$(".ward").jqxDropDownList({theme: 'olbius', source: wardData, width: 200, height: 28, valueMember: "geoId", displayMember: "description", filterable: true});
	$("#alterpopupWindow").jqxWindow({
		theme: 'olbius', width: 800, height: 500, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7          
    });
	$('#descriptionInput').jqxEditor({
        height: "200px",
        width: '605px',
        theme: 'olbiuseditor'
    });
	$("#alterpopupWindow").jqxValidator({
	   	rules: [{
			input: '#groupNameInput',
			message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
			action: 'blur',
			rule: 'required'
		},{ 
			input: '#roleType', 
			message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', 
			action: 'blur', 
			rule: function (input, commit) {
              var selected = input.jqxDropDownList("getSelectedIndex");
              return selected != -1;
            }
		}]
	});
	$("#alterpopupWindow").on("close", function(){
		$(this).jqxValidator("hide");
	});
	$("#updateAddressPopup").jqxWindow({
		theme: 'olbius', width: 600, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7          
    });
	$("#updateAddressPopup").jqxValidator({
	   	rules: [{ 
			input: '#countryUpdate', 
			message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', 
			action: 'blur', 
			rule: function (input, commit) {
              var selected = input.jqxDropDownList("getSelectedIndex");
              return selected != -1;
            }
		},{ 
			input: '#provinceUpdate', 
			message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', 
			action: 'blur', 
			rule: function (input, commit) {
              var selected = input.jqxDropDownList("getSelectedIndex");
              return selected != -1;
            }
		},{ 
			input: '#addressUpdate', 
			message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', 
			action: 'blur', 
			rule: 'required'
		}]
	});
	$("#updateAddressPopup").on("close", function(){
		$(this).jqxValidator("hide");
	});
    $("#alterSave").click(function () {
    	if(!$("#alterpopupWindow").jqxValidator("validate")){
    		return;
    	}
    	var i = $("#country").jqxDropDownList("getSelectedItem");
    	var country = i && i.value ? i.value : "";
    	i = $("#province").jqxDropDownList("getSelectedItem");
    	var province = i && i.value ? i.value : "";
    	i = $("#district").jqxDropDownList("getSelectedItem");
    	var district = i && i.value ? i.value : "";
    	i = $("#ward").jqxDropDownList("getSelectedItem");
    	var ward = i && i.value ? i.value : "";
    	i = $("#roleType").jqxDropDownList("getSelectedItem");
    	var roleTypeId = i.value ? i.value : "";
        var row = { 
    		email: $("#emailInput").val(),
    		groupName: $("#groupNameInput").val(),
    		phone: $("#phoneNumber").val(),
    		fax : $("#faxNumber").val(),
    		description : $("#descriptionInput").val(),
    		roleTypeId: roleTypeId,
    		country: country,
    		province: province,
    		district: district,
    		ward: ward,
    		address: $("#address").val(),
	   };
       var grid = $("#${id}");
	   grid.jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
	   grid.jqxGrid('clearSelection');                        
	   grid.jqxGrid('selectRow', 0);  
       $("#alterpopupWindow").jqxWindow('close');
    });
    $("#saveAddress").click(function () {
    	if(!$("#updateAddressPopup").jqxValidator("validate")){
    		return;
    	}
    	var i = $("#countryUpdate").jqxDropDownList("getSelectedItem");
    	var country = i && i.value ? i.value : "";
    	i = $("#provinceUpdate").jqxDropDownList("getSelectedItem");
    	var province = i && i.value ? i.value : "";
    	i = $("#districtUpdate").jqxDropDownList("getSelectedItem");
    	var district = i && i.value ? i.value : "";
    	i = $("#wardUpdate").jqxDropDownList("getSelectedItem");
    	var ward = i && i.value ? i.value : "";
        var row = { 
        	partyId: $("#partyIdUpdateAddress").val(),
    		country: country,
    		province: province,
    		district: district,
    		ward: ward,
    		address: $("#addressUpdate").val(),
	   };
       $.ajax({
    	  url : "updateSupplierAddress",
    	  data: row,
    	  type: "POST",
    	  success: function(res){
    		  if(res['_EVENT_MESSAGE_'] && res['_EVENT_MESSAGE_'].indexOf("Success") != -1){
              	  $('#jqxNotification${id}').jqxNotification({ template: 'success'});
              	  $("#notificationContent${id}").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	  $("#jqxNotification${id}").jqxNotification("open");
    			  $("#${id}").jqxGrid("updatebounddata");
    		  }else{
				 $('#container${id}').empty();
				 $('#jqxNotification${id}').jqxNotification({ template: 'error'});
				 $("#notificationContent${id}").text(data.errorMessage);
				 $("#jqxNotification${id}").jqxNotification("open");
    		  }
    		  $("#updateAddressPopup").jqxWindow("close");
    	  }
       });
    });
    function openUpdateAddressPopup(id){
    	$("#partyIdUpdateAddress").val(id);
    	$("#updateAddressPopup").jqxWindow("open");
    }
</script>