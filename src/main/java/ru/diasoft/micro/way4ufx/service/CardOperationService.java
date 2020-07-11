package ru.diasoft.micro.way4ufx.service;

import ru.diasoft.micro.way4ufx.domain.CardOperation;

public interface CardOperationService {

    CardOperation save(CardOperation cardOperation);

    void delete(Long id);

}
