<!DOCTYPE HTML>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.css" />
	<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.3.1/jquery.mobile-1.3.1.min.js"></script>
	<script src="/js/mustache.js"></script>
	<script src="/js/student.js"></script>
	
	<title>3Rs Recording</title>
</head>
<body>
	<div data-role="page" id="studentPage">
		<div data-role="header" data-position="fixed">
			<h1>3R Target Records</h1>
			
			<a href="index.html" data-ajax="false" class="ui-btn-right" data-icon="alert">Logout</a>
			<div data-role="navbar" data-iconpos="left" >
				<ul>
					<li><a href="#recordDialog" data-rel="dialog" data-icon="plus">Add New Record</a></li>
					<li><a href="#viewRecordsPopup" data-position-to="window" data-rel="popup" data-icon="gear">Change View</a></li>
				</ul>
			</div>
		</div>
		<div data-role="content">
			<div class="ui-bar-b" >
				<p id="pupilInfoHeader" style="text-align:center">Pupil Name and Year</p>
			</div>

		
			<table data-role="table" id="targetTable" data-mode="reflow" class="ui-responsive table-stroke">
		     <thead>
		       <tr class="ui-bar-d">
		         <th data-priority="1">Subject</th>
		         <th data-priority="1">3 Levels</th>
		         <th data-priority="1">4 Levels</th>
		         <th data-priority="1">5 Levels</th>
		         <th data-priority="1">Current Level</th>
		         <th data-priority="2">Target</th>
		         <th> </th>
		       </tr>
		     </thead>
		     <tbody>
		     </tbody>
		    </table>
			<div data-role="popup" id="viewRecordsPopup" data-dismissible="false" data-theme="d" data-overlay-theme="b" style="max-width:360px;">
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
					    <li><a data-rel="back">Most Recent</a></li>
					</ul>
				    <a data-role="button" data-theme="b" data-rel="back">Cancel</a>
			    </div>
			</div>			
		</div>
		<div id="RecordTypeFooterToolbar"data-role="footer" data-position="fixed">
			<div data-role="navbar">
		        <ul>
		            <li><a data-recordtype="Resilience" class="ui-btn-active">Resilience</a></li>
		            <li><a data-recordtype="Responsibility">Responsibility</a></li>
		            <li><a data-recordtype="Reflection">Reflection</a></li>
		        </ul>
			</div>
		</div>
	</div>
	
		<div data-role="page" id="recordDialog">
		<div data-role="header">
			<h1>3R Record</h1>
		</div>
		<div data-role="content">
			<form id="recordDialogForm">
				<div data-role="fieldcontain">
					<label for="recordDialogSubject">Subject:</label>
					<select name="recordDialogSubject" id="recordDialogSubject" data-mini="true">
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="recordDialogMinTargetGrade">Minimum Target:</label>
					<select name="recordDialogMinTargetGrade" id="recordDialogMinTargetGrade" data-mini="true">
				        <option value="3c">3c</option>
				        <option value="3b">3b</option>
				        <option value="3a">3a</option>
				        <option value="4c">4c</option>
				        <option value="4b">4b</option>
				        <option value="4a">4a</option>
				        <option value="5c">5c</option>
				        <option value="5b">5b</option>
				        <option value="5a">5a</option>
				        <option value="6c">6c</option>
				        <option value="6b">6b</option>
				        <option value="6a">6a</option>
				        <option value="7c">7c</option>
				        <option value="7b">7b</option>
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="recordDialogExpectedTargetGrade">Expected Target:</label>
					<select name="recordDialogExpectedTargetGrade" id="recordDialogExpectedTargetGrade" data-mini="true">
				        <option value="3c">3c</option>
				        <option value="3b">3b</option>
				        <option value="3a">3a</option>
				        <option value="4c">4c</option>
				        <option value="4b">4b</option>
				        <option value="4a">4a</option>
				        <option value="5c">5c</option>
				        <option value="5b">5b</option>
				        <option value="5a">5a</option>
				        <option value="6c">6c</option>
				        <option value="6b">6b</option>
				        <option value="6a">6a</option>
				        <option value="7c">7c</option>
				        <option value="7b">7b</option>>
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="recordDialogAspirationalTargetGrade">Aspirational Target:</label>
					<select name="recordDialogAspirationalTargetGrade" id="recordDialogAspirationalTargetGrade" data-mini="true">
				        <option value="3c">3c</option>
				        <option value="3b">3b</option>
				        <option value="3a">3a</option>
				        <option value="4c">4c</option>
				        <option value="4b">4b</option>
				        <option value="4a">4a</option>
				        <option value="5c">5c</option>
				        <option value="5b">5b</option>
				        <option value="5a">5a</option>
				        <option value="6c">6c</option>
				        <option value="6b">6b</option>
				        <option value="6a">6a</option>
				        <option value="7c">7c</option>
				        <option value="7b">7b</option>
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="recordDialogCurrentLevel">Current Level:</label>
					<select name="recordDialogCurrentLevel" id="recordDialogCurrentLevel" data-mini="true">
				        <option value="1">1</option>
				        <option value="2">2</option>
				        <option value="3">3</option>
				        <option value="4">4</option>
				        <option value="5">5</option>
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="recordDialogTarget">Evidence:</label>
				    <textarea cols="40" rows="8" name="recordDialogTarget" placeholder="How can you improve?" id="recordDialogTarget"></textarea>
				</div>
				<input type="hidden" id="recordDialogTargetType" name="recordDialogTargetType" value="Resilience">
				<input type="hidden" id="recordDialogYear" name="recordDialogYear" value="7">
				<input type="hidden" id="recordDialogTargetId" name="recordDialogTargetId">
				<a id="recordDialogSubmit" data-rel="back" data-role="button" data-theme="b">Submit</a>
			    <a data-rel="back" data-role="button">Cancel</a>
			</form>
		</div>
	</div>
	
	<script type="text/javascript">
		$('#studentPage').on("pageshow", initialise());
	</script>	
</body>
</html>