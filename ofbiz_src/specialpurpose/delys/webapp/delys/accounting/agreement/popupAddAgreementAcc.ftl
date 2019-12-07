<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='form-window-content'>
    		<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.FormFieldTitle_agreementItemType}</label>
				</div>  
				<div class="span7">
					<div id="agreementItemTypeIdAdd"></div>
		   		</div>		
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.currencyUomId}</label>
				</div>  
				<div class="span7">
					<div id="currencyUomIdAdd"></div>
		   		</div>		
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.FormFieldTitle_agreementText}</label>
				</div>  
				<div class="span7">
					<input id="agreementTextAdd">
		   		</div>		
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.FormFieldTitle_agreementImage}</label>
				</div>  
				<div class="span7">
					<div class="ace-file-input ace-file-multiple" style="width : 250px !important;"><input multiple="" type="file" id="uploadImgAgTerm"><a class="remove" href="#"><i class="icon-remove"></i></a></div>
		   		</div>		
			</div>
    	</div>
		<div class="form-action">
			<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			<button id="alterUpdate" class='btn btn-primary form-action-button pull-right hideBt'><i class='icon-save'></i> ${uiLabelMap.CommonUpdate}</button>
		</div>
    </div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
	//Create theme
 	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	var pathImg;
    $(document).ready(function(){
    	var popup = $("#alterpopupWindow");
    	popup.jqxWindow({
	        width: 700, height: 400, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
	    });
	    $('#agreementItemTypeIdAdd').jqxDropDownList({width:250,  source: aITData, displayMember: "description", valueMember: "agreementItemTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		$('#currencyUomIdAdd').jqxDropDownList({width:250, filterable: true, source: uomData, displayMember: "description", valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		$('#agreementTextAdd').jqxInput({width:250});
		//$('#agreementImageAdd').jqxInput({width:250});
    	$("#alterSave").click(function () {
			pathImg = sendImgs(getFormData());
	        if(!saveAction()){
	        	return;
	        }
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    $("#saveAndContinue").click(function () {
	        saveAction();
	    });
    	initFormRule(popup);
    });
    function getFormData (){
    	var form_data= new FormData();
		var file = $('#uploadImgAgTerm')[0].files[0];
		form_data.append("uploadedFile",file);
		form_data.append("_uploadedFile_fileName",file.name);
		form_data.append("_uploadedFile_contentType",file.type);
		form_data.append("folder","/delys/accounting/agreementTermImages" + (new Date()).getTime());
    	return form_data;
    }
    
    function sendImgs(form_data){
    	var path;
    	$.ajax({
    		datatype : 'json',
    		url : 'uploadImagesAgreementTerm',
    		type : 'POST',
    		async : false,
    		data : form_data,
    		processData: false,
    		contentType : false,
    		success : function(response){
    			path = response.path;
    		},
    		error : function(){
    		
    		}
    	})
    	return path;
    }
    
    	
    function saveAction(){
    	if(!$("#alterpopupWindow").jqxValidator("validate")){
    		return false;
    	}
    	var row = { 
			agreementItemTypeId:$('#agreementItemTypeIdAdd').val(), 
			currencyUomId:$('#currencyUomIdAdd').val(),
			agreementText:$('#agreementTextAdd').val(),
			linkAgreementImg : pathImg
	    };
	    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        clearForm();
        return true;
    }
    function initFormRule(popup){
       	popup.jqxValidator({
       	   	rules: [{
                   input: "#agreementTextAdd", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}", 
                   action: 'blur,change', 
                   rule: function (input, commit) {
                       var val = input.val();
                       if(!val){
                    	   return false;
                       }
                       return true;
                   }
               },{
                   input: "#agreementItemTypeIdAdd", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}", 
                   action: 'change,close', 
                   rule: function (input, commit) {
                       var val = input.jqxDropDownList('val');
                       if(!val){
                    	   return false;
                       }
                       return true;
                   }
               },{
                   input: "#currencyUomIdAdd", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}", 
                   action: 'change,close', 
                   rule: function (input, commit) {
                       var val = input.jqxDropDownList('val');
                       if(!val){
                    	   return false;
                       }
                       return true;
                   }
               }]
       	 });
       	 popup.on("close", function(){
       		 setTimeout(function(){
       			clearForm();
       		 },2000)
       	 	popup.jqxValidator("hide");
       	 });
       	 
       	$('#alterUpdate').bind('click',function(){
       		var dataSL = getDataSelect();
       		updateAgreementItem(dataSL);
       	})
    }
    
    function updateAgreementItem(dataSL){
    	$.ajax({
    		url  : 'updateAgreementItJS',
    		data : {
    			'agreementId' : dataSL.agreementId,
				'agreementItemSeqId' : dataSL.agreementItemSeqId,
				'linkAgreementImg' :  sendImgs(getFormData())
    		},
    		datatype : 'json',
    		type : 'POST',
    		async : false,
    		cache : false,
    		success : function(response){
    			if(!response._ERROR_MESSAGE_LIST_ && !response._ERROR_MESSAGE_){
    				$('#jqxgrid').jqxGrid('updatebounddata');
    				$('#alterpopupWindow').jqxWindow('close');
    				clearForm();
    				Grid.updateGridMessage('jqxgrid','success','${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}');
    			}else{
    				Grid.updateGridMessage('jqxgrid','error','${StringUtil.wrapString(uiLabelMap.wgupdateerror)}');
    			}
    		},
    		error : function(){}
    	})
    	
    }
    function clearForm(){
    	$("#agreementItemTypeIdAdd").jqxDropDownList('clearSelection');
    	$("#currencyUomIdAdd").jqxDropDownList('clearSelection');
    	$("#agreementTextAdd").jqxInput('val', null);	
    	if(typeof($('#alterUpdate')) != 'undefined') $('#alterUpdate').addClass('hideBt');
		if(typeof($('#alterSave')) != 'undefined' && $('#alterSave').hasClass('hideBt'))  $('#alterSave').removeClass('hideBt');
		if(typeof($('#saveAndContinue')) != 'undefined' && $('#saveAndContinue').hasClass('hideBt'))  $('#saveAndContinue').removeClass('hideBt');
    }
</script>
	<#--===================Init upload file==========================-->
<script>
	$(function() {
		$('#id-input-file-1 , #uploadedFile').ace_file_input({
			no_file:'No File ...',
			btn_choose:'Choose',
			btn_change:'Change',
			droppable:false,
			onchange:null,
			thumbnail:false //| true | large
			//whitelist:'gif|png|jpg|jpeg'
			//blacklist:'exe|php'
			//onchange:''
			//
		});
		
		
		$('#uploadImgAgTerm').ace_file_input({
			style:'well',
			btn_choose:'Drop files here or click to choose',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			thumbnail:'small'
			//,icon_remove:null//set null, to hide remove/reset button
			/**,before_change:function(files, dropped) {
				//Check an example below
				//or examples/file-upload.html
				return true;
			}*/
			/**,before_remove : function() {
				return true;
			}*/
			,
			preview_error : function(filename, error_code) {
				//name of the file that failed
				//error_code values
				//1 = 'FILE_LOAD_FAILED',
				//2 = 'IMAGE_LOAD_FAILED',
				//3 = 'THUMBNAIL_FAILED'
				//alert(error_code);
			}
		
		}).on('change', function(){
		});
		
		//dynamically change allowed formats by changing before_change callback function
		$('#id-file-format').removeAttr('checked').on('change', function() {
			var before_change
			var btn_choose
			var no_icon
			if(this.checked) {
				btn_choose = "Drop images here or click to choose";
				no_icon = "icon-picture";
				before_change = function(files, dropped) {
					var allowed_files = [];
					for(var i = 0 ; i < files.length; i++) {
						var file = files[i];
						if(typeof file === "string") {
							//IE8 and browsers that don't support File Object
							if(! (/\.(jpe?g|png|gif|bmp)$/i).test(file) ) return false;
						}
						else {
							var type = $.trim(file.type);
							if( ( type.length > 0 && ! (/^image\/(jpe?g|png|gif|bmp)$/i).test(type) )
									|| ( type.length == 0 && ! (/\.(jpe?g|png|gif|bmp)$/i).test(file.name) )//for android's default browser which gives an empty string for file.type
								) continue;//not an image so don't keep this file
						}
						
						allowed_files.push(file);
					}
					if(allowed_files.length == 0) return false;
	
					return allowed_files;
				}
			}
			else {
				btn_choose = "Drop files here or click to choose";
				no_icon = "icon-cloud-upload";
				before_change = function(files, dropped) {
					return files;
				}
			}
			var file_input = $('#uploadImgAgTerm');
			file_input.ace_file_input('update_settings', {'before_change':before_change, 'btn_choose': btn_choose, 'no_icon':no_icon})
			file_input.ace_file_input('reset_input');
		});
		
	})


</script>