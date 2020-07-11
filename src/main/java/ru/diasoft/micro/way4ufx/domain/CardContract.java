package ru.diasoft.micro.way4ufx.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tCardContract")
@ApiModel(value ="CardContract", description = "Card contract with balance")
public class CardContract {

    @Id
    @Column(name = "CardContractId")
    @SequenceGenerator( name = "tcardcontract_cardcontractid_seq", sequenceName = "tcardcontract_cardcontractid_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tcardcontract_cardcontractid_seq" )
    private long id;

    @Column(name = "ClientId")
    private long clientId = 0; // TODO: Сделать добавление клиента при помощи заведения карты

    @Column(name = "CurrencyId")
    private long currencyId = 0; // TODO: Сделать справочник валют и открывать карты с разными валютами

    @Column(name = "ContractNumber")
    private String contractNumber;

    @Column(name = "CardNumber")
    private String cardNumber;

    @Column(name = "Limit")
    private BigDecimal limit;

    @Column(name = "CreditLimit")
    private BigDecimal creditLimit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}
