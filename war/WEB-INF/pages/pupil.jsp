<!DOCTYPE html>
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
	
	<script type="text/javascript">
		function loading(showOrHide) {
		    setTimeout(function(){
		        $.mobile.loading(showOrHide);
		    }, 20); 
		}
	</script>
	
	<script src="/js/logging/ClientLogging.js"></script>
	<script src="/js/Grades.js"></script>
	<script src="/js/pupil/DataStore.js"></script>
	<script src="/js/pupil/ManageTargets.js"></script>
	<script src="/js/pupil/ManageProgress.js"></script>
	<script src="/js/pupil/ManageProfile.js"></script>
	
	
	<link rel="stylesheet" href="/css/pupil.css" />
	<title>3R Target Records</title>
</head>
<body>
	<div data-role="page" id="pupilRecordsPage">
		<div data-role="header" data-position="fixed">
			<h1>3R Target Records</h1>
			<a href="#changePupilDetails" data-rel="dialog" class="ui-btn-left" data-icon="gear">Pupil Profile</a>
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
		         <th>Expected Level</th>
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
		<div id="pupilRecordsPageFooter" data-role="footer" data-position="fixed">
			<div data-role="navbar">
		        <ul>
		        	<li><a data-recordtype="Responsibility" class="ui-btn-active">Responsibility</a></li>
		            <li><a data-recordtype="Resilience">Resilience</a></li>
		            <li><a data-recordtype="Reflection">Reflection</a></li>
		        </ul>
			</div>
		</div>
	</div>
	
	<div data-role="page" id="changePupilDetails">
		<div data-role="header">
			<h1>Pupil Profile</h1>
		</div>
		<div data-role="content">
			<form id="recordDialogForm">
				<div data-role="fieldcontain">
					<label for="pupilProfileEmail">email:</label>
					<input type="text" value="${UserEmail}" readonly name="UserEmail" id="pupilProfileEmail" data-mini="true">
				</div>
				<div data-role="fieldcontain">
					<label for="pupilProfileName">Name:</label>
					<input type="text" placeholder="Real Name" name="UserName" id="pupilProfileName" data-mini="true">
				</div>
				<div data-role="fieldcontain">
					<label for="pupilProfileYear">Year:</label>
					<select name="pupilProfileYear" id="pupilProfileYear" data-mini="true">
						<option value="7">7</option>
						<option value="8">8</option>
						<option value="9">9</option>
						<option value="10">10</option>
						<option value="11">11</option>
						<option value="12">12</option>
						<option value="13">13</option>
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="pupilProfileForm">Form:</label>
					<select name="UserFormKey" id="pupilProfileForm" data-mini="true">
					</select>
				</div>
				<a id="pupilProfileSubmit" data-rel="back" data-role="button" data-theme="b">Submit</a>
			    <a data-rel="back" data-role="button">Cancel</a>
			</form>
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
				    <label for="recordDialogCurrentLevel">Expected Level:</label>
					<select name="recordDialogCurrentLevel" id="recordDialogCurrentLevel" data-mini="true">
					</select>
				</div>
				<div data-role="fieldcontain">
          <label for="recordDialogCurrentLevel">Target Type:</label>
          <select name="recordDialogTargetType" id="recordDialogTargetType" data-mini="true">
            <option value="Responsibility">Responsibility</option>
            <option value="Resilience">Resilience</option>
            <option value="Reflection">Reflection</option>
          </select>
        </div>
				<div data-role="fieldcontain">
				    <label for="recordDialogTarget">Next Steps:</label>
				    <textarea cols="40" rows="8" name="recordDialogTarget" placeholder="How can you improve to meet your targets?" id="recordDialogTarget"></textarea>
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
					<select name="manageTargetsStage" id="manageTargetsStage">
						<option value="7">Year 7</option>
						<option value="8">Year 8</option>
						<option value="4">Key Stage 4</option>
						<option value="5">Key Stage 5</option>
					</select>
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
		                         stage: "${UserEntity.form.value.stage}",
		                         formKey: "${FormEntityKey}",
		                         key: "${UserEntityKey}" };
		
		dataStore.initialise();
		$('#pupilRecordsPage').on("pageinit", progressHandler.initialise);
		$('#manageTargetsPage').on("pageinit", targetsHandler.initialise);
		$('#changePupilDetails').on('pageinit', profileHandler.initialise);
	</script>	
</body>
</html>