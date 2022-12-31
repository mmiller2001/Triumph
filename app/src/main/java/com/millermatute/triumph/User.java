package com.millermatute.triumph;

public class User {

    private String displayName;
    private String givenName;
    private String familyName;
    private String email;
    private String id;
    private String photoUrl;

    public User() {}

    public User(String displayName, String givenName, String familyName, String email, String id, String photoUrl) {
        this.displayName = displayName;
        this.givenName = givenName;
        this.familyName = familyName;
        this.email = email;
        this.id = id;
        this.photoUrl = photoUrl;
    }

    public String getDisplayName() {return displayName;}
    public String getGivenName() {return givenName;}
    public String getFamilyName() {return familyName;}
    public String getEmail() {return email;}
    public String getId() {return id;}
    public String getPhotoUrl() {return photoUrl;}

    public void setDisplayName(String displayName) {this.displayName = displayName;}
    public void setGivenName(String givenName) {this.givenName = givenName;}
    public void setFamilyName(String familyName) {this.familyName = familyName;}
    public void setEmail(String email) {this.email = email;}
    public void setId(String id) {this.id = id;}
    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}
}
