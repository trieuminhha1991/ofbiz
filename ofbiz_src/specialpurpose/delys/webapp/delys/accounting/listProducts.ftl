 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
			   
<script type="text/javascript">
				var myVar;
				var sizeSub = 40;
				function showMore(data, id) {
						$("#" + id).jqxTooltip('destroy');
						data = data.trim();
						var dataPart = data.replace("<p>", "");
						dataPart = dataPart.replace("</p>", "");
					    data = "<i onmouseenter='notDestroy()' onmouseleave='destroy(\"" + id + "\")'>" + dataPart + "</i>";
					    $("#" + id).jqxTooltip({ content: data, position: 'right', autoHideDelay: 3000, closeOnClick: false, autoHide: false});
					    myVar = setTimeout(function(){ 
							$("#" + id).jqxTooltip('destroy');
					    }, 2000);
				}
				function notDestroy() {
					clearTimeout(myVar);
				}
				function destroy(id) {
					clearTimeout(myVar);
					myVar = setTimeout(function(){
						$("#" + id).jqxTooltip('destroy');
					}, 2000);
				}
			   function executeMyData(dataShow) {
				   if (dataShow != null) {
					   var datalength = dataShow.length;
				        var dataShowShort = "";
				        if (datalength > sizeSub) {
				        	dataShowShort = dataShow.substr(0, sizeSub) + "...";
						}else {
							dataShowShort = dataShow;
						}
					   return dataShowShort;
				} else {
					 return '';
				}
			   }
			   </script>
	<#assign dataField="[{ name: 'productId', type: 'string'},
				   { name: 'internalName', type: 'string'},
				   { name: 'brandName', type: 'string'},
				   { name: 'productName', type: 'string'},
				   { name: 'description1', type: 'string'},
				   { name: 'description2', type: 'string'},
				   { name: 'weight', type: 'string'},
				   { name: 'description3', type: 'string'},
				   ]"/>

	<#assign columnlist="{ text: '${uiLabelMap.ProductProductId}', datafield: 'productId', width: 200, cellsrenderer:
			       function(row, colum, value){
			        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			        var dataShow = data.productId;
			        var link = 'EditProduct?productId=' + dataShow;
			        return '<span><a href=\"' + link + '\">' + dataShow + '</a></span>';
		        }},
			   { text: '${uiLabelMap.DAInternalName}', datafield: 'internalName', minwidth: 200},
		       { text: '${uiLabelMap.BrandName}', datafield: 'brandName', width: '100px', editable: false},
 		       { text: '${uiLabelMap.ProductName}', datafield: 'productName', width: '200px', editable: false},
			   { text: '${uiLabelMap.description}', datafield: 'description1', minwidth: 250, cellsrenderer:
			       function(row, colum, value){
			        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			        var dataShow = data.description1;
			        var dataShort = executeMyData(dataShow);
			        var id = data.productId;
			        id = id.split('.')[0];
			        return '<span id=\"' + id + '\" onmouseenter=\"showMore(' + \"'\" + dataShow + \"'\" + ',' + \"'\" + id  + \"'\" + ')\" >' + dataShort + '</span>';
		        }},
			   { text: '${uiLabelMap.QuantityUomId}', datafield: 'description2', width: 150},
			   { text: '${uiLabelMap.DAWeight}', datafield: 'weight', width: 120},
			   { text: '${uiLabelMap.WeightUomId}', datafield: 'description3', width: 140},
			   "/>
			   
			   <@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
					showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
					url="jqxGeneralServicer?sname=JQGetListProduct"
				/>
			   <script>
			   $("#jqxgrid").on("columnResized", function (event) 
						{
						    var args = event.args;
						    var dataField = args.datafield;
						    if (dataField == "description1" ||dataField == "description") {
						    	var newWidth = args.newwidth;
						    	sizeSub = 0;
						    	while (newWidth > 10) {
						    		sizeSub += 1;
						    		newWidth -= 7;
							}
						    	$('#jqxgrid').jqxGrid('refreshData');
						}
				});
			   </script>