function getGrades (stage, vocational)
{
	var keyStage3Grades = [ "2c", "2b", "2a", "3c", "3b", "3a", "4c", "4b", "4a",
	                		"5c", "5b", "5a", "6c", "6b", "6a", "7c", "7b", "7a" ]
	var keyStage4Grades = [ "U", "G", "F", "E", "D", "C", "B", "A", "A*" ]
	var keyStage5Grades = [ "U", "G", "F", "E", "D", "C", "B", "A", "A*" ]

	var keyStage4VocGrades = [ "FOUNDATION", "LEVEL1", "PASS", "MERIT", "DISTINCTION", "DISTINCTION*" ]
	var keyStage5VocGrades = [ "PASS", "MERIT", "DISTINCTION", "DISTINCTION*" ]
	
	var grades = keyStage3Grades;
	if (stage == 4)
	{
		if (vocational)
			grades = keyStage4VocGrades;
		else grades = keyStage4Grades;
	}
	else if (stage == 5)
	{
		if (vocational)
			grades = keyStage5VocGrades;
		else grades = keyStage4Grades;
	}

	return grades;
}

function yearToStage (year)
{
	if (year < 7 || year > 13)
		return -1;
	
	if (year > 11)
		return 5;
	else if (year > 8)
		return 4;
	else return year;
}

function convertIntakeToYear (intakeYear)
{
	var date = new Date();
	var yearGroup = date.getFullYear() - intakeYear + 7;
	if (date.getMonth < 8)
		yearGroup --;
	
	return yearGroup;
}

function convertIntakeToStage (intakeYear)
{
	var yearGroup = convertIntakeToYear (intakeYear);
	if (yearGroup > 11 && yearGroup < 14)
		return 5;
	else if (yearGroup > 8 && yearGroup < 12)
		return 4;
	else if (yearGroup == 7 || yearGroup == 8)
		return yearGroup;
	else return -1;
}