package booking.model;

import java.util.Objects;


 // equals и hashCode

public class Employee {

    private final String personnelNumber;
    private final String fullName;


   //  personnelNumber - табельный номер

    public Employee(String personnelNumber, String fullName) {
        if (personnelNumber == null || personnelNumber.isBlank()) {
            throw new IllegalArgumentException("Табельный номер обязателен");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("ФИО обязательно");
        }
        this.personnelNumber = personnelNumber;
        this.fullName = fullName;
    }

    public String getPersonnelNumber() { return personnelNumber; }
    public String getFullName() { return fullName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return personnelNumber.equals(employee.personnelNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personnelNumber);
    }

    @Override
    public String toString() {
        return fullName + " (" + personnelNumber + ")";
    }
}