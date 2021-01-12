package ru.diasoft.micro.way4ufx.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import ru.diasoft.micro.way4ufx.domain.W4MMsg;
import ru.diasoft.micro.way4ufx.service.Way4UfxProcessorService;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@RestController
public class Way4UfxController {

    private final Logger log = LoggerFactory.getLogger(Way4UfxController.class);

    private Way4UfxProcessorService way4UfxProcessorService;
    private Jaxb2Marshaller jaxb2Marshaller;

    @Autowired
    public Way4UfxController(Way4UfxProcessorService way4UfxProcessorService, Jaxb2Marshaller jaxb2Marshaller){
        this.way4UfxProcessorService = way4UfxProcessorService;
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    @RequestMapping(value = "/")
    public String home() {
        return "";
    }

    @PostMapping("/test")
    public String test(@RequestBody String requestBody){
        log.info("Start processing with test");
        W4MMsg w4MMsgResult = new W4MMsg();
        try {
            JAXBElement<W4MMsg> w4MMsgJAXBElement = (JAXBElement<W4MMsg>) jaxb2Marshaller.unmarshal(new StringSource(requestBody));
            if (w4MMsgJAXBElement.getValue() != null) {
                W4MMsg w4MMsg = w4MMsgJAXBElement.getValue();
                if (w4MMsg.getMsgType().equals("Doc")) {
                    w4MMsgResult = way4UfxProcessorService.processRequestDoc(w4MMsg);
                } else if (w4MMsg.getMsgType().equals("Application")) {
                    w4MMsgResult = way4UfxProcessorService.processRequestApplication(w4MMsg);
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
            log.info("Error processing request");
            e.printStackTrace();
        }
        return "Error";
    }

}
