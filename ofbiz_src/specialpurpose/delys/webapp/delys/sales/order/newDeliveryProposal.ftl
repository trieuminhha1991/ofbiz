<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<div id="main" style="overflow:hidden;">
	<form class="form-horizontal basic-custom-form" id="newDeliveryProposal" style="display: block;">
		<div class="row-fluid form-window-content">
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
						${uiLabelMap.DADeliveryProposalId}
			        </div>
					<div class="span7">
						<input type="text" name="deliveryReqId" style="height:18px;" id="deliveryReqId" />
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
						${uiLabelMap.DADeliveryDate}
					</div>
					<#--<div class="controls">
						<div class="span12">
							<@htmlTemplate.renderDateTimeField name="requirementStartDate" id="requirementStartDate" value="" event="" action="" className="" alert="" 
								title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
								timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
								classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
								pmSelected="" compositeType="" formName=""/>
						</div>
					</div>-->
					<div class="span7">
						<div id="TransferDateTime"></div>
					</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class='span5 align-right asterisk'>
						${uiLabelMap.DADescription}
			        </div>
					<div class="span7">
						<textarea class="span12" name="deliveryReqDescription" id="deliveryReqDescription">${parameters.deliveryReqDescription?if_exists}</textarea>
					</div>
				</div>
			</div><!--.span6-->
		</div><!--.row-->
	</form>
	<div class="row-fluid">
		<div class="span12 margin-top10">
			<button class="btn btn-success btn-next btn-small form-action-button pull-right" id="alterSave" data-last="Finish">${uiLabelMap.DANext} <i class="icon-arrow-right icon-on-right"></i></button>
			<button class="btn btn-prev btn-small form-action-button pull-right" disabled="disabled"><i class="icon-arrow-left"></i> ${uiLabelMap.DAPrev}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function(){
		start();
	})
	function start(){
		$("#TransferDateTime").jqxDateTimeInput({width: 220, height : 25,formatString : 'dd/MM/yyyy HH:mm:ss'});
		$("#newDeliveryProposal").jqxValidator({
			rules : [
			         {input : '#TransferDateTime', message : '${StringUtil.wrapString(uiLabelMap.DArequiredValueGreaterThanFromDate)}', action : 'keyup,blur,change', rule : function(input,commit){
			        	 var now = new Date();
//							now.setHours(0,0,0,0);
			        		if($('#TransferDateTime').jqxDateTimeInput('getDate').getTime() < now){
			        			return false;
			        		}
			        		return true; 
			         }}
	         ]
		});
		$("#alterSave").click(function(){
			$("#newDeliveryProposal").jqxValidator('validate');
		})
		$("#newDeliveryProposal").on('validationSuccess', function(event){
			var date = formatDate($('#TransferDateTime').jqxDateTimeInput('getDate'));
			var request = $.ajax({
				type : "POST",
				url : "showDeliveryProposal",
				datatype : "html",
				data : {
					deliveryReqId : $("#deliveryReqId").val(),
					requirementStartDate : date,
					deliveryReqDescription : $("#deliveryReqDescription").val()
				},
				success : function(data){
					$("html").empty();
					return "<html>" + $("html").html(data) + "</html>";
				}
			});request.done(function(data){
				window.location.href= "<@ofbizUrl>newDeliveryProposal</@ofbizUrl>";
			})
		})
	}
	var formatDate= function(date){
		if(!date || typeof(date) =='undefined') return null;
		return date.format('yyyy-mm-dd HH:MM:ss');
	}
</script>
