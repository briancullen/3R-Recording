var displayedTargets = [];
var subjectsWithTargets = [];
var subjects = [];

var keyStage3Grades = [ "2c", "2b", "2a", "3c", "3b", "3a", "4c", "4b", "4a",
                		"5c", "5b", "5a", "6c", "6b", "6a", "7c", "7b", "7a" ]
var keyStage4Grades = [ "U", "G", "F", "E", "D", "C", "B", "A", "A*" ]
var keyStage5Grades = [ "U", "G", "F", "E", "D", "C", "B", "A", "A*" ]

var keyStage4VocGrades = [ "PASS", "MERIT", "DISTINCTION" ]
var keyStage5VocGrades = [ "PASS", "MERIT", "DISTINCTION", "DISTINCTION*" ]

// Updates which subject is selected at the side of the screen
// and then updates the main display.
function updateSelectedTargetSubjectBtn(eventObj) 
{
	var key = $(eventObj.currentTarget).data("subjectkey");
	if (key != $('#manageTargetsSubjectList').data("selectedsubject"))
	{	
		$('#manageTargetsSubjectList a').removeClass("ui-btn-active");
		$(eventObj.currentTarget).addClass("ui-btn-active");
		$('#manageTargetsSubjectList').data("selectedsubject", key);
		updateDisplay();
	}
}

// Updates the main display.
function updateDisplay ()
{
	// Empties the select lists and checks that there is actually a subject
	// selected (there might be none) - if not it exits quickly.
	$('#manageTargetForm select').empty();
	if ($('#manageTargetsSubjectList a.ui-btn-active').length == 0)
	{
		$('#manageTargetForm select').empty().selectmenu("refresh");
		return;
	}

	// Gets the target data
	var target = displayedTargets[$('#manageTargetsSubjectList').data("selectedsubject")];

	// selects the grades that we want to use to display the target data.
	var grades = keyStage3Grades;
	if (target.keyStage == 4)
	{
		if (target.subject.vocational)
			grades = keyStage4VocGrades;
		else grades = keyStage4Grades;
	}
	else if (target.keyStage == 5)
	{
		if (target.subject.vocational)
			grades = keyStage5VocGrades;
		else grades = keyStage5Grades;		
	}
	
	// Updates the select menus
	$('#manageTargetForm select').empty();
	$('#manageTargetForm select').append(Mustache.render('{{#.}}\
			<option value={{.}}>{{.}}</option>{{/.}}', grades));
	
	$('#manageTargetForm select').selectmenu();
	$('#manageTarget3Levels').val(target.threeLevelsTargetGrade).selectmenu("refresh");
	$('#manageTarget4Levels').val(target.fourLevelsTargetGrade).selectmenu("refresh");
	$('#manageTarget5Levels').val(target.fiveLevelsTargetGrade).selectmenu("refresh");
}

function createNewTargetRecord(eventObj) {
	var keyStage = $('#manageTargetsKeyStage').data("selectedkeystage");
	var subjectKey = $(eventObj.currentTarget).data("subjectkey");
	var vocational = subjects[subjectKey].vocational; 
		
	var grades = keyStage3Grades;
	if (keyStage == 4)
	{
		if (vocational)
			grades = keyStage4VocGrades;
		else grades = keyStage4Grades;
	}
	else if (keyStage == 5)
	{
		if (vocational)
			grades = keyStage5VocGrades;
		else grades = keyStage5Grades;		
	}
	
	var data = { TargetPupilKey: pupilInformation.key,
	             TargetSubjectKey: subjectKey,
	             TargetKeyStage: keyStage,
	             ThreeLevelsTarget: grades[0],
	             FourLevelsTarget: grades[0],
	             FiveLevelsTarget: grades[0] };
	
	$.post ('/api/target', data,
			function (data) {
				$.get("/api/pupil/" + pupilInformation.key + "/target", data,
				updateSubjectListView, "json").fail(function () { alert("Unable to load targets from server"); });
		}).fail(function () { alert("Unable to save the record!"); });
}


function createTargetSubjectListForPopUp ()
{
	$('#manageTargetsPopupSubjectList').empty();
	
	for (var subjectKey in subjects)
	{
		var subjectName = subjects[subjectKey].subjectName;
		
		if (!(subjectKey in subjectsWithTargets))
		{
			$('#manageTargetsPopupSubjectList').append('<li><a data-rel="back" data-subjectkey="'
					+ subjectKey +'">' + subjectName + '</a></li>');
		}
	}
	$('#manageTargetsPopupSubjectList a').click(createNewTargetRecord);
	$('#manageTargetsPopupSubjectList').listview("refresh");
}

function updateSubjectListView (jsonData)
{
	$('#manageTargetsSubjectList').data("selectedsubject", "")

	$('#manageTargetsSubjectList').empty();
	$('#manageTargetsSubjectList').append(Mustache.render('{{#.}}\
			<li><a data-subjectkey={{key}}>{{subject.name}}</a></li>{{/.}}', jsonData));

	
	displayedTargets = [];
	for (var index = 0; index < jsonData.length; index++)
	{
		displayedTargets[jsonData[index].key] = jsonData[index];
		subjectsWithTargets[jsonData[index].subject.key] = jsonData[index];
	}
	
	// Display banner when no records are found. Need to
	// make sure I enable/disable things appropriately.
	if (jsonData.length == 0)
	{
		$("#manageTargetNotFoundBanner").fadeIn(2000);
		$('#manageTargetsUpdate').addClass("ui-disabled");
		$('#manageTargetsRemove').addClass("ui-disabled");
	}
	else {
		$("#manageTargetNotFoundBanner").fadeOut(1000);
		$('#manageTargetsUpdate').removeClass("ui-disabled");
		$('#manageTargetsRemove').removeClass("ui-disabled");
	}
	
	$('#manageTargetsSubjectList a').first().addClass("ui-btn-active");
	$('#manageTargetsSubjectList a').click(updateSelectedTargetSubjectBtn);
	$('#manageTargetsSubjectList').listview("refresh");
	$('#manageTargetsSubjectList').data("selectedsubject", $('#manageTargetsSubjectList a').first().data("subjectkey"));

	updateDisplay();
}

function changeTargetKeyStage(eventObj)
{
	var keyStage = $(eventObj.currentTarget).val();
	
	if (keyStage != $('#manageTargetsKeyStage').data("selectedkeystage"))
	{
		var data = { KeyStage: keyStage };
		$('#manageTargetsKeyStage').data("selectedkeystage", keyStage);
		$.get("/api/pupil/" + pupilInformation.key + "/target", data,
				updateSubjectListView, "json").fail(function () { alert("Unable to load targets from server"); });
		
		if (pupilInformation.keyStage != keyStage)
			$('#manageTargetsAddBtn').addClass("ui-disabled");
		else $('#manageTargetsAddBtn').removeClass("ui-disabled");
	}
}

function updateSubjectTarget()
{
	var data = displayedTargets[$('#manageTargetsSubjectList').data("selectedsubject")];
	var threeLevels = $('#manageTarget3Levels').val();
	var fourLevels = $('#manageTarget4Levels').val();
	var fiveLevels = $('#manageTarget5Levels').val();
	
	if ((data.threeLevelsTargetGrade == threeLevels) &&
			(data.fourLevelsTargetGrade == fourLevels) &&
			(data.fiveLevelsTargetGrade == fiveLevels))
		return;
	
	$.ajax('/api/target/'+ $('#manageTargetsSubjectList').data("selectedsubject"),
			{ type: "PUT", data: $('#manageTargetForm').serialize() }).done(function () 
					{ 
						alert("Your changes have been saved.");
						data.threeLevelsTargetGrade = threeLevels;
						data.fourLevelsTargetGrade = fourLevels;
						data.fiveLevelsTargetGrade = fiveLevels;
						updatePupilRecordTable();
					})
			.fail(function () { alert("Unable to update the record specified!"); });
}

function removeSubjectTarget()
{
	$.ajax('/api/target/'+ $('#manageTargetsSubjectList').data("selectedsubject"),
			{ type: "DELETE" }).done(function ()
					{ 
						delete displayedTargets[$('#manageTargetsSubjectList').data("selectedsubject")]; 
						updateSubjectListView(displayedTargets.slice(0));
					})
			.fail(function () { alert("Unable to delete the record specified!"); });
}

function updateSubjectList (jsonData)
{
	for (var index in jsonData)
	{
		subjects[jsonData[index].key] = jsonData[index];
	}
}

function initialiseManageTargetPage ()
{
	// Tries to select the current Key Stage automatically although it seems rather inefficient at the moment.
	$("#manageTargetsKeyStage input").removeAttr("checked").checkboxradio("refresh");
	$('#manageTargetsKeyStage input[value="' + pupilInformation.keyStage + '"]').attr("checked", "checked").checkboxradio("refresh");
	$('#manageTargetsKeyStage').data("selectedkeystage", pupilInformation.keyStage);

	// Downloads the pupils current targets.
	var data = { KeyStage: $("#manageTargetsKeyStage input:checked").val() }
	$.get("/api/pupil/" + pupilInformation.key + "/target", data,
			updateSubjectListView, "json").fail(function () {alert("Unable to load targets from server");});
	
	// Sets up click handler for when the KS buttons are pressed.	
	$("#manageTargetsKeyStage input").click(changeTargetKeyStage);
	
	$("#manageTargetsUpdate").click(updateSubjectTarget);
	$("#manageTargetsRemove").click(removeSubjectTarget);
	
	
	$.get("/api/subject", updateSubjectList, "json").fail(function () {alert("Unable to load subjects from server");});
	
	$('#manageTargetAddSubjectPopup').on("popupbeforeposition", createTargetSubjectListForPopUp);
}