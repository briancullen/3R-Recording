var targetsHandler = new function () {
	var selectedStage = null;
	var selectedTarget = null;
	
	// Updates the main display when a subject is selected.
	function updateTargetsDisplay (targets)
	{
		// Disable the update button because presumably as we are
		// switching records no changes have been made as of yet.
		$('#manageTargetsUpdate').addClass("ui-disabled");
		
		// Empties the select lists and checks that there is actually a subject
		// selected (there might be none) - if not it exits quickly.
		$('#manageTargetForm select').empty();
		if ($('#manageTargetsSubjectList a.ui-btn-active').length == 0)
		{
			$('#manageTargetForm select').empty().selectmenu("refresh");
			$("#manageTargetBanner").fadeIn(2000);
			$('#manageTargetBanner h3').text("No records found for the specified criteria - please try again.");
			$('#manageTargetsUpdate').addClass("ui-disabled");
			$('#manageTargetsRemove').addClass("ui-disabled");
			$('#manageTargetForm select').prop("disabled", true);
			return;
		}
		
		// Gets the target data
		$("#manageTargetBanner").hide();
		$('#manageTargetsRemove').removeClass("ui-disabled");
		$('#manageTargetForm select').prop("disabled", false);
		
		var target = targets[selectedTarget];
		var grades = getGrades (selectedStage, target.subject.vocational);
		
		// Updates the select menus
		$('#manageTargetForm select').empty();
		
		for (var index in grades)
		{
			$('#manageTargetForm select').append('<option value="'
					+ grades[index] + '">' + grades[index] + '</option>');					
		}
		
		$('#manageTargetForm select').selectmenu();
		$('#manageTarget3Levels').val(target.threeLevelsTargetGrade).selectmenu("refresh");
		$('#manageTarget4Levels').val(target.fourLevelsTargetGrade).selectmenu("refresh");
		$('#manageTarget5Levels').val(target.fiveLevelsTargetGrade).selectmenu("refresh");
	};
	
	// Initialises the look of the page and attaches the relevant event handlers.
	this.initialise = function () {
		$('#manageTargetAddSubjectPopup').addClass("ui-disabled");
		$('#manageTargetsPage').on("pagebeforeshow", function () {
			selectedStage = dataStore.pupil.stage;
			$('#manageTargetsStage').val(selectedStage);
			$('#manageTargetsStage').selectmenu("refresh");
			
			// Downloads the pupils current targets.
			dataStore.targets.get(selectedStage, function (data) {
					targetsHandler.updateSubjectList(data);
					$('#manageTargetAddSubjectPopup').removeClass("ui-disabled");
				}, true);
		});
				
		// Sets up click handler for when the KS buttons are pressed.	
		$("#manageTargetsStage").change(targetsHandler.changeSelectedStage);
		
		// If any of the select boxes are changed then enable the update
		// button so the change can be submitted.
		$("#manageTargetForm select").change(function () { 
			$('#manageTargetsUpdate').removeClass("ui-disabled"); 
		});
		
		// Sets up handles for the update and remove buttons
		$("#manageTargetsUpdate").click(targetsHandler.updateSubjectTarget);
		$("#manageTargetsRemove").click(targetsHandler.removeSubjectTarget);
		
		// Sets up handler for the subject chooser popup to populate the list.
		$('#manageTargetAddSubjectPopup').on("popupbeforeposition", targetsHandler.createSubjectListForPopUp);
	};
	
	this.updateSubjectList = function (data) {

		if ($.isEmptyObject(dataStore.subjects.getWithoutTarget(selectedStage)))
		{
				$('#manageTargetsAddBtn').addClass("ui-disabled");
		}
		
		// Gets rid of the old selection and rebuilds the lists
		selectedTarget = null;
		$('#manageTargetsSubjectList').empty();
		for (var key in data)
		{
			$('#manageTargetsSubjectList').append('<li><a data-subjectkey="'
					+ key + '">' + data[key].subject.name + '</a></li>');
		}

		$('#manageTargetsSubjectList a').first().addClass("ui-btn-active");
		$('#manageTargetsSubjectList a').click(targetsHandler.changeSelectedTarget);
		$('#manageTargetsSubjectList').listview("refresh");
		selectedTarget = $('#manageTargetsSubjectList a').first().data("subjectkey")

		dataStore.targets.get(selectedStage, updateTargetsDisplay);
	};
	
	// Updates which subject is selected at the side of the screen
	// and then updates the main display.
	this.changeSelectedTarget = function (eventObj)
	{
		var key = $(eventObj.currentTarget).data("subjectkey");
		if (key != selectedTarget)
		{	
			selectedTarget = key;
			$('#manageTargetsSubjectList a').removeClass("ui-btn-active");
			$(eventObj.currentTarget).addClass("ui-btn-active");
			
			dataStore.targets.get(selectedStage, updateTargetsDisplay);
		}
	};
	
	this.changeSelectedStage = function (eventObj)
	{
		var stage = $(eventObj.currentTarget).val();
		
		if (stage != selectedStage)
		{
			selectedStage = stage;
			$('#manageTargetsAddBtn').addClass("ui-disabled");
			dataStore.targets.get(stage, function (data) {
				targetsHandler.updateSubjectList(data);
				if ((dataStore.pupil.stage == stage) &&
					(!$.isEmptyObject(dataStore.subjects.getWithoutTarget(selectedStage))))
				{
					$('#manageTargetsAddBtn').removeClass("ui-disabled");
				}

			});		
		}
	};
	
	this.createSubjectListForPopUp = function ()
	{
		$('#manageTargetsPopupSubjectList').empty();
		var subjects = dataStore.subjects.getWithoutTarget(selectedStage);
				
		for (var subjectKey in subjects)
		{
			$('#manageTargetsPopupSubjectList').append('<li><a data-rel="back" data-subjectkey="'
					+ subjectKey +'">' + subjects[subjectKey].subjectName + '</a></li>');
		}
		
		$('#manageTargetsPopupSubjectList a').click(targetsHandler.createSubjectTarget);
		$('#manageTargetsPopupSubjectList').listview("refresh");
	};
	
	this.removeSubjectTarget = function (eventObj)
	{
		$('#manageTargetsRemove').addClass("ui-disabled");
		$('#manageTargetsUpdate').addClass("ui-disabled");
		dataStore.targets.remove(selectedTarget, selectedStage, function (data) {
			$('#manageTargetsAddBtn').removeClass("ui-disabled");
			$('#manageTargetsSubjectList a.ui-btn-active').parents('li').remove();

			$('#manageTargetsSubjectList').listview("refresh");
			if ($('#manageTargetsSubjectList li').length != 0)
			{
				$('#manageTargetsSubjectList a').first().trigger("click");
			}
			else updateTargetsDisplay(data);
		});
	};
	
	this.updateSubjectTarget = function (eventObj) {
		$('#manageTargetsRemove').addClass("ui-disabled");
		$('#manageTargetsUpdate').addClass("ui-disabled");

		var data = {
				ThreeLevelsTarget: $('#manageTarget3Levels').val(),
				FourLevelsTarget: $('#manageTarget4Levels').val(),
				FiveLevelsTarget: $('#manageTarget5Levels').val()
		};

		dataStore.targets.update(selectedTarget, selectedStage, data, function(data) {
			$('#manageTargetBanner h3').text("Target details have been updated.");
			$('#manageTargetBanner').fadeIn(2000);
			$('#manageTargetsRemove').removeClass("ui-disabled");
		});
	};
	
	this.createSubjectTarget = function(eventObj) {
		
		dataStore.targets.create($(eventObj.currentTarget).data("subjectkey"),
				selectedStage, function (data) 
				{
					if ($.isEmptyObject(dataStore.subjects.getWithoutTarget(selectedStage)))
					{
						$('#manageTargetsAddBtn').addClass("ui-disabled");
					}

					$('#manageTargetsSubjectList').prepend('<li><a data-subjectkey="'
							+ data.key + '">' + data.subject.name + '</a></li>');
					$('#manageTargetsSubjectList').listview("refresh");
					$('#manageTargetsSubjectList a').first().click(targetsHandler.changeSelectedTarget);
					$('#manageTargetsSubjectList a').first().trigger("click");
					$('#manageTargetBanner h3').text("New target has been created - please update below.");
					$('#manageTargetBanner').fadeIn(2000);
				});
	};
};