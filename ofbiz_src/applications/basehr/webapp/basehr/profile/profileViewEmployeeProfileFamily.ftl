
<style>
	.highlightCell{
		background-color: #DDEE90 !important;
		color: #009900 !important
	}
</style>
<div class="tab-pane" id="familyMemberTab">
	<#assign dataField="[{ name: 'relPartyId', type: 'string' },
					 { name: 'personFamilyBackgroundId', type: 'string' },
					 { name: 'partyId', type: 'string'},
					 { name: 'partyFamilyId', type: 'string' },
					 { name: 'familyFullName', type: 'string' },
					 { name: 'partyRelationshipTypeId', type: 'string' },
					 { name: 'occupation', type: 'string' },
					 { name: 'birthDate', type: 'date', other:'Timestamp' },
					 { name: 'placeWork', type: 'string'},
					 { name: 'phoneNumber', type: 'number'},
					 { name: 'isDependent', type: 'bool'},
					 { name: 'dependentStartDate', type: 'date'},
					 ]"/>

	<#assign columnlist="
				{ text: '${uiLabelMap.personFamilyBackgroundId}', datafield: 'personFamilyBackgroundId',hidden: true},	
				{ text: '${uiLabelMap.partyId}', datafield: 'partyId', hidden: true, editable: false},	
				{ text: '${uiLabelMap.HRFullName}', datafield: 'familyFullName', width: '15%', filtertype: 'input',
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},	
				{ text: '${uiLabelMap.HRRelationship}', datafield: 'partyRelationshipTypeId', width: '11%', filtertype: 'list', columntype: 'dropdownlist', editable: true,
					cellsrenderer: function(row, column, value){
						for(var i = 0; i < partyRelationshipType.length; i++){
							if(value == partyRelationshipType[i].partyRelationshipTypeId){
								return '<span title=' + value + '>' + partyRelationshipType[i].partyRelationshipName + '</span>';
							}
						}
						return '<span>' + value + '</span>';
					},
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					},
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				        editor.jqxDropDownList({source: partyRelationshipType, valueMember: 'partyRelationshipTypeId', displayMember:'partyRelationshipName' });
				    },
				    createfilterwidget: function (column, htmlElement, editor) {
		                editor.jqxDropDownList({ source: fixSelectAll(partyRelationshipType), displayMember: 'partyRelationshipName', valueMember: 'partyRelationshipTypeId' ,
		                	renderer: function (index, label, value) {
		                		if (index == 0) {
		                			return value;
		                		}
		                        for(var i = 0; i < partyRelationshipType.length; i++){
		                        	if(value == partyRelationshipType[i].partyRelationshipTypeId){
		                        		return partyRelationshipType[i].partyRelationshipName; 
		                        	}
		                        }
		                    }});
		                editor.jqxDropDownList('checkAll');
		            }
				},
				{ text: '${uiLabelMap.BirthDate}', datafield: 'birthDate', width: '15%', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDateTimeInput({width: '110px', height: '31px'});
					},
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: '${uiLabelMap.HROccupation}', datafield: 'occupation', width: '16%',
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: '${uiLabelMap.placeWork}', datafield: 'placeWork', width: '16%', filtertype: 'string',
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: '${uiLabelMap.PartyPhoneNumber}', datafield: 'phoneNumber', width: '12%', columntype: 'numberinput', filtertype: 'number',
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxNumberInput({inputMode: 'simple', spinButtons: false});
					},
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: '${uiLabelMap.PersonDependent}', datafield: 'isDependent',filtertype: 'list', columntype: 'checkbox', editable: false,
					filterable: false,
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{
					datafield: 'dependentStartDate', hidden: true, cellsformat: 'dd/MM/yyyy',
				}
				 "/>

	<@jqGrid addrow="true" addType="popup" customTitleProperties="${StringUtil.wrapString(uiLabelMap.FamilyMembers)}" isShowTitleProperty="true" id="jqxgrid" filtersimplemode="true" showtoolbar="true"
		 deleterow="true" addrefresh="true" alternativeAddPopup="addPersonFamilyWindow" editable="false" dataField=dataField columnlist=columnlist
		 url="jqxGeneralServicer?sname=JQGetListEmplFamily&partyId=${parameters.partyId}" filterable="false" showlist="false" functionAfterAddRow="functionAfterAddRow"
		 width="100%" bindresize="false"
		 removeUrl="jqxGeneralServicer?sname=deletePersonFamilyBackgroundSv&jqaction=D" deleteColumn="personFamilyBackgroundId"
		 updateUrl="jqxGeneralServicer?sname=editPersonFamilyBackgroundSv&jqaction=U" jqGridMinimumLibEnable="false"
		 editColumns="personFamilyBackgroundId;firstName;middleName;lastName;partyRelationshipTypeId;occupation;birthDate(java.sql.Date);placeWork;phoneNumber;isDependent;dependentStartDate(java.sql.Timestamp)"
	/>
	
</div>	 
${setContextField("addNewWindowId", "addPersonFamilyWindow")}
<#include "AddPersonFamilyBackground.ftl"/>
<script type="text/javascript">
$(document).ready(function(){
	globalVar.dependentStatusList = [
		<#if dependentStatusList?has_content>
			<#list dependentStatusList as dependentStatus>
				{
					statusId: '${dependentStatus.statusId}',
					description: '${StringUtil.wrapString(dependentStatus.description)}'
				},
			</#list>
		</#if>
	];
	$("#${addNewWindowId}").on('createPersonFamilySuccess', function(){
		$("#jqxgrid").jqxGrid('updatebounddata');
	});
});
</script>