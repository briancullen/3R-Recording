
function updateSubjectListView (jsonData)
{
	$('#manageTargetsSubjectList').empty();
	$('#manageTargetsSubjectList').append(Mustache.render('{{#.}}\
				<li><a data-subjectkey={{key}}>{{subjectName}}</a></li>{{/.}}', jsonData));
	
	$('#manageTargetsSubjectList').listview("refresh");
}

function initialiseManageTargetPage ()
{
	$.get("/api/subject", updateSubjectListView, "json");
}