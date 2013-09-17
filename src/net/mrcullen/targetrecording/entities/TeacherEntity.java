package net.mrcullen.targetrecording.entities;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
public class TeacherEntity extends PersonEntity {
	private boolean admin;
	@Index private String staffCode;
	
	protected TeacherEntity () { }
	
	public TeacherEntity (String email)
	{
		this (email, email, null);
	}
	
	public TeacherEntity (String email, String name, String staffCode)
	{
		this (email, name, staffCode, false);
	}
	
	public TeacherEntity (String email, String name, String staffCode, boolean adminLevel)
	{
		super (email, name);
		admin = adminLevel;
		setStaffCode(staffCode);
	}
	
	public boolean isAdmin () { return admin; }
	public void toggleAdminPermission ()
	{
		admin = !admin;
	}
	
	public String getStaffCode () { return staffCode; }
	public boolean setStaffCode (String newCode) {
		if ((newCode != null) && (!newCode.isEmpty()))
		{
			staffCode = newCode.toUpperCase();
			return true;
		}
		
		return false;
	}

	public void setAdminPermission(boolean newPermission) {
		admin = newPermission;
	}
}
