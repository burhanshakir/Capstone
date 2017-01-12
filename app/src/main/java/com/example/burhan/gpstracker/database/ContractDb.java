package com.example.burhan.gpstracker.database;

import android.provider.BaseColumns;

public final class ContractDb {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    int _id;
    String _location;
    String _date;

    // Empty constructor
    public ContractDb(){

    }
    // constructor
    public ContractDb(int id, String name, String _phone_number){
        this._id = id;
        this._location = _location;
        this._date = _date;
    }

    // constructor
    public ContractDb(String location, String _date){
        this._location = location;
        this._date = _date;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }


    public String getLocation(){
        return this._location;
    }


    public void setLocation(String location){
        this._location = location;
    }

    public String getDate(){
        return this._date;
    }

    // setting phone number
    public void setDate(String date){
        this._date = date;
    }


}
