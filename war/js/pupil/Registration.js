var formsByYear = { 7: [], 8: [], 9: [], 10: [], 11: [], 12: [], 13: [] }

function saveTargetRecord()
{
	$('#studentRegistrationFormSubmit').addClass('ui-disabled');
	
	$.mobile.loading("show");
	$.post ('/register.html', $('#studentRegistrationForm').serialize(),
		function (data) {
			$.mobile.loading("hide");
			location.href = "/pupil";
	}, "json").fail(function () { $.mobile.loading("hide"); alert("Unable to save the record!"); });
}

function updateFormYearGroup () {
	
	var yearGroup = $('#studentRegistrationYearGroup').val();
	
	$('#studentRegistrationFormCode').empty();
	for (var index in formsByYear[yearGroup])
	{
		$('#studentRegistrationFormCode').append('<option value="' + formsByYear[yearGroup][index].formKey + '">' 
				+ yearGroup + formsByYear[yearGroup][index].formCode + '</option>');	
	}
	$('#studentRegistrationFormCode').selectmenu("refresh");
	
	if ($('#studentRegistrationFormCode').val() != null)
		$('#studentRegistrationFormSubmit').removeClass('ui-disabled');
	else $('#studentRegistrationFormSubmit').addClass('ui-disabled'); 
}

function initialise (formInformation)
{
	$('#studentRegistrationFormSubmit').click(saveTargetRecord);
	for (var index in formInformation)
	{
		var date = new Date();
		var yearGroup = date.getFullYear() - formInformation[index].intakeYear + 7;
		if (date.getMonth < 8)
			yearGroup --;
		
		formsByYear[yearGroup].push( { formKey: formInformation[index].key, formCode: formInformation[index].formCode });
	}
	
	$('#studentRegistrationYearGroup').change(updateFormYearGroup);
	for (var year in formsByYear)
	{
		$('#studentRegistrationYearGroup').append('<option value="' + year + '">' + year + '</option>');
	}
	
	var yearGroup = $('#studentRegistrationYearGroup').val();
	
	$('#studentRegistrationFormCode').empty();
	for (var index in formsByYear[yearGroup])
	{
		$('#studentRegistrationFormCode').append('<option value="' + formsByYear[yearGroup][index].formKey + '">' 
				+ yearGroup + formsByYear[yearGroup][index].formCode + '</option>');	
	}
	if ($('#studentRegistrationFormCode').val() != null)
		$('#studentRegistrationFormSubmit').removeClass('ui-disabled');
	else $('#studentRegistrationFormSubmit').addClass('ui-disabled'); 
}