<#assign productDim = productDim!>
<script language="JavaScript" type="text/javascript">
             $(document).ready(function () {
              $('#operator').on('change', function(){
              	if($(this).val() == "trongkhoang"){
              		$('#lastYear').attr("disabled",false);
              	}else{
              		$('#lastYear').attr("disabled",true);
              	}
              });
              
//combobox for week
			var data = new Array();
        			data[0] = {status:'Select all', value:"all"};
					for (var i = 1; i < 54; i++) {
    					var row = {};
    					row["status"] = 'Tuan: '+i;
    					row["value"] = i;
					    data[i] = row;
					}
			$("#jqxComboBoxWeek").jqxComboBox({
      				source: data,
      				placeHolder: 'Chon tuan',
      				theme: 'energyblue',
      				displayMember: 'status',
      				width: '220px',
     				height: '25px',
      				checkboxes: true,
      				renderSelectedItem: function(index, item)
    				{
      					return "";     
    				}
  				});
  			$("#jqxComboBoxWeek").on('checkChange', function (event) {
      				var args = event.args;
     				if (args) {
         				var index = args.item.index;
          				var item = args.item;
          				var label = item.label;
          				var value = item.value;
          				var checked = args.item.checked;
          				if(checked == true && index == 0){
          					$("#jqxComboBoxWeek").jqxComboBox("checkAll");
          				}
          				if(checked == false && index == 0){
          					$("#jqxComboBoxWeek").jqxComboBox("uncheckAll");
          				}
          				var checkedItems = "";
     					var items = $("#jqxComboBoxWeek").jqxComboBox('getCheckedItems');
      					$.each(items, function (index) {
          					checkedItems += this.value + ",";
      					});
          				
          				$("#selectFieldWeek").val(checkedItems);
          				
      				}
  				});
              

//combobox for product
             		<#assign flag = 1/>
             		var source_product = new Array(
             		{status:'${StringUtil.wrapString(uiLabelMap.CheckAll)}', value:"all"},
             		<#list productDim as proDim>
             		<#if proDim.dimensionId != '_NA_' && proDim.dimensionId != '_NF_'>
             		<#if flag == productDim.size()>
        			{status:'${StringUtil.wrapString(proDim.internalName)}', value:"${StringUtil.wrapString(proDim.dimensionId)}"}
        			<#else>
        			{status:'${StringUtil.wrapString(proDim.internalName)}', value:"${StringUtil.wrapString(proDim.dimensionId)}"},
        			</#if>
        			</#if>
        			<#assign flag = flag +1/>
        			</#list>
    				);
    				
    				$("#jqxComboBoxPro").jqxComboBox({
    				
      				source: source_product,
      				placeHolder: '${StringUtil.wrapString(uiLabelMap.SelectProduct)}',
      				displayMember: 'status',
      				theme: 'energyblue',
      				width: '220px',
     				height: '25px',
      				checkboxes: true
  					});
  				
  				$("#jqxComboBoxPro").jqxComboBox("checkAll");
  				var checkedItemsPro = "";
     					var items = $("#jqxComboBoxPro").jqxComboBox('getCheckedItems');
      					$.each(items, function (index) {
          					checkedItemsPro += this.value + ",";
      					});
          		$("#selectFieldProduct").val(checkedItemsPro);
  				$("#jqxComboBoxPro").on('checkChange', function (event) {
      				var args = event.args;
     				if (args) {
         				var index = args.item.index;
          				var item = args.item;
          				var label = item.label;
          				var value = item.value;
          				var checked = args.item.checked;
          				if(checked == true && index == 0){
          					$("#jqxComboBoxPro").jqxComboBox("checkAll");
          				}
          				if(checked == false && index == 0){
          					$("#jqxComboBoxPro").jqxComboBox("uncheckAll");
          				}
          				var checkedItems = "";
     					var items = $("#jqxComboBoxPro").jqxComboBox('getCheckedItems');
      					$.each(items, function (index) {
          					checkedItems += this.value + ",";
      					});
          				
          				$("#selectFieldProduct").val(checkedItems);
          				
      				}
  });
  				
// combobox for month
				var source = new Array(
        			{status:'${StringUtil.wrapString(uiLabelMap.CheckAll)}', value:"all"},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 1', value:1},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 2', value:2},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 3', value:3},
       			 	{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 4', value:4},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 5', value:5},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 6', value:6},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 7',  value:7},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 8', value:8},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 9', value:9},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 10', value:10},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 11', value:11},
        			{status:'${StringUtil.wrapString(uiLabelMap.Month)}: 12', value:12}
    			);
				$("#jqxComboBox").jqxComboBox({
      				source: source,
      				theme: 'energyblue',
      				placeHolder: '${StringUtil.wrapString(uiLabelMap.SelectMonth)}',
      				displayMember: 'status',
      				width: '220px',
     				height: '25px',
      				checkboxes: true
  				});
  			$("#jqxComboBox").on('checkChange', function (event) {
      				var args = event.args;
     				if (args) {
         				var index = args.item.index;
          				var item = args.item;
          				var label = item.label;
          				var value = item.value;
          				var checked = args.item.checked;
          				if(checked == true && index == 0){
          					$("#jqxComboBox").jqxComboBox("checkAll");
          				}
          				if(checked == false && index == 0){
          					$("#jqxComboBox").jqxComboBox("uncheckAll");
          				}
          				var checkedItems = "";
     					var items = $("#jqxComboBox").jqxComboBox('getCheckedItems');
      					$.each(items, function (index) {
          					checkedItems += this.value + ",";
      					});
          				
          				$("#selectField").val(checkedItems);
          				
      				}
  				});
  				
  				$('#detail').on('click', function(){
  					if($(this).val() == "N"){
  						$('.row_hidden').css("display", "table-row");
  						$(this).val("Y");
  					}else{
  						$('.row_hidden').css("display", "none");
  						$(this).val("N");
  					}
  					
  				});

       });
              

</script>
<form method="post" action="<@ofbizUrl>getInventoryHistorys</@ofbizUrl>" name="create">
	<table id="tbl">
		<tr>
			<td colspan="5">&nbsp;</td>
		</tr>
		<tr>
			<td></td>
			<td>${uiLabelMap.Facility}:</td>
			<td>
				<select name="facility" style="margin-right:30px!important;">
					<#list facilities as nextFacility>
                            <option value="${(nextFacility.facilityId)?if_exists}">${(nextFacility.facilityName)?if_exists}</option>
                    </#list>			
				</select>
			</td>
			<td>Chon chi tiet:<input id="detail" name="detail" value="N" type="checkbox"/><span style="margin-left:10px!important; margin-top: -5px!important;" class="lbl"></span></td>
			<td>&nbsp;</td>
		</tr>
		
		<tr>
			<td><input type="radio" value="Y" checked="checked"/></td>
			<td><label>Nam:</label></td>
			<td>
				<select name="operator" id="operator" style="margin-right:30px!important;">
					<option value="bang">bang</option>
					<option value="trongkhoang">trong khoang</option>
				</select>
			</td>
			<td style="width: 220px!important;">Tu:<input name="firstYear" id="firstYear" style="margin-left:10px!important;width: 50px!important;" type="text"/></td>
			<td style="width: 220px!important;">Den:<input type="text" name="lastYear" id="lastYear" disabled="true" style="width: 50px!important;margin-left: 10px!important;"/></td>
		</tr>
		<tr class="row_hidden" style="margin-top:10px!important; display: none;">
			<td colspan="1" >&nbsp;</td>
			<td>${uiLabelMap.SelectProduct}:</td>
			<td colspan="2">
				<div id="jqxComboBoxPro"></div>
			</td>
			<td>
				<input type="hidden" id="selectFieldProduct" value="other" name="selectProduct"/>
			</td>
		</tr>
		<tr class="row_hidden" style="margin-top:20px!important; display: none;">
			<td colspan="1" >&nbsp;</td>
			<td>${uiLabelMap.SelectMonth}:</td>
			<td colspan="2">
				<div id="jqxComboBox"></div>
			</td>
			<td>
				<input type="hidden" id="selectField" value="other" name="select"/>
			</td>
		</tr>
		<tr class="row_hidden" style="margin-top:20px!important; display:none;">
			<td colspan="1" >&nbsp;</td>
			<td>${uiLabelMap.SelectWeek}:</td>
			<td colspan="2">
				<div id="jqxComboBoxWeek"></div>
			</td>
			<td>
				<input type="hidden" id="selectFieldWeek" value="other" name="selectWeek"/>
			</td>
		</tr>
		<tr>
			<td colspan="5"></td>
		</tr>
		
		<tr>
			<td>&nbsp;</td>
			<td colspan="4"><input style="margin-top:20px!important;" type="submit" value="${uiLabelMap.Search}" class="btn btn-small btn-primary"></input></td>
		</tr>
		<tr>
			<td colspan="5"><hr /></td>
		</tr>
	</table>
</form>
 