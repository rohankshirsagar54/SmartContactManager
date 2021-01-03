
function toggleSidebar(){

if($('.sidebar').is(":visible")){

$(".sidebar").css("display","");
$(".content").css("margin-top","0%");

}
else{

$(".sidebar").css("display","block");
$(".content").css("margin-top","20%");

}
};