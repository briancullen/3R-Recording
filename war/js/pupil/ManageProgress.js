var progressHandler = new function () {
	
	var selectedYear = null;
	var selectedType = null;
	
	// Changes the record type when the footer is clicked and
	// triggers an update of the table when necessary.
	 function changeRecordType(eventObj)
	{
		var type = $(eventObj.currentTarget).data('recordtype');
		if (type != selectedType)
		{
			selectedType = type;
			dataStore.progress.get(selectedYear, selectedType, progressHandler.updateProgressTable);
		}
	};
	
	// Change the year saved when a new year is clicked in
	// the year view popup. Only refreshes the records if
	// necessary.
	function changeYearView(eventObj)
	{
		var year = $(eventObj.currentTarget).data('year');
		if (year != selectedYear)
		{
			selectedYear = year;
			dataStore.progress.get(selectedYear, selectedType, progressHandler.updateProgressTable);
			dataStore.targets.get(yearToKeyStage(selectedYear),function (targets) {
				
				if ((dataStore.pupil.year != selectedYear) || ($.isEmptyObject(targets)))
					$('#addProgressBtn').addClass("ui-disabled");
				else $('#addProgressBtn').removeClass("ui-disabled");
			});
		}
	};
	
	this.initialise = function () {

		$('#pupilRecordTable').footable({
		    breakpoints: {
		    	phone: 480,
		    	tablet: 900,
		    	desktop: 1150,
		    	widedesktop: 1500
		    }
		});
		
		$('#pupilRecordsPage').on('pageshow', $('#pupilRecordTable').data("footable").resize);
		
		// Save the year and type to the datatable for later use and triggers and
		// update of the records shown in the table.
		selectedYear = dataStore.pupil.year;
		selectedType = $('#pupilRecordsPageFooter a.ui-btn-active').data("recordtype");
		
		// Handler for clicking buttons in footer.
		$('#pupilRecordsPageFooter a').click(changeRecordType);
		
		// Handlers for the year view popup.
		$('#pupilRecordsYearPopup ul a').click(changeYearView);
		$('#pupilRecordsYearPopup').on('popupbeforeposition', function() {
			$('#pupilRecordsYearPopup ul a').removeClass('ui-disabled');
			$('#pupilRecordsYearPopup ul a[data-year="' + selectedYear + '"]').addClass('ui-disabled');
		});
		
		// Hides all the year options that should be in the future for
		// this pupil ... hope it doesn't cause trouble.
		$('#pupilRecordsYearPopup ul a').each(function(index)
				{
					if ($(this).data("year") > dataStore.pupil.year)
						$(this).parents('li').addClass("ui-screen-hidden");
					else $(this).parent('li').removeClass("ui-screen-hidden");
				});
		$('#pupilRecordsYearPopup ul').listview("refresh");
		
		// Resets the buttons in the footer so the appropriate R is
		// shown after we retrun to the page from somewhere else (e.g. dialog).
		$('#pupilRecordsPage').on('pagebeforeshow', function () {
					$('#pupilRecordsPageFooter a').removeClass('ui-btn-active');
					$('#pupilRecordsPageFooter a[data-recordtype="' + selectedType + '"]').addClass('ui-btn-active');
					
						dataStore.targets.get(yearToKeyStage(selectedYear),function (targets) {
						if ((dataStore.pupil.year != selectedYear) || ($.isEmptyObject(targets)))
							$('#addProgressBtn').addClass("ui-disabled");
						else $('#addProgressBtn').removeClass("ui-disabled");
					});
		});
		$('#pupilRecordsPage').on("pageshow", function () {
			dataStore.progress.get(selectedYear, selectedType, progressHandler.updateProgressTable, true);
		});
		
		$('#recordDialogSubmit').click(progressHandler.createProgressRecord);
		$('#recordDialogTarget').change(function () {
			if ($('#recordDialogTarget').val().length > 0)
				$('#recordDialogSubmit').removeClass("ui-disabled");
			else $('#recordDialogSubmit').addClass("ui-disabled");
		});
		
		$('#progressRecordDialog').on('pagebeforeshow', progressHandler.initialiseProgressDialog);
		$('#progressRecordDialog').on('pagehide', function () { $('#recordDialogForm').removeData("recordkey"); });
	};
	
	this.initialiseProgressDialog = function (eventObj) {
		
		$('#recordDialogTarget').val("");
		$('#recordDialogSubmit').addClass("ui-disabled");
		
		dataStore.targets.get(yearToKeyStage(selectedYear),function (targets) {

			$('#recordDialogSubject').empty();
			for (var key in targets)
			{
				$('#recordDialogSubject').append('<option value="' + key + '" data-vocational="'
						+ targets[key].subject.vocational + '">' + targets[key].subject.name + '</option>');
			}
			$('#recordDialogSubject').selectmenu("refresh");
			$('#recordDialogSubject').change(progressHandler.updateDialogLevelSelect);

			var key = $('#recordDialogForm').data('recordkey');
			if ((key !== undefined) && (key != ""))
			{
				dataStore.progress.get(selectedYear, selectedType, function (data) {

					$('#recordDialogSubject').prop("disabled", "disabled");
					$('#recordDialogSubject').val(data[key].target.key);
					$('#recordDialogTarget').val(data[key].progress.nextSteps);
					$('#recordDialogSubmit').removeClass("ui-disabled");
					progressHandler.updateDialogLevelSelect ();
					$('#recordDialogCurrentLevel').val(data[key].progress.currentLevel);
					$('#progressRecordDialog select').selectmenu("refresh");
				});
			}
			else {
				progressHandler.updateDialogLevelSelect ();
				$('#recordDialogSubject').prop("disabled", false);
			}
		});					
	}
	
	this.updateDialogLevelSelect = function (eventObj)
	{
		var vocational = $('#recordDialogSubject option[value="' + $('#recordDialogSubject').val() + '"]').data("vocational");
		var	grades = getGrades(yearToKeyStage(selectedYear), vocational);
		
		$('#recordDialogCurrentLevel').empty();
		for (var index in grades)
		{
			$('#recordDialogCurrentLevel').append('<option value=' + grades[index] + '>' + grades[index] + '</option>');
		}
		$('#recordDialogCurrentLevel').selectmenu("refresh");
	};
	
	this.createProgressRecord = function () {
		$('#recordDialogSubmit').addClass("ui-disabled");
		var record = { TargetType: selectedType,
						TargetYear: selectedYear,
						TargetCurrentLevel: $('#recordDialogCurrentLevel').val(),
						TargetProgress: $('#recordDialogTarget').val(),
						PupilTargetKey: $('#recordDialogSubject').val() };
		
		var key = $('#recordDialogForm').data('recordkey');
		if ((key !== undefined) && (key != ""))
		{
			dataStore.progress.update(key, record, function (data) {
				dataStore.progress.get(selectedYear, selectedType, progressHandler.updateProgressTable);
			});
		}
		else {
			dataStore.progress.create(record, function (data) {
				dataStore.progress.get(selectedYear, selectedType, progressHandler.updateProgressTable);
			});
		}
	}
	
	this.updateProgressTable = function (data)
	{
		var footable = $('#pupilRecordTable').data("footable");
		// $('#pupilRecordTable tbody').empty();
		$('#pupilRecordTable tbody tr').each(function (index){ footable.removeRow($(this)); });
	
		if (!$.isEmptyObject(data))
		{
			
			for (var key in data)
			{
				var record = data[key];
				console.log(record);
				footable.appendRow('<tr>'
						 + '<td>' + record.target.subject.name + '</td>\
				         <td>' + record.target.threeLevelsTargetGrade + '</td>\
				         <td>' + record.target.fourLevelsTargetGrade + '</td>\
				         <td>' + record.target.fiveLevelsTargetGrade + '</td>\
				         <td>' + record.progress.currentLevel + '</td>\
					     <td>' + record.progress.nextSteps + '</td>\
					     <td><div data-role="controlgroup" data-type="horizontal" data-mini="true"><a \
					     	href="#progressRecordDialog" data-rel="dialog" data-recordkey="'
					     	+ record.progress.key + '" data-icon="gear">Edit</a>\
					     <a href="#" data-recordkey="' + record.progress.key
							+ '" data-icon="delete">Delete</a></div></td></tr>');
			}
			
			
			// Styles the newly created buttons.
			// $('#pupilRecordTable a').button();
			$('#pupilRecordTable div').controlgroup();
			
			// Adds handlers to the newly created buttons.
			$('#pupilRecordTable').on("click", 'a[data-icon="delete"]', function (eventObj) {
				console.log("Delete triggered");
				var recordKey = $(eventObj.currentTarget).data('recordkey');
				dataStore.progress.remove(recordKey, selectedYear, selectedType, progressHandler.updateProgressTable);
			});
			
			$('#pupilRecordTable').on("click", 'a[data-icon="gear"]', function (eventObj) {
				console.log("Click for gears!");
				$('#recordDialogForm').data('recordkey', $(eventObj.currentTarget).data('recordkey'));
			});
			
			$('#manageProgressNotFoundBanner').hide();
		}
		else {
			$('#manageProgressNotFoundBanner').fadeIn(2000);
		}

		// Update the banner even if nothing was recieved.
		$('#pupilRecordBanner').text(dataStore.pupil.displayName + " (" 
				+ dataStore.pupil.email + ") for Year " + selectedYear);
	};
};
