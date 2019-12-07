<div id="alterpopupWindow" style="display:none">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNewProductFeature)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSProductFeatureCategory}</label>
						</div>
						<div class='span7'>
							<input id="wn_productFeatureCategoryId"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_productFeatureTypeId" class="required">${uiLabelMap.BSProductFeatureType}</label>
						</div>
						<div class='span7'>
							<div id="wn_productFeatureTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_productFeatureId" class="required">${uiLabelMap.BSProductFeatureId}</label>
						</div>
						<div class='span7'>
							<input id="wn_productFeatureId"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<input id="wn_description"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSAbbrev}</label>
						</div>
						<div class='span7'>
							<input id="wn_abbrev"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSSequenceNum}</label>
						</div>
						<div class='span7'>
							<div id="wn_defaultSequenceNum"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSUom}</label>
						</div>
						<div class='span7'>
							<input id="wn_uomId"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSNumberSpecified}</label>
						</div>
						<div class='span7'>
							<div id="wn_numberSpecified"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSAmount}</label>
						</div>
						<div class='span7'>
							<input id="wn_defaultAmount"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSIdCode}</label>
						</div>
						<div class='span7'>
							<input id="wn_idCode"/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave1" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel1" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<@jqOlbCoreLib hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	<#assign featureTypeList = delegator.findList("ProductFeatureType", null , null, orderBy,null, false)>
	var featureTypeList = [
	<#if featureTypeList?exists>
	    <#list featureTypeList as featureTypeL>
	    {	productFeatureTypeId : "${featureTypeL.productFeatureTypeId}",
	    	description: "${StringUtil.wrapString(featureTypeL.get("description", locale))}"
	    },
	    </#list>
	</#if>
	];
	
	$(function(){
		OlbProdFeatureNew.init();
	});
	
	var OlbProdFeatureNew = (function(){
		var validatorVAL;
		
		var init = function(){
			initWindow();
			initElement();
			initElementComplex();
			initValidateForm();
			initEvent();
		};
		var initWindow = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindow"), {width: 520, height: 510, cancelButton: $("#alterCancel1")});
		};
		var initElement = function(){
			jOlbUtil.input.create($("#wn_productFeatureId"), {width: '96%'});
			jOlbUtil.input.create($("#wn_description"), {width: '96%'});
			jOlbUtil.input.create($("#wn_uomId"), {width: '96%'});
			jOlbUtil.input.create($("#wn_defaultAmount"), {width: '96%'});
			jOlbUtil.input.create($("#wn_abbrev"), {width: '96%'});
			jOlbUtil.input.create($("#wn_idCode"), {width: '96%'});
			jOlbUtil.input.create($("#wn_productFeatureCategoryId"), {width: '96%', disabled: true});
			jOlbUtil.numberInput.create($("#wn_numberSpecified"), {width: '98%', min: 0, decimalDigits: 0});
			jOlbUtil.numberInput.create($("#wn_defaultSequenceNum"), {width: '98%', min: 0, decimalDigits: 0});
			
			$("#wn_productFeatureCategoryId").val('${productFeatureCategoryId?if_exists}');
		};
		var initElementComplex = function(){
			var configProductFeatureType = {
				width: '98%',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
				useUrl: false,
				url: '',
				key: 'productFeatureTypeId',
				value: 'description',
				displayDetail: true,
				autoDropDownHeight: false,
				filterable: true
			}
			new OlbDropDownList($("#wn_productFeatureTypeId"), featureTypeList, configProductFeatureType, []);
		};
		var initEvent = function(){
			$('#alterSave1').click(function(){
				$('#alterpopupWindow').jqxValidator('validate');
			});
			
			$('#alterpopupWindow').on('validationSuccess',function(){
				var uomId = $('#wn_uomId').val();
				var row = {};
				row = {
					productFeatureTypeId : $('#wn_productFeatureTypeId').val(),
					productFeatureId : $('#wn_productFeatureId').val(),
					description : $('#wn_description').val(),
					if(uomId){
						uomId : uomId;
					},
					numberSpecified : $('#wn_numberSpecified').val(),
					defaultAmount : $('#wn_defaultAmount').val(),
					defaultSequenceNum : $('#wn_defaultSequenceNum').val(),
					abbrev : $('#wn_abbrev').val(),
					idCode : $('#wn_idCode').val(),
					productFeatureCategoryId : '${productFeatureCategoryId?if_exists}',
				};
				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
				$("#jqxgrid").jqxGrid('clearSelection');                        
				$("#jqxgrid").jqxGrid('selectRow', 0);  
				$("#alterpopupWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
			});
			
			$('#alterpopupWindow').on('open',function(){
				$('#wn_productFeatureId').focus();
			});
			
			$('#alterpopupWindow').on('close',function(){
				$('#alterpopupWindow').jqxValidator('hide');
				$('#jqxgrid').jqxGrid('refresh');
				$('#wn_description').val(null);
				$('#wn_productFeatureId').val(null);
				$("#wn_productFeatureTypeId").jqxDropDownList('clearSelection'); 
				$('#wn_description').val(null);
				$('#wn_numberSpecified').val(null);
				$('#wn_defaultSequenceNum').val(null);
				$('#wn_defaultAmount').val(null);
				$('#wn_abbrev').val(null);
				$('#wn_idCode').val(null);
			});
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
		            {input: '#wn_productFeatureId', type: 'validCannotSpecialCharactor'},
					{input: '#wn_productFeatureId', type: 'validInputNotNull'},
					{input: '#wn_description', type: 'validInputNotNull'},
					{input: '#wn_productFeatureTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
	            ];
			validatorVAL = new OlbValidator($('#alterpopupWindow'), mapRules, extendRules, {position: 'bottom'});
		};
		return {
			init: init
		}
	}());
</script>