package ru.diasoft.micro.way4ufx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import ru.diasoft.micro.way4ufx.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.util.List;

@Service
public class Way4UfxProcessorServiceImpl implements Way4UfxProcessorService {

    private static Logger logger = LogManager.getLogger();

    private Jaxb2Marshaller jaxb2Marshaller;

    @Autowired
    public Way4UfxProcessorServiceImpl(Jaxb2Marshaller jaxb2Marshaller){
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    @Override
    public String processRequest(String requestBody) {
        W4MMsg w4MMsgResult = new W4MMsg();
        try {
            JAXBElement<W4MMsg> w4MMsgJAXBElement = (JAXBElement<W4MMsg>) jaxb2Marshaller.unmarshal(new StringSource(requestBody));
            if (w4MMsgJAXBElement.getValue() != null) {
                W4MMsg w4MMsg = w4MMsgJAXBElement.getValue();
                if (w4MMsg.getMsgType().equals("Doc")) {
                    w4MMsgResult = processRequestDoc(w4MMsg);
                } else if (w4MMsg.getMsgType().equals("Application")) {
                    w4MMsgResult = processRequestApplication(w4MMsg);
                } else {
                    w4MMsgResult = null;
                }

                try {
                    StringResult result = new StringResult();
                    JAXBElement<W4MMsg> jaxbElement = new JAXBElement<>(new QName("UFXMsg"), W4MMsg.class, w4MMsgResult);
                    jaxb2Marshaller.marshal(jaxbElement, result);
                    return result.toString();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }

            } else {
                throw new Exception("Illegal request body");
            }
        }
        catch (Exception e) {
            logger.info("Error processing request");
            e.printStackTrace();
        }
        return "Error";
    }

    private W4MMsg processRequestDoc(W4MMsg w4MMsg) {
        W4MMsg resW4MMsg = null;
        final String msgCode = w4MMsg.getMsgData().getDoc().getTransType().getTransCode().getMsgCode();
        if (msgCode.equals(Constants.MSG_02000F) ||
            msgCode.equals(Constants.MSG_02200P) ||
            msgCode.equals(Constants.MSG_04000F) ||
            msgCode.equals(Constants.MSG_04200P) ||
            msgCode.equals(Constants.MSG_01000B) ||
            msgCode.equals(Constants.MSG_04200F) ||
            msgCode.equals(Constants.MSG_BILLING_PAYMENT_WITH_CARD) ||
            msgCode.equals(Constants.MSG_BILLING_PAYMENT_BILLER_CHECK)) {

            resW4MMsg = enrichDataAndChangeDirrection(w4MMsg);
            checkForNegativeAnswer(msgCode, w4MMsg);
        }

        return resW4MMsg;
    }

    private W4MMsg processRequestApplication(W4MMsg w4MMsg) {
        return null;
    }

    private W4MMsg enrichDataAndChangeDirrection(W4MMsg request) {

        request.setDirection(W4DRqRs.RS);
        request.setRespClass("I");
        request.setRespCode(0L);

        final W4UDoc doc = request.getMsgData().getDoc();
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
        w4CParm.setValue("123456");
        parm.add(w4CParm);

        W4CBalanceList balances = dataRs.getBalances();
        if (balances == null) {
            balances = new W4CBalanceList();
            dataRs.setBalances(balances);
        }
        final List<W4CBalance> balList = balances.getBalance();

        W4CBalance bal;
        bal = new W4CBalance();
        bal.setName("AVAILABLE");
        bal.setAmount(BigDecimal.valueOf(1260.12));

        String rur = "RUR";
        if (!request.getMsgData().getDoc().getTransType().getTransCode().getMsgCode().equals(Constants.MSG_01000B)) {
            rur = request.getMsgData().getDoc().getTransaction().getCurrency();
        }
        bal.setCurrency(rur);
        balList.add(bal);

        bal = new W4CBalance();
        bal.setName("BLOCKED");
        bal.setAmount(BigDecimal.valueOf(240.24));
        bal.setCurrency(rur);
        balList.add(bal);

        bal = new W4CBalance();
        bal.setName("TOTAL_BALANCE");
        bal.setAmount(BigDecimal.valueOf(1500.15));
        bal.setCurrency(rur);
        balList.add(bal);

        bal = new W4CBalance();
        bal.setName("CR_LIMIT");
        bal.setAmount(BigDecimal.valueOf(1109.19));
        bal.setCurrency(rur);
        balList.add(bal);


        if(doc.getDestination()!=null) {
            if(doc.getDestination().getRBSNumber()==null) {
                doc.getDestination().setRBSNumber("888888888888888888888888");
            }
        }

        W4DCStatus status = new W4DCStatus();
        doc.setStatus(status);
        status.setRespClass("Information");
        status.setRespCode(0L);
        status.getRespText().add("Successfully processed");

        doc.setStatus(status);

        return request;
    }

    //    Для запроса баланса (тип записи = Card_InfoRequest) предлагается использовать следующую логику:
    //          - Если номер карты является четным, эмулятор должен отвечать положительно.
    //          - Если номер карты является нечетным, эмулятор должен отвечать отрицательно:
    //    <Status>
    //    <RespClass>Error</RespClass>
    //    <RespCode>5</RespCode>
    //    <RespText>Do not Honour</RespText>
    //    </Status>
    //
    //    Для операции авторизации фин.операции (тип записи = Card_FinRequest) предлагается использовать следующую логику:
    //          - Если целая часть суммы операции является четной, эмулятор должен отвечать положительно.
    //          - Если целая часть суммы операции является нечетной, эмулятор должен отвечать отрицательно:
    //    <Status>
    //    <RespClass>Error</RespClass>
    //    <RespCode>51</RespCode>
    //    <RespText>Not sufficient funds</RespText>
    //    </Status>
    //
    //    Для операции отмены авторизации фин.операции (тип записи = Card_ReversalRequest) предлагается использовать следующую логику:
    //          - Если целая часть суммы операции кратна 5, эмулятор должен отвечать положительно.
    //          - Если целая часть суммы операции не кратна 5, эмулятор должен отвечать отрицательно:
    //    <Status>
    //    <RespClass>Error</RespClass>
    //    <RespCode>79</RespCode>
    //    <RespText>Already reversed</RespText>
    //    </Status>
    private void checkForNegativeAnswer(final String msgCode, final W4MMsg reqMsg) {

        W4UDoc doc = reqMsg.getMsgData().getDoc();
        if (msgCode.equals(Constants.MSG_01000B)) {

            String contractNumber = doc.getRequestor().getContractNumber();
            String substring = contractNumber.substring(contractNumber.length() - 1);
            byte last = Byte.parseByte(substring);
            if (last % 2 != 0) {
                setErrorStatus(doc, 5, "Do not Honour", reqMsg);
            }
            return;
        }
        if (msgCode.equals(Constants.MSG_02000F) || (msgCode.equals(Constants.MSG_02200P))) {
            BigDecimal amount = doc.getTransaction().getAmount();
            if (amount.intValue() % 2 != 0) {
                setErrorStatus(doc, 5, "Not sufficient funds", reqMsg);
            }
        }
        if (msgCode.equals(Constants.MSG_04200P) || msgCode.equals(Constants.MSG_04200F)) {
            BigDecimal amount = doc.getTransaction().getAmount();
            if (amount.intValue() % 5 != 0) {
                setErrorStatus(doc, 79, "Already reversed", reqMsg);
            }
        }

        if (msgCode.equals(Constants.MSG_BILLING_PAYMENT_BILLER_CHECK) || msgCode.equals(Constants.MSG_BILLING_PAYMENT_WITH_CARD)) {
            String cardNumber = doc.getRequestor().getContractNumber();
            if(cardNumber.endsWith("15") || cardNumber.endsWith("16")) {
                setErrorStatus(doc, 1770, "Validation Failure", reqMsg);
            }
        }

    }

    private void setErrorStatus(final W4UDoc doc, final int respCode, final String msg, final W4MMsg reqMsg) {

        W4DCStatus status = doc.getStatus();
        status.setRespClass("Error");
        status.setRespCode(respCode);
        reqMsg.setRespCode((long) respCode);
        List<String> respText = status.getRespText();
        respText.clear();
        respText.add(msg);
    }
}
