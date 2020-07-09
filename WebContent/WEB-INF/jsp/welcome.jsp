<html>
<head>
<title>CS5197 Project</title>
<style>
table, td, th {
	padding: 10px;
	border: 2px solid #1c87c9;
	border-radius: 5px;
	background-color: #e5e5e5;
	text-align: center;
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
	<canvas id="sitemap" width="1280" height="720"></canvas>
	<div id="rankTable" hidden="true" style="padding: 20px">
		<label>Argument Rankings</label><br />
	</div>
	<div style='padding: 20px' id='argumentTable'></div>
	<div style='padding: 20px' id='attackTable'></div>
	<div id="extensionTable" hidden="true" style="padding: 20px">
		<label>Extensions</label>
	</div>
</body>
</html>