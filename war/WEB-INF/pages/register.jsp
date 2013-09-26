<!DOCTYPE HTML>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css" />
	<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script>
	<script src="/js/pupil/Registration.js"></script>
	
	<title>Pupil Registration</title>
</head>
<body>
	<div data-role="page" id="studentRegistration">
		<div data-role="header" data-position="fixed">
			<h1>Student Registration</h1>
		</div>
		<div data-role="content">
			<form id="studentRegistrationForm">
				<div data-role="fieldcontain">
					<label for="studentRegistrationFormEmail">email:</label>
					<input type="text" value="${UserEmail}" readonly name="UserEmail" id="studentRegistrationFormEmail" data-mini="true">
					</select>
				</div>
				<div data-role="fieldcontain">
					<label for="studentRegistrationFormName">Name:</label>
					<input type="text" placeholder="Real Name" name="UserName" id="studentRegistrationFormName" data-mini="true">
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="studentRegistrationYearGroup">Year Group:</label>
					<select id="studentRegistrationYearGroup" data-mini="true">
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="studentRegistrationFormCode">Form Code:</label>
					<select name="UserFormKey" id="studentRegistrationFormCode" data-mini="true">
					</select>
				</div>
				<a id="studentRegistrationFormSubmit" data-role="button" data-theme="b">Submit</a>
			    <a data-rel="back" data-role="button">Cancel</a>
			</form>
		</div>
	</div>
	

	
	<script type="text/javascript">
		
		$('#studentRegistration').on("pageshow", initialise(${FormInformation}));
	</script>	
</body>
</html>