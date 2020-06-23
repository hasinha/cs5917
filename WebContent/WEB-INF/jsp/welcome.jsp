<html>
<head>
<title>CS5197 Project</title>
<style type="text/css">
/* body { */
/* 	background-image: url('https://cdn.crunchify.com/bg.png'); */
/* } */
</style>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="./resources/js/arbor.js"></script>
<script src="./resources/js/arbor-tween.js"></script>
<script src="./resources/js/arbor-graphics.js"></script>
<script src="./resources/js/main.js"></script>
<script src="./resources/js/events.js"></script>
</head>
<body>
	<br>
	<div>The following is a test page. Test if page is served</div>
	<br>
	<form method="POST" id="form1" action="rest/uploadFile"
		enctype="multipart/form-data">
		File to upload: <input type="file" name="file"><br /> <br />
		<input type="submit" value="Upload"> Upload
	</form>
	<canvas id="sitemap" width="1280" height="720"></canvas>
</body>
</html>