package my.example;

import dev.restate.sdk.Context;
import dev.restate.sdk.JsonSerdes;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;

@Service
public class Worker {

    @Handler
    public long work(Context ctx, long started_at) {
        //System.out.println(String.format("Work! %s", started_at));
        return started_at;
    }

}

