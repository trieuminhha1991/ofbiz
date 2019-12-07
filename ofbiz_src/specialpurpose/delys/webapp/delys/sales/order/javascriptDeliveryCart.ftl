

<script language="JavaScript" type="text/javascript">
function toggleAll() {
    var cform = document.cartForm;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var e = cform.elements[i];
        if (e.name == "selectedItem") {
            toggle(e);
        }
    }
}
function removeSelected() {
    var cform = document.cartForm;
    cform.removeSelected.value = true;
    cform.submit();
}
</script>