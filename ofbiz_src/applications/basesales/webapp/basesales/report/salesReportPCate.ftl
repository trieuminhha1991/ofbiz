<script type="text/javascript">
	<#assign category = delegator.findByAnd("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryTypeId", "CATALOG_CATEGORY"), null, false)>
	var category = [
	    <#list category as categoryL>
	    {
	    	productCategoryId : "${categoryL.productCategoryId}",
	    	categoryName: "${StringUtil.wrapString(categoryL.get("categoryName", locale))}"
	    },
	    </#list>	
	];
	
	var listCategoryDataSource = [];
  	for(var x in category){
    	var categoryDataSource = {
     		text: category[x].categoryName,
     		value: category[x].productCategoryId,
    	}
    listCategoryDataSource.push(categoryDataSource);
   	} 
</script>
<script id="test">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.KRevenueProductChannel)}',
            columns: [
                { text: '${StringUtil.wrapString(uiLabelMap.KProductName)}', datafield: 'productName', type: 'string', width: '40%'},
                { text: '${StringUtil.wrapString(uiLabelMap.KQuantity)}', datafield: 'quantity1', type: 'number', width: '20%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.KTotal)}', datafield: 'total1', type: 'number', width: '20%', cellsformat: 'f0', cellsalign: 'right'}
            ]
        };

        var configPopup = [
            {
                action : 'addDropDownList',
                params : [{
                    id : 'organization',
                    label : 'organization',
                    data : [{text: '${StringUtil.wrapString(uiLabelMap.olap_true)}', value: 'true'}, {text: '${StringUtil.wrapString(uiLabelMap.olap_false)}', value: 'false'}],
                    index: 1
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'dateType',
                    label : 'dateType',
                    data : date_type_source,
                    index: 2
                }]
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
                }],
                before: 'thru_date'
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'thru_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                    value: OLBIUS.dateToString(cur_date)
                }],
                after: 'from_date'
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'category',
                    label : '${StringUtil.wrapString(uiLabelMap.KCategory)}',
                    data : listCategoryDataSource,
                    index: 0
                }]
            }
        ];


        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateSalesOlapGridByCategory', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'orig': oLap.val('organization'),
                'dateType': oLap.val('dateType'),
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'category': oLap.val('category')
            }, oLap.val('dateType'));
        });

        testGrid.init(function () {
            testGrid.runAjax();
        },function(oLap){
        	if(oLap){
        		var dateTypeInput = oLap.val('dateType');
            	var fromDateInput = oLap.val('from_date');
            	var thruDateInput = oLap.val('thru_date');
            	var categoryInput = oLap.val('category');
            	var dateFromDate = new Date(fromDateInput);
            	var dateThruDate = new Date(thruDateInput);
            	var dateFrom = dateFromDate.getTime();
            	var thruFrom = dateThruDate.getTime();
            	
            	window.location.href = "exportSalesReportPCTo.pdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&category=" + categoryInput;
        	}else{
        		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
        		    "class" : "btn-small btn-primary width60px",
        		    }]
        		   );
        	}
        }
        );
    });
</script>