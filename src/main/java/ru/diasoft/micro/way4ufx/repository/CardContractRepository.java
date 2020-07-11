package ru.diasoft.micro.way4ufx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.diasoft.micro.way4ufx.domain.CardContract;

@Repository
public interface CardContractRepository extends JpaRepository<CardContract, Long> {

    public CardContract findFirstByCardNumber(String cardNumber);

}
