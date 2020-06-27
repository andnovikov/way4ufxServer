package ru.diasoft.micro.way4ufx.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.diasoft.micro.way4ufx.model.W4MMsg;
import ru.diasoft.micro.way4ufx.service.Way4UfxProcessorService;

@RestController
public class Way4UfxController {

    private Way4UfxProcessorService way4UfxProcessorService;

    @Autowired
    public Way4UfxController(Way4UfxProcessorService way4UfxProcessorService){
        this.way4UfxProcessorService = way4UfxProcessorService;
    }

    @GetMapping("/ping")
    public String ping(){
        return "Ok";
    }

    @PostMapping("/test")
    public String test(@RequestBody String requestBody){
        return way4UfxProcessorService.processRequest(requestBody);
    }

}
