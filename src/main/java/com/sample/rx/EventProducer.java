package com.sample.rx;

import rx.Observable;
import rx.Scheduler;

import java.util.concurrent.TimeUnit;

public class EventProducer {

    public Observable<String> events(long periodMillis, Scheduler timeScheduler) {
        return Observable.interval(periodMillis, TimeUnit.MILLISECONDS, timeScheduler)
                .map(String::valueOf);
    }
}
