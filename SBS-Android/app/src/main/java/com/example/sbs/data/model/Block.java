package com.example.sbs.data.model;

public class Block implements IConvertible {
    public String id;
    public String name;

    @Override
    public Object getKey() {
        return id;
    }

    @Override
    public Object getValue() {
        return name;
    }
}
