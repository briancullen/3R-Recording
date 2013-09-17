package net.mrcullen.targetrecording.entities;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class PersonEntity {
	@Id private String emailAddress;
	private String displayName;
	
	protected PersonEntity () { }
	
	public PersonEntity (String email)
	{
		this (email, email);
	}
	
	public PersonEntity (String email, String name)
	{
		emailAddress = email;
		setName(name);
	}
	
	public String getEmail () { return emailAddress; }
	public String getName () { return displayName; }
	
	public void setName (String name)
	{
		if ((name != null) && (name.length() > 0))
		{
			displayName = name;
		}
	}	
}
