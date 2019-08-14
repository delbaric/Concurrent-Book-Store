package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    // data members
    private int currTick;

    // constructor
    public TickBroadcast (int currTick) {
        this.currTick = currTick;
    }

    public int getCurrTick (){
        return this.currTick;
    }
}
