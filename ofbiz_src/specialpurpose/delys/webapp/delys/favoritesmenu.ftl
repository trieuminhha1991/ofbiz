<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdragdrop.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
    	// tree
        var data = ${StringUtil.wrapString(context.listFuncs?if_exists)};
        var dataList = ${StringUtil.wrapString(context.listUserFuncs?if_exists)};
        var source =
        {
            datatype: "json",
            datafields: [
                { name: 'funcId' },
                { name: 'name' }
            ],
            id: 'funcId',
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        //dataAdapter.dataBind();
        //var records = dataAdapter.getRecordsHierarchy('funcId', 'parentFuncId', 'items', [{ name: 'name', map: 'label'}]);
        $('#jqxTreeMenu').jqxListBox({ source: dataAdapter, displayMember: "name", valueMember: "funcId", width: '300px', allowDrop: false, allowDrag: true, dragStart: function (item) 
        	{
	            for(i=0; i < dataList.length; i++){
	            	if(dataList[i].funcId == item.value){
	            		return false;
	            	}
	            }
        	}, renderer: function (index, label, value){
	            var datarecord = data[index];
	            for(i=0; i < dataList.length; i++){
	            	if(dataList[i].funcId == datarecord.funcId){
	            		return "<span style='color:red'>" + datarecord.name + "</span>";
	            	}
	            }
	            return datarecord.name;
        	}, dragEnd: function (dragItem, dropItem) {
        	}
        });
        
        // list
        
        var sourceList =
        {
            datatype: "json",
            datafields: [
                { name: 'name' },
                { name: 'funcId' }
            ],
            id: 'funcId',
            localdata: dataList 
        };
        var dataAdapterList = new $.jqx.dataAdapter(sourceList);
        $("#jqxListMenu").jqxListBox({ source: dataAdapterList, displayMember: "name", valueMember: "funcId", width: 200, height: 250, allowDrag: true, allowDrop: true});
        var tmpLength = 0;
        $("#jqxTreeMenu").on('dragStart', function (event) {
        	tmpLength = $("#jqxListMenu").jqxListBox('getItems').length;
        });
    	$("#jqxTreeMenu").on('dragEnd', function (event) {
    		if((tmpLength + 1) == $("#jqxListMenu").jqxListBox('getItems').length){
	        	dataList.push({funcId: event.args.value, name: event.args.label});
	        	data.push({funcId: event.args.value, name: event.args.label});
	        	$('#jqxTreeMenu').jqxListBox('refresh');
    		}
        });
    });
</script>
<div id="jqxTreeMenu"></div>
<div id="jqxListMenu"></div>