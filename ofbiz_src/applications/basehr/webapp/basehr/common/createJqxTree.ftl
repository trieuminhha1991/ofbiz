
var createJqxTreeDropDownBtn${defaultSuffix?if_exists} = function(jqxTreeEle, dropdownBtnEle, rootPartyArr, suffix, suffixChild, config){
	var dropDownBtnWidth = typeof(config.dropDownBtnWidth) != "undefined"? config.dropDownBtnWidth: 250;
	var dropDownHeight = typeof(config.dropDownBtnHeight) != "undefined"? config.dropDownBtnHeight: 25;
	var treeWidth = typeof(config.treeWidth) != "undefined"? config.treeWidth: 250;
	var async = typeof(config.async) != "undefined"? config.async: true;
	var url = typeof(config.url) != "undefined"? config.url: "getListPartyRelByParent"; 
	dropdownBtnEle.jqxDropDownButton({ width: dropDownBtnWidth, height: dropDownHeight, theme: 'olbius'});
	var dataTreeGroup = new Array();
	var textKey = typeof(config.textKey) != "undefined"? config.textKey : "partyName";
	var valueKey = typeof(config.textKey) != "undefined"? config.valueKey : "partyId";
	var parentKey = typeof(config.parentKey) != "undefined"? config.parentKey : "partyIdFrom";
	for(var i = 0; i < rootPartyArr.length; i++){
		dataTreeGroup.push({
   				"id": rootPartyArr[i][valueKey] + "_" + suffix,
   				"parentid": "-1",
   				"text": rootPartyArr[i][textKey],
   				"value": rootPartyArr[i][valueKey]
   		});
   		dataTreeGroup.push({
   				"id": rootPartyArr[i][valueKey] +"_" + suffixChild,
   				"parentid": rootPartyArr[i][valueKey] + "_" + suffix,
   				"text": "Loading...",
   				"value": url
   		});
	}
	var source =
    {
        datatype: "json",
        datafields: [
            { name: 'id' },
            { name: 'parentid' },
            { name: 'text' },
            { name: 'value' }
        ],
        id: 'id',
        localdata: dataTreeGroup
    };	
	
	var dataAdapter = new $.jqx.dataAdapter(source);
	dataAdapter.dataBind();
	var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
	jqxTreeEle.jqxTree({source: records, width: treeWidth, theme: 'olbius'});
	//jqxTreeEle.jqxTree({source: records});
	createExpandEventJqxTree(jqxTreeEle, config.callbackGetExtData, async, config.expandCompleteFunc, parentKey);
};