package source;

public class Product {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private UnitOfMeasure unitOfMeasure; //Поле может быть null
    private Organization manufacturer; //Поле может быть null
    private Long price; //Поле не может быть null, Значение поля должно быть больше 0
    private int manufactureCost;
    private long rating; //Значение поля должно быть больше 0
    private String partNumber; //Длина строки должна быть не меньше 25, Длина строки не должна быть больше 44, Строка не может быть пустой, Поле может быть null
    private Person owner; //Поле не может быть null
}