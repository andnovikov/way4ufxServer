package ru.diasoft.micro.way4ufx.service;

import ru.diasoft.micro.way4ufx.domain.W4MMsg;

public interface Way4UfxProcessorService {

    W4MMsg processRequestDoc(W4MMsg w4MMsg);

    W4MMsg processRequestApplication(W4MMsg w4MMsg);

}
