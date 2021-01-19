package store;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Store {
    public final String ID;
    public final Path path;

    private int leaseTime;

    private Date expiationDate;

    private boolean started = false;

    private Timer myTimer;

    public int getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

    public long getExpectedBytes() {
        return expectedBytes;
    }

    public void setExpectedBytes(long expectedBytes) {
        this.expectedBytes = expectedBytes;
    }

    private int fileNumber;

    private long expectedBytes;

    private ArrayList<StoreExpirationListener> expirationListeners = new ArrayList<StoreExpirationListener>();

    public Store(String ID, Path path, int leaseTime) {
        super();
        this.ID = ID;
        this.path = path;
        this.leaseTime = leaseTime;
        myTimer = new Timer();
    }

    public void start(){
        if(started)
            return; //TODO : what should we do if the timer is already started ? Reschedule ? nothing ?

        started = true;
        this.expiationDate = new Date(System.currentTimeMillis() + ((long) leaseTime * 1000L) );

        Store tempThis = this;

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for(int i=0; i<expirationListeners.size(); i++){
                    StoreExpirationListener exp = expirationListeners.get(i);
                    exp.onExpired(tempThis);
                }
            }
        }, expiationDate);
    }

    public void addExpirationListener(StoreExpirationListener l){
        this.expirationListeners.add(l);
    }

    public Date getExpirationDate(){
        if(started){
            return this.expiationDate;
        }
        return null;
    }

    public void removeExpirationListener(StoreExpirationListener l){
        this.expirationListeners.remove(l);
    }

    public boolean isStarted(){
        return started;
    }

}
