/*
 * Copyright (c) 2024 - Restate Software, Inc., Restate GmbH
 *
 * This file is part of the Restate examples,
 * which is released under the MIT license.
 *
 * You can find a copy of the license in the file LICENSE
 * in the root directory of this repository or package or at
 * https://github.com/restatedev/examples/
 */

package my.example;

import dev.restate.sdk.*;
import dev.restate.sdk.annotation.Handler;
import dev.restate.sdk.annotation.Service;
import dev.restate.sdk.annotation.VirtualObject;
import dev.restate.sdk.annotation.Workflow;
import dev.restate.sdk.http.vertx.RestateHttpEndpointBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Workflow
public class Benchy {
    public static void main(String[] args) {
        RestateHttpEndpointBuilder.builder()
                .bind(new Benchy())
                .bind(new Worker())
                .buildAndListen();
    }

    @Workflow
    public String run(WorkflowContext ctx, int num_requests) {
        long started_at =
                ctx.run(JsonSerdes.LONG, System::nanoTime);
        // send num_requests to Worker handler, all with the same start_at
        var client = WorkerClient.fromContext(ctx);
        //var all_work = new ArrayList();
        //all_work.ensureCapacity(num_requests);
        for (int i = 0; i < num_requests; i++) {
            ctx.run(JsonSerdes.INT, () -> 7);
            //all_work.add(client.work(started_at));
        }
        //awaitableAll(all_work);

        long finished =
                ctx.run(JsonSerdes.LONG, System::nanoTime);
        var duration_ms = (finished - started_at) / 1000000;
        return String.format("Finished %s req/sec. Total time: %sms",  ((float) num_requests / duration_ms) * 1000, duration_ms);
    }

    @Handler
    public void interactWithWorkflow(SharedWorkflowContext ctx, String input) {
        // implement interaction logic here
    }

    private static void awaitableAll(List<Awaitable<?>> awaitables) {
        if (awaitables.size() == 1) {
            awaitables.get(0).await();
        } else if (awaitables.size() == 2) {
            Awaitable.all(awaitables.get(0), awaitables.get(1)).await();
        } else if (awaitables.size() >= 2) {
            Awaitable.all(
                            awaitables.get(0),
                            awaitables.get(1),
                            awaitables.subList(2, awaitables.size()).toArray(Awaitable[]::new))
                    .await();
        }
    }
}
