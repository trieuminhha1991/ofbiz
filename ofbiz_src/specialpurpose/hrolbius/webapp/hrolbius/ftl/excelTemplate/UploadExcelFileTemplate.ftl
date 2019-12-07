<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<#assign datafield = "[{name: 'contentId', type: 'string'},
					   {name: 'insuranceContentTypeId', type: 'string'},
					   {name: 'contentName', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'path', type: 'string'}]"/>

<script type="text/javascript">
var insuranceContentTypeArr = [
	<#if insuranceContentTypeList?has_content>
		<#list insuranceContentTypeList as insuranceContentType>
			{
				insuranceContentTypeId: '${insuranceContentType.insuranceContentTypeId}',
				description: '${StringUtil.wrapString(insuranceContentType.description)}'
			},
		</#list>
	</#if>
];
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.InsuranceTemplateType)}', datafield: 'insuranceContentTypeId', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < insuranceContentTypeArr.length; i++){
									if(insuranceContentTypeArr[i].insuranceContentTypeId == value){
										return '<span>' + insuranceContentTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							}
						},
						{datafield: 'contentId', hidden: true},
						{datafield: 'fromDate', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceTemplateContentName)}', datafield: 'contentName', width: 230},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonDownload)}', datafield: 'path', width: 70, cellsalign: 'center',
							cellsrenderer: function (row, column, value) {
								return \"<span style='text-align: center'><a href='\" + value + \"' title='${StringUtil.wrapString(uiLabelMap.HrCommonDownload)}'><i class='icon-cloud-download'></i></a></span>\";
							}
						}
						" />
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist 
					 filterable="false" addType="popup" alternativeAddPopup="popupAddRowWindow" deleterow="true" editable="false" addrow="true"
					 url="jqxGeneralServicer?sname=JQGeInsuranceContent&hasrequest" id="jqxgrid" jqGridMinimumLibEnable="false"
					 removeUrl="jqxGeneralServicer?sname=deleteInsuranceContent&jqaction=D" deleteColumn="contentId;insuranceContentTypeId;fromDate(java.sql.Timestamp)"
					 updateUrl="" addrefresh="true"
					 editColumns="" 
					 selectionmode="singlerow" 
				/>
<div class="row-fluid">
	<div id="popupAddRowWindow" class='hide'>
		<div id="popupWindowHeader">
			${StringUtil.wrapString(uiLabelMap.InsuranceAddNewTemplate)}
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<form enctype="multipart/form-data" method="post" id="upLoadFileForm">
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label asterisk">${uiLabelMap.InsuranceTemplateType}</label>
						</div>
						<div class="span7">
							<div id="insuranceTemplateType"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.InsuranceFileTemplate}</label>
						</div>
						<div class="span7">
							<div class="row-fluid">
								<div class="span11">
									<input type="file" name="uploadedFile" id="uploadedFile">
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="uploadFileBtn">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
var contentTypeArr = [
	<#if contentTypeList?has_content>
		<#list contentTypeList as contentType>
			{
				contentTypeId: '${contentType.contentTypeId}',
				description: '${StringUtil.wrapString(contentType.description)}'
			},
		</#list>
	</#if>
];
$(document).ready(function(){
	initJqxDropdownlist();
	initJqxNotification();
	initUploadFileInput();
	initBtnEvent();
	initJqxWindow();
	initJqxValidator();
}); 
function initJqxDropdownlist(){
	var source =
    {
        localdata: contentTypeArr,
        datatype: "array"
    };
    var dataAdapter = new $.jqx.dataAdapter(source);
	$("#insuranceTemplateType").jqxDropDownList({source: dataAdapter, displayMember: "description", valueMember: "contentTypeId", height: 25, width: '96%', theme: 'olbius'});
	if(contentTypeArr.length < 8){
		$("#insuranceTemplateType").jqxDropDownList({autoDropDownHeight: true});
	}
}

function initJqxNotification(){
	$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
}

function initUploadFileInput(){
	$('#uploadedFile').ace_file_input({
		
  		no_file:'No File ...',
		btn_choose:'${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}',
		droppable:false,
		onchange:null,
		thumbnail:false,	
		width: '100px',
		whitelist:'xsl',
		blacklist:'.',
		preview_error : function(filename, error_code) {
		}

	}).on('change', function(){
	});
	
	$("#btnCancel").click(function(event){
		$("#popupAddRowWindow").jqxWindow('close');
	});
}

function initBtnEvent(){
	$("#uploadFileBtn").click(function(event){
		var valid = $("#popupAddRowWindow").jqxValidator('validate');
		if(!valid){
			return;
		}
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ConfirmCreateInsuranceContent)}",
			[{
				"label" : "${uiLabelMap.CommonSubmit}",
    		    "class" : "btn-primary btn-mini icon-ok",
    		    "callback": function() {
    		 		uploadInsuranceFileTemplate();   	
    		    }	
			},
			{
    		    "label" : "${uiLabelMap.CommonClose}",
    		    "class" : "btn-danger btn-mini icon-remove",
    		    "callback": function() {
    		    	
    		    }
    		}]		
		);
	});
}

function uploadInsuranceFileTemplate(){
	var form = jQuery("#upLoadFileForm");
	var file = form.find('input[type=file]').eq(0);
	if(file.data('ace_input_files')){
		var fileUpload = $('#uploadedFile')[0].files[0];
		//jQuery("#_uploadedFile_fileName").val(fileUpload.name);
		//jQuery("#_uploadedFile_contentType").val(fileUpload.type);
		var dataSubmit = new FormData(jQuery('#upLoadFileForm')[0]);	
	}else{
		var dataSubmit = new FormData();				
	}
	dataSubmit.append("contentTypeId", $("#insuranceTemplateType").val());
	$.ajax({
		url: 'upLoadInsuranceExcelFile',
		type: 'POST',
		data: dataSubmit,
		cache: false,			        
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data){
        	if(data._EVENT_MESSAGE_){
        		$("#jqxNtfContent").text(data._EVENT_MESSAGE_);
				$("#jqxNtf").jqxNotification({template: 'info'});
				$("#jqxNtf").jqxNotification("open");
				$("#jqxgrid").jqxGrid('updatebounddata');
			}else{
				$("#jqxNtfContent").text(data._ERROR_MESSAGE_);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");					
			}
        	$("#popupAddRowWindow").jqxWindow('close');
        }
	});
}

function initJqxWindow(){
	$("#popupAddRowWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
		height: 200, width: 500, isModal: true, theme:'olbius',
		initContent: function(){
			
		}
	});
	//initJqxWindowEvent();
}

function initJqxValidator(){
	$("#popupAddRowWindow").jqxValidator({
		rules:[
			{
				input: "#insuranceTemplateType",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
				rule: function (input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
			},
			{
				input: "#uploadedFile",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
				rule: function (input, commit){
					if(!input.data('ace_input_files')){
						return false;
					}
					return true;
				}
			}
		]
	});
}
</script>