package com.cf.tech.ble.search.events;

import com.cf.beans.CmdData;

public class OperationEvent {
    public int mCmdType;
    public CmdData mCmdData;

    public OperationEvent(int pCmdType, CmdData pCmdData) {
        mCmdType = pCmdType;
        mCmdData = pCmdData;
    }
}
