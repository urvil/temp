package com.reveautomation.revesmartsecuritykit_online.model;

/**
 * Created by d on 15-04-2017.
 */

public class MyLocks {

    public String lockName = "",Zone_list = "",
    lockMacAddress = "";
    public int id =-1,type = 0;
    public boolean isConnected = false;

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        MyLocks guest = (MyLocks) object;
        return this.lockMacAddress != null && this.lockMacAddress.equals(guest.lockMacAddress);
    }

    @Override
    public int hashCode() {
        int result = 17;
        //for String
        result = 31 * result + (this.lockMacAddress == null ? 0 : this.lockMacAddress.hashCode());
        return result;
    }
}
