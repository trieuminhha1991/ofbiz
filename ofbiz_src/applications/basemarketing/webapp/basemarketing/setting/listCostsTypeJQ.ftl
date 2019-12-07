<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign dayStart = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />

<style>
	.line-height-25{
		line-height: 25px;
	}
</style>

<div id="CreateCostsForm" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="row-fluid">
				<div class="row-fluid no-left-margin m-bot-5">
					<label class="span5 line-height-25 align-right">${uiLabelMap.KMarketingCostTypeId}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<input id="nameAdd" class='no-space'/>
					</div>
				</div>

				<div class="row-fluid no-left-margin m-bot-5">
					<label class="span5 line-height-25 asterisk align-right">${uiLabelMap.KDescription}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<input id="descriptionAdd" />
					</div>
				</div>
			</div>
		</div>

		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button type="button" id="alterCancel" class='btn btn-danger form-action-button pull-right'>
						<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
					</button>
					<button type="button" id="alterSave" class='btn btn-primary form-action-button pull-right'>
						<i class='fa-check'> </i> ${uiLabelMap.Save}
					</button>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField = "[
	{name: 'enumId', type: 'string'},
	{name: 'enumTypeId', type: 'string'},
	{name: 'enumCode', type: 'string'},
	{name: 'description', type: 'string'},
]"/>

<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.KMarketingCostTypeId)}', dataField: 'enumCode', width: '25%',
		createeditor: function(row, value, editor){
			editor.addClass('text-edit no-space');
			BasicUtils.initNoSpace(editor);
		}
	},
	{text: '${StringUtil.wrapString(uiLabelMap.KDescription)}', dataField: 'description', width: '75%',
		createeditor: function(row, value, editor){
			editor.addClass('text-edit');
		}
	}
"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="CreateCostsForm" columnlist=columnlist dataField=dataField
	viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
	url="jqxGeneralServicer?sname=getListCosts" mouseRightMenu="true"
	createUrl="jqxGeneralServicer?sname=createCostsType&jqaction=C" addColumns="enumCode;description"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=editCostsType" editColumns="enumId;enumTypeId;enumCode;description"
/>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var popup = $('#CreateCostsForm');
	var grid = $("#jqxgrid");
	var initWindow = function(){
		popup.jqxWindow({
			width: 400,
			height : 160,
			resizable: false,
			isModal: true,
			autoOpen: false,
			cancelButton: $("#alterCancel"),
			modalOpacity: 0.7,
			initContent: function(){
				initElement();
				initRule();
			}
		});
	};
	var initElement = function(){
		$("#descriptionAdd").jqxInput({height: 19, width: 193, minLength: 1 });
		$("#nameAdd").jqxInput({height: 19, width: 193, minLength: 1 });
	};
	var initRule = function(){
		popup.jqxValidator({
			rules : [
				{input: '#descriptionAdd', message: '${StringUtil.wrapString(uiLabelMap.KDescriptionNotEmpty)}', action: 'blur', rule:
					function (input, commit) {
						var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var bindEvent = function(){
		$('#alterSave').click(function(){
			if(!popup.jqxValidator('validate')){
				return;
			}
			var row = {
				enumCode : $('#nameAdd').val(),
				description : $('#descriptionAdd').val()
			};
			grid.jqxGrid('addRow', null, row, "first");
			grid.jqxGrid('clearSelection');
			grid.jqxGrid('selectRow', 0);
			popup.jqxWindow('close');
		});
	};
	$(document).ready(function(){
		initWindow();
		bindEvent();
		popup.on('close',function(){
			Grid.clearForm(popup);
		});
	});
</script>