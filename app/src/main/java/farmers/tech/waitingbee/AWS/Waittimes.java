package farmers.tech.waitingbee.AWS;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

/**
 * Created by GauthamVejandla on 7/26/16.
 */
@DynamoDBTable(tableName = "Waittimes")
public class Waittimes {
    private String placeid;
    private long updatetime;
    private int upvote;
    private long waititme;


    @DynamoDBHashKey(attributeName = "placeid")
    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    @DynamoDBAttribute(attributeName = "updatetime")
    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    @DynamoDBAttribute(attributeName = "upvote")
    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    @DynamoDBAttribute(attributeName = "waititme")
    public long getWaititme() {
        return waititme;
    }

    public void setWaititme(long waititme) {
        this.waititme = waititme;
    }
}