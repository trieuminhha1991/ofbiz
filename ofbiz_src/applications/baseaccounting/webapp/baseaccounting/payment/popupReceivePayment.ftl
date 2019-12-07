<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<@jqGridMinimumLib/>
<@useLocalizationNumberFunction />
<#if orderTypeId == "PURCHASE_ORDER">
	<#assign orderRoles = delegator.findByAnd("OrderRole",Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId","${parameters.orderId}","roleTypeId","BILL_TO_CUSTOMER"),null,false)/>
    <#assign orderInvoiceReference = delegator.findByAnd("OrderInvoiceReference",{"orderId" : '${parameters.orderId?if_exists}'},null,false) />
    <#if orderInvoiceReference?exists && orderInvoiceReference?has_content>
        <#assign orderInvoice = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderInvoiceReference)/>
        <#assign paymentApplications = delegator.findByAnd("PaymentApplication",{"invoiceId" : '${orderInvoice.invoiceId?if_exists}'},null,false) />
    </#if>
<#else>
	<#assign orderRoles = delegator.findByAnd("OrderRole",Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId","${parameters.orderId}","roleTypeId","BILL_FROM_VENDOR"),null,false)/>
</#if>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<div id="jqxWindowPaymentDetail" style="display : none;">
	<div><#if orderTypeId == "PURCHASE_ORDER">${StringUtil.wrapString(uiLabelMap.BACCOrderSendPayments)}<#else>${StringUtil.wrapString(uiLabelMap.BACCOrderReceivePayments)}</#if></div>
	<div id="jqxContent" style="overflow-x : hidden !important;" >
		<div id="container"></div>
		<div id="jqxGridReceivePayment">
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSaveReceivePayment" class='btn btn-primary form-action-button'><i class='fa-money'></i> ${uiLabelMap.BACCPayment}</button>
				<button id="alterCancelReceivePayment" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div id="jqxNotification" style="display: none;">
	<div id="jqxNotificationContent"></div>	
</div>

<div id="jqxContextMenusPayment" class="hide">
	<ul>
		<li action="fillPayment" name="fillPayment">
			<i class="fa fa-cc-paypal"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BACCAutoFillFullPayment)}
		</li>
	</ul>
</div>

<div id="quickAddConversionFactor" class="hide">
    <div>${uiLabelMap.BACCPleaseChooseAcc}...</div>
    <div class='form-window-container' style="position: relative;">
        <div class='form-window-content'>
            <div class='row-fluid'>
                <div class="span5 text-algin-right">
                    <label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCExchangedRate)}</label>
                </div>
                <div class="span7">
                    <div id="conversionFactorPopup"></div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <button type="button" class='btn btn-danger form-action-button pull-right'id="cancelQuickAddConversionFactor">
                <i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>
            <button type="button" class='btn btn-primary form-action-button pull-right' id="saveQuickAddConversionFactor">
                <i class='fa fa-check'></i>&nbsp;${uiLabelMap.BACCOK}</button>
        </div>
    </div>
</div>

<@useLocalizationNumberFunction />	
<script type="text/javascript">
	var approveClick = false;
	$(document).ready(function(){
		OlbReceivePayment.init();
	});
	var OlbReceivePayment = (function(){
		
		var wi = $("#jqxWindowPaymentDetail");
		var grid = $('#jqxGridReceivePayment');
		var saveRecevei = $('#alterSaveReceivePayment');
		var accountOneValue = $('#accountOneValue');
		var accountTwoValue = $('#accountTwoValue');
		var jqxMenus = {};
		var jqxContextMenusPayment = $("#jqxContextMenusPayment");
		var amountApplied = 0;
		var amountNotApplied  = 0;
		var isEqualAppls  = false;
		
		var init = function(){
		    initElement();
			initNtfId();
			initJqxWindow();
            _inIt(grid);
            initContextMenus();
			initAmountApplied();
			bindEvent();
		};

		var initElement = function() {
            $("#conversionFactorPopup").jqxNumberInput({ width: '92%',  max : 9999999999999, digits: 12, decimalDigits:2, spinButtons: true, min: 0});
        };
		
		var initNtfId = function(){
			var ntfid = '${parameters.ntfId?if_exists}';
			localStorage.setItem('ntfId',ntfid);
		};
		
		var getNtfId = function(){	
			return localStorage.getItem('ntfId');
		};
		
		var initContextMenus = function(){
//			jOlbUtil.contextMenu.create($("#jqxContextMenusPayment"));
            jqxMenus = OlbPage.prototype.createContextMenu('#jqxContextMenusPayment',1,{popupZIndex: 99999999999,theme : 'olbius'});

        };
		
		var initJqxWindow = function(){
			wi.jqxWindow({
			       maxWidth : 1100,width: 1050, height : 400, isModal: true, autoOpen: false, cancelButton: $("#alterCancelReceivePayment"), modalOpacity: 0.7
			    });
            $("#quickAddConversionFactor").jqxWindow({
                maxWidth : 1000, width: 450, height: 140, isModal: true, autoOpen: false, cancelButton: $("#cancelQuickAddConversionFactor"), position: {x: 200, y: 500}
            });
        };
		
		function replaceReg(str,find,replace){
			if(typeof str != 'string') str = str.toString();
			if(str){
				for(var i = 0 ; i< str.length;i++){
					if(str.charAt(i).indexOf(find) != -1){
						str = str.replace(str.charAt(i),replace);
					}else continue;
				}	
			}
			return str.trim();
		}
		
		var initAmountApplied = function(){
			localStorage.countClick = 0;
			<#if orderPreference?exists && orderPreference?has_content>
				<#list orderPreference as pre>
				<#if orderTypeId == "PURCHASE_ORDER">
					<#assign _payment = pre.getRelated("Payment",null,null,false) !>
					<#if _payment?exists && _payment?has_content>
						<#if _payment.get(0)?exists && (_payment.get(0).statusId == 'PMNT_SENT' || _payment.get(0).statusId == 'PMNT_CONFIRMED')>
							amountApplied += Number(replaceReg('${pre.maxAmount?if_exists?default(0)}',',','.'))
						</#if>
					<#else>
						amountApplied += Number(replaceReg('${pre.maxAmount?if_exists?default(0)}',',','.'))
					</#if>
					<#assign amountAppl = amountAppl  +  pre.maxAmount?if_exists?default(0) /> 
				<#else>
					<#if !pre.paymentMethodId?exists && !pre.paymentMethodId?has_content>
					
					<#else>
					<#assign amountAppl = amountAppl  +  pre.maxAmount?if_exists?default(0) /> 
					amountApplied += Number(replaceReg('${pre.maxAmount?if_exists?default(0)}',',','.'))
					</#if>
				</#if>
				</#list>
			</#if>
            <#if orderTypeId == "PURCHASE_ORDER">
                amountApplied = 0;
                <#list paymentApplications as paymentApplication>
                    <#assign _payment = delegator.findOne("Payment", {"paymentId" : paymentApplication.paymentId}, true) />
                    <#if _payment?exists && _payment?has_content>
                        <#if _payment?exists && (_payment.statusId == 'PMNT_SENT' || _payment.statusId == 'PMNT_CONFIRMED')>
                            amountApplied += Number(replaceReg('${paymentApplication.amountApplied?if_exists?default(0)}',',','.'))
                        </#if>
                    </#if>
                </#list>
            </#if>
			amountNotApplied = Number(replaceReg(accountOneValue.val(),',','.')) - amountApplied;
			amountNotApplied  = Number(amountNotApplied.toFixed(2));
			<#if orderTypeId == "PURCHASE_ORDER">
			//render amount not payment for purchase orders
			if($('#_totalValue').length > 0){
				var valx = Number(replaceReg(accountOneValue.val(),',','.')) - amountApplied;
				var total = formatcurrency(Number(replaceReg(valx,',','.')),accountTwoValue.val());
				$('#_totalValue').empty();							
				$('#_totalValue').append('<img src="/aceadmin/jqw/jqwidgets/styles/images/loader.gif"/>');
				var timout = setTimeout(function(){
					$('#_totalValue').empty();	
					$('#_totalValue').append('<label style=\"color:red;font-weight:bold;font-size : 17px;display : -webkit-inline-box;\">' +formatcurrency(Number(replaceReg(amountApplied,',','.')),accountTwoValue.val())  + ' / '+ formatcurrency(Number(replaceReg(accountOneValue.val(),',','.'),accountTwoValue.val()),accountTwoValue.val()) + '</label>');	
				},1500);
				if(valx == 0 || '${orderHeader.statusId?if_exists}' == 'ORDER_APPROVED' || '${orderHeader.statusId?if_exists}' == 'ORDER_COMPLETED'){
					if($('.fonthead').children().length <= 1){
						if('${orderHeader.statusId}' == 'ORDER_APPROVED'){
							$('.fonthead').append('<label>(${StringUtil.wrapString(uiLabelMap.BACCApproved?default(''))})</label>');
						}else{
							$('.fonthead').append('<label>(${StringUtil.wrapString(uiLabelMap.BACCFinished?default(''))})</label>');
						}
						$('.fonthead').attr('href','javascript:void(0)');
					}
				}
			}
			</#if>
		}
		
		function _inIt(grid){
		    var config = {
		        source: {localdata: [], cache: false},
                pagesize: 4,
                pagesizeoptions: ['4'],
                autohieght: false,
                pageable: true,
                selectionmode: 'multiplecellsadvanced',
                autorowheight: true,
                rendertoolbar: function(toolbar) {
                    var valx = Number(replaceReg(accountOneValue.val(),',','.')) - amountApplied;
                    var total = formatcurrency(Number(replaceReg(valx,',','.')),accountTwoValue.val());

                    var content = $('<div id="headerToolbar" class="widget-header"></div>');
                    var header = $('<h4>${uiLabelMap.OrderOrderTotal}&nbsp;<span class=\"acccustom\">&nbsp; '  + total  + '</span></h4>');
                    content.append(header);
                    toolbar.append(content);
                    return toolbar;
                },
                autoheight: true,
                width: '100%',
                editable: true,
                showtoolbar: true
            };
			Grid.initGrid(config, functionUse.initDataFields(),  functionUse.initColumns(), null,grid);
            Grid.createContextMenu(grid, jqxContextMenusPayment, false);
		}
		
		var bindEvent = function(){
			
			window.addEventListener("scroll", function(event) {
			    var top = this.scrollY,
			        left = this.scrollX;
			    var width = wi.jqxWindow('width'),height = wi.jqxWindow('height');
//			    wi.jqxWindow({position : {x: left + width/3,y : top + height/2}})
			}, false);
			
			wi.bind('close',function(){
			    	$('#info_loader').css('display','none');
		    });

		    saveRecevei.bind('click',function(){
			  functionUse.saveReceivePayment();
		    });	
			
			grid.on('contextmenu', function () {
	                return false;
            });
			
			grid.on('rowclick',function(event){
				 if (event.args.rightclick) {
	                    grid.jqxGrid('selectrow', event.args.rowindex);
	                    var scrollTop = $(window).scrollTop();
	                    var scrollLeft = $(window).scrollLeft();
	                    jqxContextMenusPayment.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
	                    return false;
	                }
			});
			
			grid.on('cellendedit',function(event){
				grid.jqxGrid('showloadelement');
				var args = event.args;
				var enterPayment = 0 ;
				setTimeout(function(){
					enterPayment = getValueEntered()
					}
				,1000);
			});
			
			jqxContextMenusPayment.on('itemclick', function (event) {
                var args = event.args;
                var rowindex = grid.jqxGrid('getselectedrowindex');
                if ($.trim($(args).text()) == '${StringUtil.wrapString(uiLabelMap.BACCAutoFillFullPayment)}') {
                	var enteredVal = getValueEntered();
                	if(enteredVal == amountNotApplied){
                		return;
                	}else if(enteredVal > amountNotApplied){
                		//grid.jqxGrid('setcellvalue', rowindex, "orderAmount", amountNotApplied);
                		return;
                	}
                	grid.jqxGrid('setcellvalue', rowindex, "orderAmount", amountNotApplied - enteredVal);
                	grid.trigger('cellendedit')
                }
            });

            <#if security.hasPermission("ACC_POAPPROVED_ADMIN",session) && statusId?exists && statusId =="ORDER_CREATED" && orderTypeId == "PURCHASE_ORDER">
                    $('#updateSttOrder').on('click', function(){
                    openNotify();
                });
            </#if>

            $("#cancelQuickAddConversionFactor").click(function(){
                $("#quickAddConversionFactor").jqxWindow('close');
            });
            $("#saveQuickAddConversionFactor").click(function(){
                var conversionFactor = $("#conversionFactorPopup").val();
                functionUse.processPaymentData(conversionFactor);
                $("#quickAddConversionFactor").jqxWindow('close');
            });
		};
		<#if orderTypeId =="PURCHASE_ORDER">
		function openNotify(){
			bootbox.dialog("${uiLabelMap.AreYouSureApprove}", 
			[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll(); approveClick = false;}
	        }, 
	        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	if (!approveClick) {
						var orderId = '${orderId}';
						var statusId = "ORDER_APPROVED";
						var setItemStatus = 'Y';
						var shipAfterDateData = '${nowTimestamp}';
						changeOrderStatus(orderId, statusId, setItemStatus, shipAfterDateData);	            	
	            		approveClick = true;
	            	}
	            }
	        }]);
		}
		
		function changeOrderStatus(orderId, statusId, setItemStatus, shipAfterDateData){
			$.ajax({
				beforeSend: function(){
	                $("#loader_page_common_loading").show();
	            },
	            complete: function(){
	            	var timeout = setTimeout(function(){
	            		$("#loader_page_common_loading").hide();
		            	location.reload();
		            	clearTimeout(timeout);
	            	},300);
	            },
				url: "changeOrderStatusByAccountant",
				type: "POST",
				data: {orderId: orderId, statusId: statusId, setItemStatus: setItemStatus,shipAfterDate: shipAfterDateData,amount : amountNotApplied},
				success: function(data) {
					
				}
			});
		}
		</#if>
		function getValueEntered(){
			var total = 0;
			var data  = grid.jqxGrid('getdatainformation');
			if(data.rowscount){
				for(var i = 0 ; i < data.rowscount;i++){
					var amount = grid.jqxGrid('getcellvalue',i,'orderAmount');
					total += Number(replaceReg(amount,',','.'));
				}
			}
			if( Number(replaceReg(total,',','.')) == amountNotApplied){
				isEqualAppls = true;
				$('.custom-cell-grid2').addClass('notifull')
			}else {
				isEqualAppls = false;
				$('.custom-cell-grid2').removeClass('notifull')
			}
			grid.jqxGrid('hideloadelement');
			return total;
		}
		
		function receivePayment(url) {
			var data = {
				orderId : '${parameters.orderId?if_exists}',
				avo : accountOneValue.val(),
				avt : $("#accountTwoValue").val()
			}
			if(data){
				functionUse.sendRequest(data);
			}
			wi.jqxWindow('open');
		}
		
		var functionUse = (function(){
			function initBootBox(message,action){
				bootbox.dialog(message, [{
		            "label"   : '${StringUtil.wrapString(uiLabelMap.CommonCancel)}',
		            "icon"    : 'fa fa-remove',
		            "class"   : 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {
		            	bootbox.hideAll();
		            }
		        }, {
		            "label"   : '${StringUtil.wrapString(uiLabelMap.CommonSave)}',
		            "icon"    : 'fa-check',
		            "class"   : 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	if(typeof(action) == "function"){
		            		action();
		            	}else return -1;
		           	}
		        }]);
				
			}
			
			
			function checkPayment(total){
				var oneVal = amountNotApplied;
				if(Number(total) !== Number(oneVal)){
					return false;
				}
				return true;
			}
			
			function processDataPayment(data,totalPayment){
				
			}
			
			function saveReceivePayment(){
				if('VND' != accountTwoValue.val()) { //TODO fix me
                    accutils.openJqxWindow($("#quickAddConversionFactor"));
                }
                else {
                    processPaymentData(1);
                }
//				processDataPayment(data,totalPayment);
			}

			function processPaymentData(conversionFactor) {
                var data = {};
                var totalPayment = 0;
                data['orderId'] = '${parameters.orderId?if_exists}';
                <#if orderRoles?exists>
                    data['partyId'] = '${orderRoles[0].partyId?if_exists}';
                </#if>
                data['ntfId'] = getNtfId();
                data['workEffortId'] = '${requestParameters.workEffortId?if_exists}';
                data['isAcc'] = 'isAcc';
                data['conversionFactor'] = conversionFactor;
                var datainformation = grid.jqxGrid('getdatainformation');
                var rowscount = datainformation.rowscount ? datainformation.rowscount : 0 ;
                for(var i = 0 ; i < rowscount;i ++){
                    var dataRows = grid.jqxGrid('getrowdata',i);
                    for(var key in dataRows){
                        if(key.indexOf('orderAmount') != -1){
                            data[dataRows['realName']] = dataRows[key];
                            totalPayment += Number(dataRows[key]);
                        }
                        if(key.indexOf('__reference') != -1){
                            data[key] = dataRows[key];
                        }
                    }
                }
                if(!checkPayment(totalPayment)){
                    var message = '<span class=\"custom-world-form\"><#if orderTypeId == "PURCHASE_ORDER">${uiLabelMap.BACCTotalPaymentSend} <#else>${uiLabelMap.BACCTotalPaymentReceipt} </#if></span><span class=\"acccustom\"> ' + formatcurrency(totalPayment,accountTwoValue.val())  +  ' </span><span class=\"custom-world-form\">${StringUtil.wrapString(uiLabelMap.BACCValuePayable)}</span> <span class=\"acccustom\">' + formatcurrency(Number(replaceReg(Number(replaceReg(accountOneValue.val(),',','.')) - amountApplied,',','.')),accountTwoValue.val()) + '</span><span class=\"custom-world-form\">.${StringUtil.wrapString(uiLabelMap.BACCConfirmsPayment)}</span>';
                    initBootBox(message,function(){
                        sendPayment(data);
                    });
                }else{
                    if(data){
                        initBootBox("${uiLabelMap.BACCConfirmsPayment}",function(){
                            sendPayment(data);
                        });
                    }
                }
            }
			
			function sendPayment(data){
				localStorage.countClick++;
				if(Number(localStorage.countClick) > 1){
					return;
				}
				<#if orderTypeId == "PURCHASE_ORDER">
					data.orderType = "Purchase";
				</#if>
				$.ajax({
					url : 'receiveOfflinePaymentsJSON',
					type : 'POST',
					data : data,
					datatype  :'json',
					async : false,
					success : function(response){
						<#if orderTypeId == "PURCHASE_ORDER">
							window.location.href = 'viewDetailPO?orderId=${parameters.orderId?if_exists}&activeTab=payment-tab';
						<#else>
							window.location.href = 'viewOrder?orderId=${parameters.orderId?if_exists}&activeTab=payment-tab';
						</#if>
					},
					error : function(err){
						bootbox.alert('error when receivei payment cause : ' + err);
					}	
				})
			}
			
			function sendRequest(data){
				$.ajax({
					url  : 'getListReceiptPaymentJSON',
					data : data,
					datatype :'json',
					type :'POST',
					beforeSend : function(){
						$('#info_loader').css('display','block');
					},
					success : function(response){
						initGridReceivePayment(response);
						setTimeout(function(){
							$('#info_loader').css('display','none');
						}, 1000);
					},
					error : function(){
						
					}
				});
			}
			
			var processData = function(data){
				var dataList = [];
				for(var i = 0;i < data.length;i++){
					var dataTmp = {};
					for(var key in data[i]){
						if(key.indexOf('_amount') != -1){
							dataTmp['orderAmount'] = data[i][key];
							dataTmp['realName'] = key;
						}else if(key.indexOf('_reference') != -1){
							dataTmp['orderReference'] = data[i][key];
						}else {
							dataTmp[key] = data[i][key];
						}
					}
					dataList.push(dataTmp);
				}
				return dataList;
			}
			
			var initColumns = function(){
				var columns = [
								{ 'text' : '${uiLabelMap.BACCBankName}','datafield' : 'bankName' ,editable : false,width : 200},
								{'text' : '${uiLabelMap.BACCBankOwner}','datafield' : 'bankOwner',editable : false ,width : 200},
								{'text' : '${uiLabelMap.BACCBankCode}','datafield' : 'bankCode',editable : false,width : 200,cellsrenderer : function(row){		
									var data = grid.jqxGrid('getrowdata',row);
									return '<div class=\"custom-cell-grid2\" style=\"font-weight : bold\">' + data.bankCode + '</div>';
								}},
								{'text' : '${uiLabelMap.BACCBankType}','datafield' : 'bankType' ,editable : false,width : 200},
								{'text' : '${uiLabelMap.BACCAmount}','datafield' : 'orderAmount',columntype  : 'numberinput',cellsrenderer : function(row){
									var data = grid.jqxGrid('getrowdata',row);
									if(Number(data.orderAmount) != 0){
										return '<div class=\"custom-cell-grid2\">' + formatcurrency(data.orderAmount,accountTwoValue.val()) + '</div>';
									}
									return '';
								},createeditor : function(row,column,editor){
									var data = grid.jqxGrid('getrowdata',row);
									editor.jqxNumberInput({digits : 20,decimalDigits : 2,min : 0,max : 999999999999});	
									editor.jqxNumberInput('val', data.orderAmount);
								},
								validation: function (cell, value) {
							        if (value < 0) {
							            return { result: false, message: "${StringUtil.wrapString(uiLabelMap.BACCRequiredValuePaymentNotValid)}" };
							        }
							        return true;
							    },
								},
								{'text' : '${uiLabelMap.BACCOrderReference}','datafield' : 'orderReference',hidden: true,width : 200,columntype  : 'numberinput',createeditor : function(row,column,editor){
									var data = grid.jqxGrid('getrowdata',row);
									editor.jqxNumberInput({digits : 20,decimalDigits : 2,min : 0,max : 9999999999999});	
									editor.jqxNumberInput('val', data.orderReference);
								} ,cellsrenderer : function(row){
									var data = grid.jqxGrid('getrowdata',row);
									if(data){
										return '<span>'+ formatcurrency(data.orderReference,accountTwoValue.val()) + '</span>';
									}
									return '';
								}}
							]
				return columns;
			} 
			
			
			var initDataFields = function(){
				var datafields =  [
									{name : 'bankCode',type : 'string'},
									{name : 'bankName',type : 'string'},
									{name : 'bankOwner',type : 'string'},
									{name : 'bankType',type : 'string'},
									{name : 'orderAmount',type : 'number'},
									{name : 'orderReference',type : 'number'},
									{name : 'realName',type : 'string'}
								];
				
				return datafields;
			} 
			
			function configGrid(source){
				if(grid.jqxGrid('source') !== undefined){
					grid.jqxGrid('source')._source.localdata = source;
					grid.jqxGrid('updatebounddata');
				}
				/*grid.jqxGrid({
					source : dataAdapter,
					editable : true,
					theme : 'olbius',
					autorowheight : true,
					showtoolbar : true,
					pagesize : 4,
					selectionmode: 'multiplecellsadvanced',
					localization: getLocalization(),
					rendertoolbar : function(toolbar){
						var valx = Number(replaceReg(accountOneValue.val(),',','.')) - amountApplied;
						var total = formatcurrency(Number(replaceReg(valx,',','.')));
						var content = $('<div id="headerToolbar" class="widget-header"></div>');
						var header = $('<h4>${uiLabelMap.BACCOrderReceivePayments} : ${uiLabelMap.OrderOrderTotal}&nbsp;<span class=\"acccustom\">&nbsp; '  + total  + '</span></h4>');
						content.append(header);
						toolbar.append(content);
						return toolbar;
					},
					pagesizeoptions:['4'],
					pageable:true,
					autoheight : false,
					height : 300,
					width  : '100%',
					columns : functionUse.initColumns()
				});*/
			}
			
			function initGridReceivePayment(response){
				if(response && response.hasOwnProperty('listIterator')){
					var localdata = response.listIterator ? response.listIterator : null ;
					localdata = processData(localdata);
					if(localdata != null){
						/*var source = {
							datatype : 'json',
							datafields : functionUse.initDataFields(),
							localdata : localdata
						};*/
						/*var dataAdapter  = new $.jqx.dataAdapter(source,{autoBind : true});*/
						configGrid(localdata);
					}
				}
				
			}
			
			return {
				initBootBox : initBootBox,
				replaceReg : replaceReg,
				checkPayment : checkPayment,
				saveReceivePayment : saveReceivePayment,
				sendPayment : sendPayment,
				sendRequest : sendRequest,
				processData : processData,
				processDataPayment : processDataPayment,
				initDataFields : initDataFields,
				initColumns  :initColumns,
				initGridReceivePayment : initGridReceivePayment,
				processPaymentData : processPaymentData
			}
			
		}(grid))
		
		return {
			init : init,
			receivePayment:receivePayment,
			functionUse : functionUse
		}
		
	}())
	
</script>