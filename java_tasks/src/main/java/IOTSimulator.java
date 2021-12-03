import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.*;
import redis.clients.jedis.Jedis;

public class IOTSimulator {

    public static void main(String[] args) {
        // create redis connection
        Jedis jedis = new Jedis("host.docker.internal", 6379);
        // create device id
        String deviceId = UUID.randomUUID().toString();
        // create timer
        Timer timer = new Timer();
        // send message to redis from the device every second
        timer.schedule(new SendMessage(jedis, deviceId), 0, 1000);
    }

    // class that handle device's message creation and send it to Redis
    public static class SendMessage extends TimerTask {
        Jedis jedis;
        String deviceId;
        public SendMessage(Jedis jedis, String deviceId) {
            this.jedis = jedis;
            this.deviceId = deviceId;
        }
        @Override
        public void run() {
            JSONObject message = getMessage(this.deviceId);
            // send device message to application queue on redis
            this.jedis.rpush("application", message.toString());
        }
    }

    // function that create a random device's message
    public static JSONObject getMessage(String deviceId) {
        // prepare data variable
        long currentTime = System.currentTimeMillis()/1000;
        String time = Long.toString(currentTime);
        int temperature = -40 + (int) (Math.random()*80);
        JSONObject location = getRandomLocation();
        // fill data object
        JSONObject data = new JSONObject();
        data.put("deviceId", deviceId);
        data.put("temperature", temperature);
        data.put("location", location);
        data.put("time", time);
        // fill and return message object
        JSONObject message = new JSONObject();
        message.put("data", data);
        return message;
    }

    // function that create a random location
    public static JSONObject getRandomLocation() {
        // initialize random longitude and random latitude
        double randomLongitude = -180 + Math.random() * 360;
        double randomLatitude = -180 + Math.random() * 360;
        // put them in a location JSON object and return it
        JSONObject randomLocation = new JSONObject();
        randomLocation.put("latitude", Double.toString(randomLatitude));
        randomLocation.put("longitude", Double.toString(randomLongitude));
        return randomLocation;
    }

}