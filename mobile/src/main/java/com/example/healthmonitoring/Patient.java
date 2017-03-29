package com.example.healthmonitoring;

/**
 * Created by dapik on 3/28/2017.
 */

public class Patient {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getHr_limits() {
        return hr_limits;
    }

    public void setHr_limits(String hr_limits) {
        this.hr_limits = hr_limits;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmer_contact() {
        return emer_contact;
    }

    public void setEmer_contact(String emer_contact) {
        this.emer_contact = emer_contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String id;
    private String f_name;
    private String l_name;
    private String hr_limits;
    private String address;
    private String age;
    private String city;
    private String state;
    private String emer_contact;
    private String gender;
    private String phone;
}
