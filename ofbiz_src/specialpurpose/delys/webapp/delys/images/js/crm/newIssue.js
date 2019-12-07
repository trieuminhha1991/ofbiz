function enableEditor(){
	CKEDITOR.replace( 'issueGeneral', {enableTabKeyToolsv: true, skin: 'office2013', height: 220});	
}
function getIssue(){
	return {
		type: $("#directionCommGeneral").val(),
		support: $("#supportGeneral").val(),
		content: CKEDITOR.instances.issueGeneral.getData()
	};
}
