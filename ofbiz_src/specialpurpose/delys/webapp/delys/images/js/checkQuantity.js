function checkQuantity (quantityE, qohE) {
	var quantity = parseInt(quantityE.value);
	var qohstr = qohE.value;
		var str2 = qohstr.replace(/\./g,"").replace(/\,/g,"");
	var qoh = parseInt(str2);
	if(quantity > qoh){
		alert("Đã nhập quá số lượng!");
		quantityE.value="";
		quantityE.focus();
		quantityE.style.backgroundColor= "red";
		
	}
	else{
		quantityE.style.backgroundColor= "yellow";
		
	}
	
	return false;
}

function FindFacility(store){
		var hh = store.value;
		alert(hh);
}
var i=0;
function KQ(){
//	 $('#ListHangHuy table tbody>tr:last').clone(true).insertAfter('#ListHangHuy table tbody>tr:last');
//	 $('#ListHangHuy table tbody>tr:last').val('fdsf');
//     return false;
	var name = $('#Test_name').val();
	var tuoi = $('#Test_tuoi').val();
	var ngheNghiep = $('#Test_ngheNghiep').val();
	var diaChi = $('#Test_diaChi').val();
	
	i++;
//	$('#ListTest table').append('<tr><td>'+name+'</td><td>'+tuoi+'</td><td>'+ngheNghiep+'</td><td>'+diaChi+'</td></tr>');
//	alert(name);
	ajaxUpdateArea('ListTest', 'abc', jQuery('#Test').serialize());
	alert(i);
}

function editPhysical(quan){
	alert(quan.value);
	
}

//
//$('document').ready(function(){
//	var tt = $('#ListHangHuy table tbody>tr:last');
//	$(tt).click(function(){
//		alert('hoooo');
//		
//	});

//})