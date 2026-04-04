package com.rsu.peru.corazon.gourmet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "meseros")
@Data
@EqualsAndHashCode(callSuper = true)
public class Mesero extends Usuario {

}
