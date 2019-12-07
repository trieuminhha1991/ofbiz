<script>
	//Prepare for skill type data
	<#assign listSkillTypes = delegator.findList("SkillType", null, null, null, null, false) />
	var skillTypeData = new Array();
	<#list listSkillTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['skillTypeId'] = '${item.skillTypeId}';
		row['description'] = '${description}';
		skillTypeData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'skillTypeId', type: 'string' },
					 { name: 'yearsExperience', type: 'string' },
					 { name: 'rating', type: 'string' },
					 { name: 'skillLevel', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.skillTypeId}', datafield: 'skillTypeId', width: 200,
						cellsrenderer: function(column, row, value){
							for(var i = 0; i < skillTypeData.length; i++){
								if(value == skillTypeData[i].skillTypeId){
									return '<span title=' + value + '>' + skillTypeData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						}
					 },
                     { text: '${uiLabelMap.yearsExperience}', datafield: 'yearsExperience', width: 200},
                     { text: '${uiLabelMap.rating}', datafield: 'rating', width: 200},
                     { text: '${uiLabelMap.skillLevel}', datafield: 'skillLevel'}
					 "/>

<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplSkill&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
		 />