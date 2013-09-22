var formHandler = new function () {
	
	function displayFormInformation (data) {
		$('#manageFormsList').empty();

		if (!$.isEmptyObject(data))
		{
			var date = new Date();
			for (var key in data) {
				var yearGroup = date.getFullYear() - data[key].intakeYear + 7;
				if (date.getMonth < 8)
					yearGroup --;

				$('#manageFormsList').append('<li><a data-formkey="' + key + '">' + yearGroup + data[key].formCode + '</a></li>');
			}
			$('#manageFormsList a').click(formHandler.changeSelectedForm);
			
			$('#formCode').prop("disabled", false);
			$('#formIntakeYear').prop("disabled", false);
			$('#manageFormsList a:first').trigger("click");
		}
		else {
			$('#formKey').val("");
			$('#formCode').val("");
			$('#formIntakeYear').val($('#formIntakeYear option:first').val());
			
			$('#formCode').prop("disabled", true);
			$('#formIntakeYear').prop("disabled", true);
			$('#formIntakeYear').selectmenu("refresh");
			
			$('#formRemoveButton').addClass("ui-disabled");
		}
		$('#manageFormsList').listview("refresh");
	};
	
	this.initialise = function () {
		var date = new Date();

		dataStore.forms.get(displayFormInformation);
		
		$('#formIntakeYear').empty();
		var startYear = date.getFullYear();
		if (date.getMonth < 8)
			startYear --;
		
		for (var index = 0; index < 7; index++)
		{
			var year = startYear - index;
			$('#formIntakeYear').append("<option value=" + year + ">" + year + "</option>");
		}
		$('#formIntakeYear').selectmenu("refresh");
		
		$('#formUpdateButton').addClass("ui-disabled");
		$('#formUpdateButton').click(formHandler.updateFormData);
		$('#formRemoveButton').click(formHandler.removeFormData);
		
		$('#formCode').change(formHandler.formDataChanged);
		$('#formIntakeYear').change(formHandler.formDataChanged);
		
		$('#formAddButton').click(function () {
			$('#formRemoveButton').addClass("ui-disabled");
			$('#formUpdateButton').removeClass("ui-disabled");
			
			$('#manageFormsList a').removeClass("ui-btn-active");
			
			$('#formKey').val("New Record");
			$('#formCode').val("");
			$('#formIntakeYear').val(startYear);
			$('#formCode').prop("disabled", false);
			$('#formIntakeYear').prop("disabled", false);
			$('#formIntakeYear').selectmenu("refresh");
		});
		
		$('#formBulkAddSubmit').addClass("ui-disabled");
		$('#bulkFormData').change(function (eventObj) {
			if ($(eventObj.currentTarget).val() == "")
				$('#formBulkAddSubmit').addClass("ui-disabled");
			else $('#formBulkAddSubmit').removeClass("ui-disabled");
		});
		
		$('#formBulkAddSubmit').click(formHandler.bulkAddFormData);
	};
	
	this.changeSelectedForm = function (eventObj) {
		var formKey = $(eventObj.currentTarget).data("formkey");
		
		$('#manageFormsList a').removeClass("ui-btn-active");
		$(eventObj.currentTarget).addClass("ui-btn-active");
		
		dataStore.forms.get(function(data) {
			var form = data[formKey];
			
			$('#formKey').val(form.formId);
			$('#formCode').val(form.formCode);
			$('#formIntakeYear').val(form.intakeYear);
			$('#formIntakeYear').selectmenu("refresh");
			
			$('#formRemoveButton').removeClass("ui-disabled");
			$('#formUpdateButton').addClass("ui-disabled");
		});
		
	};
	
	this.formDataChanged = function ()
	{
		$('#formUpdateButton').removeClass("ui-disabled");
	};
	
	this.createFormData = function () {
		dataStore.forms.create($('#formDetailsForm').serialize(), function (data) { 
				$('#formUpdateButton').addClass("ui-disabled");
				$('#formRemoveButton').removeClass("ui-disabled");

				var date = new Date();
				var yearGroup = date.getFullYear() - data.intakeYear + 7;
				if (date.getMonth < 8)
					yearGroup --;
				
				$('#manageFormsList').prepend('<li><a data-formkey="' + data.key + '">' + yearGroup + data.formCode + '</a></li>')
				$('#manageFormsList').listview("refresh");
				$('#manageFormsList a:first').click(formHandler.changeSelectedForm);
				$('#manageFormsList a:first').trigger("click");
			});		
	};
	
	this.updateFormData = function () {
		var selectedForm = $('#manageFormsList a.ui-btn-active');
		
		if (selectedForm.length == 0)
		{
			formHandler.createFormData();
		}
		else {
			dataStore.forms.update(selectedForm.data("formkey"), $('#formDetailsForm').serialize(),
				function (data) { 
					$('#formUpdateButton').addClass("ui-disabled");			
					var date = new Date();
					var yearGroup = date.getFullYear() - data.intakeYear + 7;
					if (date.getMonth < 8)
						yearGroup --;

					selectedForm.text(yearGroup + data.formCode);
					
				});
		}
	};
	
	this.removeFormData = function () {
		var selectedForm = $('#manageFormsList a.ui-btn-active')
		dataStore.forms.remove(selectedForm.data("formkey"), function (data) {

			selectedForm.closest("li").remove();
			if ($('#manageFormsList').length > 0)
			{
				$('#manageFormsList a:first').trigger("click");
			}
			else {
				$('#formUpdateButton').addClass("ui-disabled");
				$('#formRemoveButton').addClass("ui-disabled");
			}
			
			$('#manageFormsList').listview("refresh");
		});
	};
	
	this.bulkAddFormData = function () {
		dataStore.forms.bulkCreate($('#formBulkAddForm').serialize(), function () {
			dataStore.forms.get(displayFormInformation);
		});
	};
};