import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import redis.clients.jedis.Jedis;

import java.net.*;

public class AggregateComponent {
    public static void main(String[] args) throws IOException {

        // connect to socket
        Socket s = new Socket("127.0.0.1", 4999);
        // connect to redis
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        // prepare writing stream
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        while(true) {
            // infinite redis queue pop and writing the message to the socket
            List<String> redisElement = jedis.brpop(0, "application");
            String message = redisElement.get(1);
            pr.println(message);
            pr.flush();
        }
    }

}


