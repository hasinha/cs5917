$(document).ready(function(){
	$('#canvasWidth').val($('#sitemap').attr('width'));
	$('#canvasHeight').val($('#sitemap').attr('height'));
	$('#form1').submit(function(e){
		$("#canvasWidth, #canvasHeight").attr('disabled', true);
		$("#rankTable, #argumentTable, #attackTable, #extensionTable").empty();
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
				$("#argumentTable").append($('<label>').text('Arguments'));
				$('#argumentTable').append(argTable);
				$('#argumentTable').attr('hidden', false);
				populateAttacks(dataO.attackRelation);
				populateExtensions(dataO.candidates);
				
				generate(dataO);
				
				$('.argClick').click(function(){
					highlightNode(this);
					$('.argClick, .attClick, td.inArg').css('background-color', '#e5e5e5');
					$(this).css('background-color', 'red');
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
		$('#attackTable').append($('<label>').text('Attacks'));
		$('#attackTable').append(attTable);
		$('.attClick').click(function(){
			highlightAttack(this);
			$('.argClick, .attClick, td.inArg').css('background-color', '#e5e5e5');
			$(this).css('background-color', 'red');
		})
	}
	
	function populateExtensions(candidates){
		var extTable = $('<table>');
		var row = $('<tr>');
		var inTh = $('<th>').text('IN');
		var outTh = $('<th>').text('OUT');
		var undecTh = $('<th>').text('UNDEC');
		row.append(inTh).append(outTh).append(undecTh);
		extTable.append(row)
		$.each(candidates, function(index, value){
			var newRow = $('<tr>').attr('class', 'extClick');
			var inTd = $('<td>').text(value.inArguments).attr('class', 'inArg');
			var outTd = $('<td>').text(value.outArguments).attr('class', 'outArg');
			var undecTd = $('<td>').text(value.undecArguments).attr('class', 'undecArg');
			newRow.append(inTd).append(outTd).append(undecTd);
			extTable.append(newRow);
		});
		$('#extensionTable').append(extTable).attr('hidden', false);
		$('.extClick').click(function(){
			highlightExtension(this);
			$('.extClick td.inArg, .argClick, .attClick').css('background-color', '#e5e5e5');
			$(this).find('td.inArg').css('background-color', 'red');
		})
	}
	
	$("#canvasWidth").on('change', function(){
		$("#sitemap").attr('width', $("#canvasWidth").val());
	})
	
	$("#canvasHeight").on('change', function(){
		$("#sitemap").attr('height', $("#canvasHeight").val());
	})
})