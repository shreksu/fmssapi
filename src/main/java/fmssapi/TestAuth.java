package fmssapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suyuanyang
 * @create 2017-12-07 下午9:36
 */
@RestController
@RequestMapping("/testsay")
public class TestAuth {
    @RequestMapping(value = "/say", method = RequestMethod.GET)
    public String hello() {
        return "saying ...";
    }
}
