<#macro jqxTreeGridLib>
	<#--
	<script type="text/javascript" src="../../scripts/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="/jqwidgets/scripts/demos.js"></script>
	-->
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
</#macro>
<#macro jqxTreeGrid id="jqxTreeGrid" srcUrl="" localData="" dataFields="[]" columnList="[]" columnGroups="[]" idKey="id" idKeyParent="parentId" 
	rootDataName="result" parentRow=false columnsResize=false width="100%" height="100%" editable="false" selectionMode="singleRow" expandAll=false theme="energyblue">
	<@jqxTreeGridLib />
	<script type="text/javascript">
        function getTextElementByColor(color) {
            if (color == 'transparent' || color.hex == "") {
                return $("<div style='text-shadow: none; position: relative; padding-bottom: 4px; margin-top: 4px;'>transparent</div>");
            }
            var element = $("<div style='text-shadow: none; position: relative; padding-bottom: 4px; margin-top: 4px;'>#" + color.hex + "</div>");
            var nThreshold = 105;
            var bgDelta = (color.r * 0.299) + (color.g * 0.587) + (color.b * 0.114);
            var foreColor = (255 - bgDelta < nThreshold) ? 'Black' : 'White';
            element.css('color', foreColor);
            element.css('background', "#" + color.hex);
            element.addClass('jqx-rc-all');
            return element;
        }
        $(document).ready(function () {
            var source = {
				dataType: "json",
				dataFields: ${dataFields},
				timeout: 10000,
				<#if localData?has_content && !parentRow>
					hierarchy: {
						root: "${rootDataName}"
					},
					localData: ${localData},
				<#elseif srcUrl?has_content>
					hierarchy: {
	                    keyDataField: { name: '${idKey}' },
	                    parentDataField: { name: '${idKeyParent}' }
	                },
	                id: '${idKey}',
					root: '${rootDataName}',
	                url: "${srcUrl}",
	            <#elseif localData?has_content && parentRow>
		            hierarchy: {
		                keyDataField: { name: '${idKey}' },
		                parentDataField: { name: 'parentId' }
		            },
		            id:'${idKey}',
		            localData: ${localData},
				</#if>
         	};
         	
            var dataAdapter = new $.jqx.dataAdapter(source, {
                loadComplete: function () {
                }
            });
			$.jqx.theme = '${theme}';
			theme = $.jqx.theme;
           	$("#${id}").jqxTreeGrid({
              	source: dataAdapter,
              	theme: theme,
              	width: '${width?string}',
              	height: '${height?string}',
              	altRows: true,
              	autoRowHeight: false,
              	editSettings: { 
              		saveOnPageChange: true, 
              		saveOnBlur: false, 
              		saveOnSelectionChange: true, 
              		cancelOnEsc: true, 
              		saveOnEnter: true, 
              		editSingleCell: true,
              		editOnDoubleClick: true, 
              		editOnF2: true 
              	},
              	editable: ${editable},
              	selectionMode: '${selectionMode}',
              	ready: function () {
              		<#if expandAll>
              			//$("#${id}").jqxTreeGrid('expandAll');
              			expandAllTreeGrid("${id}");
              		<#else>
              			$("#${id}").jqxTreeGrid('expandRow', 1);
              		</#if>
	            },
              	columns: ${columnList},
              	columnGroups: ${columnGroups},
              	columnsResize: '${columnsResize?string}',
          	});
        });
        
        // These methods receive the id of the treeGrid to work with.
        function expandAllTreeGrid(treeGridId) {
           traverseTreeGrid(treeGridId,"expand");
        }
        function collapseAllTreeGrid(treeGridId) {
           traverseTreeGrid(treeGridId,"collapse");
        }
        function traverseTreeGrid(treeGridId, action) {
           var treeGrid = "$(\"#" + treeGridId + "\")";
           var rows = eval(treeGrid).jqxTreeGrid('getRows');
            for(var i = 0; i < rows.length; i++) {
                if (rows[i].records) {
                    if (action == "expand") {
                       eval(treeGrid).jqxTreeGrid('expandRow',rows[i]["${idKey}"]);
                    } else if (action == "collapse") {
                       eval(treeGrid).jqxTreeGrid('collapseRow',rows[i]["${idKey}"]);
                    }
                    traverseTree(treeGrid, rows[i].records, action);
                }
            }
        };
        function traverseTree(treeGrid, rows, action) {
        	for(var i = 0; i < rows.length; i++) {
                if (rows[i].records) {
                    if (action == "expand") {
                       eval(treeGrid).jqxTreeGrid('expandRow',rows[i]["${idKey}"]);
                    } else if (action == "collapse") {
                       eval(treeGrid).jqxTreeGrid('collapseRow',rows[i]["${idKey}"]);
                    }
                    traverseTree(rows[i].records, action);
                }
            }
        }
    </script>
    <div id="container" style="background-color: transparent; overflow: auto;">
    </div>
    <div id="jqxNotification">
        <div id="notificationContent">
        </div>
    </div>
    
    <div id="${id}"></div>
</#macro>

<#global jqxTreeGrid=jqxTreeGrid/>