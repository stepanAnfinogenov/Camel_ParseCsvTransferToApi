package com.anf2.esbparsecsvtransfertoapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private String name;
    private String surname;
    private String department;
    private int salary;
}
