$(document).ready(function(){
	$('#form1').submit(function(e){
	    e.preventDefault();
	    var url=$(this).closest('form').attr('action');
	    $.ajax({
	        url:url,
	        type:'post',
	        data: new FormData($('#form1')[0]),
	        processData: false,
	        contentType: false,
	        success:function(){
				alert("Form uploaded");
				$("#form1").attr('hidden', true);
	       }
		});
	});
})