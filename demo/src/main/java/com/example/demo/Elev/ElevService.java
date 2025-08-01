package com.example.demo.Elev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElevService {


    @Autowired
    public ElevRepository elevRepository;

    public void addElev(Elev elev)
    {
        elevRepository.save(elev);
    }
    public List<Elev> getElev()
    {
        return elevRepository.findAll();
    }
    public Optional<Elev> getElevul(Long id)
    {
        boolean Exist = elevRepository.existsById(id);
        if(!Exist){
            throw new IllegalStateException("Nu exista" + id);

        }
        else
        return elevRepository.findById(id);
    }

}
