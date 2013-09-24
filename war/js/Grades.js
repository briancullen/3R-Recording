function getGrades (keyStage, vocational)
{
	var keyStage3Grades = [ "2c", "2b", "2a", "3c", "3b", "3a", "4c", "4b", "4a",
	                		"5c", "5b", "5a", "6c", "6b", "6a", "7c", "7b", "7a" ]
	var keyStage4Grades = [ "U", "G", "F", "E", "D", "C", "B", "A", "A*" ]
	var keyStage5Grades = [ "U", "G", "F", "E", "D", "C", "B", "A", "A*" ]

	var keyStage4VocGrades = [ "PASS", "MERIT", "DISTINCTION", "DISTINCTION*" ]
	var keyStage5VocGrades = [ "PASS", "MERIT", "DISTINCTION", "DISTINCTION*" ]
	
	var grades = keyStage3Grades;
	if (keyStage == 4)
	{
		if (vocational)
			grades = keyStage4VocGrades;
		else grades = keyStage4Grades;
	}
	else if (keyStage == 5)
	{
		if (vocational)
			grades = keyStage5VocGrades;
		else grades = keyStage4Grades;
	}

	return grades;
}

function yearToKeyStage (year)
{
	if (year < 7 || year > 13)
		return -1;
	
	if (year > 11)
		return 5;
	else if (year > 8)
		return 4;
	else return 3;
}