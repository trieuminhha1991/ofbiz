numberRowAdd = parameters.numberRowAdd;
addRow = parameters.addRow;
if(numberRowAdd){
	try{
		numberRowAdd = Integer.parseInt(numberRowAdd);
	}catch(Exception e){
		numberRowAdd = 0;
	}
}else{
	numberRowAdd = 0;
}
if(addRow){
	try {
		addRow = Integer.parseInt(addRow);
	} catch (Exception e) {
		addRow = 0;
	}
}else{
	addRow = 0;
}
numberRowAdd += addRow;
if(numberRowAdd < 0){
	numberRowAdd = 0;
}
context.numberRowAdd = numberRowAdd;