package com.example.demo.Elev;

import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/elev")
public class ElevController {

    @Autowired
    ElevService elevService;


    @PostMapping
    public void addElev(@RequestBody Elev elev)
    {
        elevService.addElev(elev);
    }
    @GetMapping
    public List<Elev> getElev()
    {
        return elevService.getElev();
    }

    @GetMapping(path = "/{Id}")
    public Optional<Elev> getElevul(@PathVariable("Id") Long id){
        return elevService.getElevul(id);
    }

}
