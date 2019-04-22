package net.ys.utils;

import com.thoughtworks.xstream.XStream;

public class NewXmlUtil {

    public static void main(String[] args) {
        XStream xStream = new XStream();
        xStream.alias("person", Person.class);//给类名起个别名  <net.ys.utils.Person> 变成 <person>
        Person person = new Person(1, "hello");
        String s = xStream.toXML(person);
        System.out.println(s);
        Object o = xStream.fromXML(s);
        System.out.println(o);
    }

}

class Person {

    private int id;
    private String name;

    Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}