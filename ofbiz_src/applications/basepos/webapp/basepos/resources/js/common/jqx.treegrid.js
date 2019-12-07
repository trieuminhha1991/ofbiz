var jqxTreeGridObject = (function(){
	var createJqxTreeGrid = function(treeGrid, source, columns, config){
		var dataAdapter = new $.jqx.dataAdapter(source);
		$("#"+ treeGrid).jqxTreeGrid(
                {
                    width: "100%",
                    source: dataAdapter,
                    selectionMode: "multipleRows",
                    columnsResize: false,
                    sortable: true,
                    pageable: true,
                    pagerMode: 'advanced',
                    pageSize: 15,
                    theme: 'olbius',
                    pageSizeOptions: ['5', '10', '15'],
                    columns: columns
                });
		 if(typeof(config) != null && config != null){
			 $("#"+treeGrid).jqxTreeGrid(conifg);
		 }
	};
	var createCustomControlButton = function(treeGrid, container, value){
		var tmpStr = value.split("@");
		var id = treeGrid.attr('id');
		var str = '';
		var group = $('.custom-control-toolbar').length + 1;
        if(tmpStr.length == 4){
            str = '<div class="custom-control-toolbar">'
		+'<a id="customcontrol' + id + group + '" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '">'
		+'<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a></div>';
            container.append(str);
        }else{
			str = '<div class="custom-control-toolbar"><a id="customcontrol' + id + group +'" style="color:#438eb9;" href="' + tmpStr[2] +'">'
				+'<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a></div>';
            container.append(str);
        }
	};
	var createFilterButton = function(treeGrid, container, label){
		var id = treeGrid.attr('id');
		var str = '<button id="filterbutton'+id+'" style="margin-left:20px;"><i class="icon-filter"></i><span>'+label+'</span></button>';
		container.append(str);
        var obj = $('#filterbutton' + id); 
        obj.jqxButton();
        obj.click(function () {
        	treeGrid.jqxtreeGrid('clearfilters');
        });
	};
	return{
		createJqxTreeGrid: createJqxTreeGrid
	}
	
}());