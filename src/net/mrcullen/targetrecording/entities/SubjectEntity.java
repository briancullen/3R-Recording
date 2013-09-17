package net.mrcullen.targetrecording.entities;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class SubjectEntity {
	private @Id Long subjectId;
	private String subjectName;
	private boolean vocational;
	
	protected SubjectEntity () { subjectId = null; vocational = false; }
	
	public SubjectEntity (String name)
	{
		this ();
		setName(name);
	}
	
	public SubjectEntity (String name, boolean isVocational)
	{
		this ();
		setName(name);
		setVocational(isVocational);
	}

	
	public Long getSubjectId () { return subjectId; }
	
	public String getName () { return subjectName; }
	public void setName (String name)
	{
		if ((name != null) && (name.length() > 0))
		{
			subjectName = name;
		}
	}
	
	public void setVocational (boolean newValue)
	{
		vocational = newValue;
	}
	
	public boolean isVocational ()
	{
		return vocational;
	}
}
