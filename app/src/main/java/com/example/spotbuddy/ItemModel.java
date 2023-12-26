package com.example.spotbuddy;
public class ItemModel {
    private String City, State, Name, Birthday, Birthmonth, Birthyear, Profession, College, School, MBTI, Bio, Interests, Photo1, Photo2, Photo3, Photo4, Gender, PIN, Country, UID;

    // Empty constructor
    public ItemModel() {
    }

    public ItemModel(String city, String state, String name, String birthday, String birthmonth, String birthyear, String profession, String college, String school, String mbti, String bio, String interests, String photo1, String photo2, String photo3, String photo4, String gender, String PIN, String country, String uid) {
        this.City = city;
        this.State = state;
        this.Name = name;
        this.Birthday = birthday;
        this.Birthmonth = birthmonth;
        this.Birthyear = birthyear;
        this.Profession = profession;
        this.College = college;
        this.School = school;
        this.MBTI = mbti;
        this.Bio = bio;
        this.Interests = interests;
        this.Photo1 = photo1;
        this.Photo2 = photo2;
        this.Photo3 = photo3;
        this.Photo4 = photo4;
        this.Gender = gender;
        this.PIN = PIN;
        this.Country = country;
        this.UID = uid;

    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getBirthmonth() {
        return Birthmonth;
    }

    public void setBirthmonth(String birthmonth) {
        Birthmonth = birthmonth;
    }

    public String getBirthyear() {
        return Birthyear;
    }

    public void setBirthyear(String birthyear) {
        Birthyear = birthyear;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String getCollege() {
        return College;
    }

    public void setCollege(String college) {
        College = college;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        School = school;
    }

    public String getMBTI() {
        return MBTI;
    }

    public void setMBTI(String mbti) {
        MBTI = mbti;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getInterests() {
        return Interests;
    }

    public void setInterests(String interests) {
        Interests = interests;
    }

    public String getPhoto1() {
        return Photo1;
    }

    public void setPhoto1(String photo1) {
        Photo1 = photo1;
    }

    public String getPhoto2() {
        return Photo2;
    }

    public void setPhoto2(String photo2) {
        Photo2 = photo2;
    }

    public String getPhoto3() {
        return Photo3;
    }

    public void setPhoto3(String photo3) {
        Photo3 = photo3;
    }

    public String getPhoto4() {
        return Photo4;
    }

    public void setPhoto4(String photo4) {
        Photo4 = photo4;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
