var selectedTarget = -1;
var pupilInformation = {};
var currentTargetInfo = [];

function updatePupilInfoHeader ()
{
	$('#pupilInfoHeader').empty();
	$('#pupilInfoHeader').append(pupilInformation.displayName + " - Year " + $('#targetTable').data("recordyear"));
}


function editRecord(eventObj)
{
	var recordId = $(eventObj.currentTarget).data('recordid');
	
	for (var index = 0; index < currentTargetInfo.length; index++)
	{
		if (currentTargetInfo[index].subjectTargetId == recordId)
		{
			selectedTarget = index;
			break;
		}
	}
	
	$.mobile.changePage("#recordDialog", { transition: "pop", role: "dialog"});
}


function deleteRecord(eventObj)
{
	var recordId = $(eventObj.currentTarget).data('recordid');
	$.get('/records/'+recordId+'/delete', function () {
		updateTable();
	}).fail(function () { alert("Unable to delete the record specified!"); });
}


function updateTable ()
{
	var request = new Object();
	
	if ($('#targetTable').data("recordyear") != undefined)
		request.RECORD_YEAR  = $('#targetTable').data("recordyear");
	
	request.RECORD_TYPE = $('#targetTable').data("recordtype");
	
	$.get('/records', request, function (data, textStatus, jqXHR) {
		$('#targetTable tbody').empty();
		
		var jsonData = jQuery.parseJSON(data);
		currentTargetInfo = jsonData;
		if (jsonData.length > 0)
		{
			$('#targetTable tbody').append(Mustache.render('{{#.}}\
		       <tr>\
		         <td>{{subject.name}}</td>\
		         <td>{{minTargetGrade}}</td>\
		         <td>{{expectedTargetGrade}}</td>\
		         <td>{{aspirationalTargetGrade}}</td>\
		         <td>{{level}}</td>\
			     <td>{{target}}</td>\
			     <td><div data-role="controlgroup" data-type="horizontal" data-mini="true"><a data-role="button" data-recordid="{{subjectTargetId}}" data-icon="gear" data-iconpos="notext">Edit</a>\
					<a data-rel="dialog" data-role="button" data-recordid="{{subjectTargetId}}" data-icon="delete" data-iconpos="notext">Delete</a></div></td>\
			   </tr>{{/.}}', jsonData));
			
			$('#targetTable a').button();
			$('#targetTable div').controlgroup();
			
			$('#targetTable a[data-icon="gear"]').click(editRecord);
			$('#targetTable a[data-icon="delete"]').click(deleteRecord);
			
			$('#targetTable').data("recordyear", jsonData[0].yearGroup);
		}
		else {
			$('#targetTable tbody').append('<tr><td colspan="6">No records found for this type of target.</td></tr>');
		}
	}).always(function () { updatePupilInfoHeader(); });
}

function initialiseRecordDialog ()
{
	$.ajax({url: '/subjects',
		async: false,
		success: function (data, textStatus, jqXHR) {
		$('#recordDialogSubject').empty();
		
		jsonData = jQuery.parseJSON(data);
		if (jsonData.length > 0)
		{
			$('#recordDialogSubject').append(Mustache.render('{{#.}}\
				<option value="{{subjectId}}">{{subjectName}}</option>{{/.}}',jsonData));
		}
	}});
}

function resetRecordDialog ()
{
	$('#recordDialog select').prop('selectedIndex', 0);	
	$('#recordDialogTarget').val("");
	
	var type = $('#targetTable').data('recordtype');
	$('#recordDialogTargetType').val(type);
	
	var year = $('#targetTable').data('recordyear');
	if (year == undefined)
		$('#recordDialogYear').val(7);
	else $('#recordDialogYear').val(year);
	
	if (selectedTarget != -1)
	{
		var target = currentTargetInfo[selectedTarget];
		selectedTarget = -1;
		
		$('#recordDialogMinTargetGrade option[value="' + target.minTargetGrade + '"]').prop("selected", "true");
		$('#recordDialogExpectedTargetGrade option[value="' + target.expectedTargetGrade + '"]').prop("selected", "true");
		$('#recordDialogAspirationalTargetGrade option[value="' + target.aspirationalTargetGrade + '"]').prop("selected", "true");
		$('#recordDialogCurrentLevel').val(target.level);
		$('#recordDialogTarget').val(target.target);
		
		$('#recordDialogSubject option[value="' + target.subject.id + '"]').prop("selected", "true");
		$('#recordDialogTargetId').val(target.subjectTargetId);
	}
	else {
		$('#recordDialogTargetId').removeAttr('value');
	}
	
	$('#recordDialog select').selectmenu('refresh');
}

function changeRecordType(eventObj)
{
	var type = $(eventObj.currentTarget).data('recordtype');
	if (type != $('#targetTable').data('recordtype'))
	{
		$('#targetTable').data('recordtype', type);
		updateTable();
	}
}

function saveTargetRecord()
{
	$.post ('/records', $('#recordDialogForm').serialize(),
		function (data) {
			updateTable();
	}).fail(function () { alert("Unable to save the record!"); });
}

function showStudentPage()
{
	var type = $('#targetTable').data("recordtype");
	
	$('#RecordTypeFooterToolbar a').removeClass('ui-btn-active');
	$('#RecordTypeFooterToolbar a[data-recordtype="' + type + '"]').addClass('ui-btn-active');
}

function changeYearView (eventObj)
{
	var year = $(eventObj.currentTarget).data('year');
	
	if (year == undefined)
	{
		if ($('#targetTable').data('recordyear') != undefined)
		{
			$('#targetTable').removeData('recordyear');
			updateTable();
		}
	}
	else if (year != $('#targetTable').data('recordyear'))
	{
		$('#targetTable').data('recordyear', year);
		updateTable();
	}
}

function initialise ()
{
	$.ajaxSetup({
		async: false
	});
	
	$.getJSON("/pupils", function (data) {
		pupilInformation = data;
	});
	
	$.ajaxSetup({
		async: true
	});
	
	var intakeDate = new Date ();
	intakeDate.setFullYear (pupilInformation.form.intakeYear, 8, 1);
	var yearsInSchool = Math.floor(((new Date()).getTime() - intakeDate.getTime())/31536000000);
	var currentYear = 7 + yearsInSchool;
	if (currentYear > 13)
		currentYear = 13;
	
	$('#targetTable').data('recordyear', currentYear);
	
	var type = $('#RecordTypeFooterToolbar a.ui-btn-active').data('recordtype');
	$('#targetTable').data("recordtype", type);
	updateTable();
	
	$('#studentPage').on('pagebeforeshow', showStudentPage)
	
	$('#recordDialog').on('pageinit', initialiseRecordDialog);
	$('#recordDialog').on('pagebeforeshow', resetRecordDialog)
	
	$('#RecordTypeFooterToolbar a').click(changeRecordType);
	$('#recordDialogSubmit').click(saveTargetRecord);
	
	$('#viewRecordsPopup ul a').click(changeYearView);
	$('#viewRecordsPopup').on('popupbeforeposition', function () {
		$('#viewRecordsPopup ul a').removeClass('ui-disabled');
		var year = $('#targetTable').data('recordyear');
		
		if (year == undefined)
		{
			$('#viewRecordsPopup ul a').last().addClass('ui-disabled');
		}
		else {
			$('#viewRecordsPopup ul a[data-year="' + year + '"]').addClass('ui-disabled');
		}
	});
}