package Business;
import java.io.Serializable;

public class Link implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Status {
		NOT_STARTED(0), STARTED(1), FINISHED(2);
		
		private final int value;
		private Status(int value) {
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Status getStudyStatus() {
		return studyStatus;
	}
	public Status getProcStatus() {
		return procStatus;
	}
	public void setProcStatus(Status procStatus) {
		this.procStatus = procStatus;
	}
	public void setStudyStatus(Status status) {
		this.studyStatus = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public static int getNewId() {
		return newId;
	}
	public static void setNewId(int newId) {
		Link.newId = newId;
	}
	
	private static int newId = 0;
	private int id;
	private String ref;
	private String name;
	private int priority;
	private Status studyStatus;
	private Status procStatus;
	
	public Link(){id = newId++;};
	public Link(String ref){
		id = newId++;
		this.ref = ref;
		this.name = "";
		this.priority = 0;
		this.studyStatus = Status.NOT_STARTED;
		this.procStatus = Status.NOT_STARTED;
	}
	
	@Override
	public boolean equals(Object link){
		return ref.equals(((Link)link).getRef());
	}
	
	@Override
	public int hashCode(){
		return ref.hashCode();
	}
	
	public String toString(){
		return "Article: " + name + "\nID: " + id + 
			   "\nReference: " + ref + "\nPriority: " + 
			   priority + "\nStudy status: " + studyStatus +
			   "\nProcess status: " + procStatus + "\n\n";
	}
}