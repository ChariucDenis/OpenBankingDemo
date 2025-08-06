package com.example.demo.Animal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {


    @Autowired
    AnimalRepository animalRepository;

    public void add(Animal animal)
    {
        animalRepository.save(animal);
    }
    public List<Animal> getAll()
    {
        return animalRepository.findAll();
    }

    public void deleteAnimal(Long Id) {
        boolean exists = animalRepository.existsById(Id);
        if(!exists)
            throw new IllegalStateException("student with id " + Id + "does not exist");
        animalRepository.deleteById(Id);
    }

    public Optional<Animal> getAnimal(Long Id) {
        boolean exists = animalRepository.existsById(Id);
        if(!exists)
            throw new IllegalStateException("student with id " + Id + "does not exist");
       return animalRepository.findById(Id);
    }
}
