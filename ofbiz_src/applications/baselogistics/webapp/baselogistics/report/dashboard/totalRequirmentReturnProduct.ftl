<link rel="stylesheet" type="text/css" href="../../logresources/css/reportStyle.css" />
<script>
function formatnumber(num){
    if(num == null){
        return "";
    }
    decimalseparator = ",";
    thousandsseparator = ".";

    var str = num.toString(), parts = false, output = [], i = 1, formatted = null;
    if(str.indexOf(".") > 0) {
        parts = str.split(".");
        str = parts[0];
    }
    str = str.split("").reverse();
    var c;
    for(var j = 0, len = str.length; j < len; j++) {
        if(str[j] != ",") {
        	if(str[j] == '-'){
        		if(output && output.length > 1){
        			if(output[output.length - 1] == '.'){
        				output.splice(output.length - 1,1);
        			}
            		c = true;
            		break;
        		}
        	} 
            output.push(str[j]);
            if(i%3 == 0 && j < (len - 1)) {
            	output.push(thousandsseparator);
            }
            i++;
        }
    }
    if(c) output.push("-");
    formatted = output.reverse().join("");
    return(formatted);
};

<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ORDER_RETURN_STTS"), null, null, null, false)>
var listStatusItems = [
	<#if statusItems?exists>
		<#list statusItems as item>
			{
				statusId: "${item.statusId?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
			},
		</#list>
	</#if>
];

var mapStatusItemData = {
	<#if statusItems?exists>
		<#list statusItems as item>
			<#assign s1 = StringUtil.wrapString(item.get("statusId", locale)?if_exists)/>
			"${item.description?if_exists}": "${s1}",
		</#list>
	</#if>	
};
</script>
<div>
	<div class='titleTextReturn'>
		<i class='fa-history'></i> <lable>${uiLabelMap.Return}</lable>
	</div>
	<div class="valueTotal">
		<div class='form-window-container'>
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span9" style="text-align: left; word-wrap: break-word;">
							<label style="cursor: auto;"> ${uiLabelMap.BLReqImProductReturn}: </label>
						</div>
						<div class="span3" style="text-align: right; word-wrap: break-word;" >
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick='showListProductReturn("BLReqImProductReturn")'>
								<div class="productReturn"></div>
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$( document ).ready(function() {
				$.ajax({
					url: 'getTotalCustomerReturnProduct',
				    type: 'post',
				    async: false,
				    data: {checkTime: "BLReqImProductReturn"},
				    success: function(data) {
				    	var productReturnCount = data.productReturnCount;
				    	$(".productReturn").html(formatnumber(productReturnCount));	
				    },
				    error: function(data) {
				    	alert('Error !!');
				    }
				});
			});
		</script>
	</div>
	
	<div class="valueTotal">
		<div class='form-window-container'>
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">	
						<div class="span9" style="text-align: left; word-wrap: break-word;">
							<label style="cursor: auto;"> ${uiLabelMap.BLReqExpectImProductReturnToday}: </label>
						</div>
						<div class="span3" style="text-align: right; word-wrap: break-word;" >
							<a style="text-decoration: underline !important; color: #333;" href="javascript:void(0)" title="${uiLabelMap.ViewDetail}" onclick='showListProductReturn("BLReqExpectImProductReturnToday")'>
								<div class="productReturnToday"></div> 
							</a>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$( document ).ready(function() {
				$.ajax({
					url: 'getTotalCustomerReturnProduct',
				    type: 'post',
				    async: false,
				    data: {checkTime: "BLReqExpectImProductReturnToday"},  
				    success: function(data) {
				    	var productReturnCount = data.productReturnCount;
				    	$(".productReturnToday").html(formatnumber(productReturnCount));	
				    },
				    error: function(data) {
				    	alert('Error !!');
				    }
				});
			});
		</script>
	</div>
</div>


<div id="alterpopupWindowCustomerReturn" class="hide">
	<div class="row-fluid">
		${uiLabelMap.ListTranferRequireToday}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span12">
				<div id="jqxgridProductReturn">
	            </div>
			</div>
	    </div>
		<div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="addButtonCancelRe" class='btn btn-danger form-action-button pull-right' style="margin-right:10px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>

<script>
	$("#alterpopupWindowCustomerReturn").jqxWindow({
		maxWidth: 700, minWidth: 550, height:450, width:1000, minHeight: 100, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancelRe"), modalOpacity: 0.7, theme:'olbius'           
	});
	
	function showListProductReturn(valueProReturn){
		if(valueProReturn == "BLReqImProductReturn"){   
			$('#alterpopupWindowCustomerReturn').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.BLReqListReqReturn)}');
		}
		if(valueProReturn == "BLReqExpectImProductReturnToday"){
			$('#alterpopupWindowCustomerReturn').jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.BLReqListReqReturnToday)}');
		}
		viewListProductCustomerReturn(valueProReturn);   
	}
	
	function viewListProductCustomerReturn(valueProReturn){
		$.ajax({
			url: "loadListProductCustomerReturn", 
			type: "POST",
			data: {valueCheck: valueProReturn},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var listProductReturn = data["listProductReturn"];
			for(var i in listProductReturn){
				var entryDate = listProductReturn[i].entryDate;
				if(entryDate){
					listProductReturn[i].entryDate = entryDate.time;
				}
			}
			bindingDataProductReturn(listProductReturn);
		});
	}
	
	function bindingDataProductReturn(listProductReturn){
		var sourceOrderItem =
		{
		     datafields:[
		                 {name: 'returnId', type: 'string'},
		                 {name: 'statusId', type: 'string'},
		                 {name: 'entryDate', type: 'date', other: 'Timestamp'},
		                 {name: 'viewDetail', type: 'string'},
		 				],
		     localdata: listProductReturn,
		     datatype: "array",
		}; 
		
		var dataAdapter = new $.jqx.dataAdapter(sourceOrderItem);
		$("#jqxgridProductReturn").jqxGrid({
		     source: dataAdapter,
		     filterable: true,
		     showfilterrow: true,
		     theme: 'olbius',
		     autoheight:false,
		     pageable: true, 
		     localization: getLocalization(),
		     sortable: true,
		     pagesize: 10,
		     width: '100%',
		     height: 340,
		     columns: [  
	                {text: '${uiLabelMap.ReturnId}', datafield: 'returnId', width: '200',
	                },
	                { text: '${uiLabelMap.RequiredByDate}', dataField: 'entryDate', cellsformat: 'dd/MM/yyyy', filtertype:'range',
	                	cellsrenderer: function(row, colum, value) {
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (typeof(data) != 'undefined') {
								var returnStr = '<span>';
								returnStr += jOlbUtil.dateTime.formatFullDate(data.entryDate);
								returnStr += '</span>';
								return returnStr;
							}
						}
	                },
	                {text: '${uiLabelMap.BLDetail}', datafield: 'viewDetail', width: '200', filterable: false, sortable: false,
	                	cellsrenderer: function(row, colum, value) {
                			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
                			var returnId = data.returnId;
                			return '<span><a target=\"_blank\" href=\"' + 'getDetailCustomerReturn?returnId=' + returnId + '\"><i class=\"fa fa-eye\"></i>' + '${uiLabelMap.BLDetail}' + '</a></span>';
						}
	                },
	     		  ] 
		});
	
		$('#alterpopupWindowCustomerReturn').jqxWindow('open');
	}
	
	var getLocalization = function () {
		var localizationobj = {};
		localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
		localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
		localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
		localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
		localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
		localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
		localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
		localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
		localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
		localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
		localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
		localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
		localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
		localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
		localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
		return localizationobj;
	};
</script>
