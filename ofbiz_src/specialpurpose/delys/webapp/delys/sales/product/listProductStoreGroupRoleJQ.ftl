<#assign listRoleType = Static["com.olbius.util.SalesPartyUtil"].getListRoleTypeProductStoreRole(delegator)/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript">
	<#if listRoleType?exists>
		var roleTypeData = [
			<#list listRoleType as roleTypeItem>
				{
					roleTypeId: "${roleTypeItem.roleTypeId}",
					description: "${StringUtil.wrapString(roleTypeItem.get("description", locale))}"
				},
			</#list>
		];
	<#else>
		var roleTypeData = [];
	</#if>
	var sourceRoleType = {
			localdata: roleTypeData,
	        datatype: "array",
	        datafields: [
	            { name: 'roleTypeId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterRoleType = new $.jqx.dataAdapter(sourceRoleType, {
	        	formatData: function (data) {
	                if ($("#RoleTypeAdd").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#RoleTypeAdd").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	var sourcePartyIdsApply = {
			datatype: "json",
	        datafields: [
	            { name: 'partyId' },
                { name: 'firstName' },
                { name: 'middleName' },
                { name: 'lastName' },
                { name: 'groupName' }
	        ],
	        data: {
	        	roleTypeData : JSON.stringify(roleTypeData)
	        },
	        type: "POST",
	        contentType: 'application/x-www-form-urlencoded',
	        url: "getPartiesJson"
	    };
	    var dataAdapterPartyIdsApply = new $.jqx.dataAdapter(sourcePartyIdsApply, {
	            formatData: function (data) {
	                if ($("#PartyAdd").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#PartyAdd").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    var sourceRoleTypeIdOder = {
				datatype: "json",
		        datafields: [
		            { name: 'description' },
	                { name: 'roleTypeId' },
		        ],
		        data : {
		        	partyLabel : null
		        },
		        type: "POST",
		        root: "listRoleTypeIdOder",
		        contentType: 'application/x-www-form-urlencoded',
		        url: "getListRoleTypeIdOrder"
		        
		    };
		var dataAdapterRoleTypeIdOder = new $.jqx.dataAdapter(sourceRoleTypeIdOder, {
			beforeLoadComplete : function(records){
				var RoleTypeIdOderAndCondition = new Array(); 	
				if(records && records.length > 0){
				for(var i=0;i<records.length;i++){
	        		for(var j=0;j<dataAdapterRoleType.records.length;j++){
	        			if(records[i].roleTypeId == dataAdapterRoleType.records[j].roleTypeId){
	        				var tmpchild = {roleTypeId:null,description :null};
	        				tmpchild.roleTypeId = records[i].roleTypeId;
	        				tmpchild.description =records[i].description;
	        				RoleTypeIdOderAndCondition.push(tmpchild);
	        				break;
	        			}else continue;
	        		}
	        	}
				$("#RoleTypeAdd").jqxComboBox({source : RoleTypeIdOderAndCondition,displayMember: "description",valueMember:"roleTypeId"});
			}
			}
		});
			var sourcePartyIdOder = {
					datatype : "json",
					datafields : [
			             { name: 'partyId' },
			             { name: 'firstName' },
			             { name: 'middleName' },
			             { name: 'lastName' },
			             { name: 'groupName' }
		              ],
		            data : {
		            	roleTypeValue : null,
		            	searchKey : null
		            },
		            type : "POST",
		            root : "listPartyIdOder",
		            contentType: 'application/x-www-form-urlencoded',
		            url: "getListPartyIdOrder"
			};
		var dataAdapterPartyIdOder = new $.jqx.dataAdapter(sourcePartyIdOder, {
			autobind :  true,
			formatData: function (data) {
                if ($("#PartyAdd").jqxComboBox('searchString') != undefined) {
                    data.searchKey = $("#PartyAdd").jqxComboBox('searchString');
                    return data;
                }
            }
		});
</script>
<#assign dataField = "[
		{name : 'partyId', type : 'String'},	
		{name : 'productStoreGroupId', type :'String'},
		{name : 'roleTypeId', type : 'String'},
	]"/>
<#assign columnlist = "
		{text : '${uiLabelMap.DAPartyId}', dataField :'partyId', width : '50%'},
		{text : '${uiLabelMap.DARoleType}', dataField : 'roleTypeId', filtertype: 'checkedlist', createfilterwidget : function(column, columnElement, widget){
			var filterBoxAdapter = new $.jqx.dataAdapter(roleTypeData,
	                {
	                    autoBind: true
	                });
	                var uniqueRecords = filterBoxAdapter.records;
	   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
	   				widget.jqxDropDownList({ source: uniqueRecords, displayMember: 'description', valueMember : 'roleTypeId', renderer: function (index, label, value) 
					{
						for(i=0;i < roleTypeData.length; i++){
							if(roleTypeData[i].roleTypeId == value){
								return roleTypeData[i].description;
							}
						}
					    return value;
					}});
					widget.jqxDropDownList('checkAll');
		}}
	"/>
<@jqGrid filterable ="true" editable = "false" clearfilteringbutton="true" alternativeAddPopup="alterpopupWindowCreate" columnlist=columnlist dataField=dataField addrow="true" addType="popup" addrefresh="true"
		url="jqxGeneralServicer?sname=JQGetListProDuctStoreGroupRole&productStoreGroupId=${parameters.productStoreGroupId?if_exists}"
		createUrl ="jqxGeneralServicer?sname=JQAddProductStoreGroupRole&jqaction=C" addColumns ="partyId;roleTypeId;productStoreGroupId"
		removeUrl="jqxGeneralServicer?sname=JQremoveProductStoreGroupRole&jqaction=D" deleteColumn="partyId;roleTypeId;productStoreGroupId" deleterow="true"
/>
<div id="alterpopupWindowCreate" style="display:none">
<div>${uiLabelMap.DAAddToProductStoreGroupChild}</div>
	<div style="overflow: hidden;">
	<form id="alterpopupWindowCreateform" class="form-horizontal">
		<div class="row-fluid  form-window-content">
			<div class="span12">
				<div class="row-fluid margin-bottom10">
				
					<div class='span5 align-right asterisk'>
			        	${uiLabelMap.DAPartyId}
			        </div>
					<div class="span7">
						<div id="PartyAdd"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
		            	${uiLabelMap.DARoleType}
		            </div>
					<div class="span7 span7edit">
						<div id="RoleTypeAdd"></div>
					</div>
				</div>
			</div>
		</div>
	</form>
	<div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
	</div>
</div>
<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#alterpopupWindowCreate").jqxWindow({width:480, height:250,resizable: false,isModal: true,autoOpen: false,cancelButton: $("#alterCancel"),modalOpacity : 0.8,theme : theme});
	$("#PartyAdd").jqxComboBox({
        width: 248,
        placeHolder: " ${StringUtil.wrapString(uiLabelMap.DAChoosePartyIdApply)}",
        dropDownWidth: 248,
        height: 23,
        source: dataAdapterPartyIdsApply,
        remoteAutoComplete: true,
        autoDropDownHeight: false,
        dropDownHeight : 100,
        displayMember: "partyId",
        valueMember: "partyId",
        renderer: function (index, label, value) {
            var item = dataAdapterPartyIdsApply.records[index];
            if (item != null) {
                var label = item.partyId;
                return label;
            }
            return "";
        },
        renderSelectedItem: function(index, item)
        {
            var item = dataAdapterPartyIdsApply.records[index];
            if (item != null) {
                var label = item.partyId;
                return label;
            }
            return "";
        },
        search: function (searchString) {
            dataAdapterPartyIdsApply.dataBind();
        }
    });
	$("#PartyAdd").on('select', function(event){
		var args = event.args;
		var item = args.item;
        if((item.label) && (!$("#RoleTypeAdd").val())){
        	sourceRoleTypeIdOder.data.partyLabel = item.label;
        	dataAdapterRoleTypeIdOder.dataBind();
        }
	});
	$("#PartyAdd").on('change', function(event){
		if(!$("PartyAdd").val()){
			$("#RoleTypeAdd").jqxComboBox({source :dataAdapterRoleType});
		}
	});
	$("#RoleTypeAdd").jqxComboBox({source: dataAdapterRoleType, multiSelect: false, width: 248, height: 23,
    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
    	displayMember: "description", 
    	dropDownWidth: 248, 
    	autoDropDownHeight: true, 
    	valueMember: "roleTypeId", 
    	renderer: function (index, label, value) {
                var valueStr = label + " [" + value + "]";
                return valueStr;
            },
        renderSelectedItem: function(index, item) {
            var item = dataAdapterRoleType.records[index];
            if (item != null) {
                var label = item.description;
                return label;
            }
            return "";
        },
        search: function (searchString) {
            dataAdapterRoleType.dataBind();
        }
    });
	$("#RoleTypeAdd").on('select', function(event){
		var args = event.args;
		var item = args.item;
		var value = item.value;
		if((value) && (!$("#PartyAdd").val())){
			$("#PartyAdd").on('change', function(event){
				sourcePartyIdOder.data.roleTypeValue = value;
				sourcePartyIdOder.data.searchKey = $("#PartyAdd").jqxComboBox('searchString');
				dataAdapterPartyIdOder.dataBind();
			})
			$("#PartyAdd").jqxComboBox({source : dataAdapterPartyIdOder});
		}
	});
	$("#alterpopupWindowCreateform").jqxValidator({
		rules  : [
          {
        	  input : '#PartyAdd',
        	  message : '${uiLabelMap.DAThisFieldIsRequired}',
        	  action : 'close,change,keyup',
        	  rule : function(input,commit){
        		  if(!$('#PartyAdd').val()){
        			  return false;
        		  }
        		  return true;
        	  }
          },
          {
        	  input : '#RoleTypeAdd',
        	  message : '${uiLabelMap.DAThisFieldIsRequired}',
        	  action : 'close,change,keyup',
        	  rule : function(input,commit){
        		  if(!$('#RoleTypeAdd').val()){
        			  return false;
        		  }
        		  return true;
        	  }
          }
      ]
	});
	$("#alterSave").click(function(){
		$('#alterpopupWindowCreateform').jqxValidator('validate');
	});
	$("#alterpopupWindowCreateform").on('validationSuccess', function(event){
		var rows = $('#jqxgrid').jqxGrid('getrows');
		var row = {};
		row = {
				partyId : $('#PartyAdd').val(),
				roleTypeId : $('#RoleTypeAdd').val(),
				productStoreGroupId : '${parameters.productStoreGroupId?if_exists}'
		};
		for(var i=0;i<rows.length;i++){
			if(row.partyId == rows[i].partyId && row.roleTypeId == rows[i].roleTypeId){
				$('#container').empty();
                $('#jqxNotification').jqxNotification({ template: 'error'});
                $("#notificationContent").text('${StringUtil.wrapString(uiLabelMap.DAduplicate)}');
                $("#jqxNotification").jqxNotification('open');
                return false;
			}
		}
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindowCreate").jqxWindow('close');
	});
</script>
