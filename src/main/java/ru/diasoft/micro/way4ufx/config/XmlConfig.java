package ru.diasoft.micro.way4ufx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import ru.diasoft.micro.way4ufx.domain.W4MMsg;

@Configuration
public class XmlConfig {

    @Bean
    Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(W4MMsg.class);

        return jaxb2Marshaller;
    }

}
