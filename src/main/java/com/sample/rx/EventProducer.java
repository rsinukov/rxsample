package com.sample.rx;

import com.google.auto.value.AutoValue;
import rx.Observable;
import rx.Scheduler;

import java.util.concurrent.TimeUnit;

public class EventProducer {

    private final Config config;

    public EventProducer(Config config) {
        this.config = config;
    }

    public Observable<String> events() {
        return Observable.interval(config.periodMillis(), TimeUnit.MILLISECONDS, config.periodScheduler())
                .map(String::valueOf)
                .delay(config.delayMillis(), TimeUnit.MILLISECONDS, config.delayScheduler())
                .replay()
                .autoConnect(config.shareCount());
    }


    @AutoValue
    public abstract static class Config {
        abstract long periodMillis();
        abstract long delayMillis();
        abstract int shareCount();
        abstract Scheduler periodScheduler();
        abstract Scheduler delayScheduler();

        public static Builder builder() {
            return new AutoValue_EventProducer_Config.Builder();
        }

        @AutoValue.Builder
        public abstract static class Builder {
            public abstract Builder periodMillis(long periodMillis);
            public abstract Builder delayMillis(long delayMillis);
            public abstract Builder shareCount(int shareCount);
            public abstract Builder periodScheduler(Scheduler periodScheduler);
            public abstract Builder delayScheduler(Scheduler delayScheduler);
            public abstract Config build();
        }
    }
}
