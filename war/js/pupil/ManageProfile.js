var profileHandler = new function () {
	
	var formInfo = {}
	
	function updateFormList() {
		$('#pupilProfileForm').empty();
		
		var year = $('#pupilProfileYear').val();
		if ((year === undefined) || (year == ""))
			return;
		
		for (var key in formInfo[year])
		{
			$('#pupilProfileForm').append('<option value="' + key + '">' + year + formInfo[year][key].formCode + '</option>');
		}
		$('#pupilProfileForm').selectmenu("refresh");
	}
	
	function updatePupilProfile () {
		$('#pupilProfileSubmit').addClass("ui-disabled");
		
		dataStore.updatePupil({UserName: $('#pupilProfileName').val(), 
			UserFormKey: $('#pupilProfileForm').val()}, function () {
				progressHandler.initialiseForYear();
			});
	}
	
	this.initialise = function () {
		dataStore.forms.get(function (data) {
			
			for (var key in data) {
				var year = convertIntakeToYear(data[key].intakeYear);
				
				if (formInfo[year] === undefined)
					formInfo[year] = {};
				
				formInfo[year][key] = data[key];
			}
			
			$('#pupilProfileSubmit').click(updatePupilProfile);
		});	
		
		$('#changePupilDetails').on('pagebeforeshow', function () {
			$('#pupilProfileName').val(dataStore.pupil.displayName);
			$('#pupilProfileYear').val(dataStore.pupil.year);
			$('#pupilProfileYear').selectmenu("refresh");
			$('#pupilProfileSubmit').addClass("ui-disabled");
		});
		
		$('#changePupilDetails').on('pageshow', function () {
			updateFormList();	
			$('#pupilProfileForm').val(dataStore.pupil.formKey);
			$('#pupilProfileForm').selectmenu("refresh");
			$('#pupilProfileYear').change(updateFormList);
			
		});
		
		$('#pupilProfileName').change(function () {
			var name = $('#pupilProfileName').val();
			if ((name == undefined) || (name.length == 0))
			{
				$('#pupilProfileSubmit').addClass("ui-disabled");
			}
			else {
				$('#pupilProfileSubmit').removeClass("ui-disabled");
			}
		});
		
		$('#pupilProfileForm').change (function () {
			$('#pupilProfileSubmit').removeClass("ui-disabled");
		});
		
		$('#pupilProfileYear').change (function () {
			$('#pupilProfileSubmit').removeClass("ui-disabled");
		});
	};
};
