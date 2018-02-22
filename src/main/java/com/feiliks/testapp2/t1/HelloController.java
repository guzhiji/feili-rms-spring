package com.feiliks.testapp2.t1;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/hello")
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/a")
    public ModelAndView a() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("a", 2);
        return new ModelAndView("a1", data);
    }

    @GetMapping("/handle")
    public String handle(Model model) {
        model.addAttribute("message", "Hello World!");
        return "redirect:/app/";
    }
}
