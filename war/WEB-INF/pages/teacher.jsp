<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.css" />
	<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.3.2/jquery.mobile-1.3.2.min.js"></script>
	
	<script src="/js/Grades.js"></script>
	<script src="/js/teacher/DataStore.js"></script>
	<script src="/js/teacher/ManageForms.js"></script>
	<script src="/js/teacher/ManageSubjects.js"></script>
	
	<link rel="stylesheet" href="/css/teacher.css" />
	<title>3R Target Records</title>
</head>
<body>
	<div data-role="page" id="pupilRecordsPage">
		<div data-role="header" data-position="fixed">
			<h1>3R Target Records</h1>
			<a href="${LogoutURL}" data-ajax="false" class="ui-btn-right" data-icon="alert">Logout</a>
			<div data-role="navbar" data-iconpos="left" >
				<ul>
					<li><a href="#manageFormsPage" data-icon="plus">Manage Forms</a></li>
					<li><a href="#manageSubjectsPage" data-icon="gear">Manage Subjects</a></li>
				</ul>
			</div>
		</div>
		<div data-role="content">
			${UserEntity.name}
		</div>
	</div>
	
	<div data-role="page" id="manageFormsPage">
		<div data-role="header" data-position="fixed">
			<h1>3R Target Records</h1>
			<a data-rel="back" class="ui-btn-right" data-icon="back">Back</a>
		</div>
		<div data-role=content>
			<div class="ui-grid-a">
				<div class="ui-block-a">
					<a data-role="button" id="formAddButton">Add Form</a>
					<a data-role="button" data-position-to="window" data-rel="popup" href="#formBulkAddPopup"
							class="seperateItems" id="formBulkAddButton">Bulk Add Forms</a>
					<ul id="manageFormsList" data-role="listview" data-filter="true"
						data-filter-placeholder="Search forms..." data-inset="true">
					</ul>
				</div>
				<div class="ui-block-b">
					<form id="formDetailsForm">
						<div data-role="fieldcontain">
							<label for="formKey">Form Key: </label>
							<input readonly name="FormKey" id="formKey" type="text">
						</div>
						<div data-role="fieldcontain">
							<label for="formCode">Form Code: </label>
							<input name="FormCode" id="formCode" type="text">
						</div>
						<div data-role="fieldcontain">
							<label for="formIntakeYear">Intake Year: </label>
							<select name="FormIntakeYear" id="formIntakeYear">
							</select>					
						</div>
						<fieldset class="ui-grid-a">
							<div class="ui-block-c"><a id="formUpdateButton" data-role="button">Save</a></div>
							<div class="ui-block-d"><a id="formRemoveButton" data-role="button">Remove</a></div>
						</fieldset>
					</form>
				</div>
			</div>
			<div data-role="popup" id="formBulkAddPopup" data-dismissible="false" data-theme="d" data-overlay-theme="b">
				<div data-role="header">
				    <h3>Select Subject</h3>
				</div>
				<div data-role="content">
					<form id="formBulkAddForm">
						<p>Paste the CSV data to be processed into the box below.</p>
						<textarea name="BulkFormCSVData" id="bulkFormData" rows="15" cols="30"></textarea>
						<fieldset class="ui-grid-a">
								<div class="ui-block-c"><a id="formBulkAddSubmit" data-rel="back" data-role="button">Submit</a></div>
								<div class="ui-block-d"><a id="formBulkAddCancle" data-rel="back" data-role="button">Cancel</a></div>
						</fieldset>
					</form>
			    </div>
			</div>
		</div>
	</div>
	
	<div data-role="page" id="manageSubjectsPage">
		<div data-role="header" data-position="fixed">
			<h1>3R Target Records</h1>
			<a data-rel="back" class="ui-btn-right" data-icon="back">Back</a>
		</div>
		<div data-role=content>
			<div class="ui-grid-a">
				<div class="ui-block-a">
					<a data-role="button" id="subjectAddButton">Add Subject</a>
					<a data-role="button" data-position-to="window" data-rel="popup" href="#subjectBulkAddPopup"
							class="seperateItems" id="subjectBulkAddButton">Bulk Add Subjects</a>
					<ul id="manageSubjectsList" data-role="listview" data-filter="true"
						data-filter-placeholder="Search subjects..." data-inset="true">
					</ul>
				</div>
				<div class="ui-block-b">
					<form id="subjectDetailsForm">
						<div data-role="fieldcontain">
							<label for="subjectKey">Subject Key: </label>
							<input readonly name="SubjectKey" id="subjectKey" type="text">
						</div>
						<div data-role="fieldcontain">
							<label for="subjectName">Subject Name: </label>
							<input name="SubjectName" id="subjectName" type="text">
						</div>
						<div data-role="fieldcontain">
							<label for="vocationalSelect">Vocational: </label>
							<select name="SubjectVocational" id="vocationalSelect">
								<option value="false">No</option>
								<option value="true">Yes</option>
							</select>					
						</div>
						<fieldset class="ui-grid-a">
							<div class="ui-block-c"><a id="subjectUpdateButton" data-role="button">Save</a></div>
							<div class="ui-block-d"><a id="subjectRemoveButton" data-role="button">Remove</a></div>
						</fieldset>
					</form>
				</div>
			</div>
			<div data-role="popup" id="subjectBulkAddPopup" data-dismissible="false" data-theme="d" data-overlay-theme="b">
				<div data-role="header">
				    <h3>Select Subject</h3>
				</div>
				<div data-role="content">
					<form id="subjectBulkAddForm">
						<p>Paste the CSV data to be processed into the box below.</p>
						<textarea name="BulkSubjectCSVData" id="bulkSubjectData" rows="15" cols="30"></textarea>
						<fieldset class="ui-grid-a">
								<div class="ui-block-c"><a id="subjectBulkAddSubmit" data-rel="back" data-role="button">Submit</a></div>
								<div class="ui-block-d"><a id="subjectBulkAddCancle" data-rel="back" data-role="button">Cancel</a></div>
						</fieldset>
					</form>
			    </div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	
		dataStore.teacher = { displayName: "${UserEntity.name}",
		                         email: "${UserEmail}",
		                         admin: "${UserAdmin}",
	                         	 key: "${UserEntityKey}" };
		
		$('#manageFormsPage').on('pageinit', formHandler.initialise);
		$('#manageSubjectsPage').on('pageinit', subjectHandler.initialise);
	</script>	
</body>
</html>