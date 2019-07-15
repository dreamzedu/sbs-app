package com.example.sbs.data.model;

import java.util.ArrayList;
import java.util.List;

public class Role
{
    public int id;
    public String name;
    public String description;
    public List<Permission> permissions = new ArrayList<Permission>();
}
