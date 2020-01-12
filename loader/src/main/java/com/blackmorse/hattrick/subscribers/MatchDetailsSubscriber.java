package com.blackmorse.hattrick.subscribers;

import com.blackmorse.hattrick.clickhouse.ClickhouseBatcher;
import com.blackmorse.hattrick.clickhouse.ClickhouseBatcherFactory;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchDetailsSubscriber implements Subscriber<MatchDetails> {
    private final ClickhouseBatcher<MatchDetails> batcher;

    @Autowired
    public MatchDetailsSubscriber(ClickhouseBatcherFactory factory) {
        this.batcher = factory.createMatchDetails();
    }

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(MatchDetails matchDetails) {
        batcher.addToBatch(matchDetails);
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
        batcher.flush();
    }
}
