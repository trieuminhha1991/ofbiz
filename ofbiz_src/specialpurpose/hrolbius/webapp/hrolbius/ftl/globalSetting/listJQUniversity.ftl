<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[
	{name : 'schoolId', type : 'string'},
	{name : 'schoolName',type : 'string'},
	{name : 'description',type : 'string'}
]"/>
<#assign columnlist="
			{text : '${uiLabelMap.CommonId}',dataField : 'schoolId',width : '150px',editable : false},
			{text : '${uiLabelMap.UniversityName}',dataField : 'schoolName',filterable : false},
			{text : '${uiLabelMap.CommonDescription}',dataField : 'description',filterable :false}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true"   addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListUniversity"
		 createUrl="jqxGeneralServicer?sname=createUniversity&jqaction=C" addColumns="schoolId;schoolName;countryGeoId;stateProvinceGeoId;description"
		 removeUrl="jqxGeneralServicer?sname=deleteUniversity&jqaction=D" deleteColumn="schoolId"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateUniversity"  editColumns="schoolId;schoolName;description"
		/>	
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.CommonAdd} ${uiLabelMap.University}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal">
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
				<div class="controls">
					<input type="text" id="schoolIdAdd"/>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.UniversityName}</label>
				<div class="controls">
					<input type="text" id="schoolNameAdd"/>
				</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonCountry}</label>
				<div class="controls">
					<select name="countryGeoId" id="AddUniversity_countryGeoId">
						${screens.render("component://common/widget/CommonScreens.xml#countries")}        
						<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
						<option selected="selected" value="${defaultCountryGeoId}">
						<#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
						${countryGeo.get("geoName",locale)}
						</option>
					</select>	
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.PartyState}</label>
				<div class="controls">
					<select name="stateProvinceGeoId" id="AddUniversity_stateProvinceGeoId">
					</select>	
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonDescription} </label>
				<div class="controls">
					<input type="text" id="descriptionAdd"/>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">&nbsp;</label>
				<div class="controls">
					<button type="button" class='btn btn-primary btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
					<button type="button" class='btn btn-danger btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				</div>
			</div>
		</form>
	</div>
</div>

<#assign listSchool = delegator.findList("EducationSchool",null,null,null,null,false) !>
<script type="text/javascript">
var dataSchool = [
			<#list listSchool as sc>
				{
					id : '${sc.schoolId?default('')}'
				},
			</#list>
		];
$.jqx.theme = 'olbius';
var theme = theme;

$('#alterpopupWindow').jqxWindow({ width: 500, height : 400,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });

	$('#schoolIdAdd').jqxInput({width : '210px',height : '25px'});
	$('#descriptionAdd').jqxInput({width : '210px',height : '25px'});
	$('#schoolNameAdd').jqxInput({width : '210px',height : '25px'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#schoolIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#schoolIdAdd',message : '${StringUtil.wrapString(uiLabelMap.NotiSchoolIdExist?default(''))}' , action : 'blur',rule : function(){
					var schoolId = $('#schoolIdAdd').val();
					for(var i= 0;i < dataSchool.length ; i++){
						if(dataSchool[i].id.toLowerCase() == schoolId.toLowerCase()){
							return false;
						}
					}
					return true;
				}},
				{input : '#schoolNameAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'}
			]
	})
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		schoolId : $('#schoolIdAdd').val(),
		schoolName : $('#schoolNameAdd').val(),
		countryGeoId : $('#AddUniversity_countryGeoId').val(),
		stateProvinceGeoId : $('#AddUniversity_stateProvinceGeoId').val(),
		description : $('#descriptionAdd').val()
	};
	 $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	// select the first row and clear the selection.
    $("#jqxgrid").jqxGrid('clearSelection');                        
    $("#jqxgrid").jqxGrid('selectRow', 0);  
    $("#alterpopupWindow").jqxWindow('close');
	});
	$('#alterpopupWindow').on('close',function(){
		$('#formAdd').trigger('reset');
	});
</script>		
			