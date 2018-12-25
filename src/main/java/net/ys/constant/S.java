package net.ys.constant;

/**
 * User: NMY
 * Date: 17-5-17
 */
public interface S {

    String PERSON_INSERT = "INSERT INTO `person` (`name`, `age`, `create_time`) VALUES (?, ?, ?)";

    String PERSON_UPDATE = "UPDATE `person` SET `name` = ?, `age` = ? WHERE `id` = ?";

    String PERSON_COUNT = "SELECT COUNT(*) FROM `person`";

    String PERSON_COUNT_LIKE = "SELECT COUNT(*) FROM `person` WHERE `name` LIKE ?";

    String PERSON_LIST = "SELECT `id`, `name`, `age`, `create_time` FROM `person1 LIMIT ?,?";

    String PERSON_LIST_LIKE = "SELECT `id`, `name`, `age`, `create_time` FROM `person` WHERE `name` LIKE ? LIMIT ?,?";

    String PERSON_SELECT = "SELECT `id`, `name`, `age`, `create_time` FROM `person` WHERE `id` = ?";


    String ADMIN_SELECT = "SELECT `id`, `mag_type`, `username`, `pwd` FROM `sys_admin` WHERE `username` = ? AND `pwd` = ?";
}