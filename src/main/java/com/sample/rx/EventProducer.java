package com.sample.rx;

import rx.Observable;
import rx.Scheduler;

import java.util.concurrent.TimeUnit;

public class EventProducer {

    public Observable<String> events(
            long periodMillis,
            long delayMillis,
            Scheduler periodScheduler,
            Scheduler delayScheduler,
            int shareCount
    ) {
        return Observable.interval(periodMillis, TimeUnit.MILLISECONDS, periodScheduler)
                .map(String::valueOf)
                .delay(delayMillis, TimeUnit.MILLISECONDS, delayScheduler)
                .replay()
                .autoConnect(shareCount);
    }
}
