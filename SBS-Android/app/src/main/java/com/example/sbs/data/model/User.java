package com.example.sbs.data.model;

import java.util.ArrayList;
import java.util.List;

public class User
{
    public String id;
    public String phone;
    public String email;
    public String name;
    public String userid;
    public String password;

    public List<Role> roles = new ArrayList<Role>();
}