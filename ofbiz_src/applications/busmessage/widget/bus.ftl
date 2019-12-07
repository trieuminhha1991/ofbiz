<@jqGridMinimumLib />

<div class="row-fluid">
    <div class="span3 align-right" style="margin-top: 3px"><label>${uiLabelMap.BSPSSalesChannel}</label></div>
    <div class="span3">
        <div id="busProductStore"></div>
    </div>
    <div class="span4">
        <button id="busPublic" type="button" class="btn btn-primary form-action-button"
                style="margin:0px auto;height: 30px;"><i
                class="icon-ok"></i>Public
        </button>
    </div>
</div>

<div id="busProduct"></div>
<script type="text/javascript">

    $(function () {
        var url = "listProductStoreBus";
        var source =
        {
            datatype: "json",
            datafields: [
                {name: 'productStoreId', type: 'string'},
                {name: 'productStoreName', type: 'string'}
            ],
            root: "data",
            url: url,
            type: 'POST'
        };
        var dataAdapter = new $.jqx.dataAdapter(source);

        $('#busProductStore').jqxDropDownList({
            source: dataAdapter,
            displayMember: "productStoreName",
            valueMember: "productStoreId",
            width: 200,
            height: 25,
            theme: 'olbius',
            placeHolder: ''
        });

        $('#busProductStore').on('select', function () {
            $('#busProduct').jqxGrid('updatebounddata');
        });
    });

    $(function () {
        var url = "listProduct";
        var source =
        {
            datatype: "json",
            datafields: [
                {name: 'productId', type: 'string'},
                {name: 'productName', type: 'string'},
                {name: 'uom', type: 'string'},
                {name: 'price', type: 'float'}
            ],
            root: "data",
            url: url,
            type: 'POST'
        };
        var dataAdapter = new $.jqx.dataAdapter(source, {
            formatData: function () {
                return {
                    productStoreId: $('#busProductStore').jqxDropDownList('val')
                }
            }
        });

        $('#busProduct').jqxGrid({
            width: '100%',
            autoheight: true,
            source: dataAdapter,
            sortable: true,
            filterable: true,
            showfilterrow: true,
            pageable: true,
            selectionmode: 'checkbox',
            altrows: true,
            theme: 'olbius',
            localization: getLocalization(),
            pagesize: 20,
            pagesizeoptions: ['20', '30', '50'],
            columns: [
                {text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', width: 250},
                {text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName'},
                {text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', datafield: 'uom', width: 100, filtertype: 'checkedlist'},
                {text: '${StringUtil.wrapString(uiLabelMap.BSUnitPrice)}', datafield: 'price', width: 100, filterable: false}
            ]
        });

        $('#busPublic').on('click', function () {
            var products = [];
            var rowindexes = $('#busProduct').jqxGrid('getselectedrowindexes');
            for (var i in rowindexes) {
                var data = $('#busProduct').jqxGrid('getrowdata', rowindexes[i]);
                products.push(data['productId']);
            }

            if(products.length > 0) {
                $.ajax({
                    url: 'busProductPublic',
                    type: 'POST',
                    async: true,
                    data: {
                        products: products,
                        productStoreId: $('#busProductStore').jqxDropDownList('val')
                    },
                    beforeSend: function () {
                        $('#busPublic').attr("disabled", true);
                        $('#busProduct').jqxGrid('showloadelement');
                    },
                    success : function () {
                        $('#busProduct').jqxGrid('clearselection');
                    },
                    complete: function () {
                        $('#busProduct').jqxGrid('hideloadelement');
                        $('#busPublic').attr("disabled", false);
                    }
                });
            }
        });
    })

</script>