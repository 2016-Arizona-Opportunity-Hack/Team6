package hackathon.petcare;



public class CobbocEvent {

    public static final int USER_OWN_LOCATION = 1;


    private boolean STATUS;

    private int TYPE;

    private Object VALUE;

    public CobbocEvent(int type) {
        this(type, true, null);
    }

    public CobbocEvent(int type, boolean status) {
        this(type, status, null);
    }

    public CobbocEvent(int type, boolean status, Object value) {
        TYPE = type;
        STATUS = status;
        VALUE = value;
    }
    public CobbocEvent(int type, boolean status, int value) {
        TYPE = type;
        STATUS = status;
        VALUE = value;
    }

//	public void setStatus(boolean status) {
//		STATUS = status;
//	}

    public boolean getStatus() {
        return STATUS;
    }

//	public void setType(int type) {
//		TYPE = type;
//	}

    public int getType() {
        return TYPE;
    }

//	public void setValue(Object value) {
//		VALUE = value;
//	}

    public Object getValue() {
        return VALUE;
    }
}
