<script>
	//Create theme
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	//Prepare for party data
	<#assign listParties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyData = new Array();
	<#list listParties as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.groupName?if_exists) + " " + StringUtil.wrapString(item.firstName?if_exists) + " " + StringUtil.wrapString(item.middleName?if_exists) + " " + StringUtil.wrapString(item.lastName?if_exists)>
		row['partyId'] = '${item.partyId}';
		row['description'] = "${description}";
		partyData[${item_index}] = row;
	</#list>

	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "RPH_STATUS"), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = "${description}";
		statusData[${item_index}] = row;
	</#list>
	
 	<#assign listEmplPositionTypes = delegator.findList("DepPositionTypeView", null, null, null, null, false) >
    var positionTypeData = new Array();
	<#list listEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['partyId'] = '${item.deptId}';
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = "${description}";
		positionTypeData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'year', type: 'string' },
					 { name: 'scheduleDate', type: 'date', other: 'Timestamp' },
					 { name: 'reason', type: 'string' },
					 { name: 'statusId', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.Department}', datafield: 'partyId', editable: false,
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < partyData.length; i++){
								if(value == partyData[i].partyId){
									return '<span title=' + value + '>' + partyData[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>';
						}
					 },
                     { text: '${uiLabelMap.Year}', datafield: 'year', width: 150, editable: false},
                     { text: '${uiLabelMap.sheduleDate}', datafield: 'scheduleDate', width: 150, cellsformat:'d', filtertype: 'range', editable: false},
                     { text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: true, columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusData.length; i++){
								if(value == statusData[i].statusId){
									return '<span title=' + value + '>' + statusData[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>';
						}
                     },
                     { text: '${uiLabelMap.comment}', datafield: 'comment', width: 150, editable: true}
					 "/>
<@jqGrid id="jqxgrid" addType="popup" editable="false" addrefresh="true" filtersimplemode="true" addrow="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListRecruitmentPlanHeader" dataField=dataField columnlist=columnlist alternativeAddPopup="alterpopupNewRPH"
		 createUrl="jqxGeneralServicer?sname=createRecruitmentPlanHeader&jqaction=C" addColumns="partyId;year;scheduleDate(java.sql.Timestamp);reason"
		 initrowdetails="true" initrowdetailsDetail="initrowdetails" updateUrl="jqxGeneralServicer?sname=updateRecruitmentPlanHeader&jqaction=U" editColumns="partyId;year;statusId;reason"
		/>

<script>

//Create row detail
var initrowdetails = function (index, parentElement, gridElement, datarecord) {
var jqxgridDetail = $($(parentElement).children()[0]);
var partyId = datarecord.partyId;
var year = datarecord.year;
var statusId = datarecord.statusId;
var id = datarecord.uid.toString();
var getUrl = "getRecruitmentPlan";
var cuUrl = "createOrUpdateRecruitmentPlan";
var idGrid = "jqxgridDetail_" + id;
$(jqxgridDetail).attr('id', idGrid);
var detailSource = {
		datafields: [
				{name: 'emplPositionTypeId', type: 'string'},
		            {name: 'firstMonth', type: 'number'},
		            {name: 'secondMonth', type: 'number'},
		            {name: 'thirdMonth', type: 'number'},
		            {name: 'fourthMonth', type: 'number'},
		            {name: 'fifthMonth', type: 'number'},
		            {name: 'sixthMonth', type: 'number'},
		            {name: 'seventhMonth', type: 'number'},
		            {name: 'eighthMonth', type: 'number'},
		            {name: 'ninthMonth', type: 'number'},
		            {name: 'tenthMonth', type: 'number'},
		            {name: 'eleventhMonth', type: 'number'},
		            {name: 'twelfthMonth', type: 'number'}
				],
	cache: false,
	datatype: 'json',
	type: 'POST',
	data: {partyId: partyId, year: year},
	url: getUrl,
        updaterow: function (rowid, rowdata, commit) {
		var data = {};
		rowdata['partyId'] = partyId;
		rowdata['year'] = year;
		$.ajax({
                url: cuUrl,
                type: "POST",
                cache: false,
                datatype: 'json',
                data: rowdata,
                success: function (data, status, xhr) {
                	if(data.responseMessage == "error"){
                		commit(false);
                	}else{
                		commit(true);
                	}
                	jqxgridDetail.jqxGrid('updatebounddata');
                }
            });
        }
};
var dataAdapter = new $.jqx.dataAdapter(detailSource);
if(jqxgridDetail && jqxgridDetail.length){
	jqxgridDetail.jqxGrid(
         {
             width: '95%',
             autoHeight: true,
             source: dataAdapter,
             showtoolbar: false,
             selectionmode: 'singlerow',
             editmode: 'selectedrow',
             pageable: true,
             editable: false,
             rendertoolbar: function (toolbar) {
            	 var container = $("<div id='toolbarcontainer' class='widget-header'>");
                 toolbar.append(container);
                 container.append('<h4></h4>');
                 container.append('<button id="addrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
                    $("#addrowbutton").jqxButton();
                // create new row.
                $("#addrowbutton").on('click', function () {
                	jqxgridDetail.jqxGrid('addrow', null, {});
                });
             },
             columnsresize: true,
             columnsresize: true,
             theme: theme,
             columns: [
               { text: '${uiLabelMap.Position}', datafield: 'emplPositionTypeId', columntype: 'dropdownlist', width: '15%',
                   createeditor: function (row, value, editor) {
					   var source = new Array();
					   var index = 0;
					   for(var i = 0; i < positionTypeData.length; i++){
						   if(positionTypeData[i].partyId == partyId){
							   source[index] = positionTypeData[i];
							   index++;
						   }
					   }
					   editor.jqxDropDownList({ source: source, displayMember: 'description', autoDropDownHeight: true, dropDownWidth: 250, valueMember: 'emplPositionTypeId' });
                   },
                   cellbeginedit: function(row, datafield, columntype, value){
							if(value && value.length) return false;
                   },
				   cellsrenderer: function(row, column, value){
						for(var i = 0; i < positionTypeData.length; i++){
							if(value == positionTypeData[i].emplPositionTypeId){
								return '<span title=' + value + '>' + positionTypeData[i].description + '</span>'
							}
						}
						return '<span>' + value + '</span>';
				   }
               },
               { text: '${uiLabelMap.FirstMonth}', datafield: 'firstMonth', width: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }   
               },
               { text: '${uiLabelMap.SecondMonth}', datafield: 'secondMonth', width: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.ThirdMonth}', datafield: 'thirdMonth', width: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }  
               },
               { text: '${uiLabelMap.FourthMonth}', datafield: 'fourthMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.FifthMonth}', datafield: 'fifthMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.SixthMonth}', datafield: 'sixthMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.SeventhMonth}', datafield: 'seventhMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.EighthMonth}', datafield: 'eighthMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }  
               },
               { text: '${uiLabelMap.NinthMonth}', datafield: 'ninthMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.TenthMonth}', datafield: 'tenthMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.EleventhMonth}', datafield: 'eleventhMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               },
               { text: '${uiLabelMap.TwelfthMonth}', datafield: 'twelfthMonth', minwidth: '7%',
            	   validation: function (cell, value) {
            	        if (!value) {
            	            return { result: false, message: "${uiLabelMap.FieldRequired}" };
            	        }
            	        return true;
            	    }     
               }
           ]
         });
    }

}
</script>