<div id="popupProductStoreGroupNew" style="display : none;">
	<div>${StringUtil.wrapString(uiLabelMap.BSPSAddNewProductStoreGroup)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSPSProductStoreGroupId}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_productStoreGroupId" class="span12" maxlength="20" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSPSProductStoreGroupName}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_productStoreGroupName" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label>${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<textarea id="wn_description" data-maxlength="50" rows="2" style="resize: vertical;margin-bottom:0" class="span12"></textarea>
				   		</div>
					</div>
				</div><!-- .span12 -->
			</div><!-- .row-fluid -->
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="wn_alterCancel" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="wn_alterSave" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		OlbProductStoreGroupNew.init();
	});
	var OlbProductStoreGroupNew = (function(){
		var init = function(){
			initElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create("#wn_productStoreGroupId", {width:'93%', height: 26});
			jOlbUtil.input.create("#wn_productStoreGroupName", {width:'93%', height:26});
			
			jOlbUtil.windowPopup.create($("#popupProductStoreGroupNew"), {width: 480, height : 260, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#wn_alterCancel")});
		};
		
		var initEvent = function(){
			$('#wn_alterSave').click(function(){
				if (!validatorVAL.validate()) return false;
				
				var row = {
					productStoreGroupId : $('#wn_productStoreGroupId').val(),
					productStoreGroupName : $('#wn_productStoreGroupName').val(),
					description : $('#wn_description').val(),
				};
				$("#jqxgridStoreGroup").jqxGrid('addRow', null, row, "first");
				$("#jqxgridStoreGroup").jqxGrid('clearSelection');                        
				$("#jqxgridStoreGroup").jqxGrid('selectRow', 0);  
				$("#popupProductStoreGroupNew").jqxWindow('close');
				$("#jqxgridStoreGroup").jqxGrid('updatebounddata');
			});
			
			$('#popupProductStoreGroupNew').on('close',function(){
				validatorVAL.hide();
				$('#wn_productStoreGroupId').val(null);
				$('#wn_productStoreGroupName').val(null);
				$('#wn_description').val(null);
			});
		};
		
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
				{input: '#wn_productStoreGroupId', type: 'validCannotSpecialCharactor'},
	            {input: '#wn_productStoreGroupName', type: 'validInputNotNull'},
			];
			validatorVAL = new OlbValidator($('#popupProductStoreGroupNew'), mapRules, extendRules);
		};
		
		return {
			init: init
		}
	}());
</script>