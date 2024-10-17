package com.wilson.dispatchintakescanner.Utilities;

public class Client {
    String clientname;
    int clientid;
    public Client(String clientname, int clientid){
        this.clientname=clientname;
        this.clientid=clientid;
    }

    public String getClientname(){
        return clientname;
    }

    public int getClientid() {
        return clientid;
    }
}
