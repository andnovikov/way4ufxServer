package ru.diasoft.micro.way4ufx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.diasoft.micro.way4ufx.domain.CardOperation;

@Repository
public interface CardOperationRepository extends JpaRepository<CardOperation, Long> {

}
