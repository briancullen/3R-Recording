var subjectHandler = new function () {
	
	function displaySubjectInformation (data) {
		$('#manageSubjectsList').empty();

		if (!$.isEmptyObject(data))
		{
			for (var key in data) {
				$('#manageSubjectsList').append('<li><a data-subjectkey="' + key + '">' + data[key].subjectName + '</a></li>');
			}
			$('#manageSubjectsList a').click(subjectHandler.changeSelectedSubject);
			
			$('#subjectName').prop("disabled", false);
			$('#vocationalSelect').prop("disabled", false);
			$('#manageSubjectsList a:first').trigger("click");
		}
		else {
			$('#subjectKey').val("");
			$('#subjectName').val("");
			$('#vocationalSelect').val("false");
			
			$('#subjectCode').prop("disabled", true);
			$('#vocationalSelect').prop("disabled", true);
			$('#vocationalSelect').selectmenu("refresh");
			
			$('#subjectRemoveButton').addClass("ui-disabled");
		}
		$('#manageSubjectsList').listview("refresh");
	};
	
	this.initialise = function () {

		dataStore.subjects.get(displaySubjectInformation);
		
		$('#subjectUpdateButton').addClass("ui-disabled");
		$('#subjectUpdateButton').click(subjectHandler.updateSubjectData);
		$('#subjectRemoveButton').click(subjectHandler.removeSubjectData);
		
		$('#subjectCode').change(subjectHandler.subjectDataChanged);
		$('#vocationalSelect').change(subjectHandler.subjectDataChanged);
		
		$('#subjectAddButton').click(function () {
			$('#subjectRemoveButton').addClass("ui-disabled");
			$('#subjectUpdateButton').removeClass("ui-disabled");
			
			$('#manageSubjectsList a').removeClass("ui-btn-active");
			
			$('#subjectKey').val("New Record");
			$('#subjectName').val("");
			$('#vocationalSelect').val("false");
			$('#subjectName').prop("disabled", false);
			$('#vocationalSelect').prop("disabled", false);
			$('#vocationalSelect').selectmenu("refresh");
		});
		
		$('#subjectBulkAddSubmit').addClass("ui-disabled");
		$('#bulkSubjectData').change(function (eventObj) {
			if ($(eventObj.currentTarget).val() == "")
				$('#subjectBulkAddSubmit').addClass("ui-disabled");
			else $('#subjectBulkAddSubmit').removeClass("ui-disabled");
		});
		
		$('#subjectBulkAddSubmit').click(subjectHandler.bulkAddSubjectData);
	};
	
	this.changeSelectedSubject = function (eventObj) {
		var subjectKey = $(eventObj.currentTarget).data("subjectkey");
		
		$('#manageSubjectsList a').removeClass("ui-btn-active");
		$(eventObj.currentTarget).addClass("ui-btn-active");
		
		dataStore.subjects.get(function(data) {
			var subject = data[subjectKey];
			
			$('#subjectKey').val(subject.subjectId);
			$('#subjectName').val(subject.subjectName);
			
			$('#vocationalSelect').val(subject.vocational?"true":"false");
			$('#vocationalSelect').selectmenu("refresh");
			
			$('#subjectRemoveButton').removeClass("ui-disabled");
			$('#subjectUpdateButton').addClass("ui-disabled");
		});
		
	};
	
	this.subjectDataChanged = function ()
	{
		$('#subjectUpdateButton').removeClass("ui-disabled");
	};
	
	this.createSubjectData = function () {
		dataStore.subjects.create($('#subjectDetailsForm').serialize(), function (data) { 
				$('#subjectUpdateButton').addClass("ui-disabled");
				$('#subjectRemoveButton').removeClass("ui-disabled");

				$('#manageSubjectsList').prepend('<li><a data-subjectkey="' + data.key + '">' + data.subjectName + '</a></li>')
				$('#manageSubjectsList').listview("refresh");
				$('#manageSubjectsList a:first').click(subjectHandler.changeSelectedSubject);
				$('#manageSubjectsList a:first').trigger("click");
			});		
	};
	
	this.updateSubjectData = function () {
		var selectedSubject = $('#manageSubjectsList a.ui-btn-active');
		
		if (selectedSubject.length == 0)
		{
			subjectHandler.createSubjectData();
		}
		else {
			dataStore.subjects.update(selectedSubject.data("subjectkey"), $('#subjectDetailsForm').serialize(),
				function (data) { 
					$('#subjectUpdateButton').addClass("ui-disabled");			
					selectedSubject.text(data.subjectName);
				});
		}
	};
	
	this.removeSubjectData = function () {
		var selectedSubject = $('#manageSubjectsList a.ui-btn-active')
		dataStore.subjects.remove(selectedSubject.data("subjectkey"), function (data) {

			selectedSubject.closest("li").remove();
			if ($('#manageSubjectsList').length > 0)
			{
				$('#manageSubjectsList a:first').trigger("click");
			}
			else {
				$('#subjectUpdateButton').addClass("ui-disabled");
				$('#subjectRemoveButton').addClass("ui-disabled");
			}
			
			$('#manageSubjectsList').listview("refresh");
		});
	};
	
	this.bulkAddSubjectData = function () {
		dataStore.subjects.bulkCreate($('#subjectBulkAddForm').serialize(), function () {
			dataStore.subjects.get(displaySubjectInformation);
		});
	};
};