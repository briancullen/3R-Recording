package net.mrcullen.targetrecording.entities;



import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
@Cache
public class PupilEntity extends PersonEntity {
	
	@Load @Index private Ref<FormEntity> form;
	
	protected PupilEntity () { }
	
	public PupilEntity (String email, String name, Ref<FormEntity> form)
	{
		super (email, name);
		this.form = form;
	}	
	
	public boolean setForm (Ref<FormEntity> form)
	{
		if (form != null)
		{
			this.form = form;
			return true;
		}
		else return false;
	}
	
	public Ref<FormEntity> getForm () { return form; }
}