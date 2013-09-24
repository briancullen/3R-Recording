<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
	<link href="/css/footable/footable.core.min.css" rel="stylesheet" type="text/css" />
	<link href="/css/footable/footable.standalone.min.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css" />
	<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script>
	<script src="/js/footable/footable.js" type="text/javascript"></script>
	<script src="/js/footable/footable.sort.min.js" type="text/javascript"></script>
	
	<script src="/js/Grades.js"></script>
	<script src="/js/pupil/DataStore.js"></script>
	<script src="/js/pupil/ManageTargets.js"></script>
	<script src="/js/pupil/ManageProgress.js"></script>
	
	
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
					<li id="addProgressBtn"><a href="#progressRecordDialog" data-rel="dialog" data-icon="plus">Record Progress</a></li>
					<li><a href="#manageTargetsPage" data-icon="gear">Manage Targets</a></li>
					<li><a href="#pupilRecordsYearPopup" data-position-to="window" data-rel="popup" data-icon="gear">Change Year</a></li>
				</ul>
			</div>
		</div>
		<div data-role="content">
			<div class="ui-bar-b" >
				<p id="pupilRecordBanner" style="text-align:center">${UserEntity.name} (${UserEmail}) in Year ${UserEntity.form.value.yearGroup}</p>
			</div>

		
			<table id="pupilRecordTable" class="footable" data-page-size="5">
		     <thead>
		       <tr> 
		         <th data-sort-initial="true">Subject</th>
		         <th data-hide="phone, tablet">3 Levels</th>
		         <th data-hide="phone, tablet">4 Levels</th>
		         <th data-hide="phone, tablet">5 Levels</th>
		         <th>Current Level</th>
		         <th data-hide="phone" data-sort-ignore="true">Target</th>
		         <th data-sort-ignore="true" data-hide="phone, tablet, desktop">Controls</th>
		       </tr>
		     </thead>
		     <tbody>
		     </tbody>
		    </table>
		    <div style="display:none;" id="manageProgressNotFoundBanner" class="ui-bar ui-bar-e">
				<h3>No records found for the specified criteria - please try again.</h3>
			</div>
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
		        	<li><a data-recordtype="Responsibility" class="ui-btn-active">Responsibility</a></li>
		            <li><a data-recordtype="Resilience">Resilience</a></li>
		            <li><a data-recordtype="Reflection">Reflection</a></li>
		        </ul>
			</div>
		</div>
	</div>
	
	<div data-role="page" id="progressRecordDialog">
		<div data-role="header">
			<h1>New Progress Record</h1>
		</div>
		<div data-role="content">
			<form id="recordDialogForm">
				<div data-role="fieldcontain">
					<label for="recordDialogSubject">Subject:</label>
					<select name="recordDialogSubject" id="recordDialogSubject" data-mini="true">

					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="recordDialogCurrentLevel">Current Level:</label>
					<select name="recordDialogCurrentLevel" id="recordDialogCurrentLevel" data-mini="true">
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="recordDialogTarget">Next Steps:</label>
				    <textarea cols="40" rows="8" name="recordDialogTarget" placeholder="How can you improve to meet you targets?" id="recordDialogTarget"></textarea>
				</div>
				<a id="recordDialogSubmit" data-rel="back" data-role="button" data-theme="b">Submit</a>
			    <a data-rel="back" data-role="button">Cancel</a>
			</form>
		</div>
	</div>
	
	<div data-role="page" id="manageTargetsPage">
		<div data-role="header">
			<h1>Manage Subject Targets</h1>
			<a href="#pupilRecordsPage" class="ui-btn-right" data-icon="back">Back</a>
		</div>
		<div data-role="content">
			<div class="ui-grid-a">
				<div class="ui-block-a">
					<fieldset id="manageTargetsKeyStage" style="text-align: center;" data-role="controlgroup" data-type="horizontal" >
	        			<input type="radio" name="manageTargetsKeyStage" id="manageTargetsKeyStage3" value="3" checked="checked">
	        			<label for="manageTargetsKeyStage3">KS3</label>
	        			<input type="radio" name="manageTargetsKeyStage" id="manageTargetsKeyStage4" id="radio-choice-d" value="4">
	        			<label for="manageTargetsKeyStage4">KS4</label>
	        			<input type="radio" name="manageTargetsKeyStage" id="manageTargetsKeyStage5" id="radio-choice-e" value="5">
	        			<label for="manageTargetsKeyStage5">KS5</label>
					</fieldset>
					<div class="seperateItems">
						<a data-role="button" href="#manageTargetAddSubjectPopup" data-position-to="window" data-rel="popup" id="manageTargetsAddBtn" data-icon="plus">Add Target</a>
					</div>
					<ul id="manageTargetsSubjectList" data-role="listview" data-filter="true"
						data-filter-placeholder="Search subjects..." data-inset="true">
						
					</ul>
				</div>
				<div class="ui-block-b">
					<div style="display:none;" id="manageTargetBanner" class="ui-bar ui-bar-e">
						<h3>No records found for the specified criteria - please try again.</h3>
					</div>
					
					<form id="manageTargetForm">
						<div data-role="fieldcontain">
						    <label for="manageTarget3Levels">3 Levels:</label>
							<select name="ThreeLevelsTarget" id="manageTarget3Levels" data-mini="true">
							</select>
						</div>
						<div data-role="fieldcontain">
						    <label for="manageTarget4Levels">4 Levels:</label>
							<select name="FourLevelsTarget" id="manageTarget4Levels" data-mini="true">
							</select>
						</div>
						<div data-role="fieldcontain">
						    <label for="manageTarget5Levels">5 Levels:</label>
							<select name="FiveLevelsTarget" id="manageTarget5Levels" data-mini="true">
							</select>
						</div>
						<fieldset class="ui-grid-a">
    						<div class="ui-block-c"><a data-role="button" id="manageTargetsUpdate">Update</a></div>
    						<div class="ui-block-d"><a data-role="button" id="manageTargetsRemove">Remove</a></div>
						</fieldset>
					</form>
					
				</div>
			</div>
			<div data-role="popup" id="manageTargetAddSubjectPopup" data-dismissible="false" data-theme="d" data-overlay-theme="b" style="max-width:360px;">
				<div data-role="header">
				    <h3>Select Subject</h3>
				</div>
				<div data-role="content">
					<div class="seperateItems">
				    	<p>Please select the subject for which you wish to enter a target.</p>
				    </div>

					<ul id="manageTargetsPopupSubjectList" data-role="listview" data-filter="true"
						data-filter-placeholder="Search subjects..." data-inset="true">
					</ul>
				    <a data-role="button" data-theme="b" data-rel="back">Cancel</a>
			    </div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	
		dataStore.pupil = { displayName: "${UserEntity.name}",
		                         email: "${UserEmail}",
	                         	 form: "${UserEntity.form.value.formCode}",
		                         year: "${UserEntity.form.value.yearGroup}",
		                         keyStage: "${UserEntity.form.value.keyStage}",
		                         key: "${UserEntityKey}" };
		
		function loading(showOrHide) {
		    setTimeout(function(){
		        jQuery.mobile.loading(showOrHide);
		    }, 1); 
		}
		
		dataStore.initialise();
		$('#pupilRecordsPage').on("pageinit", progressHandler.initialise);
		$('#manageTargetsPage').on("pageinit", targetsHandler.initialise);
	</script>	
</body>
</html>