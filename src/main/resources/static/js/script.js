/*
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
*/


const search=()=>{
    //console.log("searching");


    let query=$("#search-input").val();
    
//if nothing is searched then hide the serach box
    if(query==""){
        $(".search-result").hide();
    }
    
    else{
         // will search here if anything enters in search bar
        console.log(query);

        //sending request to server
        let url=`http://localhost:8080/search/${query}`;


        //fetch is an interface used to access/manipulate the HTTP(request & response)

        //.then () function allows multiple chaining calls, each calls return a value and pass on to the next function as an input
        
        
        
        fetch(url)
        .then((response)=>{

            return response.json();
        })
        .then((data)=>{

            //console.log(data);


            let text=`<div class='list-group'>`

            data.forEach((contact)=>{
               
                text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
            });  

            text+=`</div>`

            $(".search-result").html(text);
            $(".search-result").show();
        });

        
    }
};

