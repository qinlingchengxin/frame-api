import net.ys.bean.Person;
import net.ys.serialize.ISerialize;
import net.ys.serialize.JdkSerialize;
import net.ys.serialize.KyRoSerialize;
import net.ys.serialize.MsgPackSerialize;

/**
 * User: NMY
 * Date: 19-4-23
 */
public class SerializeTest {
    public static void main(String[] args) {

        Person person = new Person(1, "hello", 12, System.currentTimeMillis());
        ISerialize serialize = JdkSerialize.getInstance();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            System.out.println(serialize.serialize(person).length);
        }
        System.out.println(System.currentTimeMillis() - start);

        serialize = KyRoSerialize.getInstance();
        start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            System.out.println(serialize.serialize(person).length);
        }
        System.out.println(System.currentTimeMillis() - start);

        serialize = MsgPackSerialize.getInstance();
        start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            System.out.println(serialize.serialize(person).length);
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
