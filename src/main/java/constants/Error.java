package constants;

public enum Error {

    EMAIL_ALREADY_EXISTS(0, "E-Mail already exists"),
    PASSWORD_IS_INCORRECT(1, "password is incorrect");

    private final int code;
    private final String message;

    Error(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int code(){
        return this.code;
    }

    public String message(){
        return this.message;
    }
}
