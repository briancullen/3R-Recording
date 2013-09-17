<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css" />
	<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script>
	<script src="/js/mustache.js"></script>
	<script src="/js/pupil.js"></script>
	<script src="/js/manageTargets.js"></script>
	
	<link rel="stylesheet" href="/css/pupil.css" />
	<title>3R Target Records</title>
</head>
<body>
	<div data-role="page" id="pupilRecordsPage">
		<div data-role="header" data-position="fixed">
			<h1>3R Target Records</h1>
			<a href="${LogoutURL}" data-ajax="false" class="ui-btn-right" data-icon="alert">Logout</a>
			<div data-role="navbar" data-iconpos="left" >
				<ul>
					<li><a href="#pupilRecordDialog" data-rel="dialog" data-icon="plus">Record Progress</a></li>
					<li><a href="#manageTargetsPage" data-icon="gear">Manage Targets</a></li>
					<li><a href="#pupilRecordsYearPopup" data-position-to="window" data-rel="popup" data-icon="gear">Change Year</a></li>
				</ul>
			</div>
		</div>
		<div data-role="content">
			<div class="ui-bar-b" >
				<p id="pupilRecordBanner" style="text-align:center">${UserEntity.name} (${UserEmail}) for Year ${UserEntity.form.value.yearGroup}</p>
			</div>

		
			<table data-role="table" id="pupilRecordTable" data-mode="reflow" class="ui-responsive table-stroke">
		     <thead>
		       <tr class="ui-bar-d">
		         <th data-priority="1">Subject</th>
		         <th data-priority="3" class="centerColumn">3 Levels</th>
		         <th data-priority="1" class="centerColumn">4 Levels</th>
		         <th data-priority="3" class="centerColumn">5 Levels</th>
		         <th data-priority="1" class="centerColumn">Current Level</th>
		         <th data-priority="2">Target</th>
		         <th> </th>
		       </tr>
		     </thead>
		     <tbody>
		     </tbody>
		    </table>
			<div data-role="popup" id="pupilRecordsYearPopup" data-dismissible="false" data-theme="d" data-overlay-theme="b" style="max-width:360px;">
				<div data-role="header">
				    <h3>View Record</h3>
				</div>
				<div data-role="content">
				    <p>Please select the year for which you want to view records.</p>

					<ul data-role="listview" data-inset="true">
					    <li><a data-year="7" data-rel="back">Year 7</a></li>
					    <li><a data-year="8" data-rel="back">Year 8</a></li>
					    <li><a data-year="9" data-rel="back">Year 9</a></li>
					    <li><a data-year="10" data-rel="back">Year 10</a></li>
					    <li><a data-year="11" data-rel="back">Year 11</a></li>
					    <li><a data-year="12" data-rel="back">Year 12</a></li>
					    <li><a data-year="13" data-rel="back">Year 13</a></li>
					</ul>
				    <a data-role="button" data-theme="b" data-rel="back">Cancel</a>
			    </div>
			</div>			
		</div>
		<div id="pupilRecordsPageFooter"data-role="footer" data-position="fixed">
			<div data-role="navbar">
		        <ul>
		            <li><a data-recordtype="Resilience" class="ui-btn-active">Resilience</a></li>
		            <li><a data-recordtype="Responsibility">Responsibility</a></li>
		            <li><a data-recordtype="Reflection">Reflection</a></li>
		        </ul>
			</div>
		</div>
	</div>
	
	<div data-role="page" id="manageTargetsPage">
		<div data-role="header">
			<h1>Manage Subject Targets</h1>
		</div>
		<div data-role="content">
			<div class="ui-grid-a">
				<div class="ui-block-a">
						<ul id="manageTargetsSubjectList" data-role="listview" data-filter="true"
							data-filter-placeholder="Search subjects..." data-inset="true">
						</ul>
				</div>
				<div class="ui-block-b">Doooomed I tell you!</div>
				</div>
		</div>
	</div>
	
	<script type="text/javascript">
	
		var pupilInformation = { displayName: "${UserEntity.name}",
		                         email: "${UserEmail}",
	                         	 form: "${UserEntity.form.value.formCode}",
		                         year: "${UserEntity.form.value.yearGroup}", 
		                         key: "${UserEntityKey}" };
		$('#pupilRecordsPage').on("pageinit", initialise());
		$('#manageTargetsPage').on("pageinit", initialiseManageTargetPage);
	</script>	
</body>
</html>