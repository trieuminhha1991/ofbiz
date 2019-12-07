<div id="jqxwindowbillingAccountId" style="display:none;">
	<div>${uiLabelMap.SelectBillingAccountId}</div>
	<div style="overflow: hidden;">
		<table id="BillingAccountId">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowbillingAccountIdkey" value=""/>
					<input type="hidden" id="jqxwindowbillingAccountIdvalue" value=""/>
					<div id="jqxgridbillingaccountid"></div>
				</td>
			</tr>
		    <tr>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave2" value="${uiLabelMap.CommonSave}"/><input id="alterCancel2" type="button" value="${uiLabelMap.CommonCancel}"/></td>
		    </tr>
		</table>
	</div>
</div>
<@jqGridMinimumLib/>

<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	$("#jqxwindowbillingAccountId").jqxWindow({
        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
    });
    $('#jqxwindowbillingAccountId').on('open', function (event) {
    	var offset = $("#jqxgrid").offset();
   		$("#jqxwindowbillingAccountId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});
	$("#alterSave2").click(function () {
		var tIndex = $('#jqxgridbillingaccountid').jqxGrid('selectedrowindex');
		var data = $('#jqxgridbillingaccountid').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowbillingAccountIdkey').val()).val(data.billingAccountId);
		$("#jqxwindowbillingAccountId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowbillingAccountIdkey').val()).trigger(e);
	});
	// BillingAccount
    var sourceB =
    {
        datafields:
        [
            { name: 'billingAccountId', type: 'string' },
            { name: 'description', type: 'string' },
            { name: 'externalAccountId', type: 'string' }
        ],
        cache: false,
        root: 'results',
        datatype: "json",
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        },
        beforeprocessing: function (data) {
            sourceB.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridbillingaccountid").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridbillingaccountid").jqxGrid('updatebounddata');
        },
        sortcolumn: 'billingAccountId',
		sortdirection: 'asc',
        type: 'POST',
        data: {
	        noConditionFind: 'Y',
	        conditionsFind: 'N',
	    },
	    pagesize:5,
        contentType: 'application/x-www-form-urlencoded',
        url: 'jqxGeneralServicer?sname=JQGetListBillingAccoun',
    };
    var dataAdapterB = new $.jqx.dataAdapter(sourceB,
    {
    	autoBind: true,
    	formatData: function (data) {
    		if (data.filterscount) {
                var filterListFields = "";
                for (var i = 0; i < data.filterscount; i++) {
                    var filterValue = data["filtervalue" + i];
                    var filterCondition = data["filtercondition" + i];
                    var filterDataField = data["filterdatafield" + i];
                    var filterOperator = data["filteroperator" + i];
                    filterListFields += "|OLBIUS|" + filterDataField;
                    filterListFields += "|SUIBLO|" + filterValue;
                    filterListFields += "|SUIBLO|" + filterCondition;
                    filterListFields += "|SUIBLO|" + filterOperator;
                }
                data.filterListFields = filterListFields;
            }
            return data;
        },
        loadError: function (xhr, status, error) {
            alert(error);
        },
        downloadComplete: function (data, status, xhr) {
                if (!sourceB.totalRecords) {
                    sourceB.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
    $('#jqxgridbillingaccountid').jqxGrid(
    {
        width:800,
        source: dataAdapterB,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        autoheight:true,
        pageable: true,
        theme: theme, 
        pagesizeoptions: ['5', '10', '15'],
        ready:function(){
        },
        rendergridrows: function(obj)
		{
			return obj.data;
		},
         columns: [
          { text: '${uiLabelMap.FormFieldTitle_billingAccountId}', datafield: 'billingAccountId'},
          { text: '${uiLabelMap.FormFieldTitle_description}', datafield: 'description'},
          { text: '${uiLabelMap.FormFieldTitle_externalAccountId}', datafield: 'externalAccountId'}
        ]
    });
</script>