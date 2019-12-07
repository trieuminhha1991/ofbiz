<div id="jqxwindowAgreementViewer" style="display:none;">
	<div>${uiLabelMap.listAgreement}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
			<div class="span12 margin-top10">
	 			<div id='jqxAgreementViewer'></div>
			</div>
		</div>
		<div class="row-fluid">
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;width:99%;">
 			<div class="span12 margin-top10 no-left-margin">
 				<button id='alterCancelAgreementViewer' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.ImportClose}</button>
	        </div>
	    </div>
	</div>
</div>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false)>
<script>
	var listStatusItem = [
	              		<#if listStatusItem?exists>
	          				<#list listStatusItem as item>
	          				{
	          					statusId: '${item.statusId?if_exists}',
	          					description: '${StringUtil.wrapString(item.get("description", locale)?if_exists)}'
	          				},
	          				</#list>
	  					</#if>
	          	];
	var mapStatusItem = {
			<#if listStatusItem?exists>
				<#list listStatusItem as item>
					'${item.statusId?if_exists}': '${StringUtil.wrapString(item.get("description", locale)?if_exists)}',
				</#list>
			</#if>
	};
	$("#jqxwindowAgreementViewer").jqxWindow({theme: 'olbius',
	    width: 900, maxWidth: 1845, height: 455, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAgreementViewer"), modalOpacity: 0.7
	});
	$('#jqxwindowAgreementViewer').on('close', function (event) {
		bindDataAgreementViewer([]);
	});
	function loadDataAgreementViewer(productPlanId, callback) {
		var listAgreement = [];
		$.ajax({
	        url: "getDataAgreementAjax",
	        type: "POST",
	        data: {productPlanId: productPlanId},
	        async: false,
	        success : function(res) {
	        	listAgreement = res["listAgreement"];
			}
	    }).done(function() {
	    	callback(listAgreement);
	    });
	}
	function bindDataAgreementViewer(listAgreement) {
		for ( var x in listAgreement) {
			if (typeof listAgreement[x].fromDate == 'object') {
				listAgreement[x].fromDate?listAgreement[x].fromDate = listAgreement[x].fromDate['time']:listAgreement[x].fromDate;
				listAgreement[x].thruDate?listAgreement[x].thruDate = listAgreement[x].thruDate['time']:listAgreement[x].thruDate;
			}
 		}
		var sourceAgreement =
        {
            localdata: listAgreement,
            datatype: "local",
            datafields:
            [
                { name: 'agreementId', type: 'string' },
                { name: 'statusId', type: 'string' },
                { name: 'fromDate', type: 'date', other: 'Timestamp'},
                { name: 'thruDate', type: 'date', other: 'Timestamp'},
                { name: 'description', type: 'string' }
            ]
        };
        var dataAdapterAgreement = new $.jqx.dataAdapter(sourceAgreement);
        $("#jqxAgreementViewer").jqxGrid({
            source: dataAdapterAgreement,
            width: '100%',
            theme: 'olbius',
            pageable: true,
            height: 337,
            pagesize: 10,
            sortable: true,
            showfilterrow: true,
            filterable: true,
            localization: getLocalization(),
            selectionmode: 'singlerow',
            columns: [
              { text: '${uiLabelMap.AgreementId}', dataField: 'agreementId', align: 'center', width: 150},
              { text: '${uiLabelMap.AgreementName}', dataField: 'description', align: 'center'},
              { text: '${uiLabelMap.statusId}', dataField: 'statusId', align: 'center', filtertype: 'checkedlist', width: 180,
            	  cellsrenderer: function(row, colum, value){
            		  value?value=mapStatusItem[value]:value;
            		  return '<span>' + value + '</span>';
				  },createfilterwidget: function (column, htmlElement, editor) {
  		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listStatusItem), displayMember: 'statusId', valueMember: 'statusId',
                        renderer: function (index, label, value) {
                        	if (index == 0) {
                        		return value;
							}
						    return mapStatusItem[value];
		                }
		        	});
  		        	editor.jqxDropDownList('checkAll');
				}
              },
              { text: '${uiLabelMap.fromDate}', dataField: 'fromDate', align: 'center', width: 150, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' },
              { text: '${uiLabelMap.thruDate}', dataField: 'thruDate', align: 'center', width: 150, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'center' }
            ],
            handlekeyboardnavigation: function (event) {
                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                if (key == 70 && event.ctrlKey) {
                	$('#jqxAgreementViewer').jqxGrid('clearfilters');
                	return true;
                }
		 	}
        });
	}
	function fixSelectAll(dataList) {
    	var sourceST = {
		        localdata: dataList,
		        datatype: "array"
	    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
        var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
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
        return localizationobj;
    }
</script>