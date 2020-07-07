$(document).ready(function(){
	$('#form1').submit(function(e){
		$("#rankTable, #argumentTable, #attackTable").empty();
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
				var argTable = $('<table>');
				var argRow = $('<tr>');
				var th = $('<th>').text('Arguments');
				var rankTh = $('<th>').text('Rank');
				var i = 1;
				argRow.append(th);
				argRow.append(rankTh);
				argTable.append(argRow);
				$.each(dataO.arguments, function(index, value){
					var newRow = $('<tr>');
					var newTd = $('<td>').text(value.label).attr('class', 'argClick');
					var rankTd = $('<td>').text(i);
					newRow.append(newTd);
					newRow.append(rankTd);
					argTable.append(newRow);
					var th = $('<th>').text(value.label);
					row.append(th);
					i++;
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
				var label = $('#rankingSemantic>option:selected').text();
				$("#rankTable").append(label);
				$("#rankTable").append(table);
				$("#rankTable").attr('hidden', false);
				$('#argumentTable').append(argTable);
				populateAttacks(dataO.attackRelation);
				
				generate(dataO);
				
				$('.argClick').click(function(){
					highlightNode(this);
				});
	       }
		});
	});
	
	function populateAttacks(attacks){
		var attTable = $('<table>');
		var row = $('<tr>');
		var labelTh = $('<th>').text('Attack Label');
		var membersTh = $('<th>').text('Members');
		var attackedTh = $('<th>').text('Attacked');
		row.append(labelTh).append(membersTh).append(attackedTh);
		attTable.append(row);
		$.each(attacks, function(index, value){
			var newRow = $('<tr>');
			var labelTd = $('<td>').text(value.attackLabel).attr('class', 'attClick');
			var memberTd = $('<td>').text(value.attackMembers);
			var attackedTd = $('<td>').text(value.attacked.label);
			newRow.append(labelTd).append(memberTd).append(attackedTd);
			attTable.append(newRow);
		})
		$('#attackTable').append(attTable);
		$('.attClick').click(function(){
			highlightAttack(this);
		})
	}
})