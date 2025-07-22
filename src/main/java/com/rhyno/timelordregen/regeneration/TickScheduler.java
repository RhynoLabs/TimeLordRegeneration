package com.rhyno.timelordregen.regeneration;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TickScheduler {
    private static class ScheduledTask {
        long remainingTicks;
        Runnable task;

        ScheduledTask(long delayTicks, Runnable task) {
            this.remainingTicks = delayTicks;
            this.task = task;
        }
    }

    private static final List<ScheduledTask> tasks = new LinkedList<>();

    public static void schedule(long delayTicks, Runnable task) {
        tasks.add(new ScheduledTask(delayTicks, task));
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<ScheduledTask> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                ScheduledTask task = iterator.next();
                task.remainingTicks--;
                if (task.remainingTicks <= 0) {
                    task.task.run();
                    iterator.remove();
                }
            }
        });
    }
}