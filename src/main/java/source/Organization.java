package source;

public class Organization {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Address officialAddress; //Поле не может быть null
    private Float annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    private int employeesCount; //Значение поля должно быть больше 0
    private String fullName; //Длина строки не должна быть больше 1668, Значение этого поля должно быть уникальным, Поле не может быть null
    private OrganizationType type; //Поле не может быть null
}
