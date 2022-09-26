package com.endava.cats.fuzzer.headers.only;

import com.endava.cats.annotations.EmojiFuzzer;
import com.endava.cats.annotations.HeaderFuzzer;
import com.endava.cats.fuzzer.executor.HeadersIteratorExecutor;
import com.endava.cats.fuzzer.headers.base.InvisibleCharsBaseFuzzer;
import com.endava.cats.model.FuzzingStrategy;
import com.endava.cats.model.util.PayloadUtils;

import javax.inject.Singleton;
import java.util.List;

@Singleton
@HeaderFuzzer
@EmojiFuzzer
public class OnlySingleCodePointEmojisInHeadersFuzzer extends InvisibleCharsBaseFuzzer {

    public OnlySingleCodePointEmojisInHeadersFuzzer(HeadersIteratorExecutor headersIteratorExecutor) {
        super(headersIteratorExecutor);
    }

    @Override
    public String typeOfDataSentToTheService() {
        return "replace value with single code point emojis";
    }

    @Override
    public List<String> getInvisibleChars() {
        return PayloadUtils.getSingleCodePointEmojis();
    }

    @Override
    public FuzzingStrategy concreteFuzzStrategy() {
        return FuzzingStrategy.replace();
    }

    @Override
    public boolean matchResponseSchema() {
        return false;
    }
}