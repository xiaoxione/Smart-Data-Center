public class Person{
    private String name = "Person";
    int age=0;
}
class Child extends Person{
    public String grade;
    public static void main(String[] args){
       Person c = new Child();
        System.out.println(c.age);
    }
}