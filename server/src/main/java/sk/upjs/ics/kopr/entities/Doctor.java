package sk.upjs.ics.kopr.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int specializationCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpecializationCode() {
        return specializationCode;
    }

    public void setSpecializationCode(int specializationCode) {
        this.specializationCode = specializationCode;
    }

    @Override
    public String toString() {
        return "Doctor{" + "id=" + id + ", name='" + name + '\'' + ", specializationCode=" + specializationCode + '}';
    }
}