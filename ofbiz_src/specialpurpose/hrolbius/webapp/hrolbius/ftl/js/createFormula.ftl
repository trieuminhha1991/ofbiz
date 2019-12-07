<script type="text/javascript">
//var nbrIfElseBlock = 0;
var iTotcnt = 0; // total if-else condition
var iAddno = 0; 
var oOrderList = new Array();
var oSelitem;
var ohidSelitem;
var ocalSelitem;
var allFunctionAndParam = new Array();
var delemiter = "#";
function fn_Conditionyn(vi){
    if(vi == "y"){
        /* $(".traddcase").css("display","");
        $(".graydot").css("display","none"); */
        jQuery(".normalCode").css("display","none");
        fn_SetCondition("n","n","","");
        $("#ipColDes1").focus();//replace focus() with focusToEnd()
    }
    else{
        var chkvalue = "";
        $("#tbCaseInfo").find("tr").each(function(){
            chkvalue += $(this).find("td > input[name='hidval']:hidden").val();
        });

        if(chkvalue != "")
            if(!confirm('Điều kiện phụ cũng sẽ bị xóa.\n\nDữ liệu sẽ không thể phục hồi sau khi bị xóa.'))
            return;

        //	$(".traddcase").css("display","none");
        $(".normalCode").css("display","");
        $("#tbCaseInfo").find("tr").remove();
        $("#tbCaseInfo").css("display","none");
        iTotcnt = 0;
        iAddno = 0;
        jQuery(".normalCode").css("display","");
        $("#calc_des").focus();
        oOrderList = [];
    }
}

function getTrueCond(trRootId, condNo){
	var childCond = jQuery("div[parent_id="+ trRootId +"_True]")[0];
	if(jQuery("#trTrue" + condNo).css('display') != 'none'){
		//console.log("#ipColTrue" + condNo);
		//console.log(jQuery("#ipColTrue" + condNo).val());
		return jQuery("#ipColTrue" + condNo).val();	
	}else if(childCond){
		var childCondId = childCond.id;
		var childCondNo = childCondId.substring("trDefine".length);
		var tempRet = {};
		tempRet.statement = jQuery("#ipColDes" + childCondNo).val();
		tempRet.if_true = getTrueCond(childCondId, childCondNo);
		tempRet.if_false = getFalseCond(childCondId, childCondNo) 
		return tempRet; 
	}
}

function getFalseCond(trRootId, condNo){
	var childCond = jQuery("div[parent_id="+ trRootId +"_False]")[0];
	if(jQuery("#trFalse" + condNo).css('display') != 'none'){
		return jQuery("#ipColFalse" + condNo).val();
	}else if(childCond){
		var childCondId = childCond.id;
		var childCondNo = childCondId.substring("trDefine".length);
		var tempRet = {};
		tempRet.statement = jQuery("#ipColDes" + childCondNo).val();
		tempRet.if_true = getTrueCond(childCondId, childCondNo);
		tempRet.if_false = getFalseCond(childCondId, childCondNo) 
		return tempRet; 
	}
}

//@param:
//	tr_parent: if-else block inner have parent_id is: "id of if-else block outer" + "suffix" ( _True or _False)
function fn_SetCondition(pos, val_yn, del_id, tr_parent){ 
	var strBtnCon = 'IF';  
	var strBtnDel = 'Xóa'; 
	var strBtnCC = 'Hủy'; 
	var strCaseName = 'IF Điều kiện'; 
	var strTag;
	
	if(pos == "n" && val_yn == "n"){
		iTotcnt++;
	    iAddno++;
	    
	    strTag = '<div class="trgroup1" id="trDefine1">';
	    strTag += "<div class='row-fluid margin-bottom2'>";
	    strTag += "<div class='span3 text-algin-center'>";
	    strTag += '['+ strCaseName + '1]';
	    strTag += '</div>';
	    strTag += '<div class="span6">'
	    strTag += '<input type="text" id="ipColDes1" class="default width100" style="ime-mode:disabled;background-color:#f7f7f7;"' 
	    			+ 'onblur="BlurColor3(this);text_check(\'ipColDes1\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this);" onkeypress="fn_nopress(this)" />';
	    strTag += '<input type="hidden" name="hidpushpop" id="hidPopDes1"/><input type="hidden" name="hidval" disabled="disabled" id="hidColDes1"/><input type="hidden" name="cal_val" id="hidCalc_FC1"/>';
	    strTag += '</div>';
	    strTag += '<div class="span3 marginTop4">';
	    strTag += '<button class="btn btn-mini btn-danger icon-reply open-sans marginTop-10" onclick="fn_removeword(\'ipColDes1\');" >'+strBtnDel+' </button>';
	    strTag += '<button class="btn btn-mini btn-primary icon-remove open-sans marginTop-10" onclick="fn_Conditionyn(\'n\')" >'+strBtnCC+'</button>';
	    strTag += '</div>';
	    strTag += '</div>';
	    strTag += '</div>';
	    /* ------- */
	    strTag += '<div id="trTrue1" class="trgroup1">';
	    strTag += "<div class='row-fluid margin-bottom2'>";
	    strTag += "<div class='span3 text-algin-center'>";
	    strTag += '[True1]';
	    strTag += '</div>';
	    strTag += '<div class="span6">';
		strTag += '<input type="text" id="ipColTrue1" class="default width100" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColTrue1\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this);" onkeypress="fn_nopress(this)" />';	    
		strTag += '<input type="hidden" name="hidpushpop" id="hidPopTDes1"/><input type="hidden"  name="hidval" disabled="disabled" id="hidTrueDes1"/><input type="hidden" name="cal_val" id="hidCalc_FT1"/>';
		strTag += '</div>';
		strTag += '<div class="span3 marginTop4">';
		strTag += '<button type="button" class="btn btn-mini btn-danger icon-reply open-sans marginTop-10" onclick="fn_removeword(\'ipColTrue1\');">'+strBtnDel+'</button>';
	    strTag += '<button class="btn btn-mini btn-primary icon-code-fork open-sans marginTop-10" onclick="fn_SetCondition(\'trTrue1\',\'y\',\'trTrue1\', \'trDefine1_True\');">'+ strBtnCon +'</button>';
	    strTag += '</div>';
	    strTag += '</div>';
	    strTag += '</div>';
	    /* ------- */
	    
	    strTag += '<div id="trFalse1" class="trgroup1">';
	    strTag += "<div class='row-fluid margin-bottom2'>";
	    strTag += "<div class='span3 text-algin-center'>";
	    strTag += '[False1]';
	    strTag += '</div>';
	    strTag += '<div class="span6">';
	    strTag += '<input type="text" id="ipColFalse1" class="default width100" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColFalse1\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this);" onkeypress="fn_nopress(this)" />';
	    strTag += '<input type="hidden" name="hidpushpop" id="hidPopFDes1"/><input type="hidden"  name="hidval" disabled="disabled" id="hidFalseDes1"/><input type="hidden" name="cal_val" id="hidCalc_FF1"/>';
	    strTag += '</div>';
	    strTag += '<div class="span3 marginTop4">';
	    strTag += '<button class="btn btn-mini btn-danger icon-reply open-sans marginTop-10" onclick="fn_removeword(\'ipColFalse1\');">'+strBtnDel+'</button>';
	    strTag += '<button class="btn btn-mini btn-primary icon-code-fork open-sans marginTop-10" onclick="fn_SetCondition(\'trFalse1\',\'y\',\'trFalse1\', \'trDefine1_False\')">'+ strBtnCon +'</button>';
	    strTag += '</div>';
	    strTag += '</div>';
	    strTag += '</div>';
	    
	    jQuery("#tbCaseInfo").append(strTag);
	    jQuery("#tbCaseInfo").css("display","");
	    
	    jQuery("#ipColDes1").focus(); 
	    //console.log("star");
	}
	else{
	    if(val_yn == "y"){
	    	iTotcnt++;
	        iAddno++;
	        /* if(iTotcnt > 10){
	            alert('Bạn không thể tạo nhiều hơn 10 điều kiện.'); //
	            return false;
	        } */
	        strTrtag1 = "trTrue" + iAddno;
	        strTrtag2 = "trFalse" + iAddno;
	        var strTxt= pos.substr(2,pos.length);
	                          
	        strTag = '<div class="trgroup'+ iAddno +' '+ del_id +'" id="trDefine'+iAddno+'" parent_id="' + tr_parent + '">';
	        strTag += "<div class='row-fluid margin-bottom2'>";
	        strTag += "<div class='span3 text-algin-center'>";
	        strTag +=  strTxt+' ['+ strCaseName + iAddno + ']';
	        strTag += '</div>';
	        strTag += '<div class="span6">';
	        strTag += '<input type="text" id="ipColDes'+ iAddno+'" class="default width100" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColDes'+iAddno+'\',300);" readonly onfocus="fn_Selectitem(this)" onkeypress="fn_nopress(this)" />';
	        strTag += '<input type="hidden" name="hidpushpop" id="hidPopDes'+iAddno+'"/><input type="hidden" name="hidval" disabled="disabled" id="hidColDes'+iAddno+'"/><input type="hidden" name="cal_val" id="hidCalc_FC'+iAddno+'"/></td>';
	        strTag += '</div>';
	        strTag += '<div class="span3 marginTop4">';
	        strTag += '<button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColDes'+iAddno+'\');">'+strBtnDel+' </button>'
	        strTag += '<button class="btn btn-mini btn-primary icon-remove marginTop-10" onclick="fn_SetCondition(\''+ pos +'\',\'n\',\'\')">'+strBtnCC+'</button>';
	        strTag += '</div>';
	        strTag += '</div>';
	        strTag += '</div>';
	        /* --------- */
	        strTag += '<div id="trTrue'+iAddno+'" class="trgroup'+ iAddno +' '+ del_id+'">';
	        strTag += "<div class='row-fluid margin-bottom2'>";
	        strTag += "<div class='span3 text-algin-center'>";
	        strTag += '[True'+iAddno+']';
	        strTag += '</div>';
	        strTag += '<div class="span6">';
	        strTag += '<input type="text" id="ipColTrue'+iAddno+'" class="default width100" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColTrue'+iAddno+'\',300);" readonly onfocus="fn_Selectitem(this)" onkeypress="fn_nopress(this)" />';
	        strTag += '<input type="hidden" name="hidpushpop" id="hidPopTDes'+iAddno+'"/><input type="hidden" name="hidval" disabled="disabled" id="hidTrueDes'+iAddno+'"/><input type="hidden" name="cal_val" id="hidCalc_FT'+iAddno+'"/>';
	        strTag += '</div>';
	        strTag += '<div class="span3 marginTop4">';
	        strTag += '<button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColTrue'+iAddno+'\');">'+strBtnDel+'</button>';
	        strTag += '<button class="btn btn-mini btn-primary icon-code-fork marginTop-10" onclick="fn_SetCondition(\'trTrue'+iAddno+'\',\'y\',\'trTrue'+ iAddno+ ' ' + del_id +'\', \'trDefine'+ iAddno +'_True\');">'+ strBtnCon +'</button>';
	        strTag += '</div>';
	        strTag += '</div>';
	        strTag += '</div>';
	        /* --------- */
	        
	        strTag += '<div id="trFalse'+iAddno+'" class="trgroup'+ iAddno +' '+ del_id+'">';
	        strTag += "<div class='row-fluid margin-bottom2'>";
	        strTag += "<div class='span3 text-algin-center'>";
	        strTag += '[False'+iAddno+']'
	        strTag += '</div>';
	        strTag += '<div class="span6">';
	        strTag += '<input type="text" id="ipColFalse'+iAddno+'" class="default width100" style="ime-mode:disabled;background-color:#f7f7f7;" onblur="BlurColor3(this);text_check(\'ipColFalse'+iAddno+'\',300);" readonly onfocus="FocusColor2(this);fn_Selectitem(this)" onkeypress="fn_nopress(this)" />';
	        strTag += '<input type="hidden" name="hidpushpop" id="hidPopFDes'+iAddno+'"/><input type="hidden" name="hidval" disabled="disabled" id="hidFalseDes'+iAddno+'"/><input type="hidden" name="cal_val" id="hidCalc_FF'+iAddno+'"/>';
	        strTag += '</div>';
	        strTag += '<div class="span3 marginTop4">';
	        strTag += '<button class="btn btn-mini btn-danger icon-reply marginTop-10" onclick="fn_removeword(\'ipColFalse'+iAddno+'\');">'+strBtnDel+'</button>';
	        strTag += '<button class="btn btn-mini btn-primary icon-code-fork marginTop-10" onclick="fn_SetCondition(\'trFalse'+iAddno+'\',\'y\',\'trFalse'+ iAddno+ ' ' + del_id + '\', \'trDefine'+ iAddno +'_False\')" >'+ strBtnCon +'</button></td></tr>';
	        strTag += '</div>';
	        strTag += '</div>';
	        strTag += '</div>';
	        
	        jQuery("#"+pos).after(strTag);
	        jQuery("#"+pos).css("display","none");
	        jQuery("#"+pos).find("input:text").val("");
	        jQuery("#"+pos).find("input[name=hidpushpop]").val("");
	        jQuery("#"+pos).find("input[name=hidval]").val("");
	        jQuery("#"+pos).find("input[name=cal_val]").val("");
	        jQuery("#ipColDes"+iAddno).focus();
	        oOrderList.push(pos + "-" + val_yn + "-" + del_id);
	    }
	    else{
	        var chkvalue = "";
	        $("tr[class*='"+pos+"']").each(function(){
	            chkvalue += $(this).find("td > input[name='hidval']:hidden").val();
	        });
	
	        if(chkvalue != "")
	             if(!confirm('Điều kiện phụ cũng sẽ bị xóa.\n\nDữ liệu sẽ không thể phục hồi sau khi bị xóa.'))
	                return;
	        
	        $("#"+pos).css("display","");
	        var ichk = 0;
	        $("tr[class*='"+pos+"']").each(function() {
	            $(this).remove();
	            ichk++;
	        });
	        
	        ichk = parseInt(ichk/3);
	        iTotcnt = iTotcnt - ichk;
	        oOrderList.push(pos + "-" + val_yn + "-" + del_id);
	    }
	}
}

function fn_Selectitem(obj){
	//console.log("select");
    oSelitem = obj;
    if($(obj).get(0).id == "calc_des"){
        oSelpopitem = $("#hid_popdes");
        ohidSelitem = $("#hid_cd_des");
        ocalSelitem = $("#hid_cal_condition");
    }else{
        oSelitem = obj;
        /* oSelpopitem = $(obj).closest("tr").find("td > input[name='hidpushpop']:hidden");
        ohidSelitem = $(obj).closest("tr").find("td > input[name='hidval']:hidden");
        ocalSelitem = $(obj).closest("tr").find("td > input[name='cal_val']:hidden"); */ 
        oSelpopitem = $(obj).closest("div").find("input[name='hidpushpop']:hidden");
        ohidSelitem = $(obj).closest("div").find("input[name='hidval']:hidden");
        ocalSelitem = $(obj).closest("div").find("input[name='cal_val']:hidden"); 
    }
}

function fn_removeword(obj){
    oSelitem = jQuery("#"+obj);
    var textVar = oSelitem.val();
    
    if(obj == "calc_des"){
        oSelpopitem = $("#hid_popdes");
        ohidSelitem = $("#hid_cd_des");
        ocalSelitem = $("#hid_cal_condition");
    }else{
        /* oSelpopitem = $(oSelitem).closest("tr").find("td > input[name='hidpushpop']:hidden");
        ohidSelitem = $(oSelitem).closest("tr").find("td > input[name='hidval']:hidden");
        ocalSelitem = $(oSelitem).closest("tr").find("td > input[name='cal_val']:hidden"); */
    	oSelpopitem = $(oSelitem).closest("div").find("input[name='hidpushpop']:hidden");
        ohidSelitem = $(oSelitem).closest("div").find("input[name='hidval']:hidden");
        ocalSelitem = $(oSelitem).closest("div").find("input[name='cal_val']:hidden");
    }
	//var indexArr = jQuery().val() == "" ? 0 : jQuery().val().split(delemiter);
	//var index = indexArr.pop();
	
	//jQuery(oSelpopitem).val(indexArr.join(delemiter));
    
	var arDespop = $(oSelpopitem).val() == "" ? [] : $(oSelpopitem).val().split(delemiter);
	var index = arDespop.pop();
    $(oSelpopitem).val(arDespop.join(delemiter));
	if(!index){
		index = textVar.length;
	}
    //remove a word
    oSelitem.val(textVar.substring(0, textVar.length - index));
    
    var arCode = $(ohidSelitem).val() == "" ? [] : $(ohidSelitem).val().split(delemiter);
    arCode.pop();
    $(ohidSelitem).val(arCode.join(delemiter)); 

    var arCalc = $(ocalSelitem).val() == "" ? [] : $(ocalSelitem).val().split(delemiter);
    arCalc.pop();
    $(ocalSelitem).val(arCalc.join(delemiter));

    //jQuery(oSelitem).val($(ohidSelitem).val().replace(/_/gi,""));
    jQuery(oSelitem).focus().val(jQuery(oSelitem).val());
}

function escapeHtml(string) {
   return string.replace(/</g, "lt").replace(/>/g, "gt");
}

function cal_add(colval,des,gubun, is_function){	
	if(!oSelitem){
		jQuery("#calc_des").focus();
	}
    var cid = jQuery(oSelitem).get(0).id;

    if(cid.indexOf("ipColDes") < 0 && (gubun == 2 || gubun == 4 || gubun == 5)){
        alert("Nó chỉ được dùng trong điều kiện IF");
        jQuery(oSelitem).focus();
        return;
    }
    var col = jQuery(ohidSelitem).val() == "" ? [] : jQuery(ohidSelitem).val().split(delemiter);
    var SelCalc = $(ocalSelitem).val() == "" ? [] : $(ocalSelitem).val().split(delemiter);
    var last_gubun = SelCalc[SelCalc.length-1];
  	
    switch(gubun)
	    {
	        case 1:
	            if(col.length > 0){
	                if(last_gubun == gubun || last_gubun == "2" || last_gubun == "3" || (last_gubun == "8" && col[col.length-1] == ")")){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); 
	                    $(oSelitem).focus();
	                    return;
	                }
	            }
	        break;
	        case 2: //조건 값
	            if(col.length > 0){
	                if(last_gubun != "4"){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	            }
	        break;
	        case 3: //숫자
	            if(col.length > 0){
	                if(last_gubun == "1" || last_gubun == "2"){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	                if(last_gubun == "3"){
	                    var arNum = [];
	                    for(var i = SelCalc.length - 1; i > -1 ; i--){
	                        if(SelCalc[i] == "3")
	                            arNum.push(col[i]);
	                        else
	                            break;
	                    }

	                    if(arNum.length > 0){
	                        if(arNum[arNum.length-1] =="."){
	                            alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                            $(oSelitem).focus();
	                            return;
	                        }
	                        else
	                        {
	                            var chkpoint = 0;
	                            for(var i = 0; i < arNum.length; i++){
	                                if(arNum[i] == ".")
	                                    chkpoint++;
	                            }
	                            if(chkpoint > 0 && colval == "."){
	                                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                                $(oSelitem).focus();
	                                return;
	                            }
	                        }
	                    }
	                }
	                else if(gubun == "3" && colval == "."){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}.");
	                    $(oSelitem).focus();
	                    return;
	                }else if(last_gubun == "8" && col[col.length-1] == ")"){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	            }else if(colval == "."){
	                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                $(oSelitem).focus();
	                return;                        
	            }
	        break;
	        case 4: //조건 추가시 필요값
	            if(col.length > 0){
	                var chkarray = [];
	                var chkok = 0;
	                var chknextVal = 0;

	                for(var i = 0; i < SelCalc.length; i++){
	                    if(SelCalc[i] == "4")
	                        chkarray = [];
	                     else
	                        chkarray.push(SelCalc[i]);
	                }

	                if(chkarray.length > 0){
	                    for(var i = 0; i < chkarray.length; i++){
	                        if(chkarray[i] == "5" || chkarray[i] == "2")
	                        {
	                            chkok++;
	                            chknextVal = i;
	                        }
	                    }
	                }

	                if(chkok < 1){
	                    alert ("Vui lòng chọn một phép toán."); //연산자를 선택 바랍니다
	                    $(oSelitem).focus();
	                    return;
	                }
	                else if(chkarray.length - 1 == chknextVal && chkarray[chkarray.length-1] != "2"){
	                    alert ("Vui lòng chọn một phép toán."); //연산자를 선택 바랍니다
	                    $(oSelitem).focus();
	                    return;
	                }
	                else{
	                    if(!(col[col.length - 1] == ")" || last_gubun == 1 || last_gubun == 2 || last_gubun == 3 ) )
	                    {
	                        alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                        $(oSelitem).focus();
	                        return;
	                    }
	                }
	            }
	            else{
	                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                $(oSelitem).focus();
	                return;
	            }
	        break;
	        case 5: //비교값
	            if(col.length > 0){
	                if(last_gubun == "2" || last_gubun == "4" || last_gubun == "5" || last_gubun == "6" || last_gubun == "7" || (last_gubun == "8" && col[col.length-1] == "(") ){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }
	                else{
	                    var chkarray = [];
	                    var chkSetVal = [];
	                    var chkok = 0;

	                    for(var i = 0; i < SelCalc.length; i++){
	                        if(SelCalc[i] == "4"){
	                            chkarray = [];
	                            chkSetVal = [];
	                        }
	                        else{
	                            chkarray.push(SelCalc[i]);
	                            chkSetVal.push(col[i]);
	                        }
	                    }

	                    if(chkarray.length > 0){
	                        for(var i = 0; i < chkarray.length; i++){
	                            if(chkarray[i] == "5")
	                            chkok++;    
	                        }
	                    }

	                    if(chkok > 0 && gubun == 5){
	                        alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                        $(oSelitem).focus();
	                        return;
	                    };

	                    var arleft = 0;
	                    var strCalc = chkSetVal.join("");

	                    for(var i = 0 ;i < strCalc.length ; i++){
	                        if(strCalc.charAt(i) == "(")
	                            arleft++;
	                        if(strCalc.charAt(i) == ")")
	                            arleft--;
	                    }

	                    if(arleft != 0 ){
	                        alert("Công thức tính này không hợp lệ.\n\nVui lòng kiểm tra các dấu ngoặc đơn."); //계산식이 유효하지 않습니다.\n\n괄호를 확인 바랍니다.
	                        $(oSelitem).focus();
	                        return;
	                    }
	                }
	            }
	            else if(col.length == 0 && gubun == 5){
	                alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                $(oSelitem).focus();
	                return;
	            }
	        break;
	        case 6: //사칙연산
	            if(col.length == 0){
	                if(colval != "-" ){
	                    alert ("Hoạt động không thể được lựa chọn đầu tiên"); //연산자를 먼저 선택할 수 없습니다. 계산식을 정확히 입력 바랍니다.
	                    $(oSelitem).focus();
	                    return;
	                }
	            }else{
	                if(!(last_gubun == "1" || (last_gubun == "3" && SelCalc[SelCalc.length-1] != ".") || (last_gubun == "8" && colval == ")")  || col[col.length-1] == ")" || (last_gubun == "7" && colval == "-") )  ){
	                    alert ("Hoạt động không thể được lựa chọn đầu tiên"); //연산자를 먼저 선택할 수 없습니다. 계산식을 정확히 입력 바랍니다.
	                    $(oSelitem).focus();
	                    return;
	                }
	            }
	        break;
	        case 7: //특수식 Round, ceil, floor 
	            if(col.length > 0){
	                if(!(last_gubun == 4 || last_gubun == 5 ||  last_gubun == 6 || last_gubun == 7 || last_gubun == 8) ){
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    $(oSelitem).focus();
	                    return;
	                }else if(last_gubun == "8" && col[col.length-1] == ")"){
	                    $(oSelitem).focus();
	                    alert ("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\n ${StringUtil.wrapString(uiLabelMap.PayrollPleaseReviewTheFormula)}."); //계산식이 유효하지 않습니다.\n\n확인하세요
	                    return;
	                }
	            }
	        break;
	        case 8: //괄호
	            if(col.length == 0 && colval == "("){}
	            else{
	                var arleft = 0;

	                var strCalc = col.join("");

	                for(var i = 0 ;i < strCalc.length ; i++){
	                    if(strCalc.charAt(i) == "(")
	                        arleft++;
	                    if(strCalc.charAt(i) == ")")
	                        arleft--;
	                }

	                if(arleft < 0 || (arleft == 0 && colval == ")") ){
	                    alert("Vui lòng chọn một Mặt Hàng trước."); //항목을 먼저 선택 바랍니다
	                    $(oSelitem).focus();
	                    return;
	                }

	                if(col[col.length -1] == "(" && colval == ")"){
	                    alert ("Bạn không thể nhập chỉ với ’( )’. Vui lòng nhập lại."); //()만입력할 수 없습니다. 다시입력해주세요.
	                    $(oSelitem).focus();
	                    return;
	                }

	                if( !( (colval == "(" && (last_gubun == 4 || last_gubun == 5 || last_gubun == 6 || last_gubun == 7 || col[col.length-1] == "(")) || (colval == ")" && (last_gubun == 1 || last_gubun == 3 || col[col.length-1] == ")") ) ) ){
	                    alert("${StringUtil.wrapString(uiLabelMap.PayrollFormulaNotValid)}.\n\nVui lòng kiểm tra các dấu ngoặc đơn."); //계산식이 유효하지 않습니다.\n\n괄호를 확인 바랍니다.
	                    $(oSelitem).focus();
	                    return false;
	                }
	             }
	        break;
	     }
    
	 if(is_function){
		 colval = colval +"()";
	 }	
	 $(oSelitem).val($(oSelitem).val()+colval);
	 if(cid.indexOf("ipColDes") > -1 && gubun == 1){
		allFunctionAndParam.push(colval);		
	 }
	 var arDespop = jQuery(oSelpopitem).val() == "" ? [] : $(oSelpopitem).val().split(delemiter);
	 arDespop.push(colval.length);
	 $(oSelpopitem).val(arDespop.join(delemiter));
	
	 var arCode = jQuery(ohidSelitem).val() == "" ? [] : $(ohidSelitem).val().split(delemiter);
	 arCode.push(colval);
	 $(ohidSelitem).val(arCode.join(delemiter));
	
	 var arCalc = jQuery(ocalSelitem).val() == "" ? [] : $(ocalSelitem).val().split(delemiter);
	 arCalc.push(gubun);
	 $(ocalSelitem).val(arCalc.join(delemiter));
	
	 $(oSelitem).focus();
	 
	  
}

function getFormulaData(){
	var codeValue = {};
	var params = new Array();
	if(iTotcnt > 0){
		codeValue.name = "functionJson";
		$("#calc_des").val('');
		var tempIfElse = {};
		var rootTrIfElse = "trDefine1";
		tempIfElse.statement = $("#ipColDes1").val();
		tempIfElse.if_true = getTrueCond("trDefine1", 1);
		tempIfElse.if_false = getFalseCond("trDefine1", 1);
		var value = JSON.stringify(tempIfElse);
		codeValue.value = escapeHtml(value);
	}else{
		codeValue.name = "function";
		codeValue.value = jQuery("#calc_des").val();
	}
	params.push(codeValue);
	var functionRelated = "";
	if(allFunctionAndParam.length){
		for(var i = 0; i < allFunctionAndParam.length - 1; i++){
			functionRelated += allFunctionAndParam[i] + ",";
		}
		functionRelated += allFunctionAndParam[allFunctionAndParam.length - 1]
		params.push({name: "functionRelated", value: functionRelated});
	}
	return params;
}

function FocusColor2(This) { 
  This.style.backgroundColor = "#143361 !important";
}

function BlurColor3(This){ 
	This.style.backgroundColor = "#88E83B !important";
}

function text_check(text, total, result){
	
}
</script>