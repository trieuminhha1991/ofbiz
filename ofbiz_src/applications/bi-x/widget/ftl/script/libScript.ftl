<script type="text/javascript">
    function generateUUID() {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-xxxx-yxxx-xxxxxx9xxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x3|0x8)).toString(16);
        });
        return uuid;
    };
    function appendLoading(id) {
        var uuid = 'checkoutInfoLoader-'+generateUUID();
        var height = $(id).height()/2-20;
        var text = '<div id="'+uuid+'" style="width: 100%; display: none;z-index: 99999;position: absolute" class="jqx-rc-all jqx-rc-all-olbius">';
        text += '<div style="z-index: 99999; margin-left: -50px; left: 50%; top: 5%; margin-top: '+height+'px; position: relative; width: 100px; height: 33px;' +
                ' padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid;' +
                ' background: #f6f6f6; border-collapse: collapse;"class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">';
        text += '<div style="float: left;"><div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>';
        text += '<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">Loading...</span></div></div></div>';
        $(id).append(text);
        $(id).children().css('position', 'absolute');
        return uuid;
    }
    function appendContainer(id, minWidth, maxWidth, height) {
        var uuid = 'container-'+generateUUID();

        $(id).append('<div id="'+uuid+'" style="min-width: '+minWidth+'px; max-width: '+maxWidth+'px; height: '+height+'px; margin: 0 auto;position: relative"></div>');

        return uuid;
    }
    function getParentScript(script) {
        var scripts = $('script');

        for(var i in scripts) {
            if($(scripts[i]).html().indexOf(script) != -1) {
               return $(scripts[i]).parent();
            }
        }
    }
    function getChildGroups(party) {
        if(!party) return null;
        var items = [];
        jQuery.ajax({
            url: 'getChildParty',
            async: false,
            type: 'POST',
            data: {'parent': party},
            success: function (data) {
                for(var i in data.child) {
                    items.push(data.child[i]);
                }
            }
        });
        return items;
    }
    function formatNumberLength(num, length) {
        var r = "" + num;
        while (r.length < length) {
            r = "0" + r;
        }
        return r;
    }
</script>