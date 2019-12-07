function formatcurrency(num, uom){
      decimalseparator = ",";
          thousandsseparator = ".";
          currencysymbol = "đ";
          if(typeof(uom) == "undefined" || uom == null){
           uom = "${currencyUomId?if_exists}";
          }
      if(uom == "USD"){
       currencysymbol = "$";
       decimalseparator = ".";
           thousandsseparator = ",";
      }else if(uom == "EUR"){
       currencysymbol = "€";
       decimalseparator = ".";
           thousandsseparator = ",";
      }
         var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
         if(str.indexOf(".") > 0) {
             parts = str.split(".");
             str = parts[0];
         }
         str = str.split("").reverse();
         for(var j = 0, len = str.length; j < len; j++) {
             if(str[j] != ",") {
                 output.push(str[j]);
                 if(i%3 == 0 && j < (len - 1)) {
                     output.push(thousandsseparator);
                 }
                 i++;
             }
         }
         formatted = output.reverse().join("");
         /*console(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);*/
         return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
     }	