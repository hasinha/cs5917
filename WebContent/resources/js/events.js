var initDone = false, uid;

	function populateAttacks(attacks){
		var mainLabel = $('<label>').text('Attacks');
		var attacksTable = $('<table>');
		var row = $('<tr>');
		var labelTh = $('<th>').text('Attack Label');
		var membersTh = $('<th>').text('Members');
		var attackedTh = $('<th>').text('Attacked');
		row.append(labelTh).append(membersTh).append(attackedTh);
		attacksTable.append(row);
		$.each(attacks, function(index, value){
			var newRow = $('<tr>');
			var labelTd = $('<td>').text(value.attackLabel).attr('class', 'attClick');
			var memberTd = $('<td>').text(value.attackMembers);
			var attackedTd = $('<td>').text(value.attacked.label);
			newRow.append(labelTd).append(memberTd).append(attackedTd);
			attacksTable.append(newRow);
		})
		$('#attacksTable').append(mainLabel).append(attacksTable);
		$('.attClick').click(function(){
			highlightAttack(this);
			$('.argClick, .attClick, td.inArg').css('background-color', '#e5e5e5');
			$(this).css('background-color', 'red');
		})
	}
	
	function populateExtensions(candidates){
		var mainLabel = $('<label>').text('Extensions');
		var extensionsTable = $('<table>');
		var row = $('<tr>');
		var rankTh = $('<th>').text('Rank');
		var inTh = $('<th>').text('IN');
		var outTh = $('<th>').text('OUT');
		var undecTh = $('<th>').text('UNDEC');
		row.append(rankTh).append(inTh).append(outTh).append(undecTh);
		extensionsTable.append(row)
		var i = 1;
		$.each(candidates, function(index, value){
			var newRow = $('<tr>').attr('class', 'extClick');
			var rankTd = $('<td>').text(i);
			var inTd = $('<td>').text(value.inArguments).attr('class', 'inArg');
			var outTd = $('<td>').text(value.outArguments).attr('class', 'outArg');
			var undecTd = $('<td>').text(value.undecArguments).attr('class', 'undecArg');
			newRow.append(rankTd).append(inTd).append(outTd).append(undecTd);
			extensionsTable.append(newRow);
			i++;
		});
		$('#extensionsTable').append(mainLabel).append(extensionsTable);
		$('.extClick').click(function(){
			highlightExtension(this);
			$('.extClick td.inArg, .argClick, .attClick').css('background-color', '#e5e5e5');
			$(this).find('td.inArg').css('background-color', 'red');
		})
	}

function populateThings(data){
	dataO = JSON.parse(data);
				var rankingTable = $('<table>');
				var row = $('<tr>');
				var argumentsTable = $('<table>');
				var argRow = $('<tr>');
				var th = $('<th>').text('Arguments');
				var rankTh = $('<th>').text('Rank');
				var i = 1;
				argRow.append(th);
				row.append($('<th>').text('Argument')).append($('<th>').text('Strength'));
				rankingTable.append(row);
				argRow.append(rankTh);
				argumentsTable.append(argRow);
				$.each(dataO.arguments, function(index, value){
					var newRow = $('<tr>');
					var newTd = $('<td>').text(value.label).attr('class', 'argClick');
					var rankTd = $('<td>').text(i);
					newRow.append(newTd);
					newRow.append(rankTd);
					argumentsTable.append(newRow);
					var newRow2 = $('<tr>');
					var newTd2 = $("<td>").text(value.label);
					var strengthTd = $("<td>");
					if ($('#rankingSemantic').val() == 'discussion' || $('#rankingSemantic').val() == 'meanBurden' || $('#rankingSemantic').val() == 'minBurden'){
						strengthTd.text(value.pathCountStr);
					} else if($('#rankingSemantic').val() == 'nhCat' || $('#rankingSemantic').val() == 'meanCat'){
						strengthTd.text(value.strengthValue);
					}
					newRow2.append(newTd2);
					newRow2.append(strengthTd);
					rankingTable.append(newRow2);
					i++;
				})
				var label = $('#rankingSemantic>option:selected').text();
				var mainLabel = $('<label>').text('Rankings');
				$("#rankTable").append(mainLabel).append(rankingTable);
				var argLabel = $('<label>').text('Arguments');
				$('#argumentTable').append(argLabel).append(argumentsTable);
				$('.argClick').click(function(){
					highlightNode(this);
					$('.argClick, .attClick, td.inArg').css('background-color', '#e5e5e5');
					$(this).css('background-color', 'red');
				});
				populateAttacks(dataO.attackRelation);
				populateExtensions(dataO.candidates);
				
				generate(dataO);
				
				$("#buttonDiv").slideDown('fast');
				if(!initDone){ 
					$("#showRankings").click(function(){
						$(this).toggleClass('highlight');
						$("#rankTable").slideToggle('fast');
					})
					$("#showArguments").click(function(){
						$(this).toggleClass('highlight');
						$("#argumentTable").slideToggle('fast');
					})
					$("#showAttacks").click(function(){
						$(this).toggleClass('highlight');
						$("#attacksTable").slideToggle('fast');
					})
					$("#showExtensions").click(function(){
						$(this).toggleClass('highlight');
						$("#extensionsTable").slideToggle('fast');
					})
				}
				initDone = true;
			setTimeout($.unblockUI, 1000);
} 
$(document).ready(function(){
	$.when($.ajax({
		url: 'rest/getUid',
		type: 'get',
		success: function(data){
			uid = data;
		}
	})).then(function() {
		var socket = new SockJS('/chat');
                stompClient = Stomp.over(socket);  
                stompClient.connect({}, function(frame) {
                    console.log('Connected: ' + frame);
                    stompClient.subscribe('/topic/' + uid, function(messageOutput) {
						populateThings(messageOutput.body);
                    });
                });
	})
	
	$("#rankingSemantic").change(function(){
		$("#imgDiv>img").attr("hidden", true);
		var chosen = $(this).val();
		$("#" + chosen).attr("hidden", false);
	})

	$('#canvasWidth').val($('#sitemap').attr('width'));
	$('#canvasHeight').val($('#sitemap').attr('height'));
	$('#form1').submit(function(e){
		$.blockUI({ css: { 
            border: 'none', 
            padding: '15px', 
            backgroundColor: '#000', 
            '-webkit-border-radius': '10px', 
            '-moz-border-radius': '10px', 
            opacity: .5, 
            color: '#fff' 
        } }); 
		$('#overlay').css("display", 'block');
		$("#canvasWidth, #canvasHeight").attr('disabled', true);
		$("#rankTable, #argumentTable, #attacksTable, #extensionsTable").empty();
	    e.preventDefault();
	    var url=$(this).closest('form').attr('action');
		var formData = new FormData($('#form1')[0])
		formData.append("uid", uid);
	    $.ajax({
	        url:url,
	        type:'post',
	        data: formData,
	        processData: false,
	        contentType: false,
	        success:function(data){
				console.log("Form Uploaded");
	       },
			error: function(xhr, status, error){
				$.unblockUI();
				alert(xhr.responseText);
			}	
		});
	});
	
	$("#canvasWidth").on('change', function(){
		$("#sitemap").attr('width', $("#canvasWidth").val());
		$("#mainDiv").css('width', parseInt($(this).val()) + 580);
	})
	
	$("#canvasHeight").on('change', function(){
		$("#sitemap").attr('height', $("#canvasHeight").val());
	})
})