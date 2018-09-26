package entity;

import java.sql.Timestamp;
import java.util.Objects;

public class User {
    private String eMail;
    private String password;
    private Timestamp registrationDate;

    public User(String eMail, String password) {
        this.eMail = eMail;
        this.password = password;
        this.registrationDate = new Timestamp(System.currentTimeMillis());
    }

    public User(String eMail, String password, long registrationDate) {
        this.eMail = eMail;
        this.password = password;
        this.registrationDate = new Timestamp(registrationDate);
    }

    public String getMail(){
        return this.eMail;
    }

    public String getPassword(){
        return this.password;
    }

    public Timestamp getRegistrationDate(){
        return new Timestamp(registrationDate.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(eMail);
    }

    @Override
    public boolean equals(Object obj) {
        User user;

        if(obj == this)
            return true;


        if(!(obj instanceof User))
            return false;

        user = (User)obj;

        return eMail.equals(user.eMail);
    }

    @Override
    public String toString() {
        return "E-Mail: " + this.eMail;
    }
}
