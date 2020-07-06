$(document).ready(function(){
	$('#form1').submit(function(e){
		$("#rankTable").empty();
	    e.preventDefault();
	    var url=$(this).closest('form').attr('action');
	    $.ajax({
	        url:url,
	        type:'post',
	        data: new FormData($('#form1')[0]),
	        processData: false,
	        contentType: false,
	        success:function(data){
				dataO = JSON.parse(data);
				console.log(dataO);
				var table = $('<table>');
				var row = $('<tr>');
				$.each(dataO.arguments, function(index, value){
					var th = $('<th>').text(value.label);
					row.append(th);
				})
				table.append(row);
				var row2 = $('<tr>');
				$.each(dataO.arguments, function(index, value){
					if ($('#rankingSemantic').val() == 'discussion' || $('#rankingSemantic').val() == 'meanBurden' || $('#rankingSemantic').val() == 'minBurden'){
						var td = $('<td>').text(value.pathCountStr);
					} else if($('#rankingSemantic').val() == 'nhCat' || $('#rankingSemantic').val() == 'meanCat'){
						var td = $('<td>').text(value.strengthValue);
					}
					row2.append(td);
				})
				table.append(row2);
				if ($('#rankingSemantic').val() == 'discussion'){
						var label = $('<label>').text('Discussion Based Ranking')
				} else if($('#rankingSemantic').val() == 'nhCat'){
						var label = $('<label>').text('Nh Categoriser')
				} else if($('#rankingSemantic').val() == 'meanBurden'){
						var label = $('<label>').text('Mean Burden')
				} else if($('#rankingSemantic').val() == 'minBurden'){
						var label = $('<label>').text('Min Burden')
				} else if($('#rankingSemantic').val() == 'meanCat'){
						var label = $('<label>').text('Mean Categoriser')
				}
				$("#rankTable").append(label);
				$("#rankTable").append(table);
				$("#rankTable").attr('hidden', false);
				
				generate(dataO);
//				alert("Form uploaded");
//				$("#form1").attr('hidden', true);
	       }
		});
	});
})