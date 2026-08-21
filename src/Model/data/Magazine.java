
package Model.data;

import java.util.UUID;


public class Magazine extends BookData{
    private int issue;
    private String type;

    public Magazine() {
    }

    public Magazine(UUID id, String title,String author, double price, String publishDate,int issue, String type ) {
        super(id, title, author, price, publishDate);
        this.issue = issue;
        this.type = type;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    
    
}
