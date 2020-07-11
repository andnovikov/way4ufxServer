package ru.diasoft.micro.way4ufx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.diasoft.micro.way4ufx.domain.CardContract;
import ru.diasoft.micro.way4ufx.repository.CardContractRepository;

@Service
public class CardContractServiceImpl implements CardContractService {

    private final Logger log = LoggerFactory.getLogger(CardContractServiceImpl.class);

    private CardContractRepository cardContractRepository;

    @Autowired
    public CardContractServiceImpl(CardContractRepository cardContractRepository){
        this.cardContractRepository = cardContractRepository;
    }

    @Override
    public CardContract save(CardContract cardContract) {
        log.debug("Request to save CardContract : {}", cardContract);

        return cardContractRepository.save(cardContract);
    }

    @Override
    public CardContract findByCardNumber(String cardNumber) {
        log.debug("Request to find CardContract by CardNumber : {}", cardNumber);
        return cardContractRepository.findFirstByCardNumber(cardNumber);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CardContract: {}", id);
        cardContractRepository.deleteById(id);
    }
}
