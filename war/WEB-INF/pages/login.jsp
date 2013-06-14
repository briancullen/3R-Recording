<!DOCTYPE HTML>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.css" />
	<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.js"></script>
	<script type="text/javascript">
		
	</script>
</head>
<body>
	<div data-role="page">

		<div data-role="header" data-position="fixed">
			<h1>Coursework Tracker - Login Page for ${user }</h1>
		</div><!-- /header -->
	
		<div data-role="content">
			<a href="#dialog" data-role="button">Continue...</a>						
		</div><!-- /content -->
	
		<div data-role="footer" data-position="fixed">
			<h4>Page Footer</h4>
		</div><!-- /footer -->
	</div><!-- /page -->
	<div data-role="dialog" id="dialog">
		<div data-role="header">
			<h1>Login Status</h1>
		</div>
		<div data-role="content">
			<h1>Not Logged In!</h1>
			You are currently now logged into google and cannot, therefore, use this program. Please logon to google before
			trying to continue.
			<a href="${loginURL}" data-role="button">Login to Google</a>
		</div>
	</div> <!-- /dialog -->

</body>
</html>