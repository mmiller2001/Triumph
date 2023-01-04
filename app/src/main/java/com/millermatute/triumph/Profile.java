package com.millermatute.triumph;

public class Profile {

    private User user;

    public Profile() {}

    public Profile(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
