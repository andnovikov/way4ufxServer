package ru.diasoft.micro.way4ufx.service;

import ru.diasoft.micro.way4ufx.domain.CardContract;

public interface CardContractService {

    CardContract save(CardContract cardContract);

    CardContract findByCardNumber(String cardNumber);

    void delete(Long id);

}
