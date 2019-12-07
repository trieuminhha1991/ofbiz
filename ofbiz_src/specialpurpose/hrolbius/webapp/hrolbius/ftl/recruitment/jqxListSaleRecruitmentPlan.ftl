<#--IMPORT LIB-->
<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<#--/IMPORT LIB-->
<#--============================================== PREPARE DATA=========================================-->
<script>
	//Prepare for DeptPositionTypeDetail data
	<#assign listEmplPositionTypes = delegator.findByAnd("DeptPositionTypeDetail", {"deptId" : '${parameters.partyId}'}, null, false)>
	emplPositionTypeData = [
	              <#list listEmplPositionTypes as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'emplPositionTypeId': '${item.emplPositionTypeId}', 'description': '${description}'},
				  </#list>
				];
	//Prepare for term type data
	<#assign listRecruitmentPlans = delegator.findByAnd("RecruitmentPlan", {"partyId" : '${parameters.partyId}', "year" : '${parameters.year}'}, Static["org.ofbiz.base.util.UtilMisc"].toList("emplPositionTypeId ASC"), false)>
	
</script>
<#--============================================== /PREPARE DATA=========================================-->
<#--==============================================Create Plan Detail====================================-->
<div class="row-fluid" >
	<div class="span12">
		<div id="jqxNotification">
		    <div id="notificationContent">
		    </div>
		</div>
		<div id="notificationContainer"></div>
		<div id="jqxgridPlanDetail"></div>
	</div>
</div>
<style>
	.cell-green-color {
	    color: black !important;
	    background-color: #33CCFF !important;
	}
	.cell-gray-color {
		color: black !important;
		background-color: #87CEEB !important;
	}
</style>
<#--==============================================/Create Plan Detail====================================-->
<#--==============================================Create Approve Window=====================================-->
<#include "jqxApprove.ftl" />
<#--==============================================/Create Approve Window====================================-->
<script>
	var JQXAction = function(theme){
		this.theme = theme;
	};
	JQXAction.bindData = function(){
		var recuitmentPlanData = new Array();
		$.ajax({
            url: "getRecruitmentPlan",
            type: "POST",
            cache: false,
            datatype: 'json',
            async: false,
            data: {partyId : '${parameters.partyId}', year: '${parameters.year}'},
            success: function (data, status, xhr) {
            	if(!data._ERROR_MESSAGE_){
            		recuitmentPlanData = data.listGenericValue;
            	}
            }
        });
		var data = new Array();
		var firstMonth = 0;
		var secondMonth = 0;
		var thirdMonth = 0;
		var fourthMonth = 0;
		var fifthMonth = 0;
		var sixthMonth = 0;
		var seventhMonth = 0;
		var eighthMonth = 0;
		var ninthMonth = 0;
		var tenthMonth = 0;
		var eleventhMonth = 0;
		var twelfthMonth = 0;
		<#if Static['com.olbius.util.SecurityUtil'].hasRole("DELYS_ASM_GT", '${parameters.partyId}', delegator) || Static['com.olbius.util.SecurityUtil'].hasRole("DELYS_ASM_MT", '${parameters.partyId}', delegator) >
			//ASM Plan
			var index = 0;
			if(recuitmentPlanData.length > 0){
				//ASM Plan is created
				<#assign listChilds = Static["com.olbius.util.PartyUtil"].buildOrg(delegator, '${parameters.partyId}', true, false).getDirectChildList(delegator)>
				var index = 0;
				<#list listChilds as child>
					var childRecuitmentPlanData = new Array();
					$.ajax({
			            url: "getRecruitmentPlan",
			            type: "POST",
			            cache: false,
			            datatype: 'json',
			            async: false,
			            data: {"partyId" : '${child.partyId}', "year" : '${parameters.year}'},
			            success: function (data, status, xhr) {
			            	if(!data._ERROR_MESSAGE_){
			            		childRecuitmentPlanData = data.listGenericValue;
			            	}
			            }
			        });
					if(childRecuitmentPlanData.length > 0){
						//Plan for child
						firstMonth = 0;
						secondMonth = 0;
						thirdMonth = 0;
						fourthMonth = 0;
						fifthMonth = 0;
						sixthMonth = 0;
						seventhMonth = 0;
						eighthMonth = 0;
						ninthMonth = 0;
						tenthMonth = 0;
						eleventhMonth = 0;
						twelfthMonth = 0;
						for(var i = 0; i < childRecuitmentPlanData.length; i++){
							var row = {};
							row['emplPositionTypeId'] = childRecuitmentPlanData[i].emplPositionTypeId;
							row['type'] = 'POS';
							row['level'] = '1';
							row['partyId'] = childRecuitmentPlanData[i].partyId;
							row['year'] = childRecuitmentPlanData[i].year;
							row['firstMonth']  = childRecuitmentPlanData[i].firstMonth;
							row['secondMonth']  = childRecuitmentPlanData[i].secondMonth;
							row['thirdMonth']  = childRecuitmentPlanData[i].thirdMonth;
							row['fourthMonth']  = childRecuitmentPlanData[i].fourthMonth;
							row['fifthMonth']  = childRecuitmentPlanData[i].fifthMonth;
							row['sixthMonth']  = childRecuitmentPlanData[i].sixthMonth;
							row['seventhMonth']  = childRecuitmentPlanData[i].seventhMonth;
							row['eighthMonth']  = childRecuitmentPlanData[i].eighthMonth;
							row['ninthMonth']  = childRecuitmentPlanData[i].ninthMonth;
							row['tenthMonth']  = childRecuitmentPlanData[i].tenthMonth;
							row['eleventhMonth']  = childRecuitmentPlanData[i].eleventhMonth;
							row['twelfthMonth']  = childRecuitmentPlanData[i].twelfthMonth;
							firstMonth += childRecuitmentPlanData[i].firstMonth;
							secondMonth += childRecuitmentPlanData[i].secondMonth;
							thirdMonth += childRecuitmentPlanData[i].thirdMonth;
							fourthMonth += childRecuitmentPlanData[i].fourthMonth;
							fifthMonth += childRecuitmentPlanData[i].fifthMonth;
							sixthMonth += childRecuitmentPlanData[i].sixthMonth;
							seventhMonth += childRecuitmentPlanData[i].seventhMonth;
							eighthMonth += childRecuitmentPlanData[i].eighthMonth;
							ninthMonth += childRecuitmentPlanData[i].ninthMonth;
							tenthMonth += childRecuitmentPlanData[i].tenthMonth;
							eleventhMonth += childRecuitmentPlanData[i].eleventhMonth;
							twelfthMonth += childRecuitmentPlanData[i].twelfthMonth;
							data[index++] = row;
						}
						//Summary child plan
						var row = {};
						row['emplPositionTypeId'] = '${child.partyId}';
						row['type'] = 'DEPT';
						row['level'] = 1;
						row['partyId'] = '${child.partyId}';
						row['year'] = '${parameters.year}';
						row['firstMonth']  = firstMonth;
						row['secondMonth']  = secondMonth;
						row['thirdMonth']  = thirdMonth;
						row['fourthMonth']  = fourthMonth;
						row['fifthMonth']  = fifthMonth;
						row['sixthMonth']  = sixthMonth;
						row['seventhMonth']  = seventhMonth;
						row['eighthMonth']  = eighthMonth;
						row['ninthMonth']  = ninthMonth;
						row['tenthMonth']  = tenthMonth;
						row['eleventhMonth']  = eleventhMonth;
						row['twelfthMonth']  = twelfthMonth;
						data[index++] = row
						//End Plan for child
					}
				</#list>
				firstMonth = 0;
				secondMonth = 0;
				thirdMonth = 0;
				fourthMonth = 0;
				fifthMonth = 0;
				sixthMonth = 0;
				seventhMonth = 0;
				eighthMonth = 0;
				ninthMonth = 0;
				tenthMonth = 0;
				eleventhMonth = 0;
				twelfthMonth = 0;
				for(var i = 0; i < recuitmentPlanData.length; i++){
					//Loop employment position
					var row = {};
					row['emplPositionTypeId'] = recuitmentPlanData[i].emplPositionTypeId;
					row['type'] = 'POS';
					row['level'] = '2';
					row['partyId'] = recuitmentPlanData[i].partyId;
					row['year'] = recuitmentPlanData[i].year;
					row['firstMonth']  = recuitmentPlanData[i].firstMonth;
					row['secondMonth']  = recuitmentPlanData[i].secondMonth;
					row['thirdMonth']  = recuitmentPlanData[i].thirdMonth;
					row['fourthMonth']  = recuitmentPlanData[i].fourthMonth;
					row['fifthMonth']  = recuitmentPlanData[i].fifthMonth;
					row['sixthMonth']  = recuitmentPlanData[i].sixthMonth;
					row['seventhMonth']  = recuitmentPlanData[i].seventhMonth;
					row['eighthMonth']  = recuitmentPlanData[i].eighthMonth;
					row['ninthMonth']  = recuitmentPlanData[i].ninthMonth;
					row['tenthMonth']  = recuitmentPlanData[i].tenthMonth;
					row['eleventhMonth']  = recuitmentPlanData[i].eleventhMonth;
					row['twelfthMonth']  = recuitmentPlanData[i].twelfthMonth;
					firstMonth += recuitmentPlanData[i].firstMonth;
					secondMonth += recuitmentPlanData[i].secondMonth;
					thirdMonth += recuitmentPlanData[i].thirdMonth;
					fourthMonth += recuitmentPlanData[i].fourthMonth;
					fifthMonth += recuitmentPlanData[i].fifthMonth;
					sixthMonth += recuitmentPlanData[i].sixthMonth;
					seventhMonth += recuitmentPlanData[i].seventhMonth;
					eighthMonth += recuitmentPlanData[i].eighthMonth;
					ninthMonth += recuitmentPlanData[i].ninthMonth;
					tenthMonth += recuitmentPlanData[i].tenthMonth;
					eleventhMonth += recuitmentPlanData[i].eleventhMonth;
					twelfthMonth += recuitmentPlanData[i].twelfthMonth;
					data[index++] = row;
				}
				
				//Summary Parent Plan
				var row = {};
				row['emplPositionTypeId'] = '${parameters.partyId}';
				row['type'] = 'DEPT';
				row['level'] = 2;
				row['partyId'] = '${parameters.partyId}';
				row['year'] = '${parameters.year}';
				row['firstMonth']  = firstMonth;
				row['secondMonth']  = secondMonth;
				row['thirdMonth']  = thirdMonth;
				row['fourthMonth']  = fourthMonth;
				row['fifthMonth']  = fifthMonth;
				row['sixthMonth']  = sixthMonth;
				row['seventhMonth']  = seventhMonth;
				row['eighthMonth']  = eighthMonth;
				row['ninthMonth']  = ninthMonth;
				row['tenthMonth']  = tenthMonth;
				row['eleventhMonth']  = eleventhMonth;
				row['twelfthMonth']  = twelfthMonth;
				data[index++] = row
				//End ASM Plan is created
			}else{
				//ASM plan is not created
				<#assign listChilds = Static["com.olbius.util.PartyUtil"].buildOrg(delegator, '${parameters.partyId}', true, false).getDirectChildList(delegator)>
				var index = 0;
				<#list listChilds as child>
					<#assign listEmplPositionTypes = delegator.findByAnd("DeptPositionTypeDetail", {"deptId" : '${child.partyId}'}, null, false)>
					var childEmplPositionTypeData = [
					              <#list listEmplPositionTypes as item>
									<#assign description = StringUtil.wrapString(item.description?if_exists) />
									{'emplPositionTypeId': '${item.emplPositionTypeId}', 'description': '${description}'},
								  </#list>
								];
					for(var i = 0; i < childEmplPositionTypeData.length; i++){
						var row = {};
						row['emplPositionTypeId'] = childEmplPositionTypeData[i].emplPositionTypeId;
						row['type'] = 'POS';
						row['level'] = 1;
						row['partyId'] = '${child.partyId}';
						row['year'] = '${parameters.year}';
						row['firstMonth']  = 0;
						row['secondMonth']  = 0;
						row['thirdMonth']  = 0;
						row['fourthMonth']  = 0;
						row['fifthMonth']  = 0;
						row['sixthMonth']  = 0;
						row['seventhMonth']  = 0;
						row['eighthMonth']  = 0;
						row['ninthMonth']  = 0;
						row['tenthMonth']  = 0;
						row['eleventhMonth']  = 0;
						row['twelfthMonth']  = 0;
						data[index++] = row;
					}
					
					//Summary for ASM
					var row = {};
					row['emplPositionTypeId'] = '${child.partyId}';
					row['type'] = 'DEPT';
					row['level'] = 1;
					row['partyId'] = '${child.partyId}';
					row['year'] = '${parameters.year}';
					row['firstMonth']  = 0;
					row['secondMonth']  = 0;
					row['thirdMonth']  = 0;
					row['fourthMonth']  = 0;
					row['fifthMonth']  = 0;
					row['sixthMonth']  = 0;
					row['seventhMonth']  = 0;
					row['eighthMonth']  = 0;
					row['ninthMonth']  = 0;
					row['tenthMonth']  = 0;
					row['eleventhMonth']  = 0;
					row['twelfthMonth']  = 0;
					data[index++] = row
				</#list>
					
				for(var i = 0; i < emplPositionTypeData.length; i++){
					var row = {};
					row['emplPositionTypeId'] = emplPositionTypeData[i].emplPositionTypeId;
					row['type'] = 'POS';
					row['level'] = 2;
					row['partyId'] = '${parameters.partyId}';
					row['year'] = '${parameters.year}';
					row['firstMonth']  = 0;
					row['secondMonth']  = 0;
					row['thirdMonth']  = 0;
					row['fourthMonth']  = 0;
					row['fifthMonth']  = 0;
					row['sixthMonth']  = 0;
					row['seventhMonth']  = 0;
					row['eighthMonth']  = 0;
					row['ninthMonth']  = 0;
					row['tenthMonth']  = 0;
					row['eleventhMonth']  = 0;
					row['twelfthMonth']  = 0;
					data[index++] = row;
				}
				
				var row = {};
				row['emplPositionTypeId'] = '${parameters.partyId}';
				row['type'] = 'DEPT';
				row['level'] = 2;
				row['partyId'] = '${parameters.partyId}';
				row['year'] = '${parameters.year}';
				row['firstMonth']  = 0;
				row['secondMonth']  = 0;
				row['thirdMonth']  = 0;
				row['fourthMonth']  = 0;
				row['fifthMonth']  = 0;
				row['sixthMonth']  = 0;
				row['seventhMonth']  = 0;
				row['eighthMonth']  = 0;
				row['ninthMonth']  = 0;
				row['tenthMonth']  = 0;
				row['eleventhMonth']  = 0;
				row['twelfthMonth']  = 0;
				data[index++] = row
			}
			//End ASM Plan
		<#elseif Static['com.olbius.util.SecurityUtil'].hasRole("DELYS_SALESSUP_GT", '${parameters.partyId}', delegator)>
			var index = 0;
			if(recuitmentPlanData.length > 0){
				//If plan for parent is exists
				firstMonth = 0;
				secondMonth = 0;
				thirdMonth = 0;
				fourthMonth = 0;
				fifthMonth = 0;
				sixthMonth = 0;
				seventhMonth = 0;
				eighthMonth = 0;
				ninthMonth = 0;
				tenthMonth = 0;
				eleventhMonth = 0;
				twelfthMonth = 0;
				for(var i = 0; i < recuitmentPlanData.length; i++){
					//Loop employment position
					var row = {};
					row['emplPositionTypeId'] = recuitmentPlanData[i].emplPositionTypeId;
					row['type'] = 'POS';
					row['level'] = '1';
					row['partyId'] = recuitmentPlanData[i].partyId;
					row['year'] = recuitmentPlanData[i].year;
					row['firstMonth']  = recuitmentPlanData[i].firstMonth;
					row['secondMonth']  = recuitmentPlanData[i].secondMonth;
					row['thirdMonth']  = recuitmentPlanData[i].thirdMonth;
					row['fourthMonth']  = recuitmentPlanData[i].fourthMonth;
					row['fifthMonth']  = recuitmentPlanData[i].fifthMonth;
					row['sixthMonth']  = recuitmentPlanData[i].sixthMonth;
					row['seventhMonth']  = recuitmentPlanData[i].seventhMonth;
					row['eighthMonth']  = recuitmentPlanData[i].eighthMonth;
					row['ninthMonth']  = recuitmentPlanData[i].ninthMonth;
					row['tenthMonth']  = recuitmentPlanData[i].tenthMonth;
					row['eleventhMonth']  = recuitmentPlanData[i].eleventhMonth;
					row['twelfthMonth']  = recuitmentPlanData[i].twelfthMonth;
					firstMonth += recuitmentPlanData[i].firstMonth;
					secondMonth += recuitmentPlanData[i].secondMonth;
					thirdMonth += recuitmentPlanData[i].thirdMonth;
					fourthMonth += recuitmentPlanData[i].fourthMonth;
					fifthMonth += recuitmentPlanData[i].fifthMonth;
					sixthMonth += recuitmentPlanData[i].sixthMonth;
					seventhMonth += recuitmentPlanData[i].seventhMonth;
					eighthMonth += recuitmentPlanData[i].eighthMonth;
					ninthMonth += recuitmentPlanData[i].ninthMonth;
					tenthMonth += recuitmentPlanData[i].tenthMonth;
					eleventhMonth += recuitmentPlanData[i].eleventhMonth;
					twelfthMonth += recuitmentPlanData[i].twelfthMonth;
					data[index++] = row;
				}
				
				//Summary Parent Plan
				var row = {};
				row['emplPositionTypeId'] = '${parameters.partyId}';
				row['type'] = 'DEPT';
				row['level'] = 1;
				row['partyId'] = '${parameters.partyId}';
				row['year'] = '${parameters.year}';
				row['firstMonth']  = firstMonth;
				row['secondMonth']  = secondMonth;
				row['thirdMonth']  = thirdMonth;
				row['fourthMonth']  = fourthMonth;
				row['fifthMonth']  = fifthMonth;
				row['sixthMonth']  = sixthMonth;
				row['seventhMonth']  = seventhMonth;
				row['eighthMonth']  = eighthMonth;
				row['ninthMonth']  = ninthMonth;
				row['tenthMonth']  = tenthMonth;
				row['eleventhMonth']  = eleventhMonth;
				row['twelfthMonth']  = twelfthMonth;
				data[index++] = row
			}
		<#else>
			//RSM, CSM Plan
			<#assign listChilds = Static["com.olbius.util.PartyUtil"].buildOrg(delegator, '${parameters.partyId}', true, false).getDirectChildList(delegator)>
			var index = 0;
			<#list listChilds as child>
				var childRecuitmentPlanData = new Array();
				$.ajax({
		            url: "getRecruitmentPlan",
		            type: "POST",
		            cache: false,
		            datatype: 'json',
		            async: false,
		            data: {"partyId" : '${child.partyId}', "year" : '${parameters.year}'}, "statusId" : "RPH_PROPOSED",
		            success: function (data, status, xhr) {
		            	if(!data._ERROR_MESSAGE_){
		            		childRecuitmentPlanData = data.listGenericValue;
		            	}
		            }
		        });
				if(childRecuitmentPlanData.length > 0){
					//Plan for child
					firstMonth = 0;
					secondMonth = 0;
					thirdMonth = 0;
					fourthMonth = 0;
					fifthMonth = 0;
					sixthMonth = 0;
					seventhMonth = 0;
					eighthMonth = 0;
					ninthMonth = 0;
					tenthMonth = 0;
					eleventhMonth = 0;
					twelfthMonth = 0;
					for(var i = 0; i < childRecuitmentPlanData.length; i++){
						var row = {};
						row['emplPositionTypeId'] = childRecuitmentPlanData[i].emplPositionTypeId;
						row['type'] = 'POS';
						row['level'] = '1';
						row['partyId'] = childRecuitmentPlanData[i].partyId;
						row['year'] = childRecuitmentPlanData[i].year;
						row['firstMonth']  = childRecuitmentPlanData[i].firstMonth;
						row['secondMonth']  = childRecuitmentPlanData[i].secondMonth;
						row['thirdMonth']  = childRecuitmentPlanData[i].thirdMonth;
						row['fourthMonth']  = childRecuitmentPlanData[i].fourthMonth;
						row['fifthMonth']  = childRecuitmentPlanData[i].fifthMonth;
						row['sixthMonth']  = childRecuitmentPlanData[i].sixthMonth;
						row['seventhMonth']  = childRecuitmentPlanData[i].seventhMonth;
						row['eighthMonth']  = childRecuitmentPlanData[i].eighthMonth;
						row['ninthMonth']  = childRecuitmentPlanData[i].ninthMonth;
						row['tenthMonth']  = childRecuitmentPlanData[i].tenthMonth;
						row['eleventhMonth']  = childRecuitmentPlanData[i].eleventhMonth;
						row['twelfthMonth']  = childRecuitmentPlanData[i].twelfthMonth;
						firstMonth += childRecuitmentPlanData[i].firstMonth;
						secondMonth += childRecuitmentPlanData[i].secondMonth;
						thirdMonth += childRecuitmentPlanData[i].thirdMonth;
						fourthMonth += childRecuitmentPlanData[i].fourthMonth;
						fifthMonth += childRecuitmentPlanData[i].fifthMonth;
						sixthMonth += childRecuitmentPlanData[i].sixthMonth;
						seventhMonth += childRecuitmentPlanData[i].seventhMonth;
						eighthMonth += childRecuitmentPlanData[i].eighthMonth;
						ninthMonth += childRecuitmentPlanData[i].ninthMonth;
						tenthMonth += childRecuitmentPlanData[i].tenthMonth;
						eleventhMonth += childRecuitmentPlanData[i].eleventhMonth;
						twelfthMonth += childRecuitmentPlanData[i].twelfthMonth;
						data[index++] = row;
					}
					//Summary child plan
					var row = {};
					row['emplPositionTypeId'] = '${child.partyId}';
					row['type'] = 'DEPT';
					row['level'] = 1;
					row['partyId'] = '${child.partyId}';
					row['year'] = '${parameters.year}';
					row['firstMonth']  = firstMonth;
					row['secondMonth']  = secondMonth;
					row['thirdMonth']  = thirdMonth;
					row['fourthMonth']  = fourthMonth;
					row['fifthMonth']  = fifthMonth;
					row['sixthMonth']  = sixthMonth;
					row['seventhMonth']  = seventhMonth;
					row['eighthMonth']  = eighthMonth;
					row['ninthMonth']  = ninthMonth;
					row['tenthMonth']  = tenthMonth;
					row['eleventhMonth']  = eleventhMonth;
					row['twelfthMonth']  = twelfthMonth;
					data[index++] = row
					//End Plan for child
				}
			</#list>
			if(recuitmentPlanData.length > 0){
				//If plan for parent is exists
				firstMonth = 0;
				secondMonth = 0;
				thirdMonth = 0;
				fourthMonth = 0;
				fifthMonth = 0;
				sixthMonth = 0;
				seventhMonth = 0;
				eighthMonth = 0;
				ninthMonth = 0;
				tenthMonth = 0;
				eleventhMonth = 0;
				twelfthMonth = 0;
				for(var i = 0; i < recuitmentPlanData.length; i++){
					//Loop employment position
					var row = {};
					row['emplPositionTypeId'] = recuitmentPlanData[i].emplPositionTypeId;
					row['type'] = 'POS';
					row['level'] = '2';
					row['partyId'] = recuitmentPlanData[i].partyId;
					row['year'] = recuitmentPlanData[i].year;
					row['firstMonth']  = recuitmentPlanData[i].firstMonth;
					row['secondMonth']  = recuitmentPlanData[i].secondMonth;
					row['thirdMonth']  = recuitmentPlanData[i].thirdMonth;
					row['fourthMonth']  = recuitmentPlanData[i].fourthMonth;
					row['fifthMonth']  = recuitmentPlanData[i].fifthMonth;
					row['sixthMonth']  = recuitmentPlanData[i].sixthMonth;
					row['seventhMonth']  = recuitmentPlanData[i].seventhMonth;
					row['eighthMonth']  = recuitmentPlanData[i].eighthMonth;
					row['ninthMonth']  = recuitmentPlanData[i].ninthMonth;
					row['tenthMonth']  = recuitmentPlanData[i].tenthMonth;
					row['eleventhMonth']  = recuitmentPlanData[i].eleventhMonth;
					row['twelfthMonth']  = recuitmentPlanData[i].twelfthMonth;
					firstMonth += recuitmentPlanData[i].firstMonth;
					secondMonth += recuitmentPlanData[i].secondMonth;
					thirdMonth += recuitmentPlanData[i].thirdMonth;
					fourthMonth += recuitmentPlanData[i].fourthMonth;
					fifthMonth += recuitmentPlanData[i].fifthMonth;
					sixthMonth += recuitmentPlanData[i].sixthMonth;
					seventhMonth += recuitmentPlanData[i].seventhMonth;
					eighthMonth += recuitmentPlanData[i].eighthMonth;
					ninthMonth += recuitmentPlanData[i].ninthMonth;
					tenthMonth += recuitmentPlanData[i].tenthMonth;
					eleventhMonth += recuitmentPlanData[i].eleventhMonth;
					twelfthMonth += recuitmentPlanData[i].twelfthMonth;
					data[index++] = row;
				}
				
				//Summary Parent Plan
				var row = {};
				row['emplPositionTypeId'] = '${parameters.partyId}';
				row['type'] = 'DEPT';
				row['level'] = 2;
				row['partyId'] = '${parameters.partyId}';
				row['year'] = '${parameters.year}';
				row['firstMonth']  = firstMonth;
				row['secondMonth']  = secondMonth;
				row['thirdMonth']  = thirdMonth;
				row['fourthMonth']  = fourthMonth;
				row['fifthMonth']  = fifthMonth;
				row['sixthMonth']  = sixthMonth;
				row['seventhMonth']  = seventhMonth;
				row['eighthMonth']  = eighthMonth;
				row['ninthMonth']  = ninthMonth;
				row['tenthMonth']  = tenthMonth;
				row['eleventhMonth']  = eleventhMonth;
				row['twelfthMonth']  = twelfthMonth;
				data[index++] = row
				//ENd If plan for parent is exists
			}else{
				//If plan for parent is not exists
				for(var i = 0; i < emplPositionTypeData.length; i++){
					//Set data for each position
					firstMonth = 0;
					secondMonth = 0;
					thirdMonth = 0;
					fourthMonth = 0;
					fifthMonth = 0;
					sixthMonth = 0;
					seventhMonth = 0;
					eighthMonth = 0;
					ninthMonth = 0;
					tenthMonth = 0;
					eleventhMonth = 0;
					twelfthMonth = 0;
					row = {};
					row['emplPositionTypeId'] = emplPositionTypeData[i].emplPositionTypeId;
					row['type'] = 'POS';
					row['level'] = 2;
					row['partyId'] = '${parameters.partyId}';
					row['year'] = '${parameters.year}';
					//Summary from childs
					<#list listChilds as child>
						var childRecuitmentPlanData = new Array();
						$.ajax({
				            url: "getRecruitmentPlan",
				            type: "POST",
				            cache: false,
				            datatype: 'json',
				            async: false,
				            data: {"partyId" : '${child.partyId}', "year" : '${parameters.year}'},
				            success: function (data, status, xhr) {
				            	if(!data._ERROR_MESSAGE_){
				            		childRecuitmentPlanData = data.listGenericValue;
				            	}
				            }
				        });
						//Plan for child
						if(childRecuitmentPlanData.length > 0){
							for(var j = 0; j < childRecuitmentPlanData.length; j++){
								if(emplPositionTypeData[i].emplPositionTypeId == childRecuitmentPlanData[j].emplPositionTypeId){
									firstMonth += childRecuitmentPlanData[j].firstMonth;
									secondMonth += childRecuitmentPlanData[j].secondMonth;
									thirdMonth += childRecuitmentPlanData[j].thirdMonth;
									fourthMonth += childRecuitmentPlanData[j].fourthMonth;
									fifthMonth += childRecuitmentPlanData[j].fifthMonth;
									sixthMonth += childRecuitmentPlanData[j].sixthMonth;
									seventhMonth += childRecuitmentPlanData[j].seventhMonth;
									eighthMonth += childRecuitmentPlanData[j].eighthMonth;
									ninthMonth += childRecuitmentPlanData[j].ninthMonth;
									tenthMonth += childRecuitmentPlanData[j].tenthMonth;
									eleventhMonth += childRecuitmentPlanData[j].eleventhMonth;
									twelfthMonth += childRecuitmentPlanData[j].twelfthMonth;
								}
							}
						}
					</#list>
					row['firstMonth']  = firstMonth;
					row['secondMonth']  = secondMonth;
					row['thirdMonth']  = thirdMonth;
					row['fourthMonth']  = fourthMonth;
					row['fifthMonth']  = fifthMonth;
					row['sixthMonth']  = sixthMonth;
					row['seventhMonth']  = seventhMonth;
					row['eighthMonth']  = eighthMonth;
					row['ninthMonth']  = ninthMonth;
					row['tenthMonth']  = tenthMonth;
					row['eleventhMonth']  = eleventhMonth;
					row['twelfthMonth']  = twelfthMonth;
					data[index++] = row;
				}
				//Summary for parent plan
				firstMonth = 0;
				secondMonth = 0;
				thirdMonth = 0;
				fourthMonth = 0;
				fifthMonth = 0;
				sixthMonth = 0;
				seventhMonth = 0;
				eighthMonth = 0;
				ninthMonth = 0;
				tenthMonth = 0;
				eleventhMonth = 0;
				twelfthMonth = 0;
				for(var i = 0; i < emplPositionTypeData.length; i++){
					<#list listChilds as child>
						var childRecuitmentPlanData = new Array();
						$.ajax({
				            url: "getRecruitmentPlan",
				            type: "POST",
				            cache: false,
				            datatype: 'json',
				            async: false,
				            data: {"partyId" : '${child.partyId}', "year" : '${parameters.year}'},
				            success: function (data, status, xhr) {
				            	if(!data._ERROR_MESSAGE_){
				            		childRecuitmentPlanData = data.listGenericValue;
				            	}
				            }
				        });
						//Plan for child
						if(childRecuitmentPlanData.length > 0){
							for(var j = 0; j < childRecuitmentPlanData.length; j++){
								if(emplPositionTypeData[i].emplPositionTypeId == childRecuitmentPlanData[j].emplPositionTypeId){
									firstMonth += childRecuitmentPlanData[j].firstMonth;
									secondMonth += childRecuitmentPlanData[j].secondMonth;
									thirdMonth += childRecuitmentPlanData[j].thirdMonth;
									fourthMonth += childRecuitmentPlanData[j].fourthMonth;
									fifthMonth += childRecuitmentPlanData[j].fifthMonth;
									sixthMonth += childRecuitmentPlanData[j].sixthMonth;
									seventhMonth += childRecuitmentPlanData[j].seventhMonth;
									eighthMonth += childRecuitmentPlanData[j].eighthMonth;
									ninthMonth += childRecuitmentPlanData[j].ninthMonth;
									tenthMonth += childRecuitmentPlanData[j].tenthMonth;
									eleventhMonth += childRecuitmentPlanData[j].eleventhMonth;
									twelfthMonth += childRecuitmentPlanData[j].twelfthMonth;
								}
							}
						}
					</#list>
				}
				var row = {};
				row['emplPositionTypeId'] = '${parameters.partyId}';
				row['type'] = 'DEPT';
				row['level'] = 2;
				row['partyId'] = '${parameters.partyId}';
				row['year'] = '${parameters.year}';
				row['firstMonth']  = firstMonth;
				row['secondMonth']  = secondMonth;
				row['thirdMonth']  = thirdMonth;
				row['fourthMonth']  = fourthMonth;
				row['fifthMonth']  = fifthMonth;
				row['sixthMonth']  = sixthMonth;
				row['seventhMonth']  = seventhMonth;
				row['eighthMonth']  = eighthMonth;
				row['ninthMonth']  = ninthMonth;
				row['tenthMonth']  = tenthMonth;
				row['eleventhMonth']  = eleventhMonth;
				row['twelfthMonth']  = twelfthMonth;
				data[index++] = row
				//If plan for parent is not exists
			}
			//End RSM, CSM Plan
		</#if>
		return data;
	};
	
	JQXAction.bindEvent = function(source){
		var wgdeletesuccess = "${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}";
        var wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
        var wgproposesuccess = "${StringUtil.wrapString(uiLabelMap.wgproposesuccess)}";
        
        $("#btnApprove").on('click', function(){
        	$("#wdwApprove").jqxWindow('open');
        });
        
        $("#alterSaveAppr").click(function(){
        	var submitData = {};
        	submitData['reason'] = $("#apprComment").val();
        	submitData['statusId'] = $("#jqxAccepted").jqxRadioButton('val') ? "RPH_ACCEPTED" : "RPH_REJECTED";
        	submitData['partyId'] = '${parameters.partyId}';
        	submitData['year'] = '${parameters.year}';
        	//Send Request
        	$.ajax({
        		url: 'updateSaleRecruitmentPlanHeader',
        		type: "POST",
        		data: submitData,
        		dataType: 'json',
        		async: false,
        		success : function(data) {
        			if(!data._ERROR_MESSAGE_){
        				$("#wdwApprove").jqxWindow('close');
        			}else{
        				bootbox.confirm(data._ERROR_MESSAGE_, function(result) {
        					return;
        				});
        			}
                }
        	});
        });
        
        $("#btnPropose").on('click', function(){
        	var submitData = {};
        	submitData['partyId'] = '${parameters.partyId}';
        	submitData['year'] = '${parameters.year}';
        	submitData['statusId'] = 'RPH_PROPOSED';
        	var index = 0;
        	$.ajax({
                url: "updateSaleRecruitmentPlanHeader",
                type: "POST",
                cache: false,
                datatype: 'json',
                data: submitData,
                async: false,
                success: function (data, status, xhr) {
                	if(data._ERROR_MESSAGE_){
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'error' });
                		$("#notificationContent").html(data._ERROR_MESSAGE_);
                        $("#jqxNotification").jqxNotification("open");
                	}else{
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'success' });
                		$("#notificationContent").html(wgproposesuccess);
                        $("#jqxNotification").jqxNotification("open");
                	}
                }
            });
        });
        
        // create new row.
        $("#btnAccept").on('click', function () {
        	//ASM Plan
    		<#if Static['com.olbius.util.SecurityUtil'].hasRole("DELYS_ASM_GT", '${parameters.partyId}', delegator) || Static['com.olbius.util.SecurityUtil'].hasRole("DELYS_ASM_MT", '${parameters.partyId}', delegator) >
        		var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
            	var submitData = {};
            	var index = 0;
            	for(var i = 0; i < rows.length; i++){
            		var rowData = rows[i];
            		if(rowData['type'] == 'POS'){
                		submitData['partyId_o_' + index] = rowData['partyId'];
                		submitData['emplPositionTypeId_o_' + index] = rowData['emplPositionTypeId'];
                		submitData['year_o_' + index] = '${parameters.year}';
                		submitData['firstMonth_o_' + index] = rowData['firstMonth'];
                		submitData['secondMonth_o_' + index] = rowData['secondMonth'];
                		submitData['thirdMonth_o_' + index] = rowData['thirdMonth'];
                		submitData['fourthMonth_o_' + index] = rowData['fourthMonth'];
                		submitData['fifthMonth_o_' + index] = rowData['fifthMonth'];
                		submitData['sixthMonth_o_' + index] = rowData['sixthMonth'];
                		submitData['seventhMonth_o_' + index] = rowData['seventhMonth'];
                		submitData['eighthMonth_o_' + index] = rowData['eighthMonth'];
                		submitData['ninthMonth_o_' + index] = rowData['ninthMonth'];
                		submitData['tenthMonth_o_' + index] = rowData['tenthMonth'];
                		submitData['eleventhMonth_o_' + index] = rowData['eleventhMonth'];
                		submitData['twelfthMonth_o_' + index] = rowData['twelfthMonth'];
                		index++;
            		}
            	}
    		<#else>
        		var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
            	var submitData = {};
            	var index = 0;
            	for(var i = 0; i < rows.length; i++){
            		var rowData = rows[i];
            		if(rowData['type'] == 'POS' && rowData['level'] == 2){
            			submitData['partyId_o_' + index] = '${parameters.partyId}';
                		submitData['emplPositionTypeId_o_' + index] = rowData['emplPositionTypeId'];
                		submitData['year_o_' + index] = '${parameters.year}';
                		submitData['firstMonth_o_' + index] = rowData['firstMonth'];
                		submitData['secondMonth_o_' + index] = rowData['secondMonth'];
                		submitData['thirdMonth_o_' + index] = rowData['thirdMonth'];
                		submitData['fourthMonth_o_' + index] = rowData['fourthMonth'];
                		submitData['fifthMonth_o_' + index] = rowData['fifthMonth'];
                		submitData['sixthMonth_o_' + index] = rowData['sixthMonth'];
                		submitData['seventhMonth_o_' + index] = rowData['seventhMonth'];
                		submitData['eighthMonth_o_' + index] = rowData['eighthMonth'];
                		submitData['ninthMonth_o_' + index] = rowData['ninthMonth'];
                		submitData['tenthMonth_o_' + index] = rowData['tenthMonth'];
                		submitData['eleventhMonth_o_' + index] = rowData['eleventhMonth'];
                		submitData['twelfthMonth_o_' + index] = rowData['twelfthMonth'];
                		index++;
            		}
            	}
    		</#if>
    		$.ajax({
                url: "createOrUpdateRecruitmentPlan",
                type: "POST",
                cache: false,
                datatype: 'json',
                data: submitData,
                async: false,
                success: function (data, status, xhr) {
                	if(data._ERROR_MESSAGE_){
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'error' });
                		$("#notificationContent").html(data._ERROR_MESSAGE_);
                        $("#jqxNotification").jqxNotification("open");
                        source.localdata = JQXAction.bindData();
                        $("#jqxgridPlanDetail").jqxGrid('updatebounddata');
                	}else{
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'success' });
                		$("#notificationContent").html(wgupdatesuccess);
                        $("#jqxNotification").jqxNotification("open");
                        source.localdata = JQXAction.bindData();
                        $("#jqxgridPlanDetail").jqxGrid('updatebounddata');
                	}
                }
            });
        });
	}
	
	JQXAction.prototype.createPlanDetailGrid = function(){
		<#assign rph = delegator.findOne("RecruitmentPlanHeader", {"partyId" : parameters.partyId, "year" : parameters.year}, false)>
        var editable = true;
        <#if rph.statusId == "RPH_ACCEPTED" || userLogin.partyId != rph.creatorPartyId>
			editable = false;
        </#if>
		var data = JQXAction.bindData();
		var source =
        {
            localdata: data,
            datatype: "array",
            datafields:
            [
                { name: 'emplPositionTypeId', type: 'string'},
                { name: 'level', type: 'number' },
                { name: 'type', type: 'string' },
                { name: 'partyId', type: 'string' },
                { name: 'year', type: 'string' },
                { name: 'firstMonth', type: 'number' },
                { name: 'secondMonth', type: 'number' },
                { name: 'thirdMonth', type: 'number' },
                { name: 'fourthMonth', type: 'number' },
                { name: 'fifthMonth', type: 'number' },
                { name: 'sixthMonth', type: 'number' },
                { name: 'seventhMonth', type: 'number' },
                { name: 'eighthMonth', type: 'number' },
                { name: 'ninthMonth', type: 'number' },
                { name: 'tenthMonth', type: 'number' },
                { name: 'eleventhMonth', type: 'number' },
                { name: 'twelfthMonth', type: 'number' }
            ],
            updaterow: function (rowid, newdata, commit) {
            	$("#jqxgridPlanDetail").jqxGrid('updatebounddata');
            }
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
   
        $("#jqxgridPlanDetail").jqxGrid(
        {
            source: dataAdapter,
            columnsresize: true,
            theme: this.theme,
            autoheight: true,
            pageable: true,
            width: '100%',
            editable: editable,
            pagesize: 100,
            showtoolbar: true,
            rendertoolbar: function (toolbar) {
            	var container = $("<div id='toolbarcontainer' class='widget-header'>");
                toolbar.append(container);
                container.append('<h4>${uiLabelMap.RecruitmentPlanDetail}</h4>');
                <#if rph.statusId != "RPH_ACCEPTED" && userLogin.partyId == rph.creatorPartyId>
	                container.append('<button id="btnAccept" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>');
	                $("#btnAccept").jqxButton({theme: this.theme});
                </#if>
                <#if rph.statusId != "RPH_ACCEPTED" && userLogin.partyId == rph.creatorPartyId>
	                container.append('<button id="btnPropose" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="fa fa-hand-o-right"></i>${uiLabelMap.HRCommonProposal}</button>');
	                $("#btnPropose").jqxButton({theme: this.theme});
                </#if>
                <#if Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) != "DELYS_ASM_GT" && Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) != "DELYS_ASM_MT" && Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) != "DELYS_SALESSUP_GT" && userLogin.partyId != rph.creatorPartyId>
                	container.append('<button id="btnApprove" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="fa fa-thumbs-o-up"></i>${uiLabelMap.HRApprove}</button>');
	                $("#btnApprove").jqxButton({theme: this.theme});
                </#if>
                JQXAction.bindEvent(source);
            },
            columns: [
              { text: '${uiLabelMap.Position}', datafield: 'emplPositionTypeId', width: '16%',pinned: true, editable: false,
            	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                      for(var i = 0; i < emplPositionTypeData.length; i++){
                      	if(value == emplPositionTypeData[i].emplPositionTypeId){
                      		return '<span title=' + value + '>' + emplPositionTypeData[i].description + '</span>';
                      	}
                      }
                      return '<span title=' + value + '>' + value + '</span>';
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  }
              },
              { text: '${uiLabelMap.FirstMonth}', datafield: 'firstMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.SecondMonth}', datafield: 'secondMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.ThirdMonth}', datafield: 'thirdMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.FourthMonth}', datafield: 'fourthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.FifthMonth}', datafield: 'fifthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.SixthMonth}', datafield: 'sixthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.SeventhMonth}', datafield: 'seventhMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.EighthMonth}', datafield: 'eighthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.NinthMonth}', datafield: 'ninthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.TenthMonth}', datafield: 'tenthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.EleventhMonth}', datafield: 'eleventhMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.TwelfthMonth}', datafield: 'twelfthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i ++){
                			  if(rows[i].type == 'POS' && rows[i].level == 1 && rowData['emplPositionTypeId'] == rows[i].emplPositionTypeId){
                				  return false;
                			  }
                		  }
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                			  if(rows[i].type == 'POS' && rows[i].level == 2 && rows[i].emplPositionTypeId == rowData['emplPositionTypeId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }else if(rowData['type'] == 'POS' && rowData['level'] == 2){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 2){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              }
            ]
        });
	};
	$(document).ready(function() {
		<#include "jsApprove.ftl" />
		var jqxAction = new JQXAction('olbius');
		jqxAction.createPlanDetailGrid();
	});
</script>