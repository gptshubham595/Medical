package com.iitg.reportscanner;

public class User {

    public String name;
    public String image;
    public String status;
    public String thumb_image;
    public String age;

    public User() {
    }

    public User(String name, String image, String status, String thumb_image, String age) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
