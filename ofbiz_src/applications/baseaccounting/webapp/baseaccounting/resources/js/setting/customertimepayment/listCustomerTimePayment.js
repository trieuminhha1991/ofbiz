var viewListCustomerTimePaymentObj = (function(){
	var init = function(){
	    initInput();
		initEvent();
	};
	var initInput = function() {
        accutils.createJqxDropDownList($("#enumPartyTypeId"), globalVar.enumPartyTypeArr, {valueMember: 'enumId', displayMember: 'description', width: '96%', height: 25});

        $("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });

        var customerWidth = '96%';
        $("#partyId").jqxDropDownButton({
            width: customerWidth,
            height: 25,
            theme: 'olbius',
            dropDownHorizontalAlignment: 'right'
        });
        var datafield = [
            {name: 'partyId', type: 'string'},
            {name: 'partyCode', type: 'string'},
            {name: 'fullName', type: 'string'}
        ];
        var columns = [
            {text: uiLabelMap.BACCOrganizationId, datafield: 'partyCode', width: '30%'},
            {text: uiLabelMap.BACCFullName, datafield: 'fullName'}
        ];

        var config = {
            width: '100%',
            virtualmode: true,
            showfilterrow: true,
            pageable: true,
            sortable: true,
            filterable: true,
            editable: false,
            url: '',
            showtoolbar: false,
            source: {
                pagesize: 5,
            }
        };
        Grid.initGrid(config, datafield, columns, null, $("#partyGrid"));
    };

	var initEvent = function(){
        $("#partyGrid").on('rowclick', function(event){
            var args = event.args;
            var row = $("#partyGrid").jqxGrid('getrowdata', args.rowindex);
            var dropDownContent = '<div class="innerDropdownContent">' + row['fullName'] + ' [' + row.partyCode + '] ' + '</div>';
            $("#partyId").jqxDropDownButton('setContent', dropDownContent);
            $("#partyId").jqxDropDownButton('close');
            accutils.setAttrDataValue('partyId', row.partyId);
        });
	};
	return{
		init: init
	}
}());
$(document).ready(function () {
    viewListCustomerTimePaymentObj.init();
});