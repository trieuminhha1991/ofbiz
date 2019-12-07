<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<#assign dataField="[{ name: 'productId', type: 'string'},
				   { name: 'internalName', type: 'string'},
				   { name: 'description', type: 'string'},
				   { name: 'quantityUom', type: 'string'},
				   { name: 'weight', type: 'string'},
				   { name: 'weightUom', type: 'string'},
				   { name: 'thruDateQA', type: 'date', other: 'Timestamp'},
				   { name: 'expireDateQA', type: 'string'},
				   { name: 'expriseType', type: 'string'}
				   ]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', width: 170, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.DAInternalName)}', datafield: 'internalName', width: 200, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.description)}', datafield: 'description', minwidth: 250, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.QuantityUomId)}', datafield: 'quantityUom', width: 120, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.ProductWeight)}', datafield: 'weight', width: 80, align: 'center', cellsalign: 'right'},
			   { text: '${StringUtil.wrapString(uiLabelMap.WeightUomId)}', datafield: 'weightUom', width: 120, align: 'center'},
			   { text: '${uiLabelMap.thruDateOfPubich}', datafield: 'thruDateQA', width: 180, filtertype: 'range', cellsformat: 'dd/MM/yyyy', align: 'center',
				   cellsrenderer: function(row, colum, value){
			        	return excuteDate(value);
				   }
			   }
			   "/>
			   
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=JQGetListProductQA"
		contextMenuId="contextMenu" mouseRightMenu="true"
	/>
<div id="myMenuPlace"></div>
<div id="myWindows"></div>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id="ViewDetailsProduct"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewDetailsProduct}</li>
		<li id="CreateQuality"><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.CreateQuality}</li>
	</ul>
</div>

<style>
   .light {
	     width: 4px;
	     height: 25px;
	     margin: 1px 1px;
	     text-align: center;
	     font-size: 15px;
	     float: left;
	     border-radius: 0%;
	   }
   .danger { background-color: red; }
   .warn { background-color: yellow; }
   .good { background-color: green; }
</style>
<script>
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 220, height: 56, autoOpenPopup: false, mode: 'popup'});
		$("#jqxgrid").on('contextmenu', function () {
		    return false;
		});
		$("#ViewDetailsProduct").on("click", function() {
			rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
	    	var productId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "productId");
	    	window.location.href = "EditProduct?productId=" + productId;
		});
		$("#CreateQuality").on("click", function() {
			rowIndexEditing = $('#jqxgrid').jqxGrid('getSelectedRowindex');
			var productId = $('#jqxgrid').jqxGrid('getcellvalue', rowIndexEditing, "productId" );
			window.location.href = "CreateProductQuality?productId=" + productId;
		});
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
  		function showMenu(event, productId) {
  			var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            var myMenu = "<div id='jqxMenu'><ul>" +
            			"<li><a href='EditProduct?productId=" + productId + "''>${uiLabelMap.EditDetailsProduct}</a></li>" +
            			"<li><a href='CreateProductQuality?productId=" + productId + "''>${uiLabelMap.CreateQuality}</a></li>" +
            			"<ul></div>";
            $("#myMenuPlace").html(myMenu);
            $("#jqxMenu").jqxMenu({ width: '190px', height: '60px', autoOpenPopup: false, mode: 'popup'});
  			$("#jqxMenu").jqxMenu('open', parseInt(event.clientX) + 10 + scrollLeft, parseInt(event.clientY) + 2 + scrollTop);
  			$('#jqxMenu').on('closed', function () {
  				$('#jqxMenu').jqxMenu('destroy');
  			});
		}
  		function fnEditProduct(productId) {
        	var wd = "";
        	wd += "<div id='window01'><div>${uiLabelMap.AgreementScanFile}</div><div>";
        	
        	wd += "<div id='myContent'>aaa</div>";
        	
        	wd += "</div></div>";
        	$("#myWindows").html(wd);
        	$('#window01').jqxWindow({ height: 450, width: 700, maxWidth: 1200, isModal: true, modalOpacity: 0.7 });
        	$('#window01').on('close', function (event) {
            	 $('#window01').jqxWindow('destroy');
        	});
        	jQuery.ajax({
                url: "EditProduct",
                type: "POST",
                data: {productId: productId},
                success: function(res) {
                	$("#myContent").html(res);
                }
            })
		}
  		function getContent(productId, url) {
  			jQuery.ajax({
                url: url,
                type: "POST",
                data: {productId: productId},
                success: function(res) {
                	$("#myContent").html(res);
                }
            })
		}
  		var now;
  		$(document).ready(function(){
  			setInterval(function(){
//			  				$(".light").fadeToggle(400);
  			}, 600);
  			now = new Date().getTime();
  		});
  		function excuteDate(value) {
  			value = value.toString().toMilliseconds();
  			var leftTime;
  			if (value == "") {
				return '<span title=\'${uiLabelMap.QuantityPublicationNotAvalible}\'><div class=\'light danger\' style=\'float:right;margin-top: -4px;margin-right: -4px\'></div></span>';
			}else {
				leftTime = value - now;
				leftTime = Math.ceil(leftTime/86400000);
				if (leftTime > 0 && leftTime < 10) {
					return "<span title='${uiLabelMap.QuantityPublicationHas} "+ leftTime + " ${uiLabelMap.expiring}'>" + value.toDateTime().toTimeOlbius() + '<div class=\'light warn\' style=\'float:right;margin-top: -4px;margin-right: -4px\'></div></span>';
				}
				if (leftTime <= 0 ) {
					return '<span title=\'${uiLabelMap.QuantityPublicationExpired}\'>' + value.toDateTime().toTimeOlbius() + '<div class=\'light danger\' style=\'float:right;margin-top: -4px;margin-right: -4px\'></div></span>';
				}
			}
  			return "<span title='${uiLabelMap.QuantityPublicationHas} "+ leftTime + " ${uiLabelMap.expiring}'>" + value.toDateTime().toTimeOlbius() + '<div class=\'light good\' style=\'float:right;margin-top: -4px;margin-right: -4px\'></div></span>';
		}
</script>