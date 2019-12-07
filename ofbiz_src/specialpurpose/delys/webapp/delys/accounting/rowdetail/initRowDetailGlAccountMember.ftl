<#assign getAlGlAccount="getAll"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script> 
<style>
	.custom{
		color : red !important ;
	}
</style>
<#assign initrowdetailsDetail="function(index,parentElement,gridElement,datarecord){
	var children = ($(parentElement).children())[0];
	var container = $(children);
	container.append('$(<div id=\"container\"></div>)');
	container.attr('id','listGlAccountMember' + index);
	container.css('width','95%');
	initGrid('listGlAccountMember',index,datarecord.glAccountCategoryId);
}"/>
<div id="contextMenu" style="display:none;">
	<ul>
		<li action="add">
			<i class="icon-plus-sign"></i>&nbsp;${uiLabelMap.CommonAdd}
		</li>
		<li action="delete">
			<i class="icon-trash"></i>&nbsp;${uiLabelMap.CommonDelete}
		</li>
		<li action="update">
			<i class="icon-edit"></i>&nbsp;${uiLabelMap.CommonUpdate}
		</li>
	</ul>
</div>
<div id="notification"></div>
<div id="popupAddGl"  style="display:none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAddMember">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountCategoryId}
    				</div>
    				<div class='span7'>
    					<div id="glAccountCategoryIdAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountId}
    				</div>
    				<div class='span7'>
    					<div id="glAccountIdAdd">
    						<div id="jqxgridGlAccount"></div>
    					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_fromDate}
    				</div>
    				<div class='span7'>
						<div id="fromDateAdd" ></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_thruDate}
    				</div>
    				<div class='span7'>
						<div id="thruDateAdd" ></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_percentage}
    				</div>
    				<div class='span7'>
						<div id="amountPercentageAdd" ></div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
						<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
						<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>
<script src="/delys/images/js/generalUtils.js"></script>
<script>
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var actionPopup = "C";
	<#assign listGl = delegator.findByAnd("GlAccount",null,null,false) !>
	var listGlAccount = [
	<#if listGl?exists>
		<#list listGl as gl>
			{	
				'glAccountId' : '${gl.glAccountId?if_exists}',
				'description' : '<span style="color:#037c07;">${gl.glAccountId?if_exists}</span>',
			},
		</#list>
	</#if>	
	];
	var formatDate = function(date){
		if(!date || typeof(date) =='undefined') return null;
		return date.format('yyyy-mm-dd HH:MM:ss');
	}
	
	var getData = function(){
		var row;
		row = {
			glAccountCategoryId : $('#glAccountCategoryIdAdd').text(),
			glAccountId : $('#glAccountIdAdd').jqxDropDownButton('val'),
			fromDate : formatDate($('#fromDateAdd').jqxDateTimeInput('getDate')),
			thruDate : formatDate($('#thruDateAdd').jqxDateTimeInput('getDate')),
			amountPercentage : $('#amountPercentageAdd').jqxNumberInput('val')
		}
		return row;
	}
	
	var saveMb  = function(){
		if(!$('#formAddMember').jqxValidator('validate')){return 'error';}
		var e = $('#' + $.parseJSON(localStorage.getItem('gridFocus')));
		var data = getData();
			if(data){
				$.ajax({
					url : $('#save').attr('action') == 'C' ? 'createGlAccountCategoryMemberJSON' : (($('#save').attr('action') == 'U' ) ?  'updateGlAccountCategoryMemberCustom' : ''),
					type : 'POST',
					data : data,
					datatype : 'json',
					beforeSend : function(){
					},
					success : function(response){
						if(response._ERROR_MESSAGE_LIST_ || response._ERROR_MESSAGE_){
									$('#container').empty();
									$('#notification').jqxNotification({template : 'error'});
									$('#notification').text(response._ERROR_MESSAGE_LIST_ ? response._ERROR_MESSAGE_LIST_ : response._ERROR_MESSAGE_);
									$('#notification').jqxNotification('open');
						}else{
							$('#popupAddGl').jqxWindow('close');
							$('#container').empty();
							$('#notification').jqxNotification({template : 'success'});
							if($('#save').attr('action') == 'C'){
								$('#notification').text("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
							}else if ($('#save').attr('action') == 'U'){
								$('#notification').text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
							}
							$('#notification').jqxNotification('open');
							e.jqxGrid('clearSelection');
							e.jqxGrid('updatebounddata');
								}
					},
					error : function(err){
						result = 'error';
							}
				});
			}
	}
	
	var initElement = function(){
		initDropDown($('#glAccountIdAdd'),$('#jqxgridGlAccount'));
		//$('#glAccountIdAdd').jqxDropDownList({filterable : true,theme:theme,width:250,height : 25,  source: listGlAccount, displayMember: "description", valueMember: "glAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		$('#fromDateAdd').jqxDateTimeInput({width : 250,height : 25,formatString : 'dd/MM/yyyy HH:mm:ss',allowNullDate : true,value : null,theme : theme});
		$('#thruDateAdd').jqxDateTimeInput({width : 250,height : 25,formatString : 'dd/MM/yyyy HH:mm:ss',allowNullDate : true,value : null,theme : theme});
		$('#amountPercentageAdd').jqxNumberInput({digits : 3,min : 0,max : 100,decimalDigits : 0,width  :250,height : 25});
		$('#notification').jqxNotification({appendContainer : '#container',autoClose :true,opacity : 1,autoCloseDelay : 2000});
		filterDate.init('fromDateAdd','thruDateAdd');
		initRules();
	}
	
	var initDropDown = function(dropdown,grid){
			GridUtils.initDropDownButton({url : 'getListGLAccountOACsData&getAlGlAccount=${getAlGlAccount?if_exists}',source : {pagesize  :5,cache : false},autoshowloadelement : true,width : 400,filterable : true,dropdown : {width : 250,dropDownHorizontalAlignment : true}},
			[
				{name : 'glAccountId',type : 'string'},
				{name : 'accountName',type : 'string'},
				{name : 'accountCode',type : 'string'}
			], 
			[
				{text : '${uiLabelMap.accountCode}',datafield : 'accountCode',width : '30%'},
				{text : '${uiLabelMap.accountName}',datafield : 'accountName'}
			]
			, null, grid,dropdown,'glAccountId');
		}
	
	var initRules = function(){
		$('#formAddMember').jqxValidator({
			rules : [
				{input : '#glAccountIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
					var val = input.jqxDropDownButton('val');
					if(!val) return false;
					return true;
				}},
				{input : '#fromDateAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}',action : 'change,close',rule : function(input){
					var val = input.jqxDateTimeInput('val');
					if(!val) return false;
					return true;
				}}
			]
		})
	}
	
	var initMenu = function(){
		$('#contextMenu').jqxMenu({
				width : 250,theme : theme,mode : 'popup',autoOpenPopup: false
		});
	}
	var initjqxWindowAdd = function(){
		$('#popupAddGl').jqxWindow({ width: 500,height : 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
	}
	
	
	var initGrid = function(grid,index,id){
		var datafields = [
			{name : 'glAccountId',type :'string'},
			{name : 'accountName',type :'string'},
			{name : 'glAccountCategoryId',type :'string'},
			{name : 'fromDate',type :'date',other : 'Timestamp'},
			{name : 'thruDate',type :'date',other : 'Timestamp'},
			{name : 'amountPercentage',type :'number'}
		];
		
		var columnlist = [
			{text : '${uiLabelMap.FormFieldTitle_glAccountId}',datafield : 'glAccountId',width : '15%',cellsrenderer : function(row){
				var data = $('#' + grid + index).jqxGrid('getrowdata',row);
				return '<span>'+data.glAccountId +'</span>';
			}},
			{text : '${uiLabelMap.FormFieldTitle_accountName}',datafield : 'accountName',width : '20%'},
			{text : '${uiLabelMap.FormFieldTitle_glAccountCategoryId}',datafield : 'glAccountCategoryId',width : '10%'},
			{text : '${uiLabelMap.FormFieldTitle_fromDate}',datafield : 'fromDate',width : '15%',cellsformat : 'dd/MM/yyyy HH:mm:ss',filtertype : 'range'},
			{text : '${uiLabelMap.FormFieldTitle_thruDate}',datafield : 'thruDate',width : '15%',cellclassname : 'custom',cellsformat : 'dd/MM/yyyy HH:mm:ss',filtertype : 'range'},
			{text : '${uiLabelMap.FormFieldTitle_percentage}',datafield : 'amountPercentage',filtertype : 'number',cellsformat : 'p'}
		];
		GridUtils.initGrid({url : 'getListGlAccountMember&glAccountCategoryId=' + id,handlekeyboardnavigation : handleKeyBoard,width : '95%',height : 180,autoheight : false,filterable : true,autorowheight : false}, datafields, columnlist, null, $('#' + grid + index));
		bindEvent($('#' + grid + index),id);
	};
	
	var handleKeyBoard = function(event){
		var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		if (key == 69 && event.ctrlKey) {
			$('#popupAddGl').jqxWindow('open');
		}
	}
	
	
	var initToolbar = function(toolbar){
		var container = $('<div style="width  :100%"></div>');
		if(!container.children().length){
			container.append($('<button  style="float : right;" class="buttonRt"><i class="icon-plus-sign"></i>&nbsp;Thêm mới</button>'));
		}
		toolbar.append(container);
	}
	
	var bindEvent = function(element,id){
	
		//disabled context menu of window default
		element.bind('contextmenu',function(){
			return false;
		});
		
		element.on('mousedown',function(e){
			if( (e.which && e.which == 3) || (e.button && e.button == 2)){
	           var scrollTop = $(window).scrollTop();
	           var scrollLeft = $(window).scrollLeft();
	           $('#contextMenu').jqxMenu('open', parseInt(e.clientX) + 5 + scrollLeft, parseInt(e.clientY) + 5 + scrollTop);
	           localStorage.setItem('gridFocus',JSON.stringify(element.attr('id')));
	           $('#glAccountCategoryIdAdd').html('<span style="color:#037c07;">' + id + '</span>');
	           return false;	
			}
		})	
		
		$('#contextMenu').on('itemclick',function(e){
		var element = $(e.args);
			if(element.attr("action") == "add"){
				$('#save').attr('action','C');
				setData('add');
				$('#popupAddGl').jqxWindow('open');
			}else {
				var grid = $.parseJSON(localStorage.getItem('gridFocus'));
				var t = $('#' + grid).jqxGrid('getselectedrowindex');
					if(t != -1){
						var data =  $('#' + grid).jqxGrid('getrowdata',t);
						if(element.attr("action") == "delete"){
							deleteRow(data,$('#' + grid));
						}else if(element.attr("action") == "update"){
							$('#save').attr('action','U');
							setData(data);
							$('#popupAddGl').jqxWindow('open');
						}
					}
				}
			});
		};
		
		var setData = function(data){
				if((typeof(data) == 'string' && data == 'add')){
					$('#save').empty().append('<i class="icon-ok"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.CommonSave)}');
					$('#glAccountIdAdd').jqxDropDownButton('val','');
				}else {
					$('#save').empty().append('<i class="icon-ok"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.CommonUpdate)}');
					//$('#glAccountIdAdd').jqxDropDownList('val', data.glAccountId);
					var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">' + data.glAccountId + '</div>';
					$('#glAccountIdAdd').jqxDropDownButton('setContent', dropDownContent);
				};
				$('#fromDateAdd').jqxDateTimeInput('val',(typeof(data) == 'object') ? data.fromDate  :  null);
				$('#glAccountIdAdd').jqxDropDownButton({disabled : (typeof(data) == 'string' && data == 'add') ? false : true});
				$('#fromDateAdd').jqxDateTimeInput({disabled : (typeof(data) == 'string' && data == 'add') ?false : true});
				$('#thruDateAdd').jqxDateTimeInput('val', (typeof(data) == 'string' && data == 'add') ? null : data.thruDate);
				$('#amountPercentageAdd').jqxNumberInput('val',(typeof(data) == 'string' && data == 'add') ? 'clear' : data.amountPercentage);
		}
	
	$('#save').click(function(){
				saveMb();
			});	
		
	var deleteRow = function(data,e){
			var data = {
				glAccountId : data.glAccountId,
				glAccountCategoryId : data.glAccountCategoryId,
				fromDate : formatDate(data.fromDate)
			};
			
			$.ajax({
					url : 'deleteGlAccountCategoryMemberJSON',
					type : 'POST',
					data : data,
					datatype : 'json',
					beforeSend : function(){
					
					},
					success : function(response){
						if(response._ERROR_MESSAGE_LIST_){
							$('#container').empty();
							$('#notification').jqxNotification({template : 'error'});
							$('#notification').text(response._ERROR_MESSAGE_LIST_);
							$('#notification').jqxNotification('open');
						}else{
							$('#container').empty();
							$('#notification').jqxNotification({template : 'success'});
							$('#notification').text("${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}");
							$('#notification').jqxNotification('open');
							e.jqxGrid('updatebounddata');
						}
					},
					error : function(err){
							
						}
				})	
		}
		
	 $('#popupAddGl').on('open',function(){
	 	 var wtmp = window;
	 	 var tmpwidth = $('#popupAddGl').jqxWindow('width');
        $("#popupAddGl").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
	 })
	 
	  $('#popupAddGl').on('close',function(){
	  	filterDate.resetDate();
	  	GridUtils.clearForm($('#popupAddGl'));
	  	$('#formAddMember').jqxValidator('hide');
	 })
	
	
	$(document).ready(function(){
		initMenu();
		initjqxWindowAdd();
		initElement();
	})
</script>
