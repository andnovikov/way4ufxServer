package ru.diasoft.micro.way4ufx.domain;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tCardOperation")
@ApiModel(value ="CardOperation", description = "Operation with cards")
public class CardOperation {

    @Id
    @Column(name = "CardOperationId")
    @SequenceGenerator( name = "tcardoperation_cardcontractid_seq", sequenceName = "tcardoperation_cardcontractid_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tcardoperation_cardcontractid_seq" )
    private long id;

    @Column(name ="CardContractId")
    private long cardContractId;

    @Column(name = "CurrencyId")
    private long currencyId;

    @Column(name = "DateProcessing")
    private LocalDateTime dateProcessing;

    @Column(name ="OperationAmount")
    private BigDecimal operationAmount;

    @Column(name ="CardAmount")
    private BigDecimal cardAmount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCardContractId() {
        return cardContractId;
    }

    public void setCardContractId(long cardContractId) {
        this.cardContractId = cardContractId;
    }

    public long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }

    public LocalDateTime getDateProcessing() {
        return dateProcessing;
    }

    public void setDateProcessing(LocalDateTime dateProcessing) {
        this.dateProcessing = dateProcessing;
    }

    public BigDecimal getOperationAmount() {
        return operationAmount;
    }

    public void setOperationAmount(BigDecimal operationAmount) {
        this.operationAmount = operationAmount;
    }

    public BigDecimal getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(BigDecimal cardAmount) {
        this.cardAmount = cardAmount;
    }
}
