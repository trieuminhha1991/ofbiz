<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<div id="jqxWindowPaymentDetail" style="display : none;">
	<div>${StringUtil.wrapString(uiLabelMap.OrderReceiveOfflinePayments)}</div>
	<div id="jqxContent" style="overflow-x : hidden !important;" >
		<div id="container"></div>
		<div id="jqxGridReceivePayment">
		</div>
		<div class="row" style="margin-top : 10px;">
			<div class="span12">
				<div style="position : absolute;right:10px;">
					<button type="button" class='btn btn-primary btn-mini' id="alterSaveReceivePayment"><i class='fa fa-money'></i>&nbsp;${uiLabelMap.DAPayment}</button>
					<button type="button" class='btn btn-danger btn-mini'  id="alterCancel"><i class='fa fa-arrow-left'></i>&nbsp;${uiLabelMap.CommonBack}</button>
				</div>	
			</div>
		</div>
	</div>
</div>
<div id="jqxNotification" style="display: none;">
	<div id="jqxNotificationContent"></div>	
</div>
<@useLocalizationNumberFunction />	
<#assign orderRoles = delegator.findByAnd("OrderRole",Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId","${parameters.orderId}","roleTypeId","BILL_FROM_VENDOR"))/>
<script type="text/javascript">
	$(document).ready(function(){
		$("#jqxWindowPaymentDetail").jqxWindow({
	       maxWidth : 1100,width: 1050, height : 300,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
	    });
	    $("#jqxWindowPaymentDetail").bind('close',function(){
	    	$('#info_loader').css('display','none');
	    })
	})
	
	function saveReceivePayment(){
			var data = {};
				data['orderId'] = '${parameters.orderId?if_exists}';
				data['partyId'] = '${orderRoles[0].partyId?if_exists}';
				data['workEffortId'] = '${requestParameters.workEffortId?if_exists}';
				var datainformation = $('#jqxGridReceivePayment').jqxGrid('getdatainformation');
				var rowscount = datainformation.rowscount;
				for(var i = 0 ; i < rowscount;i ++){
					var dataRows = $('#jqxGridReceivePayment').jqxGrid('getrowdata',i);
					for(var key in dataRows){
						if(key.indexOf('orderAmount') != -1){
							data[dataRows['realName']] = dataRows[key];
						}
						if(key.indexOf('__reference') != -1){
							data[key] = dataRows[key];
						}
					}
					
				}
				if(data){
					$.ajax({
						url : 'receiveOfflinePaymentsJSON',
						type : 'POST',
						data : data,
						datatype  :'json',
						async : false,
						success : function(response){
							if(!response._ERROR_MESSAGE_ && !response._ERROR_MESSAGE_LIST_){
								window.location.href = 'orderView?orderId=' + '${parameters.orderId?if_exists}';
							}else window.location.href = 'receivePayment?orderId=' + '${parameters.orderId?if_exists}';
						},
						error : function(){
						
						}	
					})
				}
		
			}
		
	  $('#alterSaveReceivePayment').bind('click',function(){
	    	saveReceivePayment();
	    });
	    
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
				setTimeout(10000,function(){
					$('#info_loader').css('display','none');
				})	
			},
			error : function(){
				
			}
		});
	}
	
	function receivePayment(url) {
		//var strUrl = url + "&avo=" + $("#accountOneValue").val() + "&avt=" + $("#accountTwoValue").val();
		var data = {
			orderId : '${parameters.orderId?if_exists}',
			avo : $("#accountOneValue").val(),
			avt : $("#accountTwoValue").val()
		}
		if(data){
			sendRequest(data);
		}
		$("#jqxWindowPaymentDetail").jqxWindow('open');
		//window.location.href = strUrl;
	}
	
	var processData = function(data){
		var dataList = [];
		for(var i = 0;i < data.length;i++){
			var dataTmp = {};
			for(var key in data[i]){
				if(key.indexOf('_amount') != -1){
					dataTmp['orderAmount'] = data[i][key];
					dataTmp['realName'] = key;
				}else if(key.indexOf('__reference') != -1){
					dataTmp['orderReference'] = data[i][key];
				}else {
					dataTmp[key] = data[i][key];
				}
			}
			dataList.push(dataTmp);
		}
		return dataList;
	}
	
	function initGridReceivePayment(response){
		if(response && response.hasOwnProperty('listIterator')){
			var localdata = response.listIterator ? response.listIterator : null ;
			localdata = processData(localdata);
			if(localdata != null){
				var source = {
					datatype : 'json',
					datafields : [
						{name : 'bankCode',type : 'string'},
						{name : 'bankName',type : 'string'},
						{name : 'bankOwner',type : 'string'},
						{name : 'bankType',type : 'string'},
						{name : 'orderAmount',type : 'number'},
						{name : 'orderReference',type : 'number'},
						{name : 'realName',type : 'string'}
					],
					localdata : localdata
				};
				var dataAdapter  = new $.jqx.dataAdapter(source,{autoBind : true});
				$('#jqxGridReceivePayment').jqxGrid({
					source : dataAdapter,
					editable : true,
					theme : 'olbius',
					autorowheight : true,
					showtoolbar : true,
					localization: getLocalization(),
					rendertoolbar : function(toolbar){
						
					},
					pagesizeoptions:['5', '10', '15'],
    				pageable:true,
					autoheight : true,
					width  : '100%',
					columns : [
						{ 'text' : '${uiLabelMap.BankName}','datafield' : 'bankName' ,editable : false,width : 200},
						{'text' : '${uiLabelMap.BankOwner}','datafield' : 'bankOwner',editable : false ,width : 200},
						{'text' : '${uiLabelMap.BankCode}','datafield' : 'bankCode',editable : false,width : 200,cellsrenderer : function(row){		
							var data = $('#jqxGridReceivePayment').jqxGrid('getrowdata',row);
							return '<div class=\"custom-cell-grid2\" style=\"font-weight : bold\">' + data.bankCode + '</div>';
						}},
						{'text' : '${uiLabelMap.BankType}','datafield' : 'bankType' ,editable : false,width : 200},
						{'text' : '${uiLabelMap.OrderAmount}','datafield' : 'orderAmount',columntype  : 'numberinput',cellsrenderer : function(row){
							var data = $('#jqxGridReceivePayment').jqxGrid('getrowdata',row);
							if(data.orderAmount){
								return '<div class=\"custom-cell-grid2\">' + convertLocalNumber(data.orderAmount) + '</div>';
							}
							return '';
						},createeditor : function(row,column,editor){
							var data = $('#jqxGridReceivePayment').jqxGrid('getrowdata',row);
							editor.jqxNumberInput({digits : 20,decimalDigits : 2,min : 0,max : 999999999999});	
							editor.jqxNumberInput('val', data.orderAmount);
						} },
						{'text' : '${uiLabelMap.OrderReference}','datafield' : 'orderReference',hidden: true,width : 200,columntype  : 'numberinput',createeditor : function(row,column,editor){
							var data = $('#jqxGridReceivePayment').jqxGrid('getrowdata',row);
							editor.jqxNumberInput({digits : 20,decimalDigits : 2,min : 0,max : 9999999999999});	
							editor.jqxNumberInput('val', data.orderReference);
						} ,cellsrenderer : function(row){
							var data = $('#jqxGridReceivePayment').jqxGrid('getrowdata',row);
							if(data.orderReference){
								return '<span>'+ convertLocalNumber(data.orderReference) + 'đ </span>';
							}
							return '';
						}}
					]
				});
			}
		}
		
	}
</script>