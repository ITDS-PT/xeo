package netgest.bo.runtime.robots.ejbtimers;

import netgest.bo.runtime.boObject;

public class boScheduleThreadHelper {
    public boScheduleThreadHelper() {
    }
    
    private boObject schedule=null;
    
    private boolean locked =false;


    public void setSchedule(boObject schedule) {
        this.schedule = schedule;
    }

    public boObject getSchedule() {
        return schedule;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}
