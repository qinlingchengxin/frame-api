package net.ys.bean;

import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * User: NMY
 * Date: 18-5-18
 */
@Message
public class Person implements Serializable {

    private static final long serialVersionUID = 4456266159406621344L;

    private int id;

    private String name;

    private int age;

    private long createTime;

    public Person() {
    }

    public Person(int id, String name, int age, long createTime) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.createTime = createTime;
    }

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", createTime=" + createTime +
                '}';
    }

    public boolean checkEmpty() {
        return this.id == 0 && this.name.equals("") && this.age == 0 && this.createTime == 0;
    }
}
