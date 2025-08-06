package com.example.demo.Elev;

import jakarta.persistence.*;

import java.util.Locale;

@Entity
@Table(name = "elev")
public class Elev {
    @Id
    @SequenceGenerator(
            name = "seq",

            allocationSize = 1


    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq"
    )

    private Long id;
    private String name;

    public Elev()
    {

    }
    public Elev(Long id,String name )
    {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
