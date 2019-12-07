<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript">
	var myVar;
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
	        if (datalength > 40) {
	        	dataShowShort = dataShow.substr(0, 40) + "...";
			}else {
				dataShowShort = dataShow;
			}
		   return dataShowShort;
	} else {
		 return '';
		}
	   }
	   var cellclassname = function (row, column, value, data) {
	        var val = $('#jqxgrid').jqxGrid('getcellvalue', row, "thruDate");
	    value = value.toString().toMilliseconds();
		var leftTime;
		if (value == "") {
			return "danger";
		}else {
			leftTime = value - now;
			leftTime = Math.ceil(leftTime/86400000);
			if (leftTime > 0 && leftTime < 10) {
				return "warn";
			}
			if (leftTime <= 0 ) {
				return "danger";
			}
		}
		return "good";
    }
</script>
<#assign dataField="[{ name: 'productId', type: 'string'},
				   { name: 'internalName', type: 'string'},
				   { name: 'description1', type: 'string'},
				   { name: 'description2', type: 'string'},
				   { name: 'weight', type: 'string'},
				   { name: 'description3', type: 'string'},
				   { name: 'thruDate',  type: 'date', other: 'Timestamp'},
				   { name: 'expireDate', type: 'string'}
				   ]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', width: 170, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.DAInternalName)}', datafield: 'internalName', width: 200, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.description)}', datafield: 'description1', minwidth: 250, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.QuantityUomId)}', datafield: 'description2', width: 120, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.ProductWeight)}', datafield: 'weight', width: 80, align: 'center', cellsalign: 'right'},
			   { text: '${StringUtil.wrapString(uiLabelMap.WeightUomId)}', datafield: 'description3', width: 120, align: 'center'},
			   { text: '${StringUtil.wrapString(uiLabelMap.expriseType)}', datafield: 'expriseType', width: 120, align: 'center'},
			   { text: '${uiLabelMap.thruDateOfPubich}', datafield: 'thruDate', width: 180, filtertype: 'range', cellsformat: 'dd/MM/yyyy', align: 'center', cellclassname: cellclassname,
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
		<li><i class="icon-edit">&nbsp;&nbsp;${uiLabelMap.EditDetailsProduct}</li>
		<li><i class="icon-plus"></i>&nbsp;&nbsp;${uiLabelMap.CreateQuality}</li>
	</ul>
</div>
			   
<style>
   .danger { background-color: #f2dede!important; }
   .warn { background-color: #fcf8e3!important; }
   .good { background-color: #dff0d8!important; }
</style>
<script>
		var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 170, height: 84, autoOpenPopup: false, mode: 'popup'});
		$("#jqxgrid").on('contextmenu', function () {
		    return false;
		});
		
  		var myEvent;
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
				return '<span title=\'${uiLabelMap.QuantityPublicationNotAvalible}\'><div></div></span>';
			}else {
				leftTime = value - now;
				leftTime = Math.ceil(leftTime/86400000);
				if (leftTime > 0 && leftTime < 10) {
					return "<span title='${uiLabelMap.QuantityPublicationHas}"+ leftTime + " ${uiLabelMap.expiring}'>" + value.toDateTime().toTimeOlbius() + '</span>';
				}
				if (leftTime <= 0 ) {
					return '<span title=\'${uiLabelMap.QuantityPublicationExpired}\'>' + value.toDateTime().toTimeOlbius() + '</span>';
				}
			}
  			return "<span title='${uiLabelMap.QuantityPublicationHas}"+ leftTime + " ${uiLabelMap.expiring}'>" + value.toDateTime().toTimeOlbius() + '</span>';
		}
</script>