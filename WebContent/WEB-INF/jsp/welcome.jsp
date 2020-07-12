<html>
<link rel="icon" href='./resources/images/favicon.ico'
	type="image/x-icon">
<head>
<title>CS5917 Project</title>
<style>
table, td, th {
	padding: 10px;
	border: 2px solid #1c87c9;
	border-radius: 5px;
	background-color: #e5e5e5;
	text-align: center;
}

.dispTable {
	margin-top: 20px;
}
</style>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="./resources/js/arbor.js"></script>
<script src="./resources/js/arbor-tween.js"></script>
<script src="./resources/js/arbor-graphics.js"></script>
<script src="./resources/js/arbor-renderer.js"></script>
<script src="./resources/js/main.js"></script>
<script src="./resources/js/events.js"></script>
<script src="./resources/js/blockui.js"></script>
<script src="./resources/js/stomp.js"></script>
<script src="./resources/js/sockjs.js"></script>
</head>
<body>
	<br>
	<div>
		<b>Ranking SETAFs</b>
	</div>
	<br>
	<form method="POST" id="form1" action="rest/uploadFile"
		enctype="multipart/form-data">
		File to upload: <input type="file" name="file"> <label>Select
			Ranking Semantic</label> &nbsp <select id="rankingSemantic"
			name='rankingSemantic'><option value="nhCat">Nh
				Categoriser</option>
			<option value="discussion">Discussion Based</option>
			<option value="meanBurden">Mean Burden Based</option>
			<option value="minBurden">Min Burden Based</option>
			<option value="meanCat">Mean Categoriser</option></select> <label
			style="margin-left: 75px">Select Extension Semantic</label> &nbsp <select
			id="extensionSemantic" name="extensionSemantic"><option
				value="prefExt">Preferred Extensions</option></select> &nbsp <input
			type="submit" value="Submit" style="margin-left: 75px">
	</form>
	<div>
		<label>Canvas Width: </label><input type="text" id="canvasWidth"
			style="width: 40px"><label style="margin-left: 20px">Canvas
			Height: </label> <input type="text" id="canvasHeight" style="width: 40px">
	</div>
	<div style='width: 1676px; overflow: auto; display: inline-block'
		id="mainDiv">
		<div style='display: table-row; overflow: auto'>
			<div
				style="margin-top: 20px; overflow: auto; width: 396px; height: 865px; display: table-cell; float: left"
				hidden="true" id="buttonDiv">
				<input id="showRankings" class="showRankings" value="Rankings"
					type="button"> <input id="showArguments"
					class="showArguments" style="margin-left: 20px" value="Arguments"
					type="button"> <input id="showAttacks" class="showAttacks"
					style="margin-left: 20px" type="button" value="Attacks"> <input
					id="showExtensions" type="button" class="showExtensions"
					style="margin-left: 20px" value="Extensions">
				<div id="rankTable" hidden="true" class="dispTable"></div>
				<div id='argumentTable' hidden="true" class="dispTable"></div>
				<div id='attacksTable' hidden="true" class="dispTable"></div>
				<div id="extensionsTable" hidden="true" class="dispTable"></div>
			</div>
			<div style='display: table-cell; float: left; overflow: auto'>
				<canvas id="sitemap" width="1280" height="720"></canvas>
			</div>
		</div>
	</div>
</body>
</html>