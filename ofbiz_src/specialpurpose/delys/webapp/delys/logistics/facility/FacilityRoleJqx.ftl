<@jqGridMinimumLib/>
<#assign addPerm = "false">
<#if security.hasPermission("LOGISTICS_CREATE", session) || security.hasPermission("FACILITY_CREATE", session) || security.hasPermission("FACILITY_ADMIN", session) || security.hasPermission("LOGISTICS_ADMIN", session)>
<#assign addPerm = "true">
</#if>
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'firstName', type: 'string'},
					 { name: 'middleName', type: 'string'},
					 { name: 'roleTypeId', type: 'string'},
					 { name: 'lastName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'}
				   ]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.faPartyId)}', datafield: 'partyId', minwidth: 100, editable: false},
					 { text: '${StringUtil.wrapString(uiLabelMap.faFullName)}', minwidth: 100, editable: false, cellsrenderer:
					       function(row, colum, value){
					        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        var firstName = data.firstName;
					        var middleName = data.middleName;
					        var lastName = data.lastName;
					        if(firstName == null){
					        	firstName = \"\";
					        }
					        if(middleName == null){
					        	middleName = \"\";
					        }
					        if(lastName == null){
					        	lastName = \"\";
					        }
			        		return '<span>' + firstName + '&nbsp;' + middleName + '&nbsp;' + lastName + '</span>';
			         }},
			         { text: '${StringUtil.wrapString(uiLabelMap.faPerm)}', datafield: 'description',minwidth: 200, editable: false},
			         { text: '${StringUtil.wrapString(uiLabelMap.fromDate)}', datafield: 'fromDate', filtertype: 'range',cellsformat: 'dd/MM/yyyy', minwidth: 100, editable: false},
			         { text: '${StringUtil.wrapString(uiLabelMap.thruDate)}', columntype: 'datetimeinput', datafield: 'thruDate', filtertype: 'range',cellsformat: 'dd/MM/yyyy', minwidth: 100, editable: true,
			        	 validation: function (cell, value) {
			        		 var data = $('#jqxgrid').jqxGrid('getrowdata', cell.row);
			        		 if(value == null || value == ''){
			        			 return true;
			        		 }
			        		 if(data.fromDate.getTime() > value.getTime()){
			        			 return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}\" };
			        		 }
			        			 return true;
			             }
		        	 }
					"/>				   
<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow=addPerm deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
	url="jqxGeneralServicer?sname=facilityPartyDetailList&facilityId=${parameters.facilityId}" addColumns="fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);facilityId;partyId;roleTypeId"
	editColumns="fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);facilityId;partyId;roleTypeId"
	updateUrl="jqxGeneralServicer?sname=updateFacilityPartyDetail&jqaction=U&facilityId=${parameters.facilityId}&passParameters=true"
	createUrl="jqxGeneralServicer?sname=createFacilityPartyDetail&jqaction=C" jqGridMinimumLibEnable="false" addrefresh="true"
/>	
<style type="text/css">
	.span6{
	    overflow:hidden;display:inline;
		margin-top:8px;
	}
	.span6 label, .span6 input, .span6 .jqxcpn {
	display:inline-block;
	}
	.span6 input div{
	    width:40%;
		margin: 0px !important;
	}
	.span6 .lblfjqx{
		margin-top:-15px;
	}
	.span6 label{
		margin-right:5px;
		text-align:right;
		width:118px;
	}
	.bordertop{
		border-top:solid 1px #CCC;
		margin-top:8px;
	}
</style>
<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.faNewPerm}</div>
	<div style="overflow: hidden;">
		<div style="width:100%;margin-right:5px;" class="form-group">
			<div class="row-fluid">
				<div class="span6"><label class="lblfjqx" for="partyId">${uiLabelMap.Owner}</label><div class="jqxcpn" id="partyId"/></div></div>
				<div class="span6"><label class="lblfjqx" for="roleTypeIdDiv">${uiLabelMap.roleTypeId}</label><div class="jqxcpn" id="roleTypeIdDiv"/></div></div>
			</div>
			<div class="row-fluid">
			<div class="span6"><label class="lblfjqx" for="fromDate">${uiLabelMap.fromDate}</label><div class="jqxcpn" id="fromDate"></div></div>
	 			<div class="span6"><label class="lblfjqx" for="thruDate">${uiLabelMap.thruDate}</label><div class="jqxcpn" id="thruDate"></div></div>
		 	</div>
		 	<div class="row-fluid">
		 		<div class="span4"></div>
		 		<div class="span4">
	    	 		<div style="margin-left:50px;margin-top:10px;">
		    	 		<input type="button" id="alterSave" value="${uiLabelMap.CommonSave}"/>
		    	 		<input style="margin-left:10px;" id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />
	    	 		</div>
		 		</div>
	        </div>
	    </div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator2.js"></script>
<script type="text/javascript">
	function expireRole(partyId, roleTypeId, fromDate){
		
	}
	$(document).ready(function () {
		var source =
            {
                datatype: "json",
                datafields: [
                    { name: 'partyId' },
                    { name: 'groupName' }
                ],
                type: "POST",
                root: "listParties",
                contentType: 'application/x-www-form-urlencoded',
                url: "facilityManagerableList"
            };
            var dataAdapter = new $.jqx.dataAdapter(source,
                {
                    formatData: function (data) {
                        if ($("#partyId").jqxComboBox('searchString') != undefined) {
                            data.searchKey = $("#partyId").jqxComboBox('searchString');
                            return data;
                        }
                    }
                }
            );
            $("#partyId").jqxComboBox(
            {
                width: 208,
                placeHolder: "${StringUtil.wrapString(uiLabelMap.wmparty)}",
                dropDownWidth: 500,
                height: 25,
                source: dataAdapter,
                remoteAutoComplete: true,
                autoDropDownHeight: true,               
                selectedIndex: 0,
                displayMember: "groupName",
                valueMember: "partyId",
                renderer: function (index, label, value) {
                    var item = dataAdapter.records[index];
                    if (item != null) {
                        var label = item.groupName + "(" + item.partyId + ")";
                        return label;
                    }
                    return "";
                },
                renderSelectedItem: function(index, item)
                {
                    var item = dataAdapter.records[index];
                    if (item != null) {
                        var label = item.groupName;
                        return label;
                    }
                    return "";   
                },
                search: function (searchString) {
                    dataAdapter.dataBind();
                }
            });
            $('#alterpopupWindow').jqxValidator({
            	rules: 
        		[	{
	            		input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
		            		if(input.jqxDateTimeInput('getDate') == null || $('#fromDate').jqxDateTimeInput('getDate') == null){
		            			return true;
		            		}
		            		if(input.jqxDateTimeInput('getDate') < $('#fromDate').jqxDateTimeInput('getDate')){
		            			return false;
		            		}
		            		return true;
	            		}
					},
					{	input: '#partyId', message: '${StringUtil.wrapString(uiLabelMap.faRoleNotEmpty)}', action: 'valueChanged', rule: function (input, commit) {
							if($('#partyId').val() == null || $('#partyId').val()==''){
							    
							    return false;
							}
							return true;
						}
					}
            	]
            });
            /*$('#alterpopupWindow').on('validationError', function (event) {
                var wtmp = window;
                var tmpwidth = $('#alterpopupWindow').jqxWindow('width');
                $("#alterpopupWindow").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
                event.preventDefault();
            });*/
        });
</script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var source = [<#list listRole as item>{key: '${item.roleTypeId}', value: '${item.description}'}<#if item_index!=(listRole?size)>,</#if></#list>];
	$("#fromDate").jqxDateTimeInput({width: '208px', height: '25px'});
	$("#thruDate").jqxDateTimeInput({width: '208px', height: '25px'});
	$("#thruDate").jqxDateTimeInput("val",null);
	
	$("#alterpopupWindow").jqxWindow({
	    width: 1000, height: 190, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme         
	});
	$("#roleTypeIdDiv").jqxDropDownList({ source: source, displayMember: 'value', valueMember: 'key', theme: theme, selectedIndex: 1, width: '208', height: '25'});
	$("#alterCancel").jqxButton();
	$("#alterSave").jqxButton();
	var html = jQuery('html');
	var scrollPosition = [
                          self.pageXOffset || document.documentElement.scrollLeft || document.body.scrollLeft,
                          self.pageYOffset || document.documentElement.scrollTop  || document.body.scrollTop
                        ];
	//html.data('scroll-position', scrollPosition);
    //html.data('previous-overflow', html.css('overflow'));
	/*$('#alterpopupWindow').on('open', function (event) {
		$('#alterpopupWindow').jqxValidator('hide');
		html.css('overflow', 'hidden'); // re-enable scroll
	});*/ 
	$('#alterpopupWindow').on('close', function (event) {
	    $('#alterpopupWindow').jqxValidator('hide');
	    //html.css('overflow', 'scroll'); // re-enable scroll
	}); 
	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
	    row = { 
	    		fromDate:$('#fromDate').jqxDateTimeInput('getDate'),
	    		partyId:$('#partyId').val(),
	    		facilityId:'${parameters.facilityId}',
	    		roleTypeId:$('#roleTypeIdDiv').val(),
	    		thruDate: $('#thruDate').jqxDateTimeInput('getDate')      
	    	  };
	    if($('#alterpopupWindow').jqxValidator('validate')){
	    	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		    // select the first row and clear the selection.
		    $("#jqxgrid").jqxGrid('clearSelection');                        
		    $("#jqxgrid").jqxGrid('selectRow', 0);  
	    	$("#alterpopupWindow").jqxWindow('close');
	    }
	});
</script>