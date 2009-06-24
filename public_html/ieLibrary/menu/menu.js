//<script>
function calculatop(x_ele){
var x_ret=0;
if (x_ele.tagName=='BODY') return 0;
else  x_ret=x_ele.offsetTop-x_ele.scrollTop+calculatop(x_ele.parentElement);
return x_ret
}

function calculaleft(x_ele){
var x_ret=0;
if (x_ele.tagName=='BODY') return 0
else x_ret=x_ele.offsetLeft-x_ele.scrollLeft+calculaleft(x_ele.parentElement);
return x_ret
}

