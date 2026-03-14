package Models;

public class Restaruant {
    private Long restaurantId;
    private String restaurantName;
    private String GSTNumber;
    private String emailId;
    private String phoneNumber;


    public Restaruant( String restaurantName, String GSTNumber, String emailId, String phoneNumber) {
        this.restaurantName = restaurantName;
        this.GSTNumber = GSTNumber;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getGSTNumber() {
        return GSTNumber;
    }

    public void setGSTNumber(String GSTNumber) {
        this.GSTNumber = GSTNumber;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
