package statistic;


import java.sql.Timestamp;

/**
 * Logger represents request information
 */
public class Logger {
    private String ip;
    private String userRequest;
    private Timestamp lastTime;
    private long sentBytes;
    private long receivedBytes;


    public Logger(String ip, String userRequest, long lastTime, long sentBytes, long receivedBytes) {
        this.ip = ip;
        this.userRequest = userRequest;
        this.lastTime = new Timestamp(lastTime);
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
    }

    public String getIp() {
        return ip;
    }

    public String getUserRequest() {
        return userRequest;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public long getSentBytes() {
        return sentBytes;
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setUserRequest(String userRequest) {
        this.userRequest = userRequest;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
    }

    public void setSentBytes(long sentBytes) {
        this.sentBytes = sentBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Logger)) return false;

        Logger logger = (Logger) o;

        if (receivedBytes != logger.receivedBytes) return false;
        if (sentBytes != logger.sentBytes) return false;
        if (!ip.equals(logger.ip)) return false;
        if (!lastTime.equals(logger.lastTime)) return false;
        if (!userRequest.equals(logger.userRequest)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip.hashCode();
        result = 31 * result + userRequest.hashCode();
        result = 31 * result + lastTime.hashCode();
        result = 31 * result + (int) (sentBytes ^ (sentBytes >>> 32));
        result = 31 * result + (int) (receivedBytes ^ (receivedBytes >>> 32));
        return result;
    }
}