<div id="jqxPanel" style="position:relative" class="form-horizontal form-window-content-custom" >	
	<div class="row-fluid">
		<div class="span12">
			<div class="span6">
				<div class="row-fluid">
					<div class="span4">
						<label class="text-info">${uiLabelMap.BACCPrefDate}</label>
					</div>
					<div class="span8">
						<div id="prefDate"></div>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="text-info">${uiLabelMap.BACCPartyId}</label>
					</div>
					<div class="span8">
						<div id="enumPartyTypeId"></div>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
						<label class="text-info"></label>
					</div>
					<div class="span8">
						<div id="partyDropDown">
							<div id="partyGrid"></div>
						</div>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span4">
					</div>
					<div class="span8 pull-left form-window-content-custom">
						<button id="alterSave" class='btn btn-primary form-action-button' style="float: left; margin:0px !important"><i class='fa-search'></i> ${uiLabelMap.BACCOK}</button>
			   		</div>
				</div>
			</div>
			<div class="span6">
			</div>
		</div><!--.span12-->
	</div><!--.row-fluid-->
</div>

<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	if(typeof(globalVar) == "undefined"){
		globalVar = {};
	}
	globalVar.enumPartyTypeArr = [
		<#if enumPartyTypeList?exists>
			<#list enumPartyTypeList as enumPartyType>
			{
				enumId: "${enumPartyType.enumId}",
				description: '${StringUtil.wrapString(enumPartyType.description)}'
			},
			</#list>
		</#if>
	];
	
	if(typeof(filter) == "undefined"){
		$.jqx.theme = 'olbius';
		
		var filter = function(){
			var init = function(){
				initFilter();
				initDropDownGrid();
				initValidator();
				bindEvent();
				$("#enumPartyTypeId").jqxDropDownList({selectedIndex: 0});
			};
			var initFilter = function(){
				$("#prefDate").jqxDateTimeInput({formatString: "dd/MM/yyyy", height: '25px', width: '80%', theme: $.jqx.theme});
				accutils.createJqxDropDownList($("#enumPartyTypeId"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: '80%', height: 25});
				
				/* var configParty = {
						useUrl: true,
						root: 'results',
						widthButton: '80%',
						showdefaultloadelement: false,
						autoshowloadelement: false,
						datafields: [{name: 'partyId', type: 'string'}, {name: 'fullName', type: 'string'}],
						columns: [
							{text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: '30%'},
							{text: '${uiLabelMap.BACCFullName}', datafield: 'fullName'}
						],
						url: "JqxGetParties",
						useUtilFunc: true,
						
						key: 'partyId',
						description: ['fullName'],
				};
				accutils.initDropDownButton($("#partyGrid"), $("#partyGrid"), null, configParty, []); */
				
			};
			
			var initDropDownGrid = function(){
				var datafield = [
				                 {name: 'partyId', type: 'string'}, 
				                 {name: 'partyCode', type: 'string'}, 
				                 {name: 'fullName', type: 'string'}
				                 ];
				var columns = [
								{text: "${StringUtil.wrapString(uiLabelMap.BACCOrganizationId)}", datafield: 'partyCode', width: '30%'},
								{text: "${StringUtil.wrapString(uiLabelMap.BACCFullName)}", datafield: 'fullName'}
							];
				
				var config = {
				   		width: '100%', 
				   		virtualmode: true,
				   		showfilterrow: true,
				   		pageable: true,
				   		sortable: true,
				        filterable: true,
				        editable: false,
				        url: '', 
				        showtoolbar: false,
			        	source: {
			        		pagesize: 5,
			        	}
			   	};
				$("#partyDropDown").jqxDropDownButton({
					width: '80%', 
					height: 25,
					theme: 'olbius',
				});
				Grid.initGrid(config, datafield, columns, null, $("#partyGrid"));
			};
			
			var bindEvent = function(){
				$('#alterSave').on('click', function(){
					var valid = $("#jqxPanel").jqxValidator('validate');
					if(!valid){
						return;
					}
					window.location = '<@ofbizUrl>LiabilityPrefDetail?organizationPartyId=${parameters.organizationPartyId}&partyId=' + $('#partyDropDown').attr('data-value') + '&prefDate=' + $('#prefDate').val() + '</@ofbizUrl>';
				});
				
				$("#enumPartyTypeId").on('select', function(event){
					var args = event.args;
				    if (args) {
				    	var item = args.item;
				    	var value = item.value;
				    	var source = $("#partyGrid").jqxGrid('source');
				    	source._source.url = 'jqxGeneralServicer?sname=JqxGetParties&enumId=' + value;
				    	$("#partyGrid").jqxGrid('source', source);
				    }
				});
				
				$("#partyGrid").on('rowclick', function(event){
					var args = event.args;
					var row = $("#partyGrid").jqxGrid('getrowdata', args.rowindex);
					var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
			        $("#partyDropDown").jqxDropDownButton('setContent', dropDownContent);
			        $("#partyDropDown").jqxDropDownButton('close');
			        accutils.setAttrDataValue('partyDropDown', row.partyId);
				});
			};
			
			var initValidator = function(){
				$("#jqxPanel").jqxValidator({
					rules: [
						{ input: '#partyDropDown', message: "${StringUtil.wrapString(uiLabelMap.FieldRequired)}", action: 'keyup, change', 
							rule: function (input, commit) {
								if($(input).val()){
									return true;
								}
								return false;
							}
						},        
					]
				});
			};

			return {
				init: init
			}
		}();
	}
</script>