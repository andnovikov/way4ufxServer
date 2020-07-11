package ru.diasoft.micro.way4ufx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.diasoft.micro.way4ufx.domain.CardOperation;
import ru.diasoft.micro.way4ufx.repository.CardOperationRepository;

@Service
public class CardOperationServiceImpl implements CardOperationService {

    private final Logger log = LoggerFactory.getLogger(CardOperationServiceImpl.class);

    CardOperationRepository cardOperationRepository;

    @Autowired
    public CardOperationServiceImpl (CardOperationRepository cardOperationRepository){
        this.cardOperationRepository = cardOperationRepository;
    }

    @Override
    public CardOperation save(CardOperation cardOperation) {
        log.debug("Request to save CardOperation : {}", cardOperation);
        return cardOperationRepository.save(cardOperation);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CardOperation : {}", id);
        cardOperationRepository.deleteById(id);
    }
}
