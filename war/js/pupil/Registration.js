function saveTargetRecord()
{
	$.mobile.loading("show");
	$.post ('/register.html', $('#studentRegistrationForm').serialize(),
		function (data) {
			$.mobile.loading("hide");
			location.href = "/pupil";
	}, "json").fail(function () { $.mobile.loading("hide"); alert("Unable to save the record!"); });
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
		
		$('#studentRegistrationFormForm').append('<option value="' + formInformation[index].key + '">' 
				+ yearGroup + formInformation[index].formCode + '</option>');
	}
	
	$('#studentRegistrationFormForm option').first().attr("selected", "selected");
}