package ru.diasoft.micro.way4ufx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.diasoft.micro.way4ufx.domain.*;

import java.math.BigDecimal;
import java.util.List;

@Service
public class Way4UfxProcessorServiceImpl implements Way4UfxProcessorService {

    private static Logger logger = LogManager.getLogger();

    private CardContractService cardContractService;
    private CardOperationService cardOperationService;

    @Autowired
    public Way4UfxProcessorServiceImpl(CardContractService cardContractService, CardOperationService cardOperationService){
        this.cardContractService = cardContractService;
        this.cardOperationService = cardOperationService;
    }

    @Override
    public W4MMsg processRequestDoc(W4MMsg w4MMsg) {
        final String msgCode = w4MMsg.getMsgData().getDoc().getTransType().getTransCode().getMsgCode();

        // Preapre answer
        w4MMsg.setDirection(W4DRqRs.RS);
        w4MMsg.setRespClass("I");
        w4MMsg.setRespCode(0L);

        final W4UDoc doc = w4MMsg.getMsgData().getDoc();
        W4DCDataRs dataRs = doc.getDataRs();
        if (dataRs == null) {
            dataRs = new W4DCDataRs();
            doc.setDataRs(dataRs);
        }

        W4CParmSet docRefSet = doc.getDocRefSet();
        if (docRefSet == null) {
            docRefSet = new W4CParmSet();
            doc.setDocRefSet(docRefSet);
        }
        final List<W4CParm> parm = docRefSet.getParm();
        final W4CParm w4CParm = new W4CParm();
        w4CParm.setParmCode("AuthCode");
        w4CParm.setValue("123456"); // TODO: Need AuthCode generator
        parm.add(w4CParm);

        if(doc.getDestination()!=null) {
            if(doc.getDestination().getRBSNumber()==null) {
                doc.getDestination().setRBSNumber("888888888888888888888888");
            }
        }

        CardContract cardContract = cardContractService.findByCardNumber(w4MMsg.getMsgData().getDoc().getRequestor().getContractNumber());
        Boolean needBalance = false;

        for (W4CParm balParm: w4MMsg.getMsgData().getDoc().getResultDtls().getParm()) {
            if (balParm.getParmCode().equals("Balance") &&
                balParm.getValue().equals("Y")) {
                needBalance = true;
            }
        }

        if (msgCode.equals(Constants.MSG_02000F)) {
            if (cardContract == null) {
                cardContract = new CardContract();
                cardContract.setCardNumber(w4MMsg.getMsgData().getDoc().getRequestor().getContractNumber());
                cardContract.setContractNumber(w4MMsg.getMsgData().getDoc().getRequestor().getContractNumber());
                cardContract.setClientId(0);
                cardContract.setCreditLimit(BigDecimal.valueOf(0));
                cardContract.setLimit(w4MMsg.getMsgData().getDoc().getTransaction().getAmount());

                cardContract = cardContractService.save(cardContract);
            } else {
                double curLimit = cardContract.getLimit().doubleValue();
                double increment = w4MMsg.getMsgData().getDoc().getTransaction().getAmount().doubleValue();
                cardContract.setLimit(BigDecimal.valueOf(curLimit + increment));

                cardContract = cardContractService.save(cardContract);
            }

            setStatus(w4MMsg, "Information", 0, "Successfully processed");
        }

        if (msgCode.equals(Constants.MSG_02200P)) {
            if (cardContract == null) {
                setStatus(w4MMsg, "Error", 14, "No such card");
            } else {
                double curLimit = cardContract.getLimit().doubleValue();
                double increment = w4MMsg.getMsgData().getDoc().getTransaction().getAmount().doubleValue();
                if (curLimit < increment) {
                    setStatus(w4MMsg, "Error", 51, "Not sufficient funds");
                } else {
                    cardContract.setLimit(BigDecimal.valueOf(curLimit - increment));
                    cardContract = cardContractService.save(cardContract);
                    setStatus(w4MMsg, "Information", 0, "Successfully processed");
                }
            }
        }

        // Получение остатков
        if (msgCode.equals(Constants.MSG_01000B) || needBalance) {
            if (cardContract == null) {
                setStatus(w4MMsg, "Error", 14, "No such card");
            } else {
                W4CBalanceList balances = dataRs.getBalances();
                if (balances == null) {
                    balances = new W4CBalanceList();
                    dataRs.setBalances(balances);
                }
                final List<W4CBalance> balList = balances.getBalance();

                W4CBalance bal;
                bal = new W4CBalance();
                bal.setName("AVAILABLE");
                bal.setAmount(cardContract.getLimit());

                bal.setCurrency("RUR");
                balList.add(bal);

                bal = new W4CBalance();
                bal.setName("BLOCKED");
                bal.setAmount(BigDecimal.valueOf(0));
                bal.setCurrency("RUR");
                balList.add(bal);

                bal = new W4CBalance();
                bal.setName("TOTAL_BALANCE");
                bal.setAmount(cardContract.getLimit());
                bal.setCurrency("RUR");
                balList.add(bal);

                bal = new W4CBalance();
                bal.setName("CR_LIMIT");
                bal.setAmount(cardContract.getCreditLimit());
                bal.setCurrency("RUR");
                balList.add(bal);

                if (msgCode.equals(Constants.MSG_01000B)) {
                    setStatus(w4MMsg, "Information", 0, "Successfully processed");
                }
            }
        }

        /*
        ||
            msgCode.equals(Constants.MSG_04000F) ||
            msgCode.equals(Constants.MSG_04200P) ||
            msgCode.equals(Constants.MSG_04200F) ||
            msgCode.equals(Constants.MSG_BILLING_PAYMENT_WITH_CARD) ||
            msgCode.equals(Constants.MSG_BILLING_PAYMENT_BILLER_CHECK))
         */

        return w4MMsg;
    }

    @Override
    public W4MMsg processRequestApplication(W4MMsg w4MMsg) {
        return null;
    }

    private void setStatus(final W4MMsg reqMsg, final String respClass, final int respCode, final String msg) {
        W4DCStatus status = reqMsg.getMsgData().getDoc().getStatus();
        if (status == null) {
            status = new W4DCStatus();
            reqMsg.getMsgData().getDoc().setStatus(status);
        }
        status.setRespClass(respClass);
        status.setRespCode(respCode);
        reqMsg.setRespCode((long) respCode);
        List<String> respText = status.getRespText();
        respText.clear();
        respText.add(msg);
    }
}
