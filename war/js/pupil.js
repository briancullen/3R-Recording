// Updates the banner at the top of the screen - for example when
// the year view is changed.
function updatePupilRecordBanner ()
{
	$('#pupilRecordBanner').empty();
	$('#pupilRecordBanner').append(pupilInformation.displayName + " (" 
			+ pupilInformation.email + ") for Year " + $('#pupilRecordTable').data("recordyear"));
}

function updatePupilRecordTable ()
{
	// Gathers the information for the query to the server.
	var request = new Object();
	if ($('#pupilRecordTable').data("recordyear") != undefined)
		request.Year  = $('#pupilRecordTable').data("recordyear");
	else request.Year = "current";
	request.Type = $('#pupilRecordTable').data("recordtype");
	
	// Send the get to the server using the pupil key saved from the servlet.
	$.get('/api/pupil/' + pupilInformation.key + '/progress', request, function (data, textStatus, jqXHR) {
		// Empties all the current records (before we know success?)
		$('#pupilRecordTable tbody').empty();
		
		var jsonData = jQuery.parseJSON(data);
		if (jsonData.length > 0)
		{
			// Constructs rows in the table for the records read.
			$('#pupilRecordTable tbody').append(Mustache.render('{{#.}}\
				       <tr>\
				         <td>{{target.subject.name}}</td>\
				         <td class="centerColumn">{{target.threeLevelsTargetGrade}}</td>\
				         <td class="centerColumn">{{target.fourLevelsTargetGrade}}</td>\
				         <td class="centerColumn">{{target.fiveLevelsTargetGrade}}</td>\
				         <td class="centerColumn">{{progress.currentLevel}}</td>\
					     <td>{{progress.nextSteps}}</td>\
					     <td><div data-role="controlgroup" data-type="horizontal" data-mini="true"><a data-role="button" data-recordkey="{{progress.key}}" data-icon="gear" data-iconpos="notext">Edit</a>\
							<a data-rel="dialog" data-role="button" data-recordkey="{{progress.key}}" data-icon="delete" data-iconpos="notext">Delete</a></div></td>\
					   </tr>{{/.}}', jsonData));
			
			// Styles the newly created buttons.
			$('#pupilRecordTable a').button();
			$('#pupilRecordTable div').controlgroup();
			
			// Adds handlers to the newly created buttons.
			$('#pupilRecordTable a[data-icon="delete"]').click(deleteRecord);
		}
		
		// Update the banner even if nothing was recieved.
		updatePupilRecordBanner()
	});
}

// Deletes a selected progress record - the id is retrieved from the
// button that was clicked to trigger the even (setup in when the table
// was updated in updatePupilRecordTable).
function deleteRecord (eventObj)
{
	var recordKey = $(eventObj.currentTarget).data('recordkey');
	
	$.ajax('/api/progress/'+ recordKey, { type: "DELETE" })
		.done(function () { updatePupilRecordTable();})
		.fail(function () { alert("Unable to delete the record specified!"); });
}

// Changes the record type when the footer is clicked and
// triggers an update of the table when necessary.
function changeRecordType(eventObj)
{
	var type = $(eventObj.currentTarget).data('recordtype');
	if (type != $('#pupilRecordTable').data('recordtype'))
	{
		$('#pupilRecordTable').data('recordtype', type);
		updatePupilRecordTable();
	}
}

// Change the year saved when a new year is clicked in
// the year view popup. Only refreshes the records if
// necessary.
function changeYearView(eventObj)
{
	var year = $(eventObj.currentTarget).data('year');
	if (year != $('#pupilRecordTable').data('recordyear'))
	{
		$('#pupilRecordTable').data('recordyear', year);
		updatePupilRecordTable();
	}
}

// Sets up the buttons in the year view popup before
// it is displayed to the user.
function setupYearViewPopup()
{
	$('#pupilRecordsYearPopup ul a').removeClass('ui-disabled');
	
	var year  = $('#pupilRecordTable').data("recordyear");
	$('#pupilRecordsYearPopup ul a[data-year="' + year + '"]').addClass('ui-disabled');
}

function initialise ()
{
	// Save the year and type to the datatable for later use and triggers and
	// update of the records shown in the table.
	$('#pupilRecordTable').data('recordyear', pupilInformation.year);
	var type = $('#pupilRecordsPageFooter a.ui-btn-active').data("recordtype");
	$('#pupilRecordTable').data("recordtype", type);
	updatePupilRecordTable();
	
	// Handler for clicking buttons in footer.
	$('#pupilRecordsPageFooter a').click(changeRecordType);
	
	// Handlers for the year view popup.
	$('#pupilRecordsYearPopup ul a').click(changeYearView);
	$('#pupilRecordsYearPopup').on('popupbeforeposition', setupYearViewPopup);
	
	// Hides all the year options that should be in the future for
	// this pupil ... hope it doesn't cause trouble.
	$('#pupilRecordsYearPopup ul a').each(function(index)
			{
				if ($(this).data("year") > pupilInformation.year)
					$(this).parents('li').addClass("ui-screen-hidden");
				else $(this).parent('li').removeClass("ui-screen-hidden");
			});
	
	// Resets the buttons in the footer so the appropriate R is
	// shown after we retrun to the page from somewhere else (e.g. dialog).
	$('#pupilRecordsPage').on('pagebeforeshow', function ()
			{
				var type = $('#pupilRecordTable').data("recordtype");
		
				$('#pupilRecordsPageFooter a').removeClass('ui-btn-active');
				$('#pupilRecordsPageFooter a[data-recordtype="' + type + '"]').addClass('ui-btn-active');
			});
}