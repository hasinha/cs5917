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
			<option value="meanCat">Mean Categoriser</option></select> <br /> <input
			type="submit" value="Upload"> Upload
	</form>
	<div style='width: 100%; overflow: hidden; display: inline-block'>
		<div style='display: table-row'>
			<div style='width: 1280px; display: table-cell; float: left'>
				<canvas id="sitemap" width="1280" height="720"></canvas>
			</div>
			<div style='display: table-cell; float: left' id='argumentTable'></div>
			<div style='display: table-cell; float: right' id='attackTable'></div>
		</div>
	</div>
	<div id="rankTable" hidden="true">
		<label>Argument Rankings</label><br />
	</div>
</body>
</html>