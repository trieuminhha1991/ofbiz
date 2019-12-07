<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<div class="row-fluid">
	<div class="span12" style="text-align: center; font-size: 20px"><b>${uiLabelMap.PageTitleResignationApplication}</b></div>
</div>
<#if emplTerminationList?has_content>
	<#assign emplTermination = emplTerminationList.get(0)/> 
</#if>
<#if emplTermination?exists>
	<div class="row-fluid">
		<div class="clearfix" style="margin-bottom: 5px">
			<div class="pull-left alert alert-success inline no-margin">
				<i class="bigger-120 blue"></i>
				${uiLabelMap.CommonStatus}:&nbsp;
				<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", emplTermination.statusId), false)>
				<span>${statusItem.description}</span>
			</div>
		</div>
	</div>
	
<#else>
	<div class="row-fluid" id="statusDisplay" style="display: none;">
		<div class="clearfix" style="margin-bottom: 5px">
			<div class="pull-left alert alert-success inline no-margin">
				<i class="bigger-120 blue"></i>
				${uiLabelMap.CommonStatus}:&nbsp;
				<span id="currStt"></span>					
			</div>					
		</div>
	</div>
</#if>
<div class="row-fluid mgt20">
	<div class="span12 boder-all-profile">
		<span class="text-header">${uiLabelMap.DAGeneralInformation}</span>
		<div class='form-window-container'>
			<div class='row-fluid form-window-content'>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.EmployeeId}
						</div>
						<div class="span7">
							<div>${lookupPerson.partyId}</div>
						</div>					
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.EmployeeName}
						</div>
						<div class="span7">
							<div>${lookupPerson.lastName?if_exists} ${lookupPerson.middleName?if_exists} ${lookupPerson.firstName?if_exists}</div> 
						</div>					
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.gender}
						</div>
						<div class="span7">
							<#if lookupPerson.gender?exists>
								<#if lookupPerson.gender == "M">
									${uiLabelMap.CommonMale}
								<#else>
									${uiLabelMap.CommonFemale}
								</#if>
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if> 
						</div>					
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.DABirthday}
						</div>
						<div class="span7">
							 <#if lookupPerson.birthDate?exists>
							 	${lookupPerson.birthDate?string["dd/MM/yyyy"]}
							 <#else>
							 	${uiLabelMap.HRCommonNotSetting}
							 </#if>
						</div>					
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.Department}
						</div>
						<div class="span7">
							<#assign department = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator, partyId)/>
							<#if department?exists>
								<#assign partyGroup = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", department.partyIdFrom), false)>
								${partyGroup.groupName?if_exists}
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if>
						</div>
					</div>				
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.FormFieldTitle_position}
						</div>
						<div class="span7">
							<#if employmentData.emplPositionType?exists>
								${employmentData.emplPositionType.description}&nbsp;
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if>
						</div>
					</div>				
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.HRPlaceWork}
						</div>
						<div class="span7">
							<#if department?exists>
								<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
								<#assign postalAddrList = Static["com.olbius.util.PartyUtil"].getPostalAddressOfOrg(delegator, department.partyIdFrom, nowTimestamp, nowTimestamp)/>
								<#if postalAddrList?has_content>
									<#assign postalAddr = postalAddrList.get(0)/>
									<#if postalAddr.stateProvinceGeoId?exists>
										<#assign stateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", postalAddr.stateProvinceGeoId), false)/>
										${stateProvinceGeo.geoName?if_exists}
									</#if>  
								<#else>
									${uiLabelMap.HRCommonNotSetting}	
								</#if>
							<#else>
								${uiLabelMap.HRCommonNotSetting}	
							</#if>
						</div>
					</div>	
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.DateJoinCompany}
						</div>
						<div class="span7">
							<#assign dateJoinCompany = Static["com.olbius.util.PersonHelper"].getDateEmplJoinOrg(delegator, partyId)!>
							<#if dateJoinCompany?has_content>
								${dateJoinCompany?string["dd/MM/yyyy"]}
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if>
						</div>
					</div>	
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid mgt20">
	<div class="span12 boder-all-profile">
		<span class="text-header">${uiLabelMap.CurrentContract}</span>
		<div class='form-window-container'>
			<div class='row-fluid form-window-content'>
				<div class="span6">
					<#assign employeeAgreement = Static["com.olbius.util.PartyHelper"].getCurrAgreementOfEmpl(delegator, partyId)!/>
					 <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.EmplAgreementId}
						</div>
						<div class="span7">
							<#if employeeAgreement?has_content>
								${employeeAgreement.agreementId}
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.agreementTypeId}
						</div>
						<div class="span7">
							<#if employeeAgreement?has_content>
								<#assign agreementType = delegator.findOne("AgreementType", Static["org.ofbiz.base.util.UtilMisc"].toMap("agreementTypeId", employeeAgreement.agreementTypeId), false)/>
								${agreementType.description?if_exists}
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.AvailableFromDate}
						</div>
						<div class="span7">
							<#if employeeAgreement?has_content>
								${employeeAgreement.fromDate?string["dd/MM/yyyy"]}
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							${uiLabelMap.AvailableThruDate}
						</div>
						<div class="span7">
							<#if employeeAgreement?has_content && employeeAgreement.thruDate?exists>
								${employeeAgreement.thruDate?string["dd/MM/yyyy"]}
							<#else>
								${uiLabelMap.HRCommonNotSetting}
							</#if>
						</div>
					</div>
				</div>
				<div class="span6">
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var statusArr = [
		<#list statusList as status >
			{
				'statusId': '${status.statusId?if_exists}',
				'description': '${status.description?if_exists}' 	
			},
		</#list>                 
	];
	<#assign datafield = "[{name: 'partyId', type: 'string'},
							{name: 'roleTypeId', type: 'string'},
							{name: 'fixedAssetId', type: 'string'},
							{name: 'fixedAssetName', type: 'string'},
							{name: 'fromDate', type: 'date'},
							{name: 'thruDate', type: 'date'},
							{name: 'statusId', type: 'string'},
							{name: 'comments', type: 'string'}]"/>
	<#assign columnlist = "{datafield: 'partyId', hidden: true},
						    {datafield: 'fixedAssetId', text: '${uiLabelMap.fixedAssetId}', editable: false, width: 130},  
						    {datafield: 'fixedAssetName', text: '${uiLabelMap.fixedAssetName}', editable: false, width: 150},
						    {datafield: 'roleTypeId', text: '${uiLabelMap.CommonRole}', editable: false, width: 150},
						    {datafield: 'statusId', text: '${uiLabelMap.CommonStatus}', editable: false, width: 150,
						    	cellsrenderer: function(row, colum, value){
						    		for(i = 0 ; i < statusArr.length; i++){
	        							if(value == statusArr[i].statusId){
	        								return '<span title=' + value +'>' + statusArr[i].description + '</span>';
	        							}
	        						}
	        						return '<span title=' + value +'>' + value + '</span>';
						    	}	
						    },
						    {datafield: 'fromDate', text: '${uiLabelMap.DateHandover}', editable: false, width:150, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput'},
						    {datafield: 'thruDate', text: '${uiLabelMap.CommonThruDate}', editable: false, width:150, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput'},
						    {datafield: 'comments', text: '${uiLabelMap.HRNotes}', editable: false}"/>
</script>
<div class="row-fluid mgt20">
	<div class="span12 boder-all-profile">
		<span class="text-header">${uiLabelMap.TransferredAsset}</span>
		<div class='form-window-container'>
			<div class='row-fluid'>
				<div class="span12">
					<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="false" showstatusbar="false"
						 showtoolbar="false" width="100%" bindresize="false"
						 filterable="false" deleterow="false" editable="false" addrow="false"
						 url="jqxGeneralServicer?hasrequest=Y&sname=getPartyFixedAssetAssignment" id="jqxgrid"
						 removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />				
				</div>
			</div>
		</div>		
	</div>
</div>
<script type="text/javascript">
	var dataInvoiceType = [
   		<#list listInvoiceType as item>
   			<#assign description = item.get("description", locale)/>
   			{
   				invoiceTypeId: '${item.invoiceTypeId}',
   				description: "${description}"
   			},
   		</#list>                       
   	];
	var dataStatusType = [
	                     	<#if listStatusItem?exists>
	                      		<#list listStatusItem as type>
	                      			{statusId : "${type.statusId}",description : "${StringUtil.wrapString(type.description)}"},
								</#list>
                      	 	</#if>
	];
	
	<#assign datafieldInvoice = "[{name: 'invoiceId', type: 'string'},
	                              {name: 'invoiceTypeId', type: 'string'},
	                              {name: 'partyIdFrom', type: 'string'},
	                              {name: 'invoiceDate', type: 'date', other:'Timestamp'},
	                              {name: 'statusId', type: 'string'},
	                              {name: 'total', type: 'number'},
	         					  {name: 'currencyUomId', type: 'string'},
	         					  {name: 'amountToApply', type: 'number'},
	         					  {name: 'description', type: 'string'},
	                              ]"/>
	<#assign columnlistInvoice = "{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:100, datafield: 'invoiceId', cellsrenderer:
								     	function(row, colum, value)
								        {
								        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								        	return \"<span><a href='/delys/control/accArinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
								        }
									},
									{text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:180, datafield: 'partyIdFrom', 
										cellsrenderer: function(row, colum, value){
								 			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								 			return \"<span>\" + data.partyNameResultFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
								 		},
								 	},
								 	{text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', filtertype: 'checkedlist', width:130, datafield: 'invoiceTypeId', 
								 		cellsrenderer: function(row, colum, value)
				                        {
				                        	for(i=0; i < dataInvoiceType.length;i++){
				                        		if(value==dataInvoiceType[i].invoiceTypeId){
				                        			return \"<span>\" + dataInvoiceType[i].description + \"</span>\";
				                        		}
				                        	}
				                        	return \"<span>\" + value + \"</span>\";
				                        }
								 	},
								 	{text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:130, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy'},
								 	{ text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:120, datafield: 'statusId', 
								 		cellsrenderer: function(row, colum, value){
				                        	for(i=0; i < dataStatusType.length;i++){
				                        		if(value==dataStatusType[i].statusId){
				                        			return \"<span>\" + dataStatusType[i].description + \"</span>\";
				                        		}
				                        	}
				                        	return value;
				                        }
				                    },
				                    {text: '${uiLabelMap.description}', width:150, datafield: 'description'},
				                    {text: '${uiLabelMap.FormFieldTitle_total}', sortable:false, filterable: false, width:120, datafield: 'total', 
				                    	cellsrenderer: function(row, colum, value){
									 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
									 		return \"<span>\" + formatcurrency(data.total,data.currencyUomId) + \"</span>\";
									 	}
				                    },
									 {text: '${uiLabelMap.FormFieldTitle_amountToApply}', sortable:false, filterable: false, datafield: 'amountToApply', 
								 		cellsrenderer: function(row, colum, value){
								 			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								 			return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";
								 		}
									 }
								 	"/>
</script>
<script type="text/javascript">
var dataPaymentType = [
	<#list 0..(listPaymentType.size() - 1) as i>
		{
			paymentTypeId:"${listPaymentType.get(i).paymentTypeId}",
			description: "${StringUtil.wrapString(listPaymentType.get(i).description?if_exists)}"	
		},
	</#list>	                       
];

var dataStatusPaymentItem = [
	<#list listStatusItemPayment as status >
	{
		'statusId': '${status.statusId?if_exists}',
		'description': '${StringUtil.wrapString(status.description?if_exists)}' 	
	},
	</#list>                             
];
<#assign dataFieldPayment ="[{ name: 'paymentId', type: 'string' },
					{ name: 'paymentTypeId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'comments', type: 'string'},
					{ name: 'partyIdTo', type: 'string'},
					{ name: 'effectiveDate', type: 'date', other:'Timestamp'},
					{ name: 'amount', type: 'number'},
					{ name: 'amountToApply', type: 'number'},
					{ name: 'currencyUomId', type: 'string'}]"/>

<#assign columnlistPayment="{ text: '${uiLabelMap.FormFieldTitle_paymentId}', width:100, filtertype:'input', datafield: 'paymentId', 
								cellsrenderer: function(row, colum, value){
                       				return \"<span><a href='/delys/control/accArpaymentOverview?paymentId=\" + value + \"'>\" + value + \"</a></span>\";
                       			}
                       		},
							{text: '${uiLabelMap.OrderPaymentType}', filtertype: 'checkedlist', width:200, datafield: 'paymentTypeId', 
                       			cellsrenderer: function(row, colum, value){
		                       		for(i=0; i < dataPaymentType.length;i++){
		                       			if(dataPaymentType[i].paymentTypeId==value){
		                       				return \"<span>\" + dataPaymentType[i].description +\"</span>\";
		                   				}
		                       		}
		                       		return \"<span>\" + value + \"</span>\";
		                       }
							},
							{text: '${uiLabelMap.FormFieldTitle_statusId}', filtertype: 'checkedlist', width: 130, datafield: 'statusId', 
								cellsrenderer: function(row, colum, value){
			                       	for(i=0; i < dataStatusPaymentItem.length;i++){
			                       		if(dataStatusPaymentItem[i].statusId==value){
			                       			return \"<span>\" + dataStatusPaymentItem[i].description +\"</span>\";
			                   			}
			                       	}
			                       	return \"<span>\" + value + \"</span>\";
		                        },
					   			
							},
							{text: '${uiLabelMap.HRNotes}', filtertype:'input', width:130, datafield: 'comments'},
							{text: '${uiLabelMap.accAccountingToParty}', filtertype: 'olbiusdropgrid', datafield: 'partyIdFrom', hidden: true},
							{text: '${uiLabelMap.accAccountingFromParty}', filtertype: 'olbiusdropgrid', width:150, datafield: 'partyIdTo'},
							{text: '${uiLabelMap.FormFieldTitle_effectiveDate}', filtertype: 'range', hidden:true, datafield: 'effectiveDate', cellsformat: 'dd/MM/yyyy'},
							{text: '${uiLabelMap.DAAmount}', sortable: false, filterable: false, width:130, datafield: 'amount', cellsrenderer:
							 	function(row, colum, value){
							 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 		return \"<span>\" + formatcurrency(data.amount,data.currencyUomId) + \"</span>\";
							 	}},
							{text: '${uiLabelMap.FormFieldTitle_amountToApply}', sortable: false, filterable: false, datafield: 'amountToApply', 
						 		cellsrenderer: function(row, colum, value){
						 			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 			return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";
						 		}
							}
					"/>					
</script>
<div class="row-fluid mgt20">
	<div class="span12 boder-all-profile">
		<span class="text-header">${uiLabelMap.accLiabilities}</span>
		<div class='form-window-container'>
			<div class='row-fluid'>
				<div class="span12">
					<h4>${uiLabelMap.InvoiceMustPaid}</h4>
					 <@jqGrid filtersimplemode="true" addType="popup" dataField=datafieldInvoice columnlist=columnlistInvoice clearfilteringbutton="false" showstatusbar="false"
						 showtoolbar="false" width="100%" bindresize="false"
						 filterable="false" deleterow="false" editable="false" addrow="false"
						 url="jqxGeneralServicer?hasrequest=Y&sname=getPartyInvoiceAR" id="jqxgridInvoice"
						 removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />
				</div>
			</div>
			<div class='row-fluid'>
				<div class="span12">
					<h4>${uiLabelMap.PaymentMustPaid}</h4>
					<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFieldPayment columnlist=columnlistPayment 
						clearfilteringbutton="false" showstatusbar="false"
						showtoolbar="false" width="100%" bindresize="false"
						filterable="false" deleterow="false" editable="false" addrow="false"
						url="jqxGeneralServicer?hasrequest=Y&sname=getPartyPaymentAR" id="jqxgridPayment"
						removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />
				</div>
			</div>
		</div>		
	</div>
</div>
<script type="text/javascript">
	var dataTerminationReason = [
		<#list terminationReasonList as termination>
			{
				terminationReasonId: "${termination.terminationReasonId}",
				description: "${StringUtil.wrapString(termination.description?if_exists)}"
			},
		</#list>
		{
			terminationReasonId: "other",
			description: "${uiLabelMap.CommonOther}"
		}	
	];	
	$(document).ready(function () {   
		<#if !emplTermination?exists> 
			$("#dateTermination").jqxDateTimeInput({width: '220px', height: '25px', theme: 'olbius'});
			var sourceTerminationReason =
	        {
	            localdata: dataTerminationReason,
	            datatype: "array"
	        };
			var dataAdapter = new $.jqx.dataAdapter(sourceTerminationReason);
			$("#terminationReason").jqxDropDownList({source: dataAdapter, displayMember: "description", valueMember: "terminationReasonId", 
				height: 25, width: 220, theme:'olbius',
	            renderer: function (index, label, value) {
	                var datarecord = dataTerminationReason[index];                                            
	                return datarecord.description;
	            }
			});
			<#if (terminationReasonList?size < 8)>
				$("#terminationReason").jqxDropDownList({autoDropDownHeight: true});
			</#if>
			$("#terminationReason").on('select', function(event){
				var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;		    
				    var value = item.value;
				    if(value == "other"){
				  		$("#otherReasonDiv").show();		  	
				    }else{
				    	$("#otherReasonDiv").hide();
				    }
			    }
			});
			$('#comment').jqxEditor({
		        theme: 'olbiuseditor',
		        width: '98%'
		    });
			$("#otherReason").jqxInput({height: 25, width: 220, minLength: 1});
			$("#submitTermination").click(function(event){
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.SendTerminationApplicationConfirm)}?",
				[
					{
						"label": "${uiLabelMap.CommonSubmit}",
						"class" : "icon-ok btn btn-mini btn-primary",
						"callback": function(){
							sendEmplTerminationProposal();	
						}
					},
					{
						"label" : "${uiLabelMap.CommonCancel}",
		    			"class" : "btn-danger icon-remove btn-mini",
		    		 	"callback": function() {
		    		   	
		    			} 
					}
				]);			
			});
		<#else>
			var resultId = $('<textarea />').html("${emplTermination.comment?if_exists}").text();
			$("#comment").append(resultId);
		</#if>
    });
	
	<#if !emplTermination?exists>
		function sendEmplTerminationProposal(){
			var dataSubmit = {};
			dataSubmit["dateTermination"] = $("#dateTermination").val('date').getTime();
			var terminationReason = $("#terminationReason").jqxDropDownList('getSelectedItem');
			if(terminationReason){
				var value = terminationReason.value;
				if(value != 'other'){
					dataSubmit["terminationReasonId"] = value;
				}else{				
					dataSubmit["otherReason"] = $("#otherReason").val();
				}
			}
			dataSubmit["comment"] = $("#comment").val();
			$.ajax({
				url: "createEmplTerminationPpsl",
				data: dataSubmit,
				type: 'POST',
				success: function(data){
					if(data.responseMessage == "success"){
						$("#submitTermination").css("display", "none");
						bootbox.dialog(data.successMessage,
							[
								{
									"label": "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
									"class" : "icon-ok btn btn-mini btn-primary",
									"callback": function(){
											
									}								
								}
							]		
						);
						$("#statusDisplay").show();
						$("#currStt").text(data.status);
					}else{
						bootbox.dialog(data.errorMessage,
							[
								{
									"label": "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
									"class" : "icon-remove btn btn-mini btn-danger",
									"callback": function(){
											
									}
								}
							]		
						);
					}
				},
				complete: function(){
					
				}
			});
		}
	</#if>
	
</script>
<div class="row-fluid mgt20">
	<div class="span12 boder-all-profile">
		<span class="text-header">${uiLabelMap.TerminationInfo}</span>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class="span6">
					 <div class='row-fluid margin-bottom10'>
					 	<div class='span5 text-algin-right'>
					 		${uiLabelMap.HREmplReasonResign}
					 	</div>
					 	<div class="span7">
					 		<#if emplTermination?exists>
					 			<#if emplTermination.terminationReasonId?exists>
						 			<#assign terminationReason = delegator.findOne("TerminationReason", Static["org.ofbiz.base.util.UtilMisc"].toMap("terminationReasonId", emplTermination.terminationReasonId), false)/>
						 			${terminationReason.description}
						 		<#elseif emplTermination.otherReason?exists>
						 			${emplTermination.otherReason}	
					 			</#if>
					 		<#else>
					 			<div id="terminationReason"></div>
					 		</#if>
					 	</div>
					</div>		
					<#if !emplTermination?exists>
						<div class='row-fluid margin-bottom10' id="otherReasonDiv" style="display: none">
						 	<div class='span5 text-algin-right'>
						 		${uiLabelMap.OtherReason} 
						 	</div>
						 	<div class="span7">
						 		<input type="text" id="otherReason">
						 	</div>
						 </div>
					 </#if>
				</div>
				<div class="span6">
					 <div class='row-fluid margin-bottom10'>
					 	<div class='span5 text-algin-right'>
					 		${uiLabelMap.HREmplResignDate}
					 	</div>
					 	<div class="span7">
					 		<#if emplTermination?exists>
					 			${emplTermination.dateTermination?string["dd/MM/yyyy"]}
					 		<#else>
					 			<div id="dateTermination"></div>
					 		</#if>
					 	</div>
					 </div>		
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class='row-fluid margin-bottom10'>
						 	<div class='span2 text-algin-right' style="margin-right: 10px; margin-left: 25px">
						 		${uiLabelMap.HRNotes}
						 	</div>
						 	<div class="span9">
						 		<#if emplTermination?exists>
						 			<#if emplTermination.comment?exists>
						 				<span id="comment"></span>
						 			<#else>
						 				${uiLabelMap.HRCommonNotSetting}
						 			</#if>
						 		<#else>
						 			<textarea id="comment"></textarea>
						 		</#if>
						 	</div>
						</div>		
					</div>
				</div>
				<#if !emplTermination?exists>
					<div class="row-fluid">
						<div class="span11" style="margin-left: 20px">
							<button id="submitTermination" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.SendTerminationApplication}</button>
						</div>
					</div>
				</#if>
			</div>
		</div>
	</div>
</div>
