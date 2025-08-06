package com.example.demo.Animal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/animal")
public class AnimalController {
    @Autowired
    AnimalService animalService;

    @PostMapping
    public void addAnimal(@RequestBody Animal animal)
    {
        animalService.add(animal);

    }
    @GetMapping
    public List<Animal> gellAll()
    {
        return animalService.getAll();
    }
    @DeleteMapping(path= "/{Id}")
    public void deleteAnimal(@PathVariable("Id") Long Id){
        animalService.deleteAnimal(Id);
    }
    @GetMapping(path = "/{Id}")
    public Optional<Animal> getAnimal(@PathVariable("Id") Long Id){
       return animalService.getAnimal(Id);
    }
}
