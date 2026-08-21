
package Model.customer;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private String phoneNum;
    private String mail;
    private boolean isMember;

    public Customer(String name, String phoneNum, String mail, boolean isMember) {
        this.id = UUID.randomUUID();;
        this.name = name;
        this.phoneNum = phoneNum;
        this.mail = mail;
        this.isMember = isMember;
    }

    public Customer(UUID id, String name, String phoneNum, String mail, boolean isMember) {
        this.id = id;
        this.name = name;
        this.phoneNum = phoneNum;
        this.mail = mail;
        this.isMember = isMember;
    }
    
    public String toTxtLine() {
        return id.toString() + "|" + name + "|" + phoneNum + "|" + mail + "|" + isMember;
    }

    public static Customer fromTxtLine(String line) {
        String[] parts = line.split("\\|");
        UUID loadedId = UUID.fromString(parts[0]); // Converts String -> UUID object
        String name = parts[1];
        String phoneNum = parts[2];
        String mail = parts[3];
        boolean isMember = Boolean.parseBoolean(parts[4]);

        return new Customer(loadedId, name, phoneNum, mail, isMember);
    }

    public UUID getId() { return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isIsMember() {
        return isMember;
    }

    public void setIsMember(boolean isMember) {
        this.isMember = isMember;
    }
    
    
}
